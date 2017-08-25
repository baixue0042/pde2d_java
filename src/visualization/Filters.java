package visualization;

import ij.gui.Plot;
import ij.gui.PlotWindow;

public class Filters {
	public boolean amplifies(){
		return true;
	}
	public void syncPlot(){
		Plot plot = new Plot("","time","conc",Plot.LINE);
		//plot.addPoints();
		PlotWindow plotWindow = plot.show();
	}

}
