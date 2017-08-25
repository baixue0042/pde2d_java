package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;

import ij.io.OpenDialog;

public class RunModel {
	public static void run(String path, String str){
		Model m;
		String[] info = str.split(";");
		String name = info[0];
		double[] time = toDouble(info[1]), space = toDouble(info[2]), k_R = toDouble(info[3]), k_D = toDouble(info[4]), p = toDouble(info[5]);
		if (k_R.length==8) m = new Model1(); 
		else m = new Model2();
		m.initialize(name,time,space, k_R,k_D,p);
		long startTime = System.currentTimeMillis();
		try {
			// delete file if already existed
			File f = new File(path+m.name+".dat");
			if (f.exists()) f.delete();
			// open output stream
			FileOutputStream fout = new FileOutputStream(f,true);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			// write configuration parameters
			oout.writeObject(m.hss); 
			oout.writeObject(m.ht*m.group); oout.writeObject(m.K); 
			oout.writeObject(m.hs); oout.writeObject(m.I); oout.writeObject(m.J);
			// time step, write concentrations
			for (int k=0; k<m.K*m.group; k++){
				m.step(); //System.out.println(k);
				if (k%m.group==0) for (int s=0; s<m.n_chemical; s++) {oout.writeObject(m.data[s].getArrayCopy() ); oout.reset();}
			}
			oout.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
		System.out.println("---------------------  "+String.format("%.3g",(double) (stopTime - startTime)/60000)+"min  ---------------------");
	}
	public static double[] toDouble(String str){
		String[] strarr = str.split(",");
		double[] arr = new double[strarr.length];
		for (int i=0; i<strarr.length; i++) { arr[i] =  Double.parseDouble(strarr[i]); }
		return arr;
	}
	public static String[] readInput(String path) {
		OpenDialog.setDefaultDirectory(path);
		OpenDialog dilog = new OpenDialog("load parameters");
		if (dilog.getFileName()==null) System.exit(0);
		File f = new File(path+dilog.getFileName());
		String info = "";
		try {
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line; while ((line = b.readLine()) != null) info+=(line.split(":")[1]+";");
			b.close();
		} catch (IOException e) { e.printStackTrace(); }
		return info.split("SPLIT;");
	}
	public static void main(String[] args){
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		for (String str : readInput(path)) run(path,str);
		System.exit(0);
	}
}
