package tool.Analy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHandler {

	public static Integer splitNumsForMenuItem(String string){
		return splitNums(string, -1);
	}
	
	public static Integer splitNumsForViews(String string){
		return splitNums(string,10000000);
	}
	
	//从string中，提取纯数字
	public static Integer splitNums(String string, int below){
		Pattern p = Pattern.compile("(\\d+)");      
		Matcher m = p.matcher(string);  
		List<Integer> integers = new ArrayList<Integer>();
		while(m.find()){
			String group = m.group();
			int i = Integer.valueOf(group);
			if(i>below)
				integers.add(Integer.valueOf(group));
		}
		
		int size = integers.size();
		if(size>1){
			//System.out.println(integers.get(1)+" "+integers.get(0));
			return integers.get(1);
			//throw new RuntimeException("One Statement has two view id");
		}
		else if(size==1){
			return integers.get(0);
		}
		else{
			return -1;
		}
	}
	
	//旨在获得class的类名。
	public static String getShortActivityName(String sourceString){
		return removeDot(getPackageActivityName(sourceString));
	}
	
	//旨在获取class的全限定名
	public static String getPackageActivityName(String sourceString){
		String tgtClass;
		String[] strings = sourceString.split(" ");
		if(strings.length>1){
			tgtClass = strings[1];
			if(sourceString.contains("\""))
				tgtClass = tgtClass.substring(tgtClass.indexOf("\""),tgtClass.lastIndexOf("\""));	
			if(sourceString.contains("/"))
				tgtClass = tgtClass.replaceAll("/", ".");
//			String[] strings2 = tgtClass.split("/");
//			tgtClass = strings2[strings2.length-1];
		}
		else {
			tgtClass = strings[0];			
		}
		return tgtClass;
	}
	
	//去除引号
	public static String removeQuot(String s){
		StringTokenizer st = new StringTokenizer(s, "\"");
		while(st.hasMoreTokens()){		
			s = st.nextToken();
		}		
		return s;
	}
	
	//去除点
	public static String removeDot(String s){
		StringTokenizer st = new StringTokenizer(s, ".");
		while(st.hasMoreTokens()){		
			s = st.nextToken();
		}
		return s;
	}
}
