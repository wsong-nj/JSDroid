package tool.Analy.type;

import java.util.Arrays;
import java.util.List;

import soot.RefType;
import soot.Type;
import tool.Analy.util.ArrayUtil;

public class Types {
	
	protected final static Type STRING_TYPE = RefType.v("java.lang.String"); 
	protected final static Type URI_TYPE = RefType.v("android.net.Uri"); 
	protected final static Type STRINGBUILDER_TYPE = RefType.v("java.lang.StringBuilder"); 
	protected final static Type INTENT_TYPE = RefType.v("android.content.Intent");
	protected static final Type VIEW_TYPE = RefType.v("android.view.View");
	protected final static Type IMAGEVIEW_TYPE = RefType.v("android.widget.ImageView");
	protected final static Type IMAGEBUTTON_TYPE = RefType.v("android.widget.ImageButton");
	protected final static Type BUTTON_TYPE = RefType.v("android.widget.Button");
	protected final static Type TEXTVIEW_TYPE = RefType.v("android.widget.TextView");
	protected final static Type EDITTEXT_TYPE = RefType.v("android.widget.EditText");

	protected final static Type[] VIEWTYPES = {VIEW_TYPE,IMAGEBUTTON_TYPE,IMAGEVIEW_TYPE,BUTTON_TYPE,TEXTVIEW_TYPE,EDITTEXT_TYPE};
	
	protected final static Type COMPONENT_TYPE = RefType.v("android.content.ComponentName");
	
	protected final static Type[] TYPES = {URI_TYPE,STRINGBUILDER_TYPE,STRING_TYPE,
		INTENT_TYPE, VIEW_TYPE, COMPONENT_TYPE,IMAGEBUTTON_TYPE,IMAGEVIEW_TYPE,BUTTON_TYPE,TEXTVIEW_TYPE,EDITTEXT_TYPE};
	
	public static List<Type> getTypes() {
		return Arrays.asList(TYPES);
	}
	
	public static Type getStringType() {
		return STRING_TYPE;
	}
	public static Type getUriType() {
		return URI_TYPE;
	}
	public static Type getStringbuilderType() {
		return STRINGBUILDER_TYPE;
	}
	public static Type getIntentType() {
		return INTENT_TYPE;
	}
	
	public static List<Type> getViewTypes() {
		return Arrays.asList(VIEWTYPES);
	}
	
	public static Type getComponentType() {
		return COMPONENT_TYPE;
	}
	
}
