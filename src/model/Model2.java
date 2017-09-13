package model;

public class Model2  extends Model{
	@ Override
	public void setHSS(){
		n_chemical = 3;
		hss = new double[n_chemical]; 
		hss[0] = fixpoint(); 
		hss[1] = k_R[5]; 
		hss[2] = hss[0];
	}
	@ Override
	public double[] f_R(double[] u){
		double[] dudt = new double[u.length];
		double A = u[0], I = u[1], F=u[2];
		dudt[0] = hill(A,k_R[0],k_R[1],1,3)*I - (1+k_R[2]*F)*A;
		dudt[1] = -dudt[0]+k_R[4]*(k_R[5]-I);
		dudt[2] = k_R[3]*(A-F);
		return dudt;
	}
	@ Override
	public double fss(double x){
		return hill(x,k_R[0],k_R[1],1,3)*k_R[5] - (1+k_R[2]*x)*x;
	}
}
