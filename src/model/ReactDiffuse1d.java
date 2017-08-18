package model;

import Jama.Matrix;

public interface ReactDiffuse1d {
	public void initialize();
	public void step();

	public static Matrix[] react_diffuse(int I, int n_chemical, double ht, Model m, Matrix[][] M, Matrix[] data){
		// react
		for (int i=0; i<I; i++){
			double[] temp,k1,k2,k3,k4;
			temp = new double[n_chemical];
			for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,0);
			k1 = m.f_R(temp);
			for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,0)+ht/2*k1[s];
			k2 = m.f_R(temp);
			for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,0)+ht/2*k2[s];
			k3 = m.f_R(temp);
			for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,0)+ht*k3[s];
			k4 = m.f_R(temp);
			for (int s=0; s<n_chemical; s++) data[s].set(i,0,data[s].get(i,0)+ht/6*(k1[s]+2*k2[s]+2*k3[s]+k4[s]));
		}
		// diffuse
		for (int s=0; s<n_chemical; s++) data[s] = M[s][0].solve(M[s][1].times(data[s]));
		return data;
	}
	public static Matrix[][] diffuse_ADI_matrix(int I, int n_chemical, double hs, double ht, double[] k_D){
		final Matrix[][] M = new Matrix[n_chemical][2];
		for (int s=0; s<n_chemical; s++){
			double alpha = 2*hs*hs/(k_D[s]*ht);
			M[s][0] = new Matrix(I,I); M[s][1] = new Matrix(I,I);
			for (int i=0; i<I; i++){
				M[s][0].set(i,i,alpha+2);
				M[s][0].set(i,periodicIndex(i-1,I),-1);
				M[s][0].set(i,periodicIndex(i+1,I),-1);
				M[s][1].set(i,i,alpha-2);
				M[s][1].set(i,periodicIndex(i-1,I),1);
				M[s][1].set(i,periodicIndex(i+1,I),1);
			}
		}
		return M;
	}
	public static Matrix[][] diffuse_ADI_matrix_noflux(int I, int n_chemical, double hs, double ht, double[] k_D){
		final Matrix[][] M = new Matrix[n_chemical][2];
		for (int s=0; s<n_chemical; s++){
			double alpha = 2*hs*hs/(k_D[s]*ht);
			M[s][0] = new Matrix(I,I); M[s][1] = new Matrix(I,I);
			for (int i=1; i<I-1; i++){
				M[s][0].set(i,i,alpha+2);
				M[s][0].set(i,i-1,-1);
				M[s][0].set(i,i+1,-1);
				M[s][1].set(i,i,alpha-2);
				M[s][1].set(i,i-1,1);
				M[s][1].set(i,i+1,1);
			}
			M[s][0].set(0,0,alpha+2);
			M[s][0].set(0,1,-2);
			M[s][0].set(I-1,I-1,alpha+2);
			M[s][0].set(I-1,I-2,-2);
			M[s][1].set(0,0,alpha-2);
			M[s][1].set(0,1,2);
			M[s][1].set(I-1,I-1,alpha-2);
			M[s][1].set(I-1,I-2,2);
		}
		return M;
	}
	public static int periodicIndex(int i, int I){
		if (i<0) i += I; 
		else if (i>(I-1)) i -= I;
		return i;
	}

}
