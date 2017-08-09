package rd2d;
import Jama.Matrix;
import ij.process.FloatProcessor;

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
	public void square_perturbation(double amp, int i, int j, int di, int dj){
		//System.out.println(i+","+j+","+di+","+dj);
		for (int ii=-di; ii<di; ii++){
			for (int jj=-dj; jj<dj; jj++){
				this.grid_set(i+ii,j+jj,amp+this.grid_get(i+ii,j+jj));
			}
		}
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
				s  += (String.format ("%.4g", this.get(i,j)) + ",");
			}
			s += "\n";
		}
		return s;
	}

}
