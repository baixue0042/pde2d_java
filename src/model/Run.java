package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;

import ij.io.OpenDialog;

public class Run {
	public static void run(String str, String path){
		try {
			// create model
			Model1_RD1d m = new Model1_RD1d(str);
			// delete file if already existed
			File f = new File(path+m.name+".dat");
			if (f.exists()) f.delete();
			// open output stream
			FileOutputStream fout = new FileOutputStream(f,true);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			// write configuration parameters
			oout.writeObject(m.hss); 
			oout.writeObject(m.ht); oout.writeObject(m.K); 
			oout.writeObject(m.hs); oout.writeObject(m.I); 
			// time step, write concentrations
			for (int k=0; k<m.K; k++){
				m.step();
				for (int s=0; s<m.n_chemical; s++) oout.writeObject(m.data[s].getRowPackedCopy() );//write to file
			}
			oout.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("---------------------------------------");
	}
	public static String openFile(String path) {
		OpenDialog.setDefaultDirectory(path);
		OpenDialog dilog = new OpenDialog("load parameters");
		return path+dilog.getFileName();
	}
	public static String[] readParameters(String path) {
		// open a file with run configuration for several simulations
		// returns a list of Strings. each element is the run configuration for a single simulation
		File f = new File(openFile(path));
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
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		String[] runs = readParameters(path);
		for (String r : runs) run1d(r,path);
		System.exit(0);
	}
}
