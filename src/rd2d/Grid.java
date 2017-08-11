package rd2d;
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
	public Grid plus(Grid X){
		return new Grid(this.plus(X));
	}
	public double grid_get(int i, int j){
		if (i<0){
			i += this.getRowDimension();
		} else if (i>(this.getRowDimension()-1)) {
			i -= this.getRowDimension();
		}
		if (j<0){
			j += this.getColumnDimension();
		} else if (j>(this.getColumnDimension()-1)) {
			j -= this.getColumnDimension();
		}
		return this.get(i,j);
	}
	public void grid_set(int i, int j, double s){
		if (i<0){
			i += this.getRowDimension();
		} else if (i>(this.getRowDimension()-1)) {
			i -= this.getRowDimension();
		}
		if (j<0){
			j += this.getColumnDimension();
		} else if (j>(this.getColumnDimension()-1)) {
			j -= this.getColumnDimension();
		}
		this.set(i,j,s);
	}
	public String toString() {
		String s = "";
		for (int i=0; i<this.getRowDimension(); i++){
			for (int j=0; j<this.getColumnDimension(); j++){
				s  += (String.format ("%.5g", this.get(i,j)) + ",");
			}
			s += "\n";
		}
		return s;
	}

}
