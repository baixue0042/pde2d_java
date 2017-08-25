package model;

import Jama.Matrix;

public interface ReactDiffuse {

	public default Matrix[] react_diffuse_1d(int I, int n_chemical, double ht, Model m, Matrix[][] M, Matrix[] data){
		// react
		double[] temp,k1,k2,k3,k4;
		temp = new double[n_chemical];
		for (int i=0; i<I; i++){
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
		temp=null; k1=null; k2=null; k3=null; k4=null;
		// diffuse
		for (int s=0; s<n_chemical; s++) 
			data[s] = M[s][0].solve(M[s][1].times(data[s]));
		return data;
	}
	public default Matrix[][] diffuse_ADI_matrix_1d(int I, int n_chemical, double hs, double ht, double[] k_D){
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
	public default Matrix[][] diffuse_ADI_matrix_1d_noflux(int I, int n_chemical, double hs, double ht, double[] k_D){
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
	public default Matrix[] react_diffuse_2d(int I, int J, int n_chemical, double ht, Model m, Matrix[][] M, Matrix[] data){
		// react
		double[] temp,k1,k2,k3,k4;
		temp = new double[n_chemical];
		for (int i=0; i<I; i++){
			for (int j=0; j<J; j++) {
				for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,j);
				k1 = m.f_R(temp);
				for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,j)+ht/2*k1[s];
				k2 = m.f_R(temp);
				for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,j)+ht/2*k2[s];
				k3 = m.f_R(temp);
				for (int s=0; s<n_chemical; s++) temp[s] = data[s].get(i,j)+ht*k3[s];
				k4 = m.f_R(temp);
				for (int s=0; s<n_chemical; s++) data[s].set(i,j,data[s].get(i,j)+ht/6*(k1[s]+2*k2[s]+2*k3[s]+k4[s]));
			}
		}
		temp=null; k1=null; k2=null; k3=null; k4=null;
		// diffuse
		Matrix U = new Matrix(I,J), U_half = new Matrix(I,J);
		for (int s=0; s<n_chemical; s++) {
			int[] index = new int[1];
			for (int i=0; i<I; i++){
				index[0] = i;
				U.setMatrix(index,0,J-1,data[s].getMatrix(index,0,J-1).times(M[s][1]));// fill right hand side of first equation
			}
			for (int j=0; j<J; j++){
				index[0] = j;
				U_half.setMatrix(0,I-1,index,M[s][0].times(U.getMatrix(0,I-1,index)));// solve U_half
			}
			for (int j=0; j<J; j++){
				index[0] = j;
				U.setMatrix(0,I-1,index,M[s][3].times(U_half.getMatrix(0,I-1,index)));// fill right hand side of second equation
			}
			for (int i=0; i<I; i++){
				index[0] = i;
				data[s].setMatrix(index,0,J-1,U.getMatrix(index,0,J-1).times(M[s][2]));// solve U_next
			}
		}
		U = null; U_half = null;
		return data;
	}
	public default Matrix[][] diffuse_ADI_matrix_2d(int I, int J, int n_chemical, double hs, double ht, double[] k_D){
		final Matrix[][] M = new Matrix[n_chemical][4];
		for (int s=0; s<n_chemical; s++){
			double alpha = 2*hs*hs/(k_D[s]*ht);
			M[s][0] = new Matrix(I,I); M[s][1] = new Matrix(I,I); M[s][2] = new Matrix(I,I); M[s][3] = new Matrix(I,I);
			for (int i=0; i<I; i++){
				M[s][0].set(i,i,alpha+2);
				M[s][0].set(i,periodicIndex(i-1,I),-1);
				M[s][0].set(i,periodicIndex(i+1,I),-1);
				M[s][3].set(i,i,alpha-2);
				M[s][3].set(i,periodicIndex(i-1,I),1);
				M[s][3].set(i,periodicIndex(i+1,I),1);
			}
			for (int j=0; j<J; j++){
				M[s][1].set(j,j,alpha-2);
				M[s][1].set(j,periodicIndex(j-1,J),1);
				M[s][1].set(j,periodicIndex(j+1,J),1);
				M[s][2].set(j,j,alpha+2);
				M[s][2].set(j,periodicIndex(j-1,J),-1);
				M[s][2].set(j,periodicIndex(j+1,J),-1);
			}
			M[s][0] = M[s][0].inverse();
			M[s][2] = M[s][2].inverse();
		}
		return M;
	}
	public static int periodicIndex(int i, int I){
		if (i<0) i += I; 
		else if (i>(I-1)) i -= I;
		return i;
	}

}
