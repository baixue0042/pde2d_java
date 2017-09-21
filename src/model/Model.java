package model;

import java.util.Arrays;

import Jama.Matrix;

abstract public class Model implements ReactDiffuse, Perturb{
	double[] hss,k_R,k_D,p; double hs,ht,spanI,spanJ;
	int I,J,K,group,n_chemical;
	String name;
	Matrix[] data; Matrix[][] M;
	
	abstract public double[] f_R(double[] u);
	abstract public double fss(double x);
	abstract public void setHSS();
	
	public Model(){}
	public void initialize(String name, double[] time, double[] space, double[] k_R, double[] k_D, double[] perturb){
		this.name = name; this.k_R = k_R; this.k_D = k_D; this.p = perturb;
		hs = space[0]; spanI= space[1]; spanJ= space[2]; I=(int) (spanI/hs); J=(int) (spanJ/hs);
		double k_D_max = k_D[0]; for (int s=1; s<k_D.length; s++) if (k_D_max<k_D[s]) k_D_max=k_D[s];
		ht = 0.5 * (hs * hs) / k_D_max; group = (int) (time[0]/ht); K = (int) time[1]; 
		setHSS(); p[1] = p[1]*hss[0];
		printSetup();
		data = new Matrix[n_chemical];
		if (J==0){// 1d case
			for (int s=0; s<n_chemical; s++) data[s] = new Matrix(I,1,hss[s]);
			M = this.diffuse_ADI_matrix_1d(I, n_chemical, hs, ht,k_D);
			data = this.addPerturb_1d(data,p,I);
		} else {// 2d case
			for (int s=0; s<n_chemical; s++) data[s] = new Matrix(I,J,hss[s]);
			M = this.diffuse_ADI_matrix_2d(I, J, n_chemical, hs, ht,k_D);
			data = this.addPerturb_2d(data,p,I,J);
		}
	}
	public void step(){
		if (J==0){// 1d case
			data = this.react_diffuse_1d(I, n_chemical, ht, this, M, data);
		} else {// 2d case
			data = this.react_diffuse_2d(I, J, n_chemical, ht, this, M, data);
		}
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
	public static double hill(double x,double k0,double k1,double k2, int n){
		return k0+k1*Math.pow(x, n)/(Math.pow(x, n)+Math.pow(k2, n));
	}
	public void printSetup(){
		System.out.println("name"+"\t\t"+name);
		System.out.println("time"+"\t\t"+group*K+";\t\t"+String.format("%.4g",ht)+";\t\t"+String.format("%.1f",group*K*ht));
		System.out.println("space"+"\t\t"+hs+";\t\t"+spanI+";\t\t"+spanJ);
		System.out.println("k_R"+"\t\t"+Arrays.toString(k_R));
		System.out.println("k_D"+"\t\t"+Arrays.toString(k_D));
		System.out.println("HSS"+"\t\t"+Arrays.toString(hss));
		System.out.println("Perturb"+"\t\t"+Arrays.toString(p));
	}
}
