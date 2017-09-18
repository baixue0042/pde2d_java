package visualization;

import ij.gui.Plot;
import ij.gui.PlotWindow;

public class CurveWindow {
	Plot plot;
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
}
