package tool.Analy.Def;

import soot.RefType;
import soot.Scene;
import soot.SootMethod;


public class Constants {
	
//android.webkit.WebChromeClient;
//android.webkit.WebSettings;
//android.webkit.WebView;
//android.webkit.WebViewClient;
	
	public static String webView_name="android.webkit.WebView";
	public static RefType string_Type = RefType.v("java.lang.String");
	public static RefType log_Type = RefType.v("android.util.Log");
	public static SootMethod logI_method = Scene.v().getMethod("<android.util.Log: int i(java.lang.String,java.lang.String)>");

}
