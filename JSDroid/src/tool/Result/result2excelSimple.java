package tool.Result;

import java.io.File;
import com.test.xmldata.ProcessManifest;
import tool.Analy.Analysis.AndroidAnalysis;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class result2excelSimple {
	public static WritableWorkbook book; //book
    public static WritableSheet sheet; //sheet
    public static String SheetName="Results"; //sheet name
    
	public void initExcel(String ExcelFileLocation){
	   try {
		    book= Workbook.createWorkbook(new File(ExcelFileLocation));//打开文件 ,若文件不存在则会创建
		    sheet=book.createSheet(SheetName,0); //生成工作表
	        Label HeadAppName=new Label(0,0,"应用名"); //表头设置
	        sheet.addCell(HeadAppName); 
	        Label HeadEnableJS= new Label(1,0,"使用JavaScript"); 
	        sheet.addCell(HeadEnableJS); 
	        Label HeadFilebasedcrosszonevulner=new Label(2,0,"基于文件的跨域脚本漏洞");
	        sheet.addCell(HeadFilebasedcrosszonevulner);
	        Label HeadWebViewUXSSVulner=new Label(3,0,"WebView跨站点脚本漏洞");
	        sheet.addCell(HeadWebViewUXSSVulner);
	        Label HeadInterfacevulner=new Label(4,0,"JavaScript-Java接口注入漏洞");
	        sheet.addCell(HeadInterfacevulner);
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
        	    LineEnableJs=new Label(1,i,"是");
             }
            else
            {
        	    LineEnableJs=new Label(1,i,"否");
            }
            sheet.addCell(LineEnableJs);
           Label LineFileVulner;
           if((myInstrumentor.flagPotentialFileVulner==true)&&(processMan.FileExported==true)){
        	   LineFileVulner=new Label(2,i,"是");
           }
           else{
        	   LineFileVulner=new Label(2,i,"否");
           }
           sheet.addCell(LineFileVulner);
           Label LineWebViewUXSSVulner;
           if((myInstrumentor.flagPotentialUXSSVulner==true)&&(processMan.HttpExported==true)){
        	   LineWebViewUXSSVulner=new Label(3,i,"是");
           }
           else{
        	   LineWebViewUXSSVulner=new Label(3,i,"否");
           }
           sheet.addCell(LineWebViewUXSSVulner);
           Label LineJSInterfaceVulner;
           if((myInstrumentor.flagPotentialInterfaceVulner==true)&&(processMan.FileExported==true||processMan.HttpExported==true)){
        	   LineJSInterfaceVulner=new Label(4,i,"是");
           }
           else{
        	   LineJSInterfaceVulner=new Label(4,i,"否");
           }
           sheet.addCell(LineJSInterfaceVulner);
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
