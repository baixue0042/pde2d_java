package rd2d;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Math;

import java.util.Arrays;

import Jama.Matrix;

public class Model_rd2d implements Serializable{
	double[] k_D, k_R, c0;
	Grid[][] data;
	public Model_rd2d(){}
	public Model_rd2d(double[] k_D, double[] k_R, double[] c0){
		this.k_D = k_D;
		this.k_R = k_R;
		this.c0 = c0;
	}
	public void run(int T){
		// chemical reactions
		int n_chemical = this.c0.length;
		// spatial domain
		double spanI= 10, spanJ = 5, hs = 0.1;//unit micrometers
		int I=(int) (spanI/hs),J = (int) (spanJ/hs);
		//k_D: diffusion coefficient, unit micrometers**2/sec
		double[] loc = {3,2,0.6,0.4}; // center and diameter of square perturbation
		// temporal domain
		//T: simulation time, unit seconds
		double temp = 0.5 * (hs * hs) / array_max(k_D);  // characteristic time step based on diffusion
		int groupt = (int) Math.ceil(1.0 / temp); // number of steps for 1 second
		double ht = 1.0 / groupt; // time step, unit seconds
		
		// initial condition
		this.data = new Grid[T+1][n_chemical]; // data dimension: time, chemicals
		for (int s=0; s<n_chemical; s++){
			this.data[0][s] = new Grid(I,J,this.c0[s]);
		}
		this.data[0][0].square_perturbation(0.1, loc, hs);
		Grid[] data_t = new Grid[n_chemical];
		Matrix[][] M = new Matrix[n_chemical][4];
		for (int s=0; s<n_chemical; s++){
			data_t[s] = this.data[0][s];
			M[s] = diffuse_ADI_matrix(I,J,hs,ht,k_D[s]);
		}
		// time step
		for (int k=1; k<(groupt*T+1); k++){
			// each time point start
			///*
			for (int i=0; i<I; i++){
				for (int j=0; j<J; j++){
					double[] cell = new double[n_chemical];
					for (int s=0; s<n_chemical; s++){
						cell[s] = data_t[s].get(i,j);
					}
					cell = RK4(cell,ht);
					for (int s=0; s<n_chemical; s++){
						data_t[s].set(i,j,cell[s]);
					}
				}
			}
			//*/
			for (int s=0; s<n_chemical; s++){
				data_t[s] = diffuse_ADI(data_t[s], M[s]);
			// each time point end
			}
			if (k%groupt==0){
				System.out.println(k);
				for (int s=0; s<n_chemical; s++){
					this.data[k/groupt][s] = data_t[s].copy();
				}
			}
		}
	}
	@Override
	public String toString() {
		String string = Arrays.toString(this.k_D)+";"+Arrays.toString(this.k_R);
		for (int t = 0; t<this.data.length; t++){
			for (int s = 0; s<this.data[t].length; s++){
				string += (this.data[t][s].toString()+";");
			}
		}
		return string;
	}

