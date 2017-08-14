package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ij.io.OpenDialog;
import visualization.Data1d;
import visualization.Data2d;

public class Setup {
	public static void openData2d() {
		String f = openFile();
		if (!(f==null)) new Data2d(f,1);
	}
	public static void openData1d() {
		String f = openFile();
		if (!(f==null)) new Data1d(f,20);
	}

	public static double[] toDouble(String[] string){
		double[] arr = new double[string.length];
		for (int i=0; i<string.length; i++) { arr[i] =  Double.parseDouble(string[i]); }
		return arr;
	}
	public static void runManySimulations() {
		File f = new File(openFile());
		String info = "";
		try {
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line;
			while ((line = b.readLine()) != null) {
				if (!line.equals("***")) info += (line.split(":")[1]+";");
				else info += "\n";
			}
			b.close();
			} catch (IOException e) { e.printStackTrace();}
		System.out.println(info);
		for (String sim : info.split("\n")) new Model0(sim);
		System.exit(0);
	}
	public static String openFile() {
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		OpenDialog.setDefaultDirectory(path);
		OpenDialog dilog = new OpenDialog("open result");
		return path+dilog.getFileName();
	}

}
