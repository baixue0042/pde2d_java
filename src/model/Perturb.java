package model;

import Jama.Matrix;

public interface Perturb {
	default public Matrix[] addPerturb_1d(Matrix[] data, double[] p, int I){
		double amp=p[1];
		int chemical=(int) p[0], ci=(int) (p[2]*I), di=(int) (p[3]*I); 
		for (int ii=-di; ii<di; ii++) data[chemical].set(ci+ii,0,amp+data[chemical].get(ci+ii,0));
		return data;
	}
	
	default public Matrix[] addPerturb_2d(Matrix[] data, double[] p, int I, int J){
		double amp=p[1];
		int chemical=(int) p[0], ci=(int) (p[2]*I), di=(int) (p[3]*I), cj=(int) (p[4]*J), dj=(int) (p[5]*J); 
		for (int ii=0; ii<di; ii++)
			for (int jj=0; jj<dj; jj++)
				data[chemical].set(ci+ii,cj+jj,amp+data[chemical].get(ci+ii,cj+jj));
		return data;
	}

}
