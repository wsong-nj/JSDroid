package tool.Analy.type;



import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Type;

public class TypeAndFormat extends Types{
	public static Map<Type,List<String>> typeToFormat = new HashMap<Type, List<String>>();
	
	//类型view
	private final static String FINDVIEWBYID = "android.view.View findViewById(int)";
	private final static String[] VIEWS = {FINDVIEWBYID};
	
	//类型StringBuilder--<init>() & append()
	private final static String INIT_STRINGBUILDER = "void <init>(java.lang.String)";
	private final static String APPEND_STRINGBUILDER = "java.lang.StringBuilder append(java.lang.String)";
	private final static String[] STRINGBUILDER = {INIT_STRINGBUILDER,APPEND_STRINGBUILDER};
	
	//类型Uri--parse()
	private final static String PARSE_URI = "android.net.Uri parse(java.lang.String)";
	private final static String[] URI = {PARSE_URI};
	
	//类型String--toString()
	private final static String TOSTRING_STRING = "java.lang.String toString()";
	private final static String[] STRING = {TOSTRING_STRING};
	
	//Intent
//	private final static List<String> INTENT = IntentConstants.getIntent_Def();
	
	//Component
	private final static String COMPONENT_INIT1  = "void <init>(java.lang.String,java.lang.String)";
	private final static String COMPONENT_INIT2  = "void <init>(android.content.Context,java.lang.String)";
	private final static String COMPONENT_INIT3  = "void <init>(android.content.Context,java.lang.Class)";
	private final static String[] COMPONENT = {COMPONENT_INIT1,COMPONENT_INIT2,COMPONENT_INIT3};
	
	static{
		typeToFormat.put(URI_TYPE,
				Arrays.asList(URI));
		typeToFormat.put(STRINGBUILDER_TYPE, 
				 Arrays.asList(STRINGBUILDER));
		typeToFormat.put(STRING_TYPE, Arrays.asList(STRING));
//		typeToFormat.put(INTENT_TYPE, INTENT);
		typeToFormat.put(VIEW_TYPE, Arrays.asList(VIEWS));
		typeToFormat.put(IMAGEBUTTON_TYPE, Arrays.asList(VIEWS));
		typeToFormat.put(IMAGEVIEW_TYPE, Arrays.asList(VIEWS));
		typeToFormat.put(BUTTON_TYPE, Arrays.asList(VIEWS));
		typeToFormat.put(TEXTVIEW_TYPE, Arrays.asList(VIEWS));
		typeToFormat.put(EDITTEXT_TYPE, Arrays.asList(VIEWS));
		typeToFormat.put(COMPONENT_TYPE, Arrays.asList(COMPONENT));
	}
	
	public static Map<Type, List<String>> getTypeToFormat() {
		return typeToFormat;
	}

	public static String getFindviewbyid() {
		return FINDVIEWBYID;
	}

	public static String[] getViews() {
		return VIEWS;
	}

	public static String getInitStringbuilder() {
		return INIT_STRINGBUILDER;
	}

	public static String getAppendStringbuilder() {
		return APPEND_STRINGBUILDER;
	}

	public static String[] getStringbuilder() {
		return STRINGBUILDER;
	}

	public static String getParseUri() {
		return PARSE_URI;
	}

	public static String[] getUri() {
		return URI;
	}

	public static String getTostringString() {
		return TOSTRING_STRING;
	}

	public static String[] getString() {
		return STRING;
	}

	//public static List<String> getIntent() {
	//	return INTENT;
	//}

	public static String getComponentInit1() {
		return COMPONENT_INIT1;
	}

	public static String getComponentInit2() {
		return COMPONENT_INIT2;
	}

	public static String getComponentInit3() {
		return COMPONENT_INIT3;
	}

	public static String[] getComponent() {
		return COMPONENT;
	}
	
	
}