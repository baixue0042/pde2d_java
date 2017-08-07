package rd2d;

import java.util.Arrays;

import Jama.Matrix;
import ij.ImageJ;

public class Model0{
	double[] k_R, c0;
	int T,n_chemical;
	String path;

	public Model0(){}
	public void setReactionParameter(double[] k_R, double[] c0, int T){
		this.k_R = k_R;
		this.c0 = c0;
		this.T = T;//T: simulation time, unit seconds
		this.n_chemical = this.c0.length;
		this.path = "~/Documents/data_working/pde2d/";
		this.path = this.path.replaceFirst("^~", System.getProperty("user.home"));
	}
	public double hill(double x,double k0,double k1,double k2, int n){
		return k0+k1*Math.pow(x, n)/(Math.pow(x, n)+Math.pow(k2, n));
	}
	public Matrix f_R(Matrix u){
		// dimension of u: 1x3
		double[] k = this.k_R;
		double A=u.get(0,0), I=u.get(0,1), F=u.get(0,2);
		double dAdt = hill(A,k[0],k[1],k[2],3)*I - hill(F,k[3],k[4],1,1)*A;
		double dIdt = -dAdt;
		double dFdt = k[5]*A - k[6]*F;
		u.set(0,0,dAdt); u.set(0,1,dIdt); u.set(0,2,dFdt);
		return u;
	}
	public static void main(String [] args){
		new ImageJ();
		/*
		double[] k_D = {0.00033,0.033,0.00033};
		double[] k_R = {0.05,1,0.4, 0.5,0.56, 0.2,0.025};
		double[] c0 = {0, 2, 0};
		int T = 2;
		Model_rd2d m = new Model_rd2d();
		m.setReactionParameter(k_R,c0,T);
		m.setSpatialParameter(k_D);
		m.run(0.4,false);
		(new ReadWrite()).writer(m.path+"temp", m);
		*/
		Model_rd2d m = (new ReadWrite()).reader("/Users/baixueyao/Documents/data_working/pde2d/temp");		
		new SyncWindow(m,m.hs,m.ht);
		//
		//System.out.println(Arrays.deepToString());
		}
}
