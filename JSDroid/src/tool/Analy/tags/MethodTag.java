package tool.Analy.tags;

import soot.SootMethod;
import soot.jimple.Stmt;
import soot.tagkit.Tag;

public class MethodTag implements Tag{

	SootMethod sm;
	public MethodTag(SootMethod sm) {
		this.sm = sm;
	}
	@Override
	public String getName() {
		return "MethodTag";
	}

	
	@Override
	public byte[] getValue() {
        throw new RuntimeException( "AnnotationTag has no value for bytecode" );
    }
	
	public SootMethod getMethod(){
		return sm;
	}
	
	public static SootMethod parseMethod(Stmt s ){
		if(s.hasTag("MethodTag")){
			Tag tag = s.getTag("MethodTag");
			MethodTag mTag = (MethodTag)tag;
			return mTag.getMethod();	
		}
		else 
			throw new RuntimeException("No methodTag in stmt "+s);
		
	}
}
