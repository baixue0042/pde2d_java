package rd2d;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.StackWindow;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.Scrollbar;
import java.awt.Frame;
import java.awt.event.WindowAdapter;

public class SyncWindow{
	StackWindow[] windows;
	Frame f;
	public SyncWindow(Model_rd2d m, double hs, double ht) {
		Grid[][] data = m.data;
		int I = data[0][0].getRowDimension(),J = data[0][0].getColumnDimension(), T= data.length, n_chemical=data[0].length;
		windows = new StackWindow[n_chemical];
		double stkmin=0, stkmax=0; double[] min_max;
		for (int s=0; s<n_chemical; s++){
			ImageStack stk = new ImageStack(J,I);
			for (int t=0; t<T; t++){
				stk.addSlice(data[t][s].getFloatProcessor());
				min_max = data[t][s].min_max();
				if (stkmin>min_max[0]){
					stkmin = min_max[0];
				}
				if (stkmax<min_max[1]){
					stkmax = min_max[1];
				}
			}
			//ImagePlus imp = new ImagePlus(m.getInfoChemical(s), stk);
			ImagePlus imp = new ImagePlus(""+s, stk);
			imp.setDisplayRange(stkmin, stkmax);
			windows[s] = new StackWindow(imp);
		}
		
		int width = GetScreenWorkingWidth(), height = GetScreenWorkingHeight();
		Dimension originalsize= windows[0].getPreferredSize();
		double scalefactor = height/n_chemical / originalsize.getHeight();
		for (int i=0; i<windows.length; i++){
			windows[i].setLocationAndSize(0,i*height/n_chemical,(int)(scalefactor * originalsize.getWidth()), height/n_chemical);
		}
		
		final Scrollbar hbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, T);
		hbar.addAdjustmentListener(new MyAdjustmentListener());
		f = new Frame("scroll bar");
		f.setSize(width/2, 80);
		f.add(hbar);
		f.setVisible(true);
		
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				f.dispose();
				for (int i=0; i<windows.length; i++){
					windows[i].close();
				}
			}
		});
		
		
	}
	
	class MyAdjustmentListener implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			for (int i=0; i<windows.length; i++){
				windows[i].showSlice(e.getValue());
			}
		}
	}
	
	public static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	public static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}
}
