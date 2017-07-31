package rd2d;
import Jama.Matrix;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;

public class Visualization {
	public Visualization(Grid[][]  data, double[][] data_min_max, String name){
		// display movie
		new ImageJ();
		int I = data[0][0].getRowDimension(),J = data[0][0].getColumnDimension(), T= data.length, n_chemical=data[0].length;
		ImageStack stk = new ImageStack(3*J,I);
		for (int t=0; t<T; t++){
			FloatProcessor fp = new FloatProcessor(3*J,I);
			for (int s = 0; s<n_chemical; s++){
				double[][] pixels = data[t][s].getArray();
				float[] pixels_normed = new float[I*J];
				for (int i=0; i<I; i++){
					for (int j=0; j<J; j++){
						pixels_normed[i*J+j] = (float) pixels[i][j];
						//pixels_normed[i*J+j] = (float) ((pixels[i][j]-data_min_max[s][0])/(data_min_max[s][1]-data_min_max[s][0]));
					}
				}
				fp.insert(new FloatProcessor(J,I,pixels_normed), s*J, 0);
			}
			stk.addSlice(fp);
		}
		ImagePlus imp = new ImagePlus(name, stk);
		GeneralPath path = new GeneralPath();
		for (int s = 1; s<n_chemical; s++){
			path.moveTo(s*J, 0f); path.lineTo(s*J, I);
		}
		imp.setOverlay(path, Color.white, null);
		imp.show();
		System.out.println("done");
	}

}
