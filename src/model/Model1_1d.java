package model;

import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;

public class Model1_1d extends Integrate1d{
	public double[] p;
	public Model1_1d(String info){
		String[] S = info.split(";");
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		this.fullfilename = path+"1d_"+S[0]+".dat";
		this.T = Integer.parseInt(S[1]);//T: simulation time, unit seconds
		double[] space = Run.toDouble(S[2].split(",")); this.spanI= space[0]; this.hs = space[1];//unit micrometers
		this.k_R = Run.toDouble(S[3].split(","));// reaction parameters
		this.c0 = Run.toDouble(S[4].split(","));// homogenous steady state
		this.k_D = Run.toDouble(S[5].split(","));//k_D: diffusion coefficient, unit micrometers**2/sec
		this.p = Run.toDouble(S[6].split(","));// perturb: chemical, center/diameter in micrometers, amplitude

		this.n_chemical = this.c0.length;
		this.I=(int) (this.spanI/this.hs);
		double k_D_max = k_D[0]; for (int i=1; i<k_D.length; i++) if (k_D_max<this.k_D[i]) k_D_max=this.k_D[i];
		double temp = 0.5 * (hs * hs) / k_D_max;  // characteristic time step based on diffusion
		this.group = (int) Math.ceil(1.0 / temp); // number of steps for 1 second
		this.ht = 1.0 / this.group; // time step, unit seconds
		System.out.println("time"+"\t\t"+printd(this.T)+";\t\t"+printd(this.ht));
		System.out.println("space"+"\t\t"+this.spanI+";\t\t"+this.hs);
		System.out.println("k_R"+"\t\t"+Arrays.toString(this.k_R));
		System.out.println("c0"+"\t\t"+Arrays.toString(this.c0));
		System.out.println("k_D"+"\t\t"+Arrays.toString(this.k_D));
		System.out.println("p"+"\t\t"+Arrays.toString(this.p));
		File f = new File(this.fullfilename);
		if (f.exists()) f.delete();
		this.integrate();
		System.out.println("---------------------------------------");
	}
	String printd(double x){
		return String.format("%.2g",x);
	}
	public void addPerturb(){
		int chemical=(int) this.p[0], ci=(int) (this.p[1]*this.I), di=(int) (this.p[2]*this.I); double amp=this.p[3];
		for (int ii=0; ii<di; ii++) data_t[chemical].set(ci+ii,0,amp+data_t[chemical].get(ci+ii,0));
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
	public double[] f_ss(double x) {
		for (double i=Math.pow(10, -6); i<1; i+=0.001) {
			
		}
	}

}
