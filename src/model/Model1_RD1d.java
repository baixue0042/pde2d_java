package model;

import Jama.Matrix;

public class Model1_RD1d extends Model1 implements ReactDiffuse1d{
	Matrix[] data; Matrix[][] M;
	public Model1_RD1d(String str){
		setParameter(str);
		initialize();
	}
	public void addPerturb(){
		int chemical=(int) p[0], ci=(int) (p[1]*this.I), di=(int) (p[2]*this.I); double amp=p[3];
		for (int ii=0; ii<di; ii++) data[chemical].set(ci+ii,0,amp+data[chemical].get(ci+ii,0));
	}
	@ Override
	public void initialize(){
		M = ReactDiffuse1d.diffuse_ADI_matrix(I, n_chemical, hs, ht,k_D);
		data = new Matrix[n_chemical];
		for (int s=0; s<n_chemical; s++) data[s] = new Matrix(I,1,hss[s]); // initialize with homogenous concentration
		addPerturb();// add perturbation
	}
	@ Override
	public void step(){
		data = ReactDiffuse1d.react_diffuse(I, n_chemical, ht, this, M, data);
	}

}
