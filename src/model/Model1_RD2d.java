package model;

import Jama.Matrix;

public class Model1_RD2d extends Model1 implements ReactDiffuse2d{
	public Model1_RD2d(String str){
		setParameter(str);
		initialize();
	}
	public void addPerturb(){
		double amp=p[1];
		int chemical=(int) p[0], ci=(int) (p[2]*I), di=(int) (p[3]*I), cj=(int) (p[4]*I), dj=(int) (p[5]*I); 
		for (int ii=0; ii<di; ii++)
			for (int jj=0; jj<dj; jj++)
				data[chemical].set(ci+ii,cj+jj,amp+data[chemical].get(ci+ii,cj+jj));
	}
	@ Override
	public void initialize(){
		M = this.diffuse_ADI_matrix(I,J, n_chemical, hs, ht, k_D);
		data = new Matrix[n_chemical];
		for (int s=0; s<n_chemical; s++) data[s] = new Matrix(I,J,hss[s]); // initialize with homogenous concentration
		addPerturb();// add perturbation
	}
	@ Override
	public void step(){
		data = this.react_diffuse(I, J, n_chemical, ht, this, M, data);
	}

}
