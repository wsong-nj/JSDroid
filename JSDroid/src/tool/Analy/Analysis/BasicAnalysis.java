package tool.Analy.Analysis;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.R.string;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.util.Chain;
import tool.Analy.MethodAnalysis.InterMethodAnalysis;

public class BasicAnalysis {
	
	List<SootClass> classesChain; // app's all classes
	
	public BasicAnalysis(){
		classesChain=resolveAllClasses(Scene.v().getClasses());
	}
	
	public List<SootClass> resolveAllClasses(Chain<SootClass> chain) {  //Traverse all classes,add them to list
		List<SootClass> allClasses = new ArrayList<SootClass>();
		for (SootClass s : chain) {
			if (s.isConcrete()) {
				if (!s.getName().startsWith("android")&&!s.getName().startsWith("java")&&!s.getName().startsWith("org")) {
					allClasses.add(s);
				}
			}
		}
		return allClasses;
	}
	
public String FMSearchStringInOneBody(SootClass sc,SootMethod sm,Body b, String valueString, int depth) { //track the url in one method with depth 7
		
		//先搜索赋值语句，再搜索参数语句
		String returnValue=null;
		System.out.println("FMSearchStringInOneBody");
		if(valueString==null){
			  System.out.println("null");
		}
		System.out.println("待查询的value:"+valueString);
		for (Unit unit : b.getUnits()) {
			Stmt stmt=(Stmt)unit;
			if(stmt instanceof AssignStmt){
				Value LeftOPValue=GetLeftOP(unit);
				Value RightOPValue=GetRightOP(unit);
				if(LeftOPValue!=null&&LeftOPValue.toString().equals(valueString)) {
					System.out.println("AssignStmt --"+unit);
					if(RightOPValue.toString().startsWith("$")&&depth < 7) {
						System.out.println("Next:"+RightOPValue.toString());
						FMSearchStringInOneBody(sc,sm,b,RightOPValue.toString(), ++depth);
					}
					else if(RightOPValue.toString().contains("<java.lang.StringBuilder: java.lang.String toString()>")){
						String newValueString=RightOPValue.toString().substring(RightOPValue.toString().indexOf(" "),RightOPValue.toString().indexOf("."));
					    System.out.println("new:"+newValueString);
					    FMSearchStringInOneBody(sc,sm,b,newValueString, ++depth);
					}
					else{
						 System.out.println("RETURN");
						 returnValue=RightOPValue.toString();
						 return returnValue;
					}
				}
			}
		}
		for(Unit unit : b.getUnits()) {
			Stmt stmt=(Stmt)unit;
			 if(stmt instanceof IdentityStmt){
			    Value LeftOPValue=GetLeftOP(unit);
			    Value RightOPValue=GetRightOP(unit);
			    if (LeftOPValue!=null&&LeftOPValue.toString().equals(valueString)) {
			    	System.out.println(" IdentityStmt --"+unit);
				    System.out.println("RETURN");
				    returnValue=RightOPValue.toString();
				    return returnValue;
			    }else{
				    continue;
			   }
		    }
		}
		for(Unit unit:b.getUnits()){
			Stmt stmt=(Stmt)unit;
			 if(stmt instanceof InvokeStmt){
					Value LeftOPValue=GetLeftOP(unit);
					Value RightOPValue=GetRightOP(unit);
					if(LeftOPValue!=null&&LeftOPValue.toString().equals(valueString)){
						System.out.println("leftopValue:"+LeftOPValue);
						System.out.println(" InvokeStmt --"+unit);
						if (RightOPValue.toString().startsWith("$")&&depth < 7) {
							FMSearchStringInOneBody(sc,sm,b,RightOPValue.toString(), ++depth);
						} else {
							System.out.println("RETURN:"+RightOPValue);
							returnValue=RightOPValue.toString();
							return returnValue;
						}
					}
				}
		}
		return returnValue;//not find
	}
	
public Value SearchStringInOneBody(Body b, Value value, int depth) {// 在一个方法体中寻找value的最终赋值，进行深度为7的搜索
	
	for (Unit unit : b.getUnits()) {
        if(unit instanceof AssignStmt){
	    	if (GetLeftOP(unit).equals(value)) {
	    		if(GetRightOP(unit)!=null){
	    			if (GetRightOP(unit).toString().startsWith("$") && depth < 7) {
	    				SearchStringInOneBody(b, GetRightOP(unit), ++depth);
	    			} else {
	    				return GetRightOP(unit);
				    }
				}
	    		else{
	    			continue;
	    		}
			}
		}
		if(unit instanceof IdentityStmt){
            if (GetLeftOP(unit).equals(value)) {// 左值匹配
				return GetRightOP(unit);
			}
		}
	}
	return null;// 遍历三次仍然没有找到就返回空
}
	
	
	public String FindValueString(SootClass sc,SootMethod sm,Body body,Unit EndUnit,Value value){
		//Find value String Forward trace
		
		String valueString = null;
		Chain<Unit> unitChains=body.getUnits();
		Unit predUnit=EndUnit;
		Unit curUnit;
		Stmt curStmt;
		Value targetValue=value;
//		System.out.println("target value:"+value);
		Unit FirstUnit=unitChains.getFirst();
		System.out.println("First unit:"+FirstUnit.toString());
		while(predUnit!=FirstUnit){ 
			System.out.println("target value need to find:"+targetValue);
			if(unitChains.getPredOf(predUnit)==null){
				break;
			}
			curUnit=unitChains.getPredOf(predUnit);
			curStmt=(Stmt) curUnit;
			if(curStmt instanceof AssignStmt){//Assign stmt
				Value leftValue=((AssignStmt) curStmt).getLeftOp();
				if(leftValue==targetValue){
					System.out.println(leftValue+"对比"+targetValue);
					System.out.println("Assign:"+curStmt.toString());
					Value rightValue=((AssignStmt) curStmt).getRightOp();
					System.out.println("LeftValue:"+leftValue.toString());
					System.out.println("RightValue:"+rightValue.toString());
					
					if(rightValue.toString().startsWith("new")){//WebViewClient
						 int index = rightValue.toString().indexOf(" ", 0);
						 valueString = rightValue.toString().substring(index + 1);
						 break;
					}
					else if(rightValue.toString().startsWith("(java.lang.String) $")){
						System.out.println("(java.lang.String) $");
						if(rightValue.getUseBoxes().size()>0){
						     targetValue=rightValue.getUseBoxes().get(0).getValue();
						 	 System.out.println("targetValue update:"+targetValue.toString());
						}
					}
					else if((rightValue.toString().startsWith("<")&&(rightValue.toString().contains(":")))){//static field of current class, goto the class to find static field
						System.out.println("static field of current class");
						String staticFiledString=rightValue.toString();
						String[] str=staticFiledString.split(":");
						String targetClassName=str[0].substring(1, str[0].length());
						System.out.println("static field string:"+staticFiledString);
						System.out.println("Class name:"+targetClassName);
						valueString=FindClassStaticValueString(targetClassName,staticFiledString);
					    System.out.println("valueString:"+valueString);
					    break;
					}
					
					else if(((rightValue.toString().contains("java.lang.String"))||(rightValue.toString().contains("java.lang.StringBuilder"))||(rightValue.toString().contains("java.lang.StringBuffer")))
							&&(rightValue.toString().contains("toString"))){
						System.out.println(leftValue+" String toString() method");
						if(rightValue.getUseBoxes().size()>0){
						     targetValue=rightValue.getUseBoxes().get(0).getValue();
						 	 System.out.println("targetValue update:"+targetValue.toString());
						}
					}
                    else if((rightValue.toString().contains("android.net.Uri$Builder"))&&(rightValue.toString().contains("android.net.Uri$Builder"))){
                    	System.out.println(leftValue+" Uri$Builder build() method");
                    	if(rightValue.getUseBoxes().size()>0){
						     targetValue=rightValue.getUseBoxes().get(0).getValue();
						 	 System.out.println("targetValue update:"+targetValue.toString());
						}
					}
					else if((rightValue.toString().contains("java.lang.String")||rightValue.toString().contains("java.lang.StringBuilder")||rightValue.toString().contains("java.lang.StringBuffer"))
							&&(rightValue.toString().contains("append")||rightValue.toString().contains("<init>")||rightValue.toString().contains("format"))){
						System.out.println(leftValue+" String append()/init()/format() method");
						Value contentAppendedValue=rightValue.getUseBoxes().get(0).getValue();
						System.out.println("content:"+contentAppendedValue.toString());
						String contentString=contentAppendedValue.toString().replaceAll("\"", "");
						if(contentString.startsWith("file:")||contentString.startsWith("data:")||contentString.startsWith("</")
								||contentString.startsWith("javascript:")||contentString.startsWith("http:")||contentString.startsWith("https:")
								||contentString.startsWith("redirect_uri")){
							valueString=contentAppendedValue.toString();
							System.out.println("valueString:"+valueString);
							break;
						}
					}
					else if(rightValue.toString().contains(": java.lang.String")||rightValue.toString().contains(": java.lang.StringBuilder")
							||rightValue.toString().contains(": java.lang.StringBuffer")){
						System.out.println(leftValue+" String other method");
						if(rightValue.getUseBoxes().size()>0){
							Value contentValue=rightValue.getUseBoxes().get(0).getValue();
							System.out.println("content:"+contentValue.toString());
							String contentString=contentValue.toString().replaceAll("\"", "");
							if(contentString.startsWith("file:")||contentString.startsWith("data:")||contentString.startsWith("</")
									||contentString.startsWith("javascript:")||contentString.startsWith("http:")||contentString.startsWith("https:")
									||contentString.startsWith("url")||contentString.startsWith("redirect_uri")){
								valueString=contentValue.toString();
								System.out.println("valueString:"+valueString);
								break;
							}
						}
					}
					else if((rightValue.toString().contains("android.net.Uri$Builder")&&rightValue.toString().contains("append"))){
						System.out.println(leftValue+" Uri append() method");
						if(rightValue.getUseBoxes().size()>0){
							Value contentAppendedValue=rightValue.getUseBoxes().get(0).getValue();
							System.out.println("content:"+contentAppendedValue.toString());
							String contentString=contentAppendedValue.toString().replaceAll("\"", "");
							if(contentString.startsWith("http:")||contentString.startsWith("https:")
									||contentString.startsWith("redirect_uri")){
								valueString=contentAppendedValue.toString();
								System.out.println("Uri append something.");
								break;
							}
						}
					}
					else if(rightValue.toString().contains("java.util.Map: java.lang.Object get")){
						System.out.println(leftValue+" Map get() method");
						if(rightValue.getUseBoxes().size()>0){
							Value contentAppendedValue=rightValue.getUseBoxes().get(0).getValue();
							System.out.println("content:"+contentAppendedValue.toString());
							String contentString=contentAppendedValue.toString().replaceAll("\"", "");
							if(contentString.startsWith("overlayHtml")||contentString.startsWith("baseUrl")
									){
								valueString=contentAppendedValue.toString();
								System.out.println("Map get() method.");
								break;
							}
						}
					}
					else if(rightValue.toString().startsWith("$")){
						targetValue=rightValue;
					}
					else{
						System.out.println("leftValue:"+leftValue);
						if(rightValue.getUseBoxes().size()>0){
						Value rightContentValue=rightValue.getUseBoxes().get(0).getValue();
						if(rightContentValue.toString().contains("virtualinvoke")){
							int index1=rightContentValue.toString().indexOf("&", 0);
							int index2=rightContentValue.toString().indexOf(".", 0);
							String invokerValue=rightContentValue.toString().substring(index1, index2);
							System.out.println("invokeValue:"+invokerValue);
//							targetValue=new Value(invokerValue);
						}
						}
					}
				}
			}
			else if(curStmt instanceof IdentityStmt){// As parameter of method
				Value leftValue=((IdentityStmt) curStmt).getLeftOp();
				if(leftValue==targetValue){
					System.out.println(leftValue+"对比"+targetValue);
					Value rightValue=((IdentityStmt) curStmt).getRightOp();
					System.out.println("Parameter of method:"+rightValue);
					//@parameter0: android.webkit.WebViewClient
		            int index=rightValue.toString().indexOf(":", 0);
					String parameterindex=rightValue.toString().substring(0,index);
					String parameterName=rightValue.toString().substring(index+2,rightValue.toString().length());
					//@parameter1: java.lang.String
					System.out.println("index:"+parameterindex);
					System.out.println("name:"+parameterName);
					if(parameterName.equals("android.webkit.WebViewClient")){
						valueString=parameterName;
					}
					else if(parameterName.equals("java.lang.String")){
						List<SootMethod> sources = InterMethodAnalysis.getSourcesMethods(sm);
						if(sources.size()==0){
							System.out.println("No method call this method!");
						}
						else{
						    valueString=FindValueStringInSourceMethod(sm,sources,rightValue);
						}
					}
					else{//WebViewClient
						valueString=parameterName;
						System.out.println("other parameter.");
					}
					break;
				}
			}
			else if((curStmt instanceof InvokeStmt)){
				System.out.println("invokeStmt:"+curStmt);
				Value invokeValue=GetInvoker(curUnit);
				if(invokeValue!=null){
//					Value rightValue=curStmt.get
					System.out.println("invoker:"+invokeValue.toString());
					if(invokeValue==targetValue){
						System.out.println(invokeValue+"对比"+targetValue);
						InvokeExpr invokeExpr=curStmt.getInvokeExpr();
					    String InvokeMethodName=invokeExpr.getMethod().getName();
					    System.out.println(invokeExpr.toString());
					    System.out.println("method:"+invokeExpr.getMethod().getName());
					    if((invokeExpr.toString().contains("java.lang.String")||invokeExpr.toString().contains("java.lang.StringBuilder")||(invokeExpr.toString().contains("java.lang.StringBuffer")))&&(InvokeMethodName.equals("<init>"))){
					    	if(invokeExpr.getArgCount()>0){
					    		String contentString=invokeExpr.getArg(0).toString();
					    		contentString=contentString.replaceAll("\"", "");
					    		System.out.println("contentString:"+contentString);
					    		if(contentString.startsWith("file:")||contentString.startsWith("data:")||contentString.startsWith("</")
										||contentString.startsWith("javascript:")||contentString.startsWith("http:")||contentString.startsWith("https:")
										||contentString.startsWith("redirect_uri")){
									valueString=contentString;
									System.out.println("here");
									System.out.println("valueString:"+valueString);
									break;
								}
					    		else if(contentString.startsWith("$")){
					    			targetValue=invokeExpr.getArg(0);
					    			System.out.println("targetvalue update:"+targetValue);
					    		}
						    }
					   }else if((invokeExpr.toString().contains("android.net.Uri$Builder")&&invokeExpr.toString().contains("append"))){
							System.out.println("Uri append() method");
							if(invokeExpr.getArgCount()>0){
					    		String contentString=invokeExpr.getArg(0).toString();
					    		contentString=contentString.replaceAll("\"", "");
					    		System.out.println("contentString:"+contentString);
						        if(contentString.startsWith("http:")||contentString.startsWith("https:")
										||contentString.startsWith("redirect_uri")){
						        	valueString=contentString;
						        	System.out.println("Uri append something.");
						        	break;
						        }else if(contentString.startsWith("$")){
					    			targetValue=invokeExpr.getArg(0);
					    			System.out.println("targetvalue update:"+targetValue);
					    		}
						    }
						}else if(invokeExpr.toString().contains("java.util.Map: java.lang.Object get")){
							System.out.println(" Map get() method");
							if(invokeExpr.getArgCount()>0){
								String contentString=invokeExpr.getArg(0).toString();
					    		contentString=contentString.replaceAll("\"", "");
					    		System.out.println("contentString:"+contentString);
					    		if(contentString.startsWith("overlayHtml")||contentString.startsWith("baseUrl")
										){
						        	valueString=contentString;
						        	System.out.println("Map get() something.");
						        	break;
						        }else if(contentString.startsWith("$")){
					    			targetValue=invokeExpr.getArg(0);
					    			System.out.println("targetvalue update:"+targetValue);
					    		}
							}
						}
					}
				}
			}
			else{
				System.out.println(curStmt.getClass().getName());
				System.out.println(curStmt.toString());
			}
			predUnit=curUnit;
		}
		return valueString;
	}
	
