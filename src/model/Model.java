package model;

import java.io.File;
import java.util.Arrays;

abstract public class Model {
	double[] hss,k_R,k_D,p; double hs,ht,spanI,spanJ,T;
	int I,J,K,n_chemical;
	String name;
	abstract public double[] f_R(double[] u);
	abstract public double fss(double x);
	abstract public void setHSS();
	
	public void setParameter(String str){
		String[] info = str.split(";");
		name = info[0];
		this.k_R = toDouble(info[3].split(","));// reaction parameters
		
		double[] space = toDouble(info[2].split(",")); hs = space[0]; spanI= space[1]; spanJ= space[2];//unit micrometers
		I=(int) (spanI/hs); J=(int) (spanJ/hs);
		
		k_D = toDouble(info[4].split(","));//diffusion coefficient, unit micrometers**2/sec
		double k_D_max = k_D[0]; for (int s=1; s<k_D.length; s++) if (k_D_max<k_D[s]) k_D_max=k_D[s];
		
		ht = 0.5 * (hs * hs) / k_D_max; // characteristic time step based on diffusion, unit seconds
		T = Double.parseDouble(info[1]);// simulation time, unit seconds
		K = (int) (T/ht);
		
		p = toDouble(info[5].split(","));// perturb: chemical, center/diameter in micrometers, amplitude
		
		setHSS();
		
		System.out.println("time"+"\t\t"+T+";\t\t"+String.format("%.2g",ht));
		System.out.println("space"+"\t\t"+hs+";\t\t"+spanI+";\t\t"+spanJ);
		System.out.println("k_R"+"\t\t"+Arrays.toString(k_R));
		System.out.println("k_D"+"\t\t"+Arrays.toString(k_D));
		System.out.println("HSS"+"\t\t"+Arrays.toString(hss));
		System.out.println("Perturb"+"\t\t"+Arrays.toString(p));
	}
	public double fixpoint(){
		double fp = 0;
		double left = Math.pow(10,-6), right = 1-left, tol = Math.pow(10,-4), stepsize = (right-left)/50;
		double f_left = fss(left), f_right;
		for (double x=left+stepsize; x<right; x+=stepsize) { 
			f_right=fss(x); 
			if (f_left*f_right<0) {fp = RecursiveBisection(x-stepsize, x, tol); break;}
			f_left = f_right;
			}
		return fp;
	}
	public double RecursiveBisection(final double left, final double right, final double tolerance) {
		if ( Math.abs(right - left) < tolerance ) return (left + right) / 2;// base case
		else { // recursive case
			double mid = (left + right)/2;
			if ( fss(left) * fss(mid) > 0 ) return RecursiveBisection (mid, right, tolerance); // on same side
			else return RecursiveBisection(left, mid, tolerance);// opposite side
		}
	}
	public static double[] toDouble(String[] string){
		double[] arr = new double[string.length];
		for (int i=0; i<string.length; i++) { arr[i] =  Double.parseDouble(string[i]); }
		return arr;
	}
	public static double hill(double x,double k0,double k1,double k2, int n){
		return k0+k1*Math.pow(x, n)/(Math.pow(x, n)+Math.pow(k2, n));
	}

}
