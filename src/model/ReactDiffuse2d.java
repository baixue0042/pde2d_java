package model;

import Jama.Matrix;

public interface ReactDiffuse2d {
	public default Matrix[] react_diffuse(int I, int J, int n_chemical, double ht, Model m, Matrix[][] M, Matrix[] data){
		// react
		for (int i=0; i<I; i++){
			for (int j=0; j<J; j++) {
				double[] temp,k1,k2,k3,k4;
				temp = new double[n_chemical];
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
		// diffuse
		for (int s=0; s<n_chemical; s++) {
			Matrix temp = new Matrix(I,J), U_half = new Matrix(I,J);
			int[] index = new int[1];
			for (int i=0; i<I; i++){
				index[0] = i;
				temp.setMatrix(index,0,J-1,data[s].getMatrix(index,0,J-1).times(M[s][1]));// fill right hand side of first equation
			}
			for (int j=0; j<J; j++){
				index[0] = j;
				U_half.setMatrix(0,I-1,index,M[s][0].times(temp.getMatrix(0,I-1,index)));// solve U_half
			}
			for (int j=0; j<J; j++){
				index[0] = j;
				temp.setMatrix(0,I-1,index,M[s][3].times(U_half.getMatrix(0,I-1,index)));// fill right hand side of second equation
			}
			for (int i=0; i<I; i++){
				index[0] = i;
				data[s].setMatrix(index,0,J-1,temp.getMatrix(index,0,J-1).times(M[s][2]));// solve U_next
			}
		}
		return data;
	}
	public default Matrix[][] diffuse_ADI_matrix(int I, int J, int n_chemical, double hs, double ht, double[] k_D){
		final Matrix[][] M = new Matrix[n_chemical][2];
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
