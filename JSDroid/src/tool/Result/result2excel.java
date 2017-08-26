package tool.Result;

import java.io.File;

import polyglot.ast.Local;

import com.test.xmldata.ProcessManifest;
import tool.Analy.Analysis.AndroidAnalysis;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class result2excel {
	public static WritableWorkbook book; //book
    public static WritableSheet sheet; //sheet
    public static String SheetName="Results"; //sheet name
    
	public void initExcel(String ExcelFileLocation){
	   try {
		    book= Workbook.createWorkbook(new File(ExcelFileLocation));//打开文件 ,若文件不存在则会创建
		    sheet=book.createSheet(SheetName,0); //生成工作表
	        Label HeadAppName=new Label(0,0,"App name"); //表头设置
	        sheet.addCell(HeadAppName); 
	        Label HeadEnableJS= new Label(1,0,"Use JS"); 
	        sheet.addCell(HeadEnableJS); 
	        Label HeadLocalPattern=new Label(2,0,"Local pattern");
	        sheet.addCell(HeadLocalPattern);
	        Label HeadLocalPatternCount=new Label(3,0,"Count");
	        sheet.addCell(HeadLocalPatternCount);
	        Label HeadJSAccess=new Label(4,0,"JSAccess");
	        sheet.addCell(HeadJSAccess);
	        Label HeadRemotePattern=new Label(5,0,"Remote pattern");
	        sheet.addCell(HeadRemotePattern);
	        Label HeadRemotePatternCount=new Label(6,0,"Count");
	        sheet.addCell(HeadRemotePatternCount);
	        Label HeadNavigableWebView=new Label(7,0,"Navigable WebView");
	        sheet.addCell(HeadNavigableWebView);
	        Label HeadInterfacePattern=new Label(8,0,"Interface pattern");
	        sheet.addCell(HeadInterfacePattern);
	        Label HeadInterfacePatternCount=new Label(9,0,"Count");
	        sheet.addCell(HeadInterfacePatternCount);
	        Label HeadUnderJellyBean=new Label(10,0,"Exposed JS interface");
	        sheet.addCell(HeadUnderJellyBean);
	        Label HeadCallbackPattern=new Label(11,0,"Callback pattern");
	        sheet.addCell(HeadCallbackPattern);
	        Label HeadCallbackPatternCount=new Label(12,0,"Count");
	        sheet.addCell(HeadCallbackPatternCount);
	        Label HeadfileRequest=new Label(13,0,"Exposed file://");
	        sheet.addCell(HeadfileRequest);
	        Label HeadhttpRequest=new Label(14,0,"Exposed http://");
	        sheet.addCell(HeadhttpRequest);
	        Label HeadFilebasedcrosszonevulner=new Label(15,0,"File-based cross-zone vulner");
	        sheet.addCell(HeadFilebasedcrosszonevulner);
	        Label headV1Count=new Label(16,0,"V1-Count");
	        sheet.addCell(headV1Count);
	        Label HeadWebViewUXSSVulner=new Label(17,0,"WebView uxss vulner");
	        sheet.addCell(HeadWebViewUXSSVulner);
	        Label headV2Count=new Label(18,0,"V2-Count");
	        sheet.addCell(headV2Count);
	        Label HeadInterfacevulner=new Label(19,0,"JS-to-Java Interface vulner");
	        sheet.addCell(HeadInterfacevulner);
	        Label headV3Count=new Label(20,0,"V3-Count");
	        sheet.addCell(headV3Count);
	        Label headLinesofCode=new Label(21,0,"Lines of code");
	        sheet.addCell(headLinesofCode);
	        Label headUseHttps=new Label(22,0,"UseHttps");
	        sheet.addCell(headUseHttps);
	        Label headTrustDomain=new Label(23,0,"TrustDomain");
	        sheet.addCell(headTrustDomain);
	       } catch (Exception e) {
		       // TODO Auto-generated catch block
		       e.printStackTrace();
	       }    
	}
	public void addOneLine2Excel(String appName,AndroidAnalysis myInstrumentor,ProcessManifest processMan,int i){//把对App分析的结果输出到excel表格的第i行
		
		System.out.println("-----------------add one record into the excel--------------------------------");
		try{
            Label LineAppName=new Label(0,i,appName);//根据分析结果为表中添加一列元素
            sheet.addCell(LineAppName);
    	    Label LineEnableJs;
            if(myInstrumentor.flagEnableJS==true)
            {
        	    LineEnableJs=new Label(1,i,"Yes");
             }
            else
            {
        	    LineEnableJs=new Label(1,i,"No");
            }
            sheet.addCell(LineEnableJs);
            Label LineLocalPattern;
            if(myInstrumentor.flagUseLocalPattern==true)
            {
        	   LineLocalPattern=new Label(2,i,"Yes");
            }
            else
            {
        	   LineLocalPattern=new Label(2,i,"No");
            }
            sheet.addCell(LineLocalPattern);
            Label LineCountLocalPattern=new Label(3,i,Integer.toString(myInstrumentor.countLocalPattern));
            sheet.addCell(LineCountLocalPattern);
            
            Label LineJSAccess;
            if(myInstrumentor.flagPotentialFileVulner==true){
            	LineJSAccess=new Label(4,i,"Yes");
            }
            else{
            	LineJSAccess=new Label(4,i,"No");
            }
            sheet.addCell(LineJSAccess);
            Label LineRemotePattern;
            if(myInstrumentor.flagUseRemotePattern==true)
            {
        	   LineRemotePattern=new Label(5,i,"Yes");
            }
            else
            {
        	   LineRemotePattern=new Label(5,i,"No");
            }
            sheet.addCell(LineRemotePattern);
            Label LineCountRemotePattern=new Label(6,i,Integer.toString(myInstrumentor.countRemotePattern));
            sheet.addCell(LineCountRemotePattern);
            Label LineNavigableWebView;
            if(myInstrumentor.flagPotentialUXSSVulner==true){
            	LineNavigableWebView=new Label(7,i,"Yes");
            }
            else{
            	LineNavigableWebView=new Label(7,i,"No");
            }
            sheet.addCell(LineNavigableWebView);
            
            Label LineInterfacePattern;
            if(myInstrumentor.flagUseInterfacePattern==true)
            {
            	LineInterfacePattern=new Label(8,i,"Yes");
            }
            else
            {
            	LineInterfacePattern=new Label(8,i,"No");
            }
            sheet.addCell(LineInterfacePattern); 
            Label LineCountInterfacePattern=new Label(9,i,Integer.toString(myInstrumentor.countInterfacePattern));
            sheet.addCell(LineCountInterfacePattern);
            Label LineExposedNoJSInterface;
            if(myInstrumentor.flagPotentialInterfaceVulner==true){
            	LineExposedNoJSInterface=new Label(10,i,"No");
            }
            else{
            	LineExposedNoJSInterface=new Label(10,i,"Yes");
            }
            sheet.addCell(LineExposedNoJSInterface);
            Label LineCallbackPattern;
            if(myInstrumentor.flagUseCallbackPattern==true)
            {
        	   LineCallbackPattern=new Label(11,i,"Yes");
            }
            else
           {
        	   LineCallbackPattern=new Label(11,i,"No");
           }
           sheet.addCell(LineCallbackPattern);
           Label LineCountCallbackPattern=new Label(12,i,Integer.toString(myInstrumentor.countCallbackPattern));
           sheet.addCell(LineCountCallbackPattern);
           Label LineFileExported;
           if(processMan.FileExported==true){
        	   LineFileExported=new Label(13,i,"Yes");
           }
           else{
        	   LineFileExported=new Label(13,i,"No");
           }
           sheet.addCell(LineFileExported);
           Label LineHttpExported;
           if(processMan.HttpExported==true){
        	   LineHttpExported=new Label(14,i,"Yes");
           }
           else{
        	   LineHttpExported=new Label(14,i,"No");
           }
           sheet.addCell(LineHttpExported);
           Label LineFileVulner;
           if((myInstrumentor.flagPotentialFileVulner==true)&&(processMan.FileExported==true)){
        	   LineFileVulner=new Label(15,i,"Yes");
           }
           else{
        	   LineFileVulner=new Label(15,i,"No");
           }
           sheet.addCell(LineFileVulner);
           Label v1Count;
           if(myInstrumentor.flagPotentialFileVulner==true){
        	    v1Count=new Label(16,i,Integer.toString(myInstrumentor.countLocalPattern));
           }
           else{
        	   v1Count=new Label(16,i,Integer.toString(0));
           }
           sheet.addCell(v1Count);
           Label LineWebViewUXSSVulner;
           if((myInstrumentor.flagPotentialUXSSVulner==true)&&(processMan.HttpExported==true)){
        	   LineWebViewUXSSVulner=new Label(17,i,"Yes");
           }
           else{
        	   LineWebViewUXSSVulner=new Label(17,i,"No");
           }
           sheet.addCell(LineWebViewUXSSVulner);
           Label v2Count;
           if(myInstrumentor.flagPotentialUXSSVulner==true){
        	    v2Count=new Label(18,i,Integer.toString(myInstrumentor.countWVhasNavigation));
           }
           else{
        	   v2Count=new Label(18,i,Integer.toString(0));
           }
           sheet.addCell(v2Count);
           Label LineJSInterfaceVulner;
           if((myInstrumentor.flagPotentialInterfaceVulner==true)&&(processMan.FileExported==true||processMan.HttpExported==true)){
        	   LineJSInterfaceVulner=new Label(19,i,"Yes");
           }
           else{
        	   LineJSInterfaceVulner=new Label(19,i,"No");
           }
           sheet.addCell(LineJSInterfaceVulner);
           Label v3Count;
           if(myInstrumentor.flagPotentialInterfaceVulner==true){
        	    v2Count=new Label(20,i,Integer.toString(myInstrumentor.countExposedJSInterface));
           }
           else{
        	   v2Count=new Label(20,i,Integer.toString(0));
           }
           sheet.addCell(v2Count);
           Label lineCodelines;
           lineCodelines=new Label(21,i,Integer.toString(myInstrumentor.LinesofCode));
           sheet.addCell(lineCodelines);
           Label LineUseHttps;
           if(processMan.HttpsExported==true)
           {
        	   LineUseHttps=new Label(22,i,"Yes");
            }
           else
           {
        	   LineUseHttps=new Label(22,i,"No");
           }
           sheet.addCell(LineUseHttps);
           Label LineTrustDomain;
   		if(myInstrumentor.trust==true){
   			LineTrustDomain=new Label(23,i,"Yes");
   		}
   		
   		else{
   			LineTrustDomain=new Label(23,i,"No");
   		}
   		sheet.addCell(LineTrustDomain);
         } 		
		 catch(Exception e){ 
        	 System.out.println(e);
         } 
	 } 
	public void WriteAll(){
		try {
    		book.write();
			book.close();//关闭表格
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
