package model;

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
import templates.Integrate1d;
import visualization.Data2d;
import visualization.Data1d;

public class Model0 extends Integrate1d{
	public double[] p;
	public Model0(){}
	public void setParameters(String fullfilename, double[] k_R, double[] c0, double[] k_D, double[] p, int T, double[] space){
		this.fullfilename = fullfilename;
		this.k_R = k_R;// reaction parameters
		this.c0 = c0;// homogenous steady state
		this.T = T;//T: simulation time, unit seconds
		this.n_chemical = this.c0.length;
		
		this.k_D = k_D;//k_D: diffusion coefficient, unit micrometers**2/sec
		this.spanI= space[0]; this.hs = space[2];//unit micrometers
		this.I=(int) (this.spanI/this.hs);
		double k_D_max = k_D[0]; for (int i=1; i<k_D.length; i++) if (k_D_max<this.k_D[i]) k_D_max=this.k_D[i];
		double temp = 0.5 * (hs * hs) / k_D_max;  // characteristic time step based on diffusion
		this.group = (int) Math.ceil(1.0 / temp); // number of steps for 1 second
		this.ht = 1.0 / this.group; // time step, unit seconds
		
		this.p = p;// perturb: chemical, center/diameter in micrometers, amplitude
		
		System.out.println("time"+"\t\t"+printd(this.T)+";\t\t"+printd(this.group)+";\t\t"+printd(this.ht));
		System.out.println("space"+"\t\t"+this.spanI+";\t\t"+this.hs);
		System.out.println("k_R"+"\t\t"+Arrays.toString(this.k_R));
		System.out.println("k_D"+"\t\t"+Arrays.toString(this.k_D));
		System.out.println("c0"+"\t\t"+Arrays.toString(this.c0));
		System.out.println("p"+"\t\t"+Arrays.toString(this.p));
	}
	String printd(double x){
		return String.format("%.2g",x);
	}

	public void addPerturb(){
		int chemical=(int) this.p[0], ci=(int) (this.p[1]*this.I), di=(int) (this.p[2]*this.I); double amp=this.p[3];
		for (int ii=-di; ii<di; ii++) data_t[chemical].set(ci+ii,0,amp+data_t[chemical].get(ci+ii,0));
	}
	public static double hill(double x,double k0,double k1,double k2, int n){
		return k0+k1*Math.pow(x, n)/(Math.pow(x, n)+Math.pow(k2, n));
	}
	public double[] f_R(double[] u){
		double[] k = this.k_R;
		double[] dudt = new double[u.length];
		double A = u[0], I = u[1], F=u[2];
		dudt[0] = hill(A,k[0],k[1],k[2],3)*I - hill(F,k[3],k[4],1,1)*A;
		dudt[1] = -dudt[0];
		dudt[2] = k[5]*A - k[6]*F;
		return dudt;
	}

	public static void openData2d() {
		String f = openFile();
		if (!(f==null)) new Data2d(f,1);
	}
	public static void openData1d() {
		String f = openFile();
		if (!(f==null)) new Data1d(f,20);
	}
	public static String openFile() {
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		OpenDialog.setDefaultDirectory(path);
		OpenDialog dilog = new OpenDialog("open result");
		return path+dilog.getFileName();
	}
	public static double[] toDouble(String[] string){
		double[] arr = new double[string.length];
		for (int i=0; i<string.length; i++) { arr[i] =  Double.parseDouble(string[i]); }
		return arr;
	}
	public static void runSimulation(String[] info) {
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		double[] k_D,k_R,c0,p,space; int T; String filename;
		filename = info[0];
		k_R = toDouble(info[1].split(","));
		c0 = toDouble(info[2].split(","));
		k_D = toDouble(info[3].split(","));
		p = toDouble(info[4].split(","));
		T = Integer.parseInt(info[5]);
		space = toDouble("5, 5, 0.1".split(","));
		Model0 m = new Model0();
		m.setParameters(path+filename, k_R, c0, k_D, p, T, space);
		m.integrate();
		System.out.println("---------------------------------------");
	}
	public static void runManySimulations() {
		File f = new File(openFile());
		try {
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			while ((readLine = b.readLine()) != null) runSimulation(readLine.split(";"));
			} catch (IOException e) { e.printStackTrace();}
	}
	public static void main(String [] args){
		//new ImageJ();
		//runManySimulations();
		//runSimulation("0;0.05,1,0.4,0.4,0.6,0.2,0.025,1;0.0846,0.915,0.677;0.1,1,0.1;0,0.5,0.05,0.1;10".split(";"));
		openData1d();
	}
}
