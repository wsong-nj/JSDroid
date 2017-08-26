/*******************************************************************************
 * Copyright (c) 2012 Secure Software Engineering Group at EC SPRIDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors: Christian Fritz, Steven Arzt, Siegfried Rasthofer, Eric
 * Bodden, and others.
 ******************************************************************************/
package com.test.xmldata;


import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.xmlpull.v1.XmlPullParser;
import com.content.res.xmlprinter.AXmlResourceParser;

public class ProcessManifest {
	
	public boolean ActivityExported;
	public boolean IntentActionView;
	public boolean IntentCategoryDefault;
	public boolean IntentCategoryBrowsable;
	public boolean IntentDataSchemeFile;
	public boolean IntentDataSchemeHttp;
	public boolean IntentDataSchemeJS;
	public boolean FileExported;
	public boolean HttpExported;
    public boolean Http;
    public boolean Https;
    public boolean HttpsExported;
    public boolean HttpInActivity;
    public boolean HttpsInActivity;
	
    private String applicationName = "";
	private int versionCode = -1;
	private String versionName = "";
	private String packageName = "";
	private int minSdkVersion = -1;
	private int targetSdkVersion = -1;
	public String mainActivity = null;
	private final Set<String> permissions = new HashSet<String>();
	
	public ProcessManifest(){
		ActivityExported=false;
		IntentActionView=false;
		IntentCategoryDefault=false;
		IntentCategoryBrowsable=false;
		IntentDataSchemeFile=false;
		IntentDataSchemeHttp=false;
		IntentDataSchemeJS=false;
		FileExported=false;
		HttpExported=false;
		Http=false;
		Https=false;
		HttpsExported=false;
		HttpInActivity=false;
		HttpsInActivity=false;


	}
	
	public boolean getFileExportedValue(){
		return FileExported;
	}
	
