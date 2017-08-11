package rd2d;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.StackWindow;
import ij.process.FloatProcessor;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.Scrollbar;
import java.awt.Frame;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;

public class SyncWindow{
	private StackWindow[] windows;
	private Frame frame;
	private Label statusLabel;
	private Scrollbar hbar;
	private double ht;
	private double[] c0,cmin, cmax;
	private int I,J,K,kstep, n_chemical, width, height;
	
	public SyncWindow(String path, String id, int n) {
		kstep = n;
		try {
			FileInputStream fin = new FileInputStream(new File(path+id));
			ObjectInputStream oin = new ObjectInputStream(fin);
			c0 = (double[]) oin.readObject(); n_chemical = c0.length; ht = (double) oin.readObject(); 
			I = (int) oin.readObject(); J = (int) oin.readObject(); K = (int) oin.readObject();
			// create stacks
			ImageStack[] stks = new ImageStack[n_chemical];
			for (int s=0; s<n_chemical; s++) stks[s] = new ImageStack(I,J);
			// initialize min and max pixel value
			cmin = c0.clone(); cmax = c0.clone();
			
			// add frames to stacks
			for (int k=0; k<K; k+=kstep) {
				for (int s=0; s<n_chemical; s++) {
					double[][] arr = (double[][]) oin.readObject();
					stks[s].addSlice(arrayToProcessor(arr, s));
				}
			}
			// create windows
			width = GetScreenWorkingWidth(); height = GetScreenWorkingHeight();
			int movieheight = height/n_chemical, moviewidth = movieheight*J/I;
			windows = new StackWindow[n_chemical];
			System.out.println("pixel range");
			for (int s=0; s<n_chemical; s++) {
				ImagePlus imp = new ImagePlus(""+s, stks[s]);
				System.out.println(printd(c0[s])+","+printd(cmin[s])+","+printd(cmax[s]));
				imp.setDisplayRange(cmin[s], cmax[s]);
				windows[s] = new StackWindow(imp);
				windows[s].setLocationAndSize(0, s*movieheight, moviewidth, movieheight);
			}

			oin.close();
			fin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		prepareGUI();
		
	}
	private FloatProcessor arrayToProcessor(double[][] arr, int s) {
		float[][] pixels = new float[I][J];
		for (int i=0; i<I; i++){
			for (int j=0; j<J; j++){
				pixels[i][j] = (float) arr[i][j];
				if (cmin[s]>pixels[i][j]) cmin[s]=pixels[i][j];
				if (cmax[s]<pixels[i][j]) cmax[s]=pixels[i][j];
			}
		}
		return new FloatProcessor(pixels);
	}
	private void prepareGUI() {
		hbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, K);
		hbar.addAdjustmentListener(new MyAdjustmentListener());
		statusLabel = new Label("t="+printd(ht));
		
		frame = new Frame("scroll bar");
		frame.setSize(width/2, 80);
		frame.setLocation(width/4,0);
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
		for (int i=0; i<windows.length; i++){
			windows[i].getCanvas().addMouseListener(new MyMouseListener());
		}
	}

	class MyMouseListener implements MouseListener{
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("Clicked!");
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
		statusLabel.setText("t="+printd((frm+1)*ht*kstep));
	}
	String printd(double x){
		return String.format("%.3g",x);
	}
}
