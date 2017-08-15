package model;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ij.io.OpenDialog;
import visualization.Data1d;
import visualization.Data2d;

public class Run {
	public static double[] toDouble(String[] string){
		double[] arr = new double[string.length];
		for (int i=0; i<string.length; i++) { arr[i] =  Double.parseDouble(string[i]); }
		return arr;
	}
	public static String openFile() {
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		OpenDialog.setDefaultDirectory(path);
		OpenDialog dilog = new OpenDialog("open result");
		return path+dilog.getFileName();
	}
	public static String[] runManySimulations() {
		// open a file with run configuration for several simulations
		// returns a list of Strings. each element is the run configuration for a single simulation
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
		return info.split("\n");
	}
	public static void main(String[] args){
		String[] runs = runManySimulations();
		for (String r : runs) new Model1_1d(r);
		System.exit(0);
	}
}