	public boolean getHttpExportedValue(){
		return HttpExported;
	}
	public boolean getHttpsExportedValue(){
		return HttpsExported;
	}
	private void handleAndroidManifestFile(String apk, IManifestHandler handler) {
		File apkF = new File(apk);
		if (!apkF.exists())
			throw new RuntimeException("file '" + apk + "' does not exist!");

		boolean found = false;
		try {
			ZipFile archive = null;
			try {
				archive = new ZipFile(apkF);
				Enumeration<?> entries = archive.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) entries.nextElement();
					String entryName = entry.getName();
					// We are dealing with the Android manifest
					if (entryName.equals("AndroidManifest.xml")) {			
						found = true;
						handler.handleManifest(archive.getInputStream(entry));
						break;
					}
				}
			}
			finally {
				if (archive != null)
					archive.close();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(
					"Error when looking for manifest in apk: " + e);
		}
		if (!found)
			throw new RuntimeException("No manifest file found in apk");
	}
	
	public void loadManifestFile(String apk) {
		handleAndroidManifestFile(apk, new IManifestHandler() {
			public void handleManifest(InputStream stream) { 
				loadClassesFromBinaryManifest(stream);
			}
		});
	}
	
	protected void loadClassesFromBinaryManifest(InputStream manifestIS) {
		try {
			AXmlResourceParser parser = new AXmlResourceParser();
			parser.open(manifestIS);
			int type = -1;
			String activityName = null;
			while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
				switch (type) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						String tagName = parser.getName();
//						System.out.println("START_TAG:"+tagName);
						if ("manifest".equals(tagName)) {
							this.packageName = getAttributeValue(parser, "package");
							String versionCode = getAttributeValue(parser, "versionCode");
							if (versionCode != null && versionCode.length() > 0)
								this.versionCode = Integer.valueOf(versionCode);
							this.versionName = getAttributeValue(parser, "versionName");
						}
						else if("activity".equals(tagName)){
//							filters = new ArrayList<IntentFilter>();
							String name = getAttributeValue(parser, "name");
							activityName = expandClassName(name);
//							System.out.println("activity name:"+activityName+" start.");
							String flag = getAttributeValue(parser, "exported");
//							System.out.println(flag);
							if(flag==null||flag=="true"){
								ActivityExported=true;
//								System.out.println(activityName+" can be exported.");
							}
							else{
//								System.out.println(activityName+" can not be exported.");
							}
						}
						//handle intent-filter
						else if("intent-filter".equals(tagName)){
//							System.out.println("intent-filter begin.");
						}
						else if("action".equals(tagName)){
							String name = getAttributeValue(parser, "name");
							if("android.intent.action.VIEW".equals(name)){
								IntentActionView=true;
//								System.out.println("view action name.");
							}
						}
						else if("category".equals(tagName)){
							String name = getAttributeValue(parser, "name");
							if("android.intent.category.DEFAULT".equals(name)){
								IntentCategoryDefault=true;
//								System.out.println("default action category.");
							}
							else if("android.intent.category.BROWSABLE".equals(name)){
								IntentCategoryBrowsable=true;
//								System.out.println("browsable action category.");
							}
						}
						else if("data".equals(tagName)){								
							String scheme = getAttributeValue(parser, "scheme");
//							String host=getAttributeValue(parser, "host");
//							String dataType=getAttributeValue(parser, "");
							if("file".equals(scheme)){
								IntentDataSchemeFile=true;
//								System.out.println("data file:");
							}
							if(("http".equals(scheme))||("https".equals(scheme))){
								IntentDataSchemeHttp=true;
//								System.out.println("data http:");
							}
							if("http".equals(scheme)){
								Http=true;
								HttpInActivity=true;
							}
							if("https".equals(scheme)){
								Https=true;
								HttpsInActivity=true;
							}
							if("javascript".equals(scheme)){
								IntentDataSchemeJS=true;
//								System.out.println("data javascript:");
							}
						}
						
						else if (tagName.equals("uses-permission")) {
							String permissionName = getAttributeValue(parser, "name");
							// We probably don't want to do this in some cases, so leave it
							// to the user
							// permissionName = permissionName.substring(permissionName.lastIndexOf(".") + 1);
							this.permissions.add(permissionName);
						}
						
						else if (tagName.equals("uses-sdk")) {
//							System.out.println(parser.getText());
							String minVersion = getAttributeValue(parser, "minSdkVersion");
//							if(minVersion!=null){
//							   System.out.println("minVersion:"+minVersion.length());
//							}
							if (minVersion != null && minVersion.length() > 0)
								this.minSdkVersion = Integer.valueOf(minVersion);
							String targetVersion = getAttributeValue(parser, "targetSdkVersion");
//							if(targetVersion!=null){
//								   System.out.println("targetVersion:"+targetVersion.length());
//								}
							if (targetVersion != null && targetVersion.length() > 0)
								this.targetSdkVersion = Integer.valueOf(targetVersion);
						}
						break;
					case XmlPullParser.END_TAG:
						String endName = parser.getName();
						if("activity".equals(endName)){	//set ActivityExported false for next activity
							if(ActivityExported==true){
								ActivityExported=false;
							}
//							System.out.println("activity end.");
						}
						else if("intent-filter".equals(endName)){	
							if((IntentActionView==true)&&(IntentCategoryDefault==true||IntentCategoryBrowsable==true)){
								if((IntentDataSchemeFile==true)||(IntentDataSchemeJS==true)){
									FileExported=true;
									System.out.println("activity name:"+activityName);
									System.out.println("FileExported");
								}
								if((IntentDataSchemeHttp==true)||(IntentDataSchemeJS==true)){
									HttpExported=true;
									System.out.println("activity name:"+activityName);
									System.out.println("HttpExported");
								}
								if(!HttpInActivity&&HttpsInActivity){
									System.out.println("该Activity："+activityName+"只接受https://浏览请求");
								}
							}
							IntentActionView=false;
							IntentCategoryDefault=false;
							IntentCategoryBrowsable=false;
							IntentDataSchemeFile=false;
							IntentDataSchemeHttp=false;
							IntentDataSchemeJS=false;
//							System.out.println("intent-filter end.");
					  }
						HttpInActivity=false;
						HttpsInActivity=false;
						break;
					case XmlPullParser.TEXT:
						break;
				}
			}
			if(!Http&&Https){
				HttpsExported=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates a full class name from a short class name by appending the
	 * globally-defined package when necessary
	 * @param className The class name to expand
	 * @return The expanded class name for the given short name
	 */
	private String expandClassName(String className) {
		if (className.startsWith("."))
			return this.packageName + className;
		else if (className.substring(0, 1).equals(className.substring(0, 1).toUpperCase()))
			return this.packageName + "." + className;
		else
			return className;
	}

	private String getAttributeValue(AXmlResourceParser parser, String attributeName) {//get attribute value
		for (int i = 0; i < parser.getAttributeCount(); i++)
			if (parser.getAttributeName(i).equals(attributeName))
				return parser.getAttributeValue(i);
		return null;
	}

    public void setApplicationName(String name) {
        this.applicationName = name;
    }

    public void setPackageName(String name) {
        this.packageName = name;
    }

	public String getApplicationName() {
		return this.applicationName;
	}
	
	public Set<String> getPermissions() {
		return this.permissions;
	}
	
	public int getVersionCode() {
		return this.versionCode;
	}
	
	public String getVersionName() {
		return this.versionName;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public int getMinSdkVersion() {
		return this.minSdkVersion;
	}
	
	public int targetSdkVersion() {
		return this.targetSdkVersion;
	}
	

	public void getMainActivity(){
		
	}
	
}
