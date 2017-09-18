package visualization;

import ij.gui.Plot;
import ij.gui.PlotWindow;

public class CurveWindow {
	Plot plot;
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

}
