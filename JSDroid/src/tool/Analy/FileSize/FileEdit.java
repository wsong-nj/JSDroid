package tool.Analy.FileSize;

import java.io.File;
import java.io.IOException;

import android.R.integer;


public class FileEdit {

	double size = 0.0;

	/**
	 * 计算文件或者文件夹的大小 ，单位 MB
	 * @param file 要计算的文件或者文件夹 ， 类型：java.io.File
	 * @return 大小，单位：MB
	 */
	public double getSize(File file) {
		//判断文件是否存在
		if (file.exists()) {
			//如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小
			if (!file.isFile()) {
				//获取文件大小
				File[] fl = file.listFiles();
				double ss = 0;
				for (File f : fl)
					ss += getSize(f);
				return ss;
			} else {
				double ss = (double) file.length() / 1024 ;
				System.out.println(file.getName() + " : " + ss + "KB");
				return ss;
			}
		} else {
			System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
			return 0.0;
		}
	}

	public static void main(String[] args) throws IOException {
		FileEdit fd = new FileEdit();
		double all = fd.getSize(new File("E://AndroidJS进展/AndroidJavaScript/研二下重新开始/DataSet/OtherApks/NationalApks"));
		System.out.println("ALL:  " + all + "KB");

	}
}

