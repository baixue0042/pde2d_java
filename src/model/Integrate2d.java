package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import Jama.Matrix;

abstract public class Integrate2d{
	public int T,n_chemical,I,J,group;
	public double spanI,spanJ,hs,ht;
	public double[] k_R, c0,k_D;
	public String fullfilename;
	public Matrix[] data_t;
	public Matrix[][] M;

	abstract public void addPerturb();
	abstract public double[] f_R(double[] u);
	
	public void integrate(){
		this.data_t = new Matrix[this.n_chemical];
		for (int s=0; s<this.n_chemical; s++) this.data_t[s] = new Matrix(this.I,this.J,this.c0[s]); // initialize with homogenous concentration
		addPerturb();// add perturbation
		// setup diffusion matrix
		this.M = new Matrix[this.n_chemical][4];
		for (int s=0; s<this.n_chemical; s++) diffuse_ADI_matrix(s);
		try {
			// open output stream
			FileOutputStream fout = new FileOutputStream(new File(this.fullfilename),true);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			// write configuration parameters
			oout.writeObject(this.c0); oout.writeObject(ht); oout.writeObject(hs); 
			oout.writeObject(this.I); oout.writeObject(this.J); oout.writeObject(this.group*this.T); 
			// time step
			for (int k=0; k<(this.group*this.T); k++){
				react_diffuse();
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
	public void react_diffuse(){
		// react
		for (int i=0; i<this.I; i++){
			for (int j=0; j<this.J; j++){
				double[] temp,k1,k2,k3,k4;
				temp = new double[this.n_chemical];
				for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,j);
				k1 = f_R(temp);
				for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,j)+this.ht/2*k1[s];
				k2 = f_R(temp);
				for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,j)+this.ht/2*k2[s];
				k3 = f_R(temp);
				for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,j)+this.ht*k3[s];
				k4 = f_R(temp);
				for (int s=0; s<this.n_chemical; s++) this.data_t[s].set(i,j,this.data_t[s].get(i,j)+this.ht/6*(k1[s]+2*k2[s]+2*k3[s]+k4[s]));
			}
		}
		// diffuse
		for (int s=0; s<this.n_chemical; s++) {
			Matrix temp = new Matrix(this.I,this.J), U_half = new Matrix(this.I,this.J);
			int[] index = new int[1];
			for (int i=0; i<I; i++){
				index[0] = i;
				temp.setMatrix(index,0,J-1,this.data_t[s].getMatrix(index,0,J-1).times(this.M[s][1]));// fill right hand side of first equation
			}
			for (int j=0; j<J; j++){
				index[0] = j;
				U_half.setMatrix(0,I-1,index,this.M[s][0].times(temp.getMatrix(0,I-1,index)));// solve U_half
			}
			for (int j=0; j<J; j++){
				index[0] = j;
				temp.setMatrix(0,I-1,index,this.M[s][3].times(U_half.getMatrix(0,I-1,index)));// fill right hand side of second equation
			}
			for (int i=0; i<I; i++){
				index[0] = i;
				this.data_t[s].setMatrix(index,0,J-1,temp.getMatrix(index,0,J-1).times(this.M[s][2]));// solve U_next
			}
		}
	}

	public void diffuse_ADI_matrix(int s){
		double alpha = 2*this.hs*this.hs/(this.k_D[s]*this.ht);
		this.M[s][0] = new Matrix(this.I,this.I);this.M[s][1] = new Matrix(this.I,this.I);this.M[s][2] = new Matrix(this.I,this.I);this.M[s][3] = new Matrix(this.I,this.I);
		for (int i=0; i<this.I; i++){
			this.M[s][0].set(i,i,alpha+2);
			this.M[s][0].set(i,periodicIndex(i-1,this.I),-1);
			this.M[s][0].set(i,periodicIndex(i+1,this.I),-1);
			this.M[s][3].set(i,i,alpha-2);
			this.M[s][3].set(i,periodicIndex(i-1,this.I),1);
			this.M[s][3].set(i,periodicIndex(i+1,this.I),1);
		}
		for (int j=0; j<this.J; j++){
			this.M[s][1].set(j,j,alpha+2);
			this.M[s][1].set(j,periodicIndex(j-1,this.J),-1);
			this.M[s][1].set(j,periodicIndex(j+1,this.J),-1);
			this.M[s][1].set(j,j,alpha-2);
			this.M[s][1].set(j,periodicIndex(j-1,this.J),1);
			this.M[s][1].set(j,periodicIndex(j+1,this.J),1);
		}
		this.M[s][0] = this.M[s][0].inverse();
		this.M[s][2] = this.M[s][2].inverse();
	}
	public static int periodicIndex(int i, int I){
		if (i<0) i += I; 
		else if (i>(I-1)) i -= I;
		return i;
	}
}
