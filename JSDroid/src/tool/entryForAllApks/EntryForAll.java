package tool.entryForAllApks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import jxl.Sheet;
import jxl.Workbook;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.button.StandardButtonShaper;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.BusinessBlueSteelSkin;
import org.jvnet.substance.skin.OfficeSilver2007Skin;
import org.jvnet.substance.title.FlatTitlePainter;
import org.jvnet.substance.watermark.SubstanceStripeWatermark;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.options.Options;
import tool.Analy.Analysis.AndroidAnalysis;
import tool.GUI.userWindowForAll;
import tool.Result.result2excel;
import tool.Result.result2excelSimple;
import android.R.integer;

import com.test.xmldata.ProcessManifest;

public class EntryForAll { //Entry
	
	private String apkFileDirectory; //APK fileS directory
	private String androidPlatformLocation; //Android platform path
	private String apkFileLocation; //APK file path
	private ArrayList<String> AllApkFilePathList; //All APK file path list
	private result2excel excel; //Excel
	private String ResultExcelLocation; //Excel path
	public long runningTime;
	public int selectedApkCount;
	
    public EntryForAll(String[] args){ //Initialization
    	
		apkFileDirectory=args[0];
		androidPlatformLocation=args[1];
		AllApkFilePathList=new ArrayList<String>();
		ResultExcelLocation="Results.xls";
		excel=new result2excel();
		excel.initExcel(this.ResultExcelLocation);
	}
    
	public ArrayList<String> getApkFiles(){ //Get APK files, return APK name list
	     
		 File f=new File(this.apkFileDirectory);
		 File[] list=f.listFiles();
		 String filePath,fileName,fileExtension; 
		 ArrayList<String> apkNameList=new ArrayList<String>();
		 for(int i=0;i<list.length;i++)
		 {
			 filePath=list[i].getAbsolutePath(); 
			 fileName=list[i].getName();  
			 int index1=filePath.lastIndexOf(".");
			 int index2=filePath.length();
			 fileExtension=filePath.substring(index1+1,index2);
			 if(fileExtension.equals("apk")){ //If the file is APK
				 this.AllApkFilePathList.add(filePath);
				 apkNameList.add(fileName);
			 }
		 }
	     return apkNameList;
	}
	
	public void AnalyzeAll(ArrayList<Integer> selectedApkIndexList){//Analyze all APKs
        /*
         * Param: Index list of selected APKs  
         */
		selectedApkCount=selectedApkIndexList.size(); //Number of selected APKs
		int curIndex; //Current APK index
		String curAppName; //Current APK name
		long start=System.currentTimeMillis(); // Time when it starts 
		for(int i=0;i<selectedApkCount;i++){
			curIndex=selectedApkIndexList.get(i);
			apkFileLocation=AllApkFilePathList.get(curIndex).toString();
			curAppName = apkFileLocation.substring(apkFileLocation.lastIndexOf("\\") + 1, apkFileLocation.lastIndexOf("."));
			System.out.println("App count: "+(i+1));
			System.out.println("App path: "+apkFileLocation);
			System.out.println("App name: "+curAppName);
			String param[] = {"-android-jars",androidPlatformLocation,"-process-dir", apkFileLocation};
			initSoot(param);
			AndroidAnalysis analysis=new AndroidAnalysis(curAppName);
			analysis.Analyze();
			ProcessManifest processMan = new ProcessManifest();
			processMan.loadManifestFile(apkFileLocation);
			System.out.println("FileExported:"+processMan.FileExported);
			System.out.println("HttpExported:"+processMan.HttpExported);
			excel.addOneLine2Excel(curAppName, analysis, processMan,i+1);
			AnalyzeVulnerAndDisplayResults(analysis,processMan);
			if(processMan.HttpsExported){
				System.out.println("该app只接受https://浏览请求");
			}
			else {
				System.out.println("该app并没有采取只接受https://浏览请求机制");
			}
			if(analysis.trust){
				System.out.println("该app采用了只加载信任域的代码机制");
			}
			else{
				System.out.println("该app没有采用只加载信任域的代码机制");

			}
			G.v();
			G.reset();
		}
		excel.WriteAll(); //Write all line into excel
		long end=System.currentTimeMillis(); //Time when it ends
	    runningTime=(end-start)/1000; // Running Time(:s)
		System.out.println("It takes "+runningTime+" seconds to analyze all these "+selectedApkCount+" apps");
	}
	
	public void AnalyzeVulnerAndDisplayResults(AndroidAnalysis analysis, ProcessManifest processMan){
		if((analysis.flagPotentialFileVulner==true)&&(processMan.FileExported==true)){
			System.out.println("The app exists File-based cross-zone vulnerability.");
		}
        else{
        	System.out.println("The app does not exist File-based cross-zone vulnerability.");
        }
		if((analysis.flagPotentialUXSSVulner==true)&&(processMan.HttpExported==true)){
			System.out.println("The app exists WebView UXSS vulnerability.");
        }
        else{
        	System.out.println("The app does not exist WebView UXSS vulnerability.");
        }
		if((analysis.flagPotentialInterfaceVulner==true)&&(processMan.FileExported==true||processMan.HttpExported==true)){
			System.out.println("The app exists JavaScript-to-Java interface vulnerability.");
        }
        else{
            System.out.println("The app does not exist JavaScript-to-Java interface vulnerability.");
        }
	}