	public String FindClassStaticValueString(String className,String staticFiledString){
		String classStaticString=null;
		for (SootClass sc : classesChain) {// every class
//			System.out.println("class:"+sc.getName());
			if(sc.getName().equals(className)){
				for (SootMethod sm : sc.getMethods()) {// every method
					System.out.println("Method:"+sm.getName());
					if(sm.getName().equals("<clinit>")){
						System.out.println("clinit");
						Body body = sm.retrieveActiveBody();
						for (Unit unit:body.getUnits()) {
							 Stmt stmt=(Stmt)unit;
							 if(stmt instanceof AssignStmt){
								Value leftValue=((AssignStmt) stmt).getLeftOp();
								if(leftValue.toString().equals(staticFiledString)){
									Value rightValue=((AssignStmt) stmt).getRightOp();
									classStaticString=rightValue.toString();
								}
							 }
						}
						break;
					}
				}
				
			}
		}
		return classStaticString;
	}
	
	public String FindValueStringInSourceMethod(SootMethod sm,List<SootMethod> sources,Value rightValue){
		//Trace parameter of a method
		
		String valueString=null;
		for (SootMethod sourcesMethod : sources) {
			System.out.println(sourcesMethod.getName());
			if (!sourcesMethod.getName().equals("dummyMainMethod")) {
				if (sourcesMethod.isConcrete()) {
					Body body = sourcesMethod.retrieveActiveBody();
					for (Unit unit : body.getUnits()) {
						Stmt curStmt=(Stmt)unit;
						if(curStmt instanceof InvokeStmt){
							String invokeMethodName=curStmt.getInvokeExpr().getMethod().getName();
							if ((invokeMethodName.equals(sm.getName()))){
								System.out.println("invokeStmt:"+curStmt.toString());
							}
						}
					}
			    }
			}
		}
		return valueString;
	} 
	
