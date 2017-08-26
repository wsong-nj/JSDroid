package tool.Analy.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import tool.Analy.MethodAnalysis.InterMethodAnalysis;
import tool.Analy.tags.MethodTag;


public class LocalAnalysis {
	
	public static AssignStmt getDefAS_IS(InvokeStmt s, UnitGraph ug){
		Value v = s.getInvokeExpr().getUseBoxes().get(0).getValue();
		return getDefAssignStmt(s, v, ug);
	}
	
	public static Value getDefValue_IS(InvokeStmt s, UnitGraph ug){
		Value v = s.getInvokeExpr().getUseBoxes().get(0).getValue();
		return getDefAssignStmt(s, v, ug)!=null?getDefAssignStmt(s, v, ug).getRightOp() :null;
	}
	
	public static Value getDefValue_AS(AssignStmt s, UnitGraph ug){
		Value value = null;
		Unit currentStmt = s;
		Value currentValue = s.getRightOp();
		AssignStmt localDef = null;
		localDef = getDefAssignStmt(currentStmt,currentValue,ug);
		value = localDef.getRightOp();
		return value;
	}
	
	public static Value getDefValue_IF(Value v,Stmt ifs, UnitGraph ug){
		return getDefDS_IF(v, ifs, ug)!=null? getDefDS_IF(v, ifs, ug).getRightOp():null;
	}
	
	public static DefinitionStmt getDefDS_IF(Value v,Stmt ifs, UnitGraph ug){
		DefinitionStmt value = null;		
		Unit currentStmt = ifs;
		AssignStmt localDef = null;
		Value currentValue = v;
		localDef = getDefAssignStmt(currentStmt,currentValue,ug);
		IdentityStmt idenStmt = null;
		
		if(localDef!=null)
			value = localDef;
		else{
			idenStmt = getIdentityStmts(currentStmt, idenStmt, currentValue, ug);
			if(idenStmt!=null){
				value = idenStmt;
			}
		}
		return value;
	}
	
	/**
	 * 求currentValue的定义语句。
	 * */
	public static AssignStmt getDefAssignStmt(Unit currentStmt, Value currentValue, UnitGraph ug){
		AssignStmt defStmt = null;
		while(true){
			if(currentStmt instanceof AssignStmt){
				AssignStmt as = (AssignStmt)currentStmt;
				Value v = as.getLeftOp();
				if(isEqual(v, currentValue)){
					Value rightOp = as.getRightOp();
					if((rightOp instanceof CastExpr)&&((CastExpr)(as.getRightOp())).getType().toString().contains("android")){
						CastExpr ce = (CastExpr)(rightOp);
						currentValue = ce.getOp();
						//continue;
					}
					else if(rightOp instanceof FieldRef){
						FieldAnalysis fieldAnalysis = new FieldAnalysis(ug.getBody().getMethod(), (FieldRef)rightOp);
						if(fieldAnalysis.checkUsedMethod()){
							currentValue = rightOp;
						}
						else{
							fieldAnalysis.build();
							return fieldAnalysis.getFinalDefUnit();
						}
					}
					else if(rightOp instanceof JimpleLocal){
						currentValue = rightOp;
						//continue;
					}
					else{
						defStmt = (AssignStmt)currentStmt;	
						defStmt.addTag(new MethodTag(ug.getBody().getMethod()));
						break;
					}		
				}
			}	
			currentStmt = ug.getPredsOf(currentStmt).get(0);
			if(ug.getHeads().contains(currentStmt))
				break;
		}
		return defStmt;
	}
	
	public static boolean isEqual(Value v1, Value v2){
		if(v2 instanceof FieldRef){
			if(v1 instanceof FieldRef){
				FieldRef fr1 = (FieldRef)v1;
				FieldRef fr2 = (FieldRef)v2;
				if(fr1.getField().equals(fr2.getField())){
					return true;
				}
			}
		}
		else if(v1.equals(v2)){
			return true;
		}
		return false;
	}
	
	public static IdentityStmt getIdentityStmts(Unit currentStmt,IdentityStmt localDef,Value currentValue,UnitGraph ug){
		while(true){
			currentStmt = ug.getPredsOf(currentStmt).get(0);
			if(currentStmt instanceof IdentityStmt){
				IdentityStmt as = (IdentityStmt)currentStmt;
				if(as.getLeftOp().equals(currentValue)){
					if(as.getRightOp() instanceof JimpleLocal){
						currentValue = as.getRightOp();
						continue;
					}
					else{
						localDef = (IdentityStmt)currentStmt;
					}		
				}
			}				
			if(ug.getHeads().contains(currentStmt))
				break;
		}
		return localDef;
	}
	
	//获得local在程序中定义所在方法。
	public static List<Node> getAllocNodes(Local local){	
		final List<Node> allocNodeList = new ArrayList<Node>();	
		PAG pag = (PAG) Scene.v().getPointsToAnalysis();
		PointsToSetInternal pti = (PointsToSetInternal)pag.reachingObjects(local);
		pti.forall(new P2SetVisitor() {			
			@Override
			public void visit(Node n) {
				allocNodeList.add(n);
			}
		});
		return allocNodeList;					
	}	
	
	//获得方法sMethod中，type类型的变量
	public static Local parseLocal(SootMethod sm, String type){
		Chain<Local> locals = sm.retrieveActiveBody().getLocals();
		Local local = null;
		for(Local l:locals){
			if(l.getType().toString().equals(type)){
				local = l;
				break;
			}
		}	
		return local;
	}
	
