package tool.Analy.util;

import soot.Scene;
import soot.SootClass;

public class ClassAnalysis {
	
	/**
	 * 获得一个类的外部类
	 * */
	public static SootClass getOutClass(SootClass innerClass){
		String innerName = innerClass.getName();
		if(innerName.contains("$"))
			innerName = innerName.substring(0,innerName.indexOf("$"));		
		return Scene.v().getSootClass(innerName);
	}
		
}
