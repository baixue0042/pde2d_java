package model;

public class Model2  extends Model{
	
	@ Override
	public void setHSS(){
		// k_R[8] * I = k_R[9] * J; I+J+A = k_R[7]; -> (k_R[9]+k_R[8])/k_R[9]*I = k_R[7]-A
		n_chemical = 4;
		hss = new double[n_chemical]; hss[0] = fixpoint(); hss[3] = k_R[5]/k_R[6]*hss[0];
		hss[1] = (k_R[7]-hss[0])*k_R[8]/(k_R[9]+k_R[8]);// J
		hss[2] = (k_R[7]-hss[0])*k_R[9]/(k_R[9]+k_R[8]);// I
	}
	@ Override
	public double[] f_R(double[] u){
		double[] dudt = new double[u.length];
		double A = u[0], J = u[1], I = u[2], F=u[3];
		dudt[0] = Model.hill(A,k_R[0],k_R[1],k_R[2],3)*I - Model.hill(F,k_R[3],k_R[4],1,1)*A;
		dudt[1] = -dudt[0] + k_R[8]*I - k_R[9]*J;
		dudt[2] = - k_R[8]*I + k_R[9]*J;
		dudt[3] = k_R[5]*A - k_R[6]*F;
		return dudt;
	}
	@ Override
	public double fss(double x){
		return hill(x,k_R[0],k_R[1],k_R[2],3)*(k_R[7]-x) - hill(k_R[5]/k_R[6]*x,k_R[3],k_R[4],1,1)*x;
	}


}
