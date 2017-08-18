package model;

abstract public class Model1 extends Model{
	@ Override
	public void setHSS(){
		n_chemical = 3;
		hss = new double[n_chemical]; hss[0] = fixpoint(); hss[1] = k_R[7]-hss[0]; hss[2] = k_R[5]/k_R[6]*hss[0];
	}
	@ Override
	public double[] f_R(double[] u){
		double[] dudt = new double[u.length];
		double A = u[0], I = u[1], F=u[2];
		dudt[0] = Model.hill(A,k_R[0],k_R[1],k_R[2],3)*I - Model.hill(F,k_R[3],k_R[4],1,1)*A;
		dudt[1] = -dudt[0];
		dudt[2] = k_R[5]*A - k_R[6]*F;
		return dudt;
	}
	@ Override
	public double fss(double x){
		return hill(x,k_R[0],k_R[1],k_R[2],3)*(k_R[7]-x) - hill(k_R[5]/k_R[6]*x,k_R[3],k_R[4],1,1)*x;
	}

}
