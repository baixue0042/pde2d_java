package rd2d;

import java.util.Arrays;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Jama.Matrix;
import ij.ImageJ;

import ij.io.DirectoryChooser;
import ij.io.OpenDialog;

public class Model0 extends Integrate2d{
	public Model0(){}
	public void setParameters(String fullfilename, double[] k_R, double[] c0, int T, double[] k_D, double spanI, double spanJ, double hs){
		this.fullfilename = fullfilename;
		this.k_R = k_R;// reaction parameters
		this.c0 = c0;// homogenous steady state
		this.T = T;//T: simulation time, unit seconds
		this.n_chemical = this.c0.length;
		
		this.k_D = k_D;//k_D: diffusion coefficient, unit micrometers**2/sec
		this.spanI= spanI; this.spanJ = spanJ; this.hs = hs;//unit micrometers
		this.I=(int) (this.spanI/this.hs); this.J = (int) (this.spanJ/this.hs);
		double temp = 0.5 * (hs * hs) / array_max(k_D);  // characteristic time step based on diffusion
		this.group = (int) Math.ceil(1.0 / temp); // number of steps for 1 second
		this.ht = 1.0 / this.group; // time step, unit seconds
		
		this.perturb = new ArrayList<double[]>(); 
		setupInfo();
	}
	public void addPerturb(double chemical,double amp,double start,double end,double cpi,double cpj,double cpdi,double cpdj){
		// each element of perturb: 0chemical,1amp,2start,3end,4cpi,5cpj,6cpdi,7cpdj
		double[] p = new double[8];
		p[0] = chemical; p[1] = amp; p[2] = start; p[3] = end; p[4] = cpi; p[5] = cpj; p[6] = cpdi; p[7] = cpdj;
		this.perturb.add(p);
	}
	void setupInfo() {
		System.out.println("time"+"\t\t"+printd(this.T)+";\t\t"+printd(this.group)+";\t\t"+printd(this.ht));
		System.out.println("space"+"\t\t"+this.spanI+";\t\t"+this.spanJ+";\t\t"+this.hs);
		System.out.println("k_R"+"\t\t"+Arrays.toString(this.k_R));
		System.out.println("k_D"+"\t\t"+Arrays.toString(this.k_D));
		System.out.println("c0"+"\t\t"+Arrays.toString(this.c0));
	}

	public static double[] toDouble(String[] string){
		double[] arr = new double[string.length];
		for (int i=0; i<string.length; i++) { arr[i] =  Double.parseDouble(string[i]); }
		return arr;
	}
	public static double array_max(double[] arr){
		double vmax = arr[0];
		for (int i=1; i<arr.length; i++){
			if (vmax<arr[i]){
				vmax = arr[i];
			}
		}
		return vmax;
	}
	String printd(double x){
		return String.format("%.2g",x);
	}
	public static void openResult() {
		String f = openFile();
		if (!(f==null)) new SyncWindow(f,1);
	}
	public static String openFile() {
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		OpenDialog.setDefaultDirectory(path);
		OpenDialog dilog = new OpenDialog("open result");
		return path+dilog.getFileName();
	}
	public static void runSimulation(String[] info) {
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		double[] k_D,k_R,c0,p; int T; String filename;
		filename = info[0];
		k_R = toDouble(info[1].split(","));
		c0 = toDouble(info[2].split(","));
		k_D = toDouble(info[3].split(","));
		p = toDouble(info[4].split(","));
		T = Integer.parseInt(info[5]);
		
		Model0 m = new Model0();
		m.setParameters(path+filename, k_R, c0, T, k_D, 5, 5, 0.1);
		m.perturb.add(p);
		m.integrate(true);
		System.out.println("---------------------------------------");
	}
	public static void runManySimulations() {
		File f = new File(openFile());
		try {
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
            while ((readLine = b.readLine()) != null) {
                runSimulation(readLine.split(";"));
            }
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
	public static void main(String [] args){
		//new ImageJ();
		//runManySimulations();
		openResult();
	}
}
