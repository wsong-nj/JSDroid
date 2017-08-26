package tool.Analy.util;

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.toolkits.graph.ExceptionalUnitGraph;
import tool.Analy.MethodAnalysis.IntraMethodAnalysis;
import tool.Analy.tags.MethodTag;

//注意：1. FieldRef指向的第一个值作为其值。 2. 优先级：若FieldRef在UsedMethod中定义，则只分析UsedMethod。
public class FieldAnalysis {
	FieldRef fr;
	SootClass sc;
	//Value value;
	AssignStmt finalDefUnit;
	AssignStmt defUnit;
	SootMethod usedMethod;
	SootMethod defMethod;
	
	public FieldAnalysis(SootMethod usedMethod,FieldRef fr) {
		this.fr = fr;
		this.usedMethod = usedMethod;
		sc = fr.getField().getDeclaringClass();
	}

	public boolean checkUsedMethod(){
		IntraMethodAnalysis intraMethodAnalysis = new IntraMethodAnalysis(usedMethod);
		for(AssignStmt as:intraMethodAnalysis.getAssignStmtsWithLineNumber(true)){
			if(as.getLeftOp() instanceof FieldRef){
				FieldRef frRef = (FieldRef)as.getLeftOp();
				if(frRef.getField().equals(fr.getField())){
					defMethod = usedMethod;
					return true;
				}
			}
		}
		return false;
	}

	public void build(){
		resolveDefMethod();
		resolveDefValue();
	}
	
	private void resolveDefMethod(){
		if(!checkUsedMethod()){
			for(SootMethod method:sc.getMethods()){
				if(method.isConcrete()){
					if(!method.equals(usedMethod)){
						IntraMethodAnalysis intraMethodAnalysis = new IntraMethodAnalysis(method);
						for(AssignStmt as:intraMethodAnalysis.getAssignStmtsWithLineNumber(true)){
							if(as.getLeftOp() instanceof FieldRef){
								FieldRef frRef = (FieldRef)as.getLeftOp();
								if(frRef.getField().equals(fr.getField())){
									defUnit = as;
									defMethod = method;
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void resolveDefValue(){
		if(defUnit!=null&&defMethod!=null){
			finalDefUnit = LocalAnalysis.getDefAssignStmt(defUnit,defUnit.getLeftOp(), new ExceptionalUnitGraph(defMethod.retrieveActiveBody()));
			finalDefUnit.addTag(new MethodTag(defMethod));
		}
	}

	public FieldRef getFr() {
		return fr;
	}

	public SootClass getSc() {
		return sc;
	}

	public AssignStmt getFinalDefUnit() {
//		if(finalDefUnit==null){
//			throw new RuntimeException("Field is null! FieldRef :"+fr.getField());
//		}
		return finalDefUnit;
	}

	public AssignStmt getDefUnit() {
		return defUnit;
	}

	public SootMethod getUsedMethod() {
		return usedMethod;
	}

	public SootMethod getDefMethod() {
		return defMethod;
	}
	
}
