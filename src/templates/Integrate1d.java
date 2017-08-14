package templates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import Jama.Matrix;

abstract public class Integrate1d {
	public int T,n_chemical,I,group;
	public double spanI,hs,ht;
	public double[] k_R, c0,k_D;
	public String fullfilename;
	public Matrix[] data_t;
	public Matrix[][] M;
	public Matrix ML,MR;
	public Integrate1d(){}
	abstract public void addPerturb();
	abstract public double[] f_R(double[] u);
	public void integrate(){
		// initial condition
		this.data_t = new Matrix[this.n_chemical];
		for (int s=0; s<this.n_chemical; s++) this.data_t[s] = new Matrix(this.I,1,this.c0[s]); // initialize with homogenous concentration
		addPerturb();
		// setup diffusion matrix
		this.M = new Matrix[this.n_chemical][2];
		for (int s=0; s<this.n_chemical; s++) diffuse_ADI_matrix(s);
		try {
			// open output stream
			FileOutputStream fout = new FileOutputStream(new File(this.fullfilename),true);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			// write configuration parameters
			oout.writeObject(this.c0); oout.writeObject(this.ht); oout.writeObject(this.hs); 
			oout.writeObject(this.I); oout.writeObject(this.group*this.T); 
			// time step
			for (int k=0; k<(this.group*this.T); k++){
				react_diffuse();
				for (int s=0; s<this.n_chemical; s++) oout.writeObject(this.data_t[s].getRowPackedCopy() );//write to file
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
		for (int i=0; i<I; i++){
			double[] temp,k1,k2,k3,k4;
			temp = new double[this.n_chemical];
			
			for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,0);
			k1 = f_R(temp);
			for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,0)+this.ht/2*k1[s];
			k2 = f_R(temp);
			for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,0)+this.ht/2*k2[s];
			k3 = f_R(temp);
			for (int s=0; s<this.n_chemical; s++) temp[s] = this.data_t[s].get(i,0)+this.ht*k3[s];
			k4 = f_R(temp);
			for (int s=0; s<this.n_chemical; s++) this.data_t[s].set(i,0,this.data_t[s].get(i,0)+this.ht/6*(k1[s]+2*k2[s]+2*k3[s]+k4[s]));
		}
		// diffuse
		for (int s=0; s<this.n_chemical; s++) this.data_t[s] = this.M[s][0].solve(this.M[s][1].times(this.data_t[s]));
	}
	public void diffuse_ADI_matrix(int s){
		double alpha = 2*this.hs*this.hs/(this.k_D[s]*this.ht);
		this.M[s][0] = new Matrix(this.I,this.I); this.M[s][1] = new Matrix(this.I,this.I);
		for (int i=0; i<this.I; i++){
			this.M[s][0].set(i,i,alpha+2);
			this.M[s][0].set(i,periodicIndex(i-1,this.I),-1);
			this.M[s][0].set(i,periodicIndex(i+1,this.I),-1);
			this.M[s][1].set(i,i,alpha-2);
			this.M[s][1].set(i,periodicIndex(i-1,this.I),1);
			this.M[s][1].set(i,periodicIndex(i+1,this.I),1);
		}
	}
	public int periodicIndex(int i, int I){
		if (i<0) i += I; 
		else if (i>(I-1)) i -= I;
		return i;
	}
	
}
