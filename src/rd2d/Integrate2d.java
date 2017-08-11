package rd2d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;

import java.util.ArrayList;
import java.util.Arrays;

import Jama.Matrix;
import ij.ImageStack;

class Integrate2d{
	int T,n_chemical,I,J,group;
	double spanI,spanJ,hs,ht;
	double[] k_R, c0,k_D;
	ArrayList<double[]> perturb;
	String path,id;
	Grid[] data_t;
	public Integrate2d(){}

	public void integrate(boolean flag){
		/* if flag==True, run perturb-reaction-diffusion
		 * if flag==False, run perturb (visualize perturbation)
		 */
		// initial condition
		this.data_t = new Grid[this.n_chemical];
		for (int s=0; s<this.n_chemical; s++) this.data_t[s] = new Grid(this.I,this.J,this.c0[s]); // initialize with homogenous concentration
		
		// setup diffusion matrix
		Matrix[][] M = new Matrix[this.n_chemical][4];
		for (int s=0; s<this.n_chemical; s++) M[s] = diffuse_ADI_matrix(this.I,this.J,this.hs,this.ht,this.k_D[s]);
		try {
			// open output stream
			Path path = FileSystems.getDefault().getPath(this.path,this.id);
			Files.deleteIfExists(path);
			FileOutputStream fout = new FileOutputStream(new File(this.path+this.id),true);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			// write configuration parameters
			oout.writeObject(this.c0); oout.writeObject(ht); 
			oout.writeObject(this.I); oout.writeObject(this.J); oout.writeObject(this.group*this.T); 
			// time step
			for (int k=0; k<(this.group*this.T); k++){
				perturb(k);
				if (flag) react_diffuse(M);
				for (int s=0; s<this.n_chemical; s++) oout.writeObject(this.data_t[s].getArrayCopy());//write to file
			}
			oout.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	public void perturb(int k){
		for (double[] p : this.perturb) {
			double start = p[2]*this.group, end = p[3]*this.group;
			double amp = p[1]*(1.0-Math.abs(k-((start+end)/2))/((end-start)/2));
			int chemical = (int) p[0], ci = (int) (p[4]*this.I), cj = (int) (p[5]*this.J), di = (int) (p[6]*this.I), dj = (int) (p[7]*this.J);
			if (amp>0) for (int ii=-di; ii<di; ii++) {for (int jj=-dj; jj<dj; jj++) data_t[chemical].set(ci+ii,cj+jj,amp+data_t[chemical].get(ci+ii,cj+jj));}
		}
	}
	public void react_diffuse(Matrix[][] M){
		// react
		for (int i=0; i<I; i++){
			for (int j=0; j<J; j++){
				double[] cell = new double[this.n_chemical];
				for (int s=0; s<this.n_chemical; s++) cell[s] = this.data_t[s].get(i,j);
				cell = RK4(cell,this.ht);
				for (int s=0; s<this.n_chemical; s++) this.data_t[s].set(i,j,cell[s]); 
			}
		}
		// diffuse
		for (int s=0; s<this.n_chemical; s++) this.data_t[s] = diffuse_ADI(this.data_t[s], M[s]);
	}

	public static double hill(double x,double k0,double k1,double k2, int n){
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

	public double[] RK4(double[] v, double ht){
		// dimension of u: 1xn_chemical
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
	
}
