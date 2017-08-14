package templates;
import Jama.Matrix;

public class Grid extends Matrix{
	public Grid(int m, int n){
		super(m, n);
	}
	public Grid(int m, int n, double s){
		super(m, n, s);
	}
	public Grid(Matrix X){
		super(X.getArray() );
	}
	public Grid(double[][] A){
		super(A);
	}
}
