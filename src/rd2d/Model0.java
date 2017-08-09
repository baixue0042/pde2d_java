package rd2d;

import java.util.Arrays;

import Jama.Matrix;
import ij.ImageJ;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Model0 extends Integrate2d{
	public Model0(){}
	public void setParameters(String path, double[] k_R, double[] c0, int T, double[] k_D){
		this.path = path;
		this.k_R = k_R;// reaction parameters
		this.c0 = c0;// homogenous steady state
		this.T = T;//T: simulation time, unit seconds
		this.n_chemical = this.c0.length;
		
		this.k_D = k_D;//k_D: diffusion coefficient, unit micrometers**2/sec
		this.spanI= 1; this.spanJ = 1; this.hs = 0.1;//unit micrometers
		this.I=(int) (this.spanI/this.hs); this.J = (int) (this.spanJ/this.hs);
		double temp = 0.5 * (hs * hs) / array_max(k_D);  // characteristic time step based on diffusion
		this.group = (int) Math.ceil(1.0 / temp); // number of steps for 1 second
		this.ht = 1.0 / this.group; // time step, unit seconds
		
		this.perturb = new ArrayList<double[]>(); 
		System.out.println(Arrays.toString(this.k_D)+";"+Arrays.toString(this.k_R)+";"+Arrays.toString(this.c0)+this.I+";"+this.J+";"+this.hs+";"+this.ht+";"+temp);
	}
	public void addPerturb(double chemical,double amp,double start,double end,double cpi,double cpj,double cpdi,double cpdj){
		// each element of perturb: 0chemical,1amp,2start,3end,4cpi,5cpj,6cpdi,7cpdj
		double[] p = new double[8];
		p[0] = chemical; p[1] = amp; p[2] = start; p[3] = end; p[4] = cpi; p[5] = cpj; p[6] = cpdi; p[7] = cpdj;
		this.perturb.add(p);
	}
	@Override
	public Grid[] getPerturbValue(double[] p, Grid[] data_t, double t){
		double amp = p[1]/this.group, start = p[2], end = p[3], mid = (p[2]+p[3])/2, halflife = (p[3]-p[2])/2;
		int chemical = (int) p[0], ci = (int) (p[4]*this.I), cj = (int) (p[5]*this.J), di = (int) (p[6]*this.I), dj = (int) (p[7]*this.J);
		if ((t>start)&&(t<end)){
			double amp_scale = 1 - Math.abs(t-mid) / (halflife);
			data_t[chemical].square_perturbation(amp * amp_scale,ci,cj,di,dj);
		}
		return data_t;
	}
	@Override
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
	@Override
	String headInfo() {
		String string = Arrays.toString(this.k_D)+";"+Arrays.toString(this.k_R)+";"+Arrays.toString(this.c0);
		string += (this.spanI+";"+this.spanJ+";"+this.hs+";"+this.ht);
		return string;
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
	public static double hill(double x,double k0,double k1,double k2, int n){
		return k0+k1*Math.pow(x, n)/(Math.pow(x, n)+Math.pow(k2, n));
	}

	public static void main(String [] args){
		String path = "~/Documents/data_working/pde2d/1/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		///*
		try {
			FileInputStream fin = new FileInputStream(new File(path+"temp"));
			ObjectInputStream oin = new ObjectInputStream(fin);
			Object o; boolean typeflag = false;
			do {
				o = oin.readObject();
				if (o instanceof double[][]) {
					typeflag = true;
					System.out.println(Arrays.deepToString((double[][]) o));
				}
			} while (typeflag);
			
			oin.close();
			fin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			// ignore
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//*/
		/*
		double[] k_D,k_R,c0; int T;
		String[] info= "0.05,1,0.4,0.5,0.56,0.2,0.025,1;0.0735,0.926,0.588;0.00033,0.033,0.00033;1".split(";");
		k_R = toDouble(info[0].split(","));
		c0 = toDouble(info[1].split(","));
		k_D = toDouble(info[1].split(","));
		T = Integer.parseInt(info[3]);
		//new ImageJ();
		///*
		Model0 m = new Model0();
		m.setParameters(path, k_R, c0, T, k_D);
		double chemical=0,amp=0.1,start=0,end=1.5,cpi=0.5,cpj=0.5,cpdi=0.02,cpdj=0.02;
		m.addPerturb(chemical,amp,start,end,cpi,cpj,cpdi,cpdj);
		m.integrate(true);*/
		}
}
