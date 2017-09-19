package visualization;

import ij.gui.Plot;
import ij.gui.PlotWindow;

public class CurveWindow {
	Plot plot; float[] disprange; int dispNt;
	public CurveWindow(){
		disprange=new float[4];
		plot = new Plot("xt plot","time","conc",Plot.LINE);// create plot window
	}
	public void displayPlotWindow(){
		plot.setLimits(disprange[0],disprange[1],disprange[2],disprange[3]);//set plotwindow display range
		PlotWindow plotWindow = plot.show(); //plotWindow.drawPlot(plot);
	}
	public void add_curve(Curve_xt c){
		plot.addPoints(c.x, c.y, Plot.LINE);
		if (c.range[0]<disprange[0]) disprange[0]=c.range[0];//xmin
		if (c.range[1]>disprange[1]) disprange[1]=c.range[1];//xmax
		if (c.range[2]<disprange[2]) disprange[2]=c.range[2];//ymin
		if (c.range[3]>disprange[3]) disprange[3]=c.range[3];//ymax
	}
}
