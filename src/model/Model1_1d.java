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
		this.c0 = findHSS(this.k_R);// homogenous steady state
		this.k_D = Run.toDouble(S[4].split(","));//k_D: diffusion coefficient, unit micrometers**2/sec
		this.p = Run.toDouble(S[5].split(","));// perturb: chemical, center/diameter in micrometers, amplitude
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
	public double[] findHSS(double[] k){
		double[] hss = new double[3];
		double left = Math.pow(10,-6), right = 1-left, tol = Math.pow(10,-4), stepsize = (right-left)/50;
		double f_old = fss(left,k), f;
		for (double x=left+stepsize; x<right; x+=stepsize) { 
			f=fss(x,k); 
			if (f_old*f<0) {hss[0] = RecursiveBisection(k, x-stepsize, x, tol); hss[1] = k[7]-hss[0]; hss[2] = k[5]/k[6]*hss[0]; break;}
			f_old = f;
			}
		return hss;
	}
	public static double RecursiveBisection(double[] k, final double left, final double right, final double tolerance) {
		double x = 0, dx = 0;
		if ( Math.abs(right - left) < tolerance ) return (left + right) / 2;// base case
		else { // recursive case
			x = (left + right)/2; dx = right - left;
			if ( fss(left,k) * fss(x,k) > 0 ) return RecursiveBisection (k, x, right, tolerance); // on same side
			else return RecursiveBisection(k, left, x, tolerance);// opposite side
		}
	}	public static double fss(double x, double[] k){
		return hill(x,k[0],k[1],k[2],3)*(k[7]-x) - hill(k[5]/k[6]*x,k[3],k[4],1,1)*x;
	}

}
