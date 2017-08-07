package rd2d;

import java.util.Arrays;

import Jama.Matrix;

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
		this.path.replaceFirst("^~", System.getProperty("user.home"));
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
		double[] k_D = {0.00033,0.033,0.00033};
		double[] k_R = {0.05,1,0.4, 0.5,0.56, 0.2,0.025};
		double[] c0 = {0, 2, 0};
		Model_rd2d m = new Model_rd2d();
		m.setReactionParameter(k_R,c0,10);
		m.setSpatialParameter(k_D,0.4);
		//System.out.println(Arrays.deepToString());
		}
		//Model_rd2d m = new Model_rd2d(5,k_D,k_R);
		//new Visualization(m.data,Arrays.toString(m.k_D)+";"+Arrays.toString(m.k_R));
		//m.writer("temp.txt");
		//*/
		/*
		Model_rd2d m = new Model_rd2d();
		m = m.reader("temp.txt");
		new Visualization(m.data,Arrays.toString(m.k_D)+";"+Arrays.toString(m.k_R));
		*/
	// rescale pixel value
	/*
	public double[][] data_rescale(double thresh){
		int T = this.data.length, n_chemical = this.data[0].length;
		double[][] data_min_max = new double[n_chemical][2];
		for (int s = 0; s<n_chemical; s++){
			data_min_max[s] = this.data[0][s].min_max(); 
			for (int t=0; t<T; t++){
				double[] temp = this.data[t][s].min_max();
				if (temp[0]>data_min_max[s][0]){
					data_min_max[s][0] = temp[0];
				}
				if (temp[1]<data_min_max[s][1]){
					data_min_max[s][1] = temp[1];
				}
			}
			System.out.print(Arrays.toString(data_min_max[s])+";");

			if (c0[s] - data_min_max[s][0]<thresh){
				data_min_max[s][0] = c0[s] - thresh;
			}
			if (data_min_max[s][1]-c0[s]<thresh){
				data_min_max[s][1] = c0[s] + thresh;
			}
			System.out.print(Arrays.toString(data_min_max[s])+"\n");
		}
		return data_min_max;
	}*/


}
