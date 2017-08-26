package tool.Analy.FileSize;

import java.io.File;
import java.io.IOException;

import android.R.integer;


public class FileEdit {

	double size = 0.0;

	/**
	 * �����ļ������ļ��еĴ�С ����λ MB
	 * @param file Ҫ������ļ������ļ��� �� ���ͣ�java.io.File
	 * @return ��С����λ��MB
	 */
	public double getSize(File file) {
		//�ж��ļ��Ƿ����
		if (file.exists()) {
			//�����Ŀ¼��ݹ���������ݵ��ܴ�С��������ļ���ֱ�ӷ������С
			if (!file.isFile()) {
				//��ȡ�ļ���С
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
			System.out.println("�ļ������ļ��в����ڣ�����·���Ƿ���ȷ��");
			return 0.0;
		}
	}

	public static void main(String[] args) throws IOException {
		FileEdit fd = new FileEdit();
		double all = fd.getSize(new File("E://AndroidJS��չ/AndroidJavaScript/�ж������¿�ʼ/DataSet/OtherApks/NationalApks"));
		System.out.println("ALL:  " + all + "KB");

	}
}

