package tool.Analy.MethodAnalysis;


import java.util.ArrayList;
import java.util.List;


import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import tool.Analy.tags.MethodTag;
import tool.Analy.type.TypeAndFormat;
import tool.Analy.util.ClassAnalysis;
import tool.Analy.util.LocalAnalysis;

public class IntraMethodAnalysis {

	protected Body body;
	protected Chain<Unit> chain;
	protected UnitGraph unitGraph;
	public IntraMethodAnalysis(SootMethod sm) {
		this.body = sm.retrieveActiveBody();
		this.chain = body.getUnits();
		this.unitGraph = new ExceptionalUnitGraph(body);
	}

//	public AssignStmt getDefitionOfLocal(Unit u, Type t){
//		Value currentValue = null;
//		AssignStmt definitionOfLocal = LocalAnalysis.getDefinitionOfLocal(u, currentValue, unitGraph);
//		
//		Value rightOp = definitionOfLocal.getRightOp();
//		boolean matched = TypeDefinition.match(rightOp, t);
//		if(matched)
//			return definitionOfLocal;
//		return null;
//	}
	
	public static List<InvokeStmt> getListenerStmtsByClass(SootClass param,String listenerMethod){
		List<InvokeStmt> invokeStmts = new ArrayList<InvokeStmt>();
		SootClass outClass = ClassAnalysis.getOutClass(param);
		List<SootMethod> outMethods = outClass.getMethods();
		for(SootMethod callMethod:outMethods){	
			if(callMethod.isConcrete()){
				List<InvokeStmt> stmts = new IntraMethodAnalysis(callMethod).getInvokeStmtsWithLineNumber(true);
				for(InvokeStmt is:stmts){
					String subSignature = is.getInvokeExpr().getMethod().getSubSignature();
					if(subSignature.equals(listenerMethod)){
						is.addTag(new MethodTag(callMethod));
						invokeStmts.add(is);
					}								
				}
			}					
		}
		return invokeStmts;
	}
	
	public static List<InvokeStmt> getListenerStmtsByMethod(SootMethod callMethod,String listenerMethod){
		List<InvokeStmt> invokeStmts = new ArrayList<InvokeStmt>();
		if(callMethod.isConcrete()){
			List<InvokeStmt> stmts = new IntraMethodAnalysis(callMethod).getInvokeStmtsWithLineNumber(true);
			for(InvokeStmt is:stmts){
				String subSignature = is.getInvokeExpr().getMethod().getSubSignature();
				if(subSignature.equals(listenerMethod)){
					is.addTag(new MethodTag(callMethod));
					invokeStmts.add(is);
				}								
			}
		}
		return invokeStmts;
	}
	
	/**
	 * 获得所有的赋值语句
	 * */
	public List<AssignStmt> getAssignStmts() {
		return getAssignStmtsWithLineNumber(false);
	}

	/**
	 * 获得所有的赋值语句和行标
	 * */
	public List<AssignStmt> getAssignStmtsWithLineNumber(Boolean b) {
		final List<AssignStmt> assignStmts = new ArrayList<AssignStmt>();
		int i = 0;
		for(Unit u:chain){
			i++;
			if(u instanceof AssignStmt){			
				Tag t = new IntegerConstantValueTag(i);
				AssignStmt stmt = (AssignStmt)u;
				if(b==true)
					stmt.addTag(t);
				assignStmts.add(stmt);
			}
		}
		return assignStmts;
	}

	/**
	 * 获得所有的调用语句
	 * */
	public List<InvokeStmt> getInvokeStmts() {
		return getInvokeStmtsWithLineNumber(false);
	}

	/**
	 * 获得所有的赋值语句和行标
	 * */
	public List<InvokeStmt> getInvokeStmtsWithLineNumber(Boolean b) {
		final List<InvokeStmt> invokeStmts = new ArrayList<InvokeStmt>();
		int i = 0;
		for(Unit u:chain){
			i++;
			if(u instanceof InvokeStmt){			
				Tag t = new IntegerConstantValueTag(i);
				InvokeStmt stmt = (InvokeStmt)u;
				if(b==true)
					stmt.addTag(t);
				invokeStmts.add(stmt);
				
			}
		}
		return invokeStmts;
	}

	public boolean containsStmt(String s) {
		for(Unit u:unitGraph){
			if(u.toString().contains(s))
				return true;
		}		
		return false;
	}

}