	public static boolean containLocal(SootMethod sm, String type){
		return parseLocal(sm, type)!=null;
	}
	
	public static List<SootMethod> parseDMBL(Local v, SootMethod sm) {
		return parseDMBL(v, sm, v.getType().toString());
	}
	
	//分析sm中的v的定义方法,类型是:v.getType()
	public static List<SootMethod> parseDMBL(Local v, SootMethod sm, String string){
		List<SootMethod> defineMethods = new ArrayList<SootMethod>();
		List<Node> nodes = getAllocNodes(v);
		for (Node n : nodes) {
			AllocNode an = (AllocNode) n;
			SootMethod sMethod = an.getMethod();
			defineMethods.add(sMethod);
		}
		// 当Scene中不含有v时，则分析v所在的方法。
		if (defineMethods.size() == 0) {
			Set<SootMethod> downDefMethods = searchDownForDefineMethods(sm, string);
			if(downDefMethods.size()>0){ 
				defineMethods.addAll(downDefMethods);
				return defineMethods;
			}
			else {
				Set<SootMethod> upDefMethods = searchUpForDefineMethods(sm, string);
				if(upDefMethods.size()>0){ 
					defineMethods.addAll(upDefMethods);
					return defineMethods;
				}
				else defineMethods.add(sm);
			}
		}
		return defineMethods;
	}
	
	//	2.1向上寻找
	//	2.2向下寻找
	//向上查找sm的前驱
	private static Set<SootMethod> searchUpForDefineMethods(SootMethod sm,String type){
		Set<SootMethod> methods = new HashSet<SootMethod>();
		//1.判断sm中是否含有intent的定义信息，若有，则返回
		if(sm.isConcrete()){
			if(containsInitMethod(sm, type)){
				methods.add(sm);
			}
			else{
				List<Type> parameterTypes = sm.getParameterTypes();
				if(parameterTypes.contains(RefType.v(type))){
					List<SootMethod> sources = InterMethodAnalysis.getSourcesMethods(sm);	
					for(SootMethod src:sources){
						if(src.equals(sm))
							continue;
						if(src.isAbstract()){
							for(SootMethod smMethod:getConcreteMethods(src)){
								Set<SootMethod> methods2 = searchUpForDefineMethods(smMethod, type);
								for(SootMethod m:methods2){
									if(!methods.contains(m)){
										methods.add(m);
									}
								}
							}
						}
						else {
							Set<SootMethod> methods2 = searchUpForDefineMethods(src, type);
							for(SootMethod m:methods2){
								if(!methods.contains(m)){
									methods.add(m);
								}
							}
						}
					}
				}
			}
		}
		return methods;
	}
	
	//向下查找sm的后继
	private static Set<SootMethod> searchDownForDefineMethods(SootMethod sm,String type){
		Set<SootMethod> methods = new HashSet<SootMethod>();
		//1.判断sm中是否含有intent的定义信息，若有，则返回
		if(containsInitMethod(sm, type)){
			methods.add(sm);
		}
		else{
			List<SootMethod> targets = InterMethodAnalysis.getTargetsMethods(sm);	
			for(SootMethod tgt:targets){
				if(tgt.equals(sm))
					continue;
				if(tgt.getReturnType().toString().contains(type)){
					if(tgt.isAbstract()){
						for(SootMethod smMethod:getConcreteMethods(tgt)){
							Set<SootMethod> methods2 = searchDownForDefineMethods(smMethod, type);
							for(SootMethod m:methods2){
								if(!methods.contains(m)){
									methods.add(m);
								}
							}
						}
					}
					else {
						Set<SootMethod> methods2 = searchDownForDefineMethods(tgt, type);
						for(SootMethod m:methods2){
							if(!methods.contains(m)){
								methods.add(m);
							}
						}
					}
				}
			}
		}
		return methods;
	}

	public static Set<SootMethod> getConcreteMethods(SootMethod abstractMethod){
		SootClass declaringClass = abstractMethod.getDeclaringClass();
		Set<SootMethod> methods = new HashSet<SootMethod>();
		List<SootClass> subclasses = Scene.v().getActiveHierarchy().getSubclassesOf(declaringClass);
		for(SootClass subClass:subclasses){
			if(subClass.declaresMethod(abstractMethod.getSubSignature())){
				SootMethod sm = subClass.getMethod(abstractMethod.getSubSignature());
				if(sm.isConcrete()){
					methods.add(sm);
				}
			}
		}
		return methods;
	}
	
	public static List<SootMethod> parseDMBT(SootMethod sMethod, String type){
		Local local = parseLocal(sMethod, type);
		return parseDMBL(local, sMethod);
	}
	
	public static boolean containsInitMethod(SootMethod method,String type){
		if(type==null){
			throw new RuntimeException("Type do not have a stopStmt");
		}
		List<SootMethod> targets = InterMethodAnalysis.getTargetsMethods(method);
		for(SootMethod sm:targets){
			if(sm.getSubSignature().contains("void <init>")){
				if(sm.getDeclaringClass().getName().contains(type)){
		//			System.out.println(method+" SubSignature: "+sm.getSubSignature()+" Signature: "+sm.getSignature());
					return true;
				}
			}
//			if(sm.getSignature().contains("android.content.Intent: void <init>")){
//				//System.out.println(sm+" -------------------"+sm.getSignature());
//				return true;
//			}
		}
		return false;
	}
	
}