	public boolean IsSuccUnit(Unit targetUnit,Unit addJSUnit,Chain<Unit> unitChains){
		//If targetUnit is succ unit of addJSUnit in the unitChains
		
		boolean flag=false;
		Unit predUnit=targetUnit;
		Unit curUnit;
//		System.out.println("targetunit:"+targetUnit);
		while(predUnit!=unitChains.getFirst()){ 
			curUnit=unitChains.getPredOf(predUnit);
//			System.out.println("here:"+curUnit);
			if(curUnit==addJSUnit){
//				System.out.println("here:==");
			    flag=true;
			    break;
		    }
			else{
			  predUnit=curUnit;
		    }
	    }
		return flag;
	}
	
	
	
	public Value GetReturnValue(Unit unit) {// 对return 语句找
		Value returnValue = null;
		Stmt stmt=(Stmt)unit;
		if(stmt instanceof ReturnStmt){
//		     returnValue=stmt.
			returnValue=((ReturnStmt) stmt).getOp();
		}
		return returnValue;
	}

	public Value GetLeftOP(Unit unit) {  //取赋值或者调用语句的最后一个参数,即左值
		Value leftop = null;
		Stmt stmt=(Stmt)unit;
		if(stmt instanceof AssignStmt){//赋值语句获取左值
			leftop=((AssignStmt) stmt).getLeftOp();
		}
		else if(stmt instanceof IdentityStmt){
			leftop=((IdentityStmt) stmt).getLeftOp();
		}
		else if(stmt instanceof InvokeStmt){//调用语句获得最后一个value
			List<ValueBox> ValueBoxList=unit.getUseAndDefBoxes();
			if(ValueBoxList.size()>2){
				leftop=ValueBoxList.get(ValueBoxList.size()-2).getValue();
			}
		}
		return leftop;
	}
	
	
	public Value GetInvoker(Unit unit){
		
		Value invokeValue=null;
		Stmt stmt=(Stmt) unit;
		if(stmt instanceof InvokeStmt){//调用语句获得最后一个value
			List<ValueBox> ValueBoxList=unit.getUseAndDefBoxes();
			if(ValueBoxList.size()>2){
				invokeValue=ValueBoxList.get(ValueBoxList.size()-2).getValue();
			}
		}
		else{
			System.out.println("Error Input,not a InvokeStmt.");
		}
		if(invokeValue!=null){
			System.out.println("invoke value:"+invokeValue.toString());
		}
		return invokeValue;
	}
	