	public static void main(String [] args){
		///*
		double[] k_D = {0.05,0.01,0.001};
		double[] k_R = {0.95087765,  0.38947365,  0.86932719,  0.1       ,  0.        ,  0. };
		double[] c0 = {0.092118289828982902, 1, 0.092118289828982902};
		Model_rd2d m = new Model_rd2d(k_D,k_R,c0);
		m.run(5);
		new Visualization(m.data, m.data_rescale(0.1), Arrays.toString(m.k_D)+";"+Arrays.toString(m.k_R));
		//System.out.println(Arrays.deepToString());
		/*
		for (int t = 0; t<100; t++){
			c0 = m.RK4(c0, 0.01);
			System.out.println(Arrays.toString(c0));*/
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
			if (c0[s] - data_min_max[s][0]<thresh){
				data_min_max[s][0] = c0[s] - thresh;
			}
			if (data_min_max[s][1]-c0[s]<thresh){
				data_min_max[s][1] = c0[s] + thresh;
			}
			//data_min_max[s][0] -= 0.01;
			//data_min_max[s][1] += 0.01;
		}
		return data_min_max;
	}
	public Matrix f_R(Matrix u){
		// dimension of u: 1x3
		double[] k = this.k_R;
		double RT=u.get(0,0), RD=u.get(0,1), A=u.get(0,2);
		double dRTdt = (1 - k[0] + k[0] * RT * RT / (RT *RT + k[1] *k[1])) * RD - (1 + k[2] * A) * RT;
		double dRDdt = k[4] * (1 - RD) - k[5] * dRTdt;
		double dAdt = k[3] * (RT - A);
		Matrix dudt = new Matrix(u.getRowDimension(),u.getColumnDimension());
		dudt.set(0,0,dRTdt);
		dudt.set(0,1,dRDdt);
		dudt.set(0,2,dAdt);
		return dudt;
	}
	public double[] RK4(double[] v, double ht){
		// dimension of u: 1x3
		Matrix u = new Matrix(v,1);
		Matrix A = new Matrix(4,4); A.set(1,0,0.5); A.set(2,1,0.5); A.set(3,2,1);
		Matrix B = new Matrix(1,4); B.set(0,0,1.0/6); B.set(0,1,1.0/3); B.set(0,2,1.0/3); B.set(0,3,1.0/6); 
		double[] C = {0,0.5,0.5,1};
		Matrix K = new Matrix(4,u.getColumnDimension());
		int[] index = new int[1];
		for (int i=0; i<4; i++){
			index[0] = i;
			K.setMatrix(index, 0, u.getColumnDimension()-1, f_R(A.getMatrix(index, 0, 3).times(K).times(ht*C[i]).plus(u)));
		}
		return B.times(K).times(ht).plus(u).getRowPackedCopy();
	}
	public void writer(String fileName){
		try {
			FileOutputStream f = new FileOutputStream(new File(fileName));
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(this);
			//System.out.println(this.toString());
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} 
	}
	public Model_rd2d reader(String fileName){
		Model_rd2d m = new Model_rd2d();
		try {
			FileInputStream fi = new FileInputStream(new File(fileName));
			ObjectInputStream oi = new ObjectInputStream(fi);
			m = (Model_rd2d) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("End of file");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return m;
	}
	public Matrix[] diffuse_ADI_matrix(int I, int J, double hs, double ht, double k_D){
		double alpha = 2*hs*hs/(k_D*ht);
		Grid matII = new Grid(I,I), matJJ = new Grid(J,J), matMII = new Grid(I,I), matMJJ = new Grid(J,J);
		for (int i=0; i<I; i++){
			matII.grid_set(i,i,1);
			matMII.grid_set(i,i,-2);
			matMII.grid_set(i,i-1,1);
			matMII.grid_set(i,i+1,1);
		}
		for (int j=0; j<J; j++){
			matJJ.grid_set(j,j,1);
			matMJJ.grid_set(j,j,-2);
			matMJJ.grid_set(j,j-1,1);
			matMJJ.grid_set(j,j+1,1);
		}
		Matrix[] X = new Matrix[4];
		X[0] = matJJ.times(alpha).plus(matMJJ);
		X[1] = matII.times(alpha).minus(matMII);
		X[2] = matII.times(alpha).plus(matMII);
		X[3] = matJJ.times(alpha).minus(matMJJ).inverse();
		return X;
	}
	public Grid diffuse_ADI(Grid U0, Matrix[] X){
		int I = U0.getRowDimension(), J = U0.getColumnDimension();
		Matrix U1 = new Matrix(I,J), U2 = new Matrix(I,J), U = new Matrix(I,J);
		int[] index = new int[1];
		for (int i=0; i<I; i++){
			index[0] = i;
			U.setMatrix(index,0,J-1,U0.getMatrix(index,0,J-1).times(X[0]));
		}
		for (int j=0; j<J; j++){
			index[0] = j;
			U1.setMatrix(0,I-1,index,X[1].solve(U.getMatrix(0,I-1,index)));
		}
		for (int j=0; j<J; j++){
			index[0] = j;
			U.setMatrix(0,I-1,index,X[2].times(U1.getMatrix(0,I-1,index)));
		}
		for (int i=0; i<I; i++){
			index[0] = i;
			U2.setMatrix(index,0,J-1,U.getMatrix(index,0,J-1).times(X[3]));
		}
		return new Grid(U2);
	}
	double array_max(double[] arr){
		double vmax = arr[0];
		for (int i=1; i<arr.length; i++){
			if (vmax<arr[i]){
				vmax = arr[i];
			}
		}
		return vmax;
	}

}
