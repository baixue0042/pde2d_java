package visualization;

import ij.gui.Plot;
import ij.gui.PlotWindow;

public class CurveWindow {
	Plot plot;
<<<<<<< HEAD
	float ymin=0, ymax=0; int Nt,s=0;
	public CurveWindow(){
		plot = new Plot("xt plot","time","conc",Plot.LINE);
		PlotWindow plotWindow = plot.show(); //plotWindow.drawPlot(plot);
	}
	public void curve_xt(Data data, double px){
		Nt=data.fp[s].getWidth();
		//System.out.println(Nt+","+Nx);
		float[] x = new float[Nt], y = new float[Nt]; 
		for (int i=0; i<Nt; i++) {
			x[i]=(float) (i*data.ht*data.kstep);
			y[i]=data.fp[s].getf(i,(int) (data.fp[s].getHeight()*px));
			if (y[i]<ymin) ymin=y[i];
			if (y[i]>ymax) ymax=y[i];
		}
	}
	public void add_curve(float[] x, float[] y){
		plot.setLimits(0,Nt,ymin,ymax);
		plot.addPoints(x, y, Plot.LINE);
	}
	public boolean amplified(float yss, float ymax){
		
		return true;
	}
=======
	float[] disprange; int dispNt;
	public CurveWindow(){}
	public void add_curve(float[] x, float[] y){
		Curve_xt c=new Curve_xt();
		plot.addPoints(x, y, Plot.LINE);
		if (ymin<dispymin) dispymin=ymin;
		if (ymax>dispymax) dispymax=ymax;
		if (Nt>dispNt) dispNt=Nt;


	}
	public void createPlotWindow(){
		plot = new Plot("xt plot","time","conc",Plot.LINE);// create plot window
	}
	public void displayPlotWindow(){
		plot.setLimits(0,dispNt,dispymin,dispymax);//set plotwindow display range
		PlotWindow plotWindow = plot.show(); //plotWindow.drawPlot(plot);
	}

>>>>>>> 4046974e9b0e7ee149ba5725248b5e0f0ec5ce66
}
