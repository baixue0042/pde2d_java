package visualization;

import ij.ImageStack;
import ij.process.FloatProcessor;
import java.awt.Dimension;

abstract public class Data {
	public String name;
	public double ht,hs;
	double[] hss,cmin, cmax;
	public int n_chemical, width, height, I, J, K, kstep, dimension;
	public FloatProcessor[] fp; public ImageStack[] stks;
	abstract public String mouseInfo(int Z, int mouseX, int mouseY, Dimension canvas);
	abstract public String stackInfo(int Z);
	
	String printd(double x){
		return String.format("%.4g",x);
	}

}
