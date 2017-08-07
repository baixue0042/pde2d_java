package rd2d;

import java.io.Serializable;

import java.util.Arrays;

import Jama.Matrix;

public class Model_rd2d extends Model0 implements Serializable{
	double[] k_D;
	double spanI,spanJ,hs,ht;
	int I,J,group;
	Grid[][] data;
	public Model_rd2d(){super();}
	
	public void setSpatialParameter(double[] k_D){
		this.k_D = k_D;//k_D: diffusion coefficient, unit micrometers**2/sec
		this.spanI= 20; this.spanJ = 10; this.hs = 0.1;//unit micrometers
		this.I=(int) (this.spanI/this.hs); this.J = (int) (this.spanJ/this.hs);
		double temp = 0.5 * (hs * hs) / array_max(k_D);  // characteristic time step based on diffusion
		this.group = (int) Math.ceil(1.0 / temp); // number of steps for 1 second
		this.ht = 1.0 / this.group; // time step, unit seconds
	}
	
	public void run(double p_amp, boolean flag){
		/* if flag==True, run perturb-reaction-diffusion
		 * if flag==False, run perturb (visualize perturbation)
		 */
		// initial condition
		this.data = new Grid[this.group*this.T][n_chemical]; // data dimension: time, chemicals
		Grid[] data_t = new Grid[n_chemical];
		for (int s=0; s<n_chemical; s++){// initialize with homogenous concentration
			data_t[s] = new Grid(I,J,this.c0[s]);
		}
		// setup perturbation
		double p_start = 0.1, p_end = 6;
		double[] p_loc = new double[4];
		p_loc[0] = this.spanI*0.5; p_loc[1] = this.spanJ*0.5; p_loc[2] = this.spanI*0.08; p_loc[3] = this.spanI*0.04; // centerx, centery, widthx, widthy
		Perturbation perturb = new Perturbation(0, p_amp/this.group, p_start, p_end, p_loc, this.hs);//int chemical, double amp, double t_start, double t_end, double[] loc
		
		// setup diffusion matrix
		Matrix[][] M = new Matrix[n_chemical][4];
		for (int s=0; s<n_chemical; s++){
			M[s] = diffuse_ADI_matrix(I,J,this.hs,this.ht,k_D[s]);
		}
		
		// time step
		for (int k=0; k<(this.group*this.T); k++){
			data_t = perturb.getValue(data_t,((double)k)/this.group);
			if (flag){
				data_t = react_diffuse(data_t,M);
			}
			for (int s=0; s<n_chemical; s++){
				this.data[k][s] = data_t[s].copy();
			}

			/*
			// whether to save current result
			if (k%this.group==0){
				System.out.println(k);
				for (int s=0; s<n_chemical; s++){
					this.data[k/this.group][s] = data_t[s].copy();
				}
			}*/
		}// end time step		
	}
	public Grid[] react_diffuse(Grid[] data_t, Matrix[][] M){
		// react
		for (int i=0; i<I; i++){
			for (int j=0; j<J; j++){
				double[] cell = new double[n_chemical];
				for (int s=0; s<n_chemical; s++){
					cell[s] = data_t[s].get(i,j);
				}
				cell = RK4(cell,this.ht);
				for (int s=0; s<n_chemical; s++){
					data_t[s].set(i,j,cell[s]);
				}
			}
		}
		// diffuse
		for (int s=0; s<n_chemical; s++){
			data_t[s] = diffuse_ADI(data_t[s], M[s]);
		}
		return data_t;
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
	public String getInfo(){
		return Arrays.toString(this.k_D)+";"+Arrays.toString(this.k_R)+";"+Arrays.toString(this.c0);
	}
	public String getInfoChemical(int s){
		return Arrays.toString(this.k_R)+";"+this.k_D[s]+";"+this.c0[s];
	}
	@Override
	public String toString() {
		String string = Arrays.toString(this.k_D)+";"+Arrays.toString(this.k_R)+";"+Arrays.toString(this.c0);
		for (int t = 0; t<this.data.length; t++){
			for (int s = 0; s<this.data[t].length; s++){
				string += (this.data[t][s].toString()+";");
			}
		}
		return string;
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
