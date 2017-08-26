package tool.Analy.util;

import soot.Scene;
import soot.SootClass;

public class ClassAnalysis {
	
	/**
	 * ���һ������ⲿ��
	 * */
	public static SootClass getOutClass(SootClass innerClass){
		String innerName = innerClass.getName();
		if(innerName.contains("$"))
			innerName = innerName.substring(0,innerName.indexOf("$"));		
		return Scene.v().getSootClass(innerName);
	}
		
}