	public  Value GetRightOP(Unit unit) {// 取赋值语句或者调用语句的第一个参数，即右值
		Value rightop = null;
		Stmt stmt=(Stmt)unit;
		if(stmt instanceof InvokeStmt){
			if(stmt.containsInvokeExpr()){
				if(stmt.containsInvokeExpr()){
					InvokeExpr expr=stmt.getInvokeExpr();
				    List<Value> argsList=expr.getArgs();
				    if(argsList.size()!=0){
				    	rightop=argsList.get(0);
				    }
				}
		    }
		}
		else if(stmt instanceof AssignStmt){
			rightop=((AssignStmt) stmt).getRightOp();
		}
		else if(stmt instanceof IdentityStmt){
			rightop=((IdentityStmt) stmt).getRightOp();
		}
		return rightop;
	}
	
	public Value GetSecondRightOP(Unit unit) {// 针对 addjavaScriptInterface语句获取接口名称，针对loadDataWithBaseURL语句,获得url
		Value secondRightValue = null;
		Stmt stmt=(Stmt)unit;
		if(stmt instanceof InvokeStmt){
			if(stmt.containsInvokeExpr()){
				InvokeExpr expr=stmt.getInvokeExpr();
		        List<Value> argsList=expr.getArgs(); 
		        if(argsList.size()>=2){
		        	secondRightValue=argsList.get(1);
		        }
			}
		}
		return secondRightValue;
	}
	
	public Value GetLastRightOP(Unit unit){//取赋值右边最后一个参数
		   Value v=null;
		   Stmt stmt=(Stmt)unit;
			if(stmt instanceof InvokeStmt){
				if(stmt.containsInvokeExpr()){
					InvokeExpr expr=stmt.getInvokeExpr();
			        List<Value> argsList=expr.getArgs();
			        for(int i=0;i<argsList.size();i++){
			        	v=argsList.get(i);
			        }
				}
			}
			else if(stmt instanceof AssignStmt){
				ValueBox vbBox=((AssignStmt) stmt).getRightOpBox();
				v=vbBox.getValue();
			}
		   return v;
	 }
	
}
