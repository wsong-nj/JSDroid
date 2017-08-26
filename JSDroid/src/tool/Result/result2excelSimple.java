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
		    book= Workbook.createWorkbook(new File(ExcelFileLocation));//���ļ� ,���ļ���������ᴴ��
		    sheet=book.createSheet(SheetName,0); //���ɹ�����
	        Label HeadAppName=new Label(0,0,"Ӧ����"); //��ͷ����
	        sheet.addCell(HeadAppName); 
	        Label HeadEnableJS= new Label(1,0,"ʹ��JavaScript"); 
	        sheet.addCell(HeadEnableJS); 
	        Label HeadFilebasedcrosszonevulner=new Label(2,0,"�����ļ��Ŀ���ű�©��");
	        sheet.addCell(HeadFilebasedcrosszonevulner);
	        Label HeadWebViewUXSSVulner=new Label(3,0,"WebView��վ��ű�©��");
	        sheet.addCell(HeadWebViewUXSSVulner);
	        Label HeadInterfacevulner=new Label(4,0,"JavaScript-Java�ӿ�ע��©��");
	        sheet.addCell(HeadInterfacevulner);
	       } catch (Exception e) {
		       // TODO Auto-generated catch block
		       e.printStackTrace();
	       }    
	}
	public void addOneLine2Excel(String appName,AndroidAnalysis myInstrumentor,ProcessManifest processMan,int i){//�Ѷ�App�����Ľ�������excel���ĵ�i��
		
		System.out.println("-----------------add one record into the excel--------------------------------");
		try{
            Label LineAppName=new Label(0,i,appName);//���ݷ������Ϊ�������һ��Ԫ��
            sheet.addCell(LineAppName);
    	    Label LineEnableJs;
            if(myInstrumentor.flagEnableJS==true)
            {
        	    LineEnableJs=new Label(1,i,"��");
             }
            else
            {
        	    LineEnableJs=new Label(1,i,"��");
            }
            sheet.addCell(LineEnableJs);
           Label LineFileVulner;
           if((myInstrumentor.flagPotentialFileVulner==true)&&(processMan.FileExported==true)){
        	   LineFileVulner=new Label(2,i,"��");
           }
           else{
        	   LineFileVulner=new Label(2,i,"��");
           }
           sheet.addCell(LineFileVulner);
           Label LineWebViewUXSSVulner;
           if((myInstrumentor.flagPotentialUXSSVulner==true)&&(processMan.HttpExported==true)){
        	   LineWebViewUXSSVulner=new Label(3,i,"��");
           }
           else{
        	   LineWebViewUXSSVulner=new Label(3,i,"��");
           }
           sheet.addCell(LineWebViewUXSSVulner);
           Label LineJSInterfaceVulner;
           if((myInstrumentor.flagPotentialInterfaceVulner==true)&&(processMan.FileExported==true||processMan.HttpExported==true)){
        	   LineJSInterfaceVulner=new Label(4,i,"��");
           }
           else{
        	   LineJSInterfaceVulner=new Label(4,i,"��");
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
			book.close();//�رձ��
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