    public Object[] getResult(int i){//Get results
       /*
        * param: index
        * return: object array of one result
        */
    	Workbook book;
		try {
	    	book = Workbook.getWorkbook(new File(ResultExcelLocation));	
			Sheet sheet=book.getSheet(result2excel.SheetName);
			int cols=sheet.getColumns();
//			Object[] rowData=new Object[cols];
			Object[] rowData=new Object[7];
//			此处需要修改，应该是选取对应的四列数据，取表格中每行的第0,1,15,17,19这五列
			rowData[0]=sheet.getCell(0,i).getContents();
			rowData[1]=sheet.getCell(1,i).getContents();
			rowData[2]=sheet.getCell(15,i).getContents();
			rowData[3]=sheet.getCell(17,i).getContents();
			rowData[4]=sheet.getCell(19,i).getContents();
			rowData[5]=sheet.getCell(22,i).getContents();
			rowData[6]=sheet.getCell(23,i).getContents();
//			for(int j=0;j<cols;j++){
//				rowData[j]=sheet.getCell(j,i).getContents();
//			}
			book.close();
			return rowData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void initSoot(String[] args){ // soot initialization
		/*
		 * Params
		 * args[0,1]: APK file location   
		 * args[2,3]: Path of Android platform 
		 */
//    	Options.v().set_validate(true);
    	Options.v().set_soot_classpath(this.apkFileLocation+";"+
				"/lib/jce.jar;" +
				"/lib/tools.jar;" +
				"lib/android.jar;"+
				"/lib/android-support-v4.jar;"+
				"/bin;"
				);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_format(Options.output_format_jimple);
		Options.v().set_output_dir("JimpleOutput");
		Options.v().set_keep_line_number(true);
		Options.v().set_prepend_classpath(true);
	    Options.v().set_allow_phantom_refs(true);
    	Options.v().set_android_jars(args[1]);
	    Options.v().set_process_dir(Collections.singletonList(args[3]));
	    Options.v().set_whole_program(true);
		Options.v().set_force_overwrite(true); 
		Scene.v().loadNecessaryClasses();	// Load necessary classes
        CHATransformer.v().transform(); //Call graph
        Scene.v().addBasicClass("java.io.BufferedReader",SootClass.HIERARCHY);
		Scene.v().addBasicClass("java.lang.StringBuilder",SootClass.BODIES);
		Scene.v().addBasicClass("java.util.HashSet",SootClass.BODIES);
		Scene.v().addBasicClass("android.content.Intent",SootClass.BODIES);
		Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES); 
        Scene.v().addBasicClass("com.app.test.CallBack",SootClass.BODIES);		
        Scene.v().addBasicClass("java.io.Serializable",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.io.Serializable",SootClass.BODIES);
        Scene.v().addBasicClass("android.graphics.PointF",SootClass.SIGNATURES);
        Scene.v().addBasicClass("android.graphics.PointF",SootClass.BODIES);
        Scene.v().addBasicClass("org.reflections.Reflections",SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.reflections.scanners.Scanner",SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.reflections.scanners.SubTypesScanner",SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.lang.ThreadGroup",SootClass.SIGNATURES);
        Scene.v().addBasicClass("com.ironsource.mobilcore.OfferwallManager",SootClass.HIERARCHY);
        Scene.v().addBasicClass("bolts.WebViewAppLinkResolver$2",SootClass.HIERARCHY);
        Scene.v().addBasicClass("com.ironsource.mobilcore.BaseFlowBasedAdUnit",SootClass.HIERARCHY);
        Scene.v().addBasicClass("android.annotation.TargetApi",SootClass.SIGNATURES);
        Scene.v().addBasicClass("com.outfit7.engine.Recorder$VideoGenerator$CacheMgr",SootClass.HIERARCHY);
        Scene.v().addBasicClass("com.alibaba.motu.crashreporter.handler.CrashThreadMsg$",SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.lang.Cloneable",SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.util.EncodingUtils",SootClass.SIGNATURES);
        Scene.v().addBasicClass("org.apache.http.protocol.HttpRequestHandlerRegistry",SootClass.SIGNATURES);
        Scene.v().addBasicClass("org.apache.commons.logging.Log",SootClass.SIGNATURES);
        Scene.v().addBasicClass("org.apache.http.params.HttpProtocolParamBean",SootClass.SIGNATURES);
        Scene.v().addBasicClass("org.apache.http.protocol.RequestExpectContinue",SootClass.SIGNATURES);
        Scene.v().loadClassAndSupport("Constants");	
	}
}
