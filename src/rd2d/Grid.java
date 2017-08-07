package rd2d;
import java.io.Serializable;

import Jama.Matrix;
import ij.process.FloatProcessor;

public class Grid extends Matrix implements Serializable{
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
	public void square_perturbation(double amp, double[] loc, double hs){
		int i = (int) (loc[0]/hs), j = (int) (loc[1]/hs), di = (int) (loc[2]/hs), dj = (int) (loc[3]/hs);
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
	public double[] min_max(){
		double[][] arr = this.getArray();
		double[] min_max = new double[2];
		min_max[0] = arr[0][0]; min_max[1] = arr[0][0];
		for (int i=0; i<arr.length; i++){
			for (int j=0; j<arr[i].length; j++){
				if (min_max[0]>arr[i][j]){
					min_max[0] = arr[i][j];
				}
				if (min_max[1]<arr[i][j]){
					min_max[1] = arr[i][j];
				}
			}
		}
		return min_max;
	}
	public FloatProcessor getFloatProcessor(){
		int I = this.getRowDimension(), J = this.getColumnDimension();
		float[] pixels = new float[I*J];
		for (int i=0; i<I; i++){
			for (int j=0; j<J; j++){
				pixels[i*J+j] = (float) this.get(i,j);
			}
		}
		return new FloatProcessor(J,I,pixels);

	}

	@Override
	public Grid copy(){
		return new Grid(this.getArrayCopy());
	}
	@Override
	public String toString() {
		String s = "";
		for (int i=0; i<this.getRowDimension(); i++){
			for (int j=0; j<this.getColumnDimension(); j++){
				s  += (String.format ("%.3f", this.get(i,j)) + ",");
			}
			s += "\n";
		}
		return s;
	}

}
