package rd2d;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.StackWindow;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.Button;
import java.awt.Scrollbar;
import java.awt.Frame;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;

public class SyncWindow{
	private StackWindow[] windows;
	private Frame frame;
	private Label statusLabel;
	private Button btnAdd;
	private Scrollbar hbar;
	private double hs, ht;
	private int width, height;
	private Integrate2d m;
	public SyncWindow(Integrate2d m, double hs, double ht) {
		this.hs = hs; this.ht = ht; this.m = m;
		this.width = GetScreenWorkingWidth(); this.height = GetScreenWorkingHeight();
		/*
		Dimension originalsize= windows[0].getPreferredSize();
		double scalefactor = height/n_chemical / originalsize.getHeight();
		for (int i=0; i<windows.length; i++){
			windows[i].setLocationAndSize(0,i*height/n_chemical,(int)(scalefactor * originalsize.getWidth()), height/n_chemical);
		}*/
		int T = openMovies();
		hbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, T);
		hbar.addAdjustmentListener(new MyAdjustmentListener());
		//hbar.setSize(this.width,40);
		statusLabel = new Label("0");
		statusLabel.addKeyListener(new MyKeyListener());
		frame = new Frame("scroll bar");
		frame.setSize(width/2, 80);
		frame.setLayout(new GridLayout(2,1));
		frame.add(hbar); frame.add(statusLabel);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				frame.dispose();
				for (int i=0; i<windows.length; i++){ windows[i].close(); }
			}
		});
		
	}
	private int openMovies(){
		Grid[][] data = this.m.data;
		int I = data[0][0].getRowDimension(),J = data[0][0].getColumnDimension(), T= data.length, n_chemical=data[0].length;
		int movieheight = this.height/n_chemical, moviewidth = movieheight*J/I;
		windows = new StackWindow[n_chemical];
		for (int s=0; s<n_chemical; s++){
			double stkmin=data[0][s].get(0,0), stkmax=data[0][s].get(0,0); double[] min_max;
			ImageStack stk = new ImageStack(J,I);
			for (int t=0; t<T; t++){
				stk.addSlice(data[t][s].getFloatProcessor());
				min_max = data[t][s].min_max();
				if (stkmin>min_max[0]){ stkmin = min_max[0]; }
				if (stkmax<min_max[1]){ stkmax = min_max[1]; }
			}
			ImagePlus imp = new ImagePlus(""+s, stk);
			imp.setDisplayRange(stkmin, stkmax);
			System.out.println(stkmin+","+stkmax);
			windows[s] = new StackWindow(imp);
			windows[s].setLocationAndSize(0, s*movieheight, moviewidth, movieheight);
		}
		return T;
	}
	class MyKeyListener implements KeyListener{
		@Override
		public void keyTyped(KeyEvent e) {/*do nothing*/}
		@Override
		public void keyReleased(KeyEvent e) {/*do nothing*/}
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_LEFT){ update(hbar.getValue()-1); }
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){ update(hbar.getValue()+1); }
		}
	}
	class MyAdjustmentListener implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) { update(e.getValue()); }
	}
	private static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}
	private static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}
	private void update(int frm){
		for (int i=0; i<windows.length; i++){
			windows[i].showSlice(frm);
		}
		statusLabel.setText("t="+String.format("%3.3f",frm*this.ht));
	}
}
