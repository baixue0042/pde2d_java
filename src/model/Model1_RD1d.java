package model;

import Jama.Matrix;

public class Model1_RD1d extends Model1 implements ReactDiffuse1d{
	public Model1_RD1d(String str){
		setParameter(str);
		initialize();
	}
	public void addPerturb(){
		double amp=p[1];
		int chemical=(int) p[0], ci=(int) (p[2]*I), di=(int) (p[3]*I); 
		for (int ii=0; ii<di; ii++) data[chemical].set(ci+ii,0,amp+data[chemical].get(ci+ii,0));
	}
	@ Override
	public void initialize(){
		M = this.diffuse_ADI_matrix_noflux(I, n_chemical, hs, ht,k_D);
		data = new Matrix[n_chemical];
		for (int s=0; s<n_chemical; s++) data[s] = new Matrix(I,1,hss[s]); // initialize with homogenous concentration
		addPerturb();// add perturbation
	}
	@ Override
	public void step(){
		data = this.react_diffuse(I, n_chemical, ht, this, M, data);
	}

}
