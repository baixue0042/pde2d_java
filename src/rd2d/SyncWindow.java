package rd2d;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.StackWindow;
import ij.process.FloatProcessor;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
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
	private float ht;
	private float[] cmin, cmax;
	private int I,J,K, n_chemical, width, height;
	public SyncWindow(String id) {
		width = GetScreenWorkingWidth(); height = GetScreenWorkingHeight();
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		try {
			FileInputStream fin = new FileInputStream(new File(path+id));
			ObjectInputStream oin = new ObjectInputStream(fin);
			String headinfo = (String) oin.readObject();// read headinfo -> n_chemical,ht,K
			// create stacks
			ImageStack[] stks = new ImageStack[n_chemical];
			for (int s=0; s<n_chemical; s++) stks[s] = new ImageStack();
			
			// initialize min and max pixel value
			cmin = new float[n_chemical]; cmax = new float[n_chemical];
			for (int s=0; s<n_chemical; s++) {
				double[][] arr = (double[][]) oin.readObject();
				cmin[s] = (float) arr[0][0]; cmax[s] = (float) arr[0][0];
				I = arr.length; J = arr[0].length;
				stks[s].addSlice(arrayToProcessor(arr, s));
				}
			// add frames to stacks
			for (int k=1; k<K; k++) {
				for (int s=0; s<n_chemical; s++) {
					double[][] arr = (double[][]) oin.readObject();
					stks[s].addSlice(arrayToProcessor(arr, s));
				}
			}
			// create windows
			int movieheight = height/n_chemical, moviewidth = movieheight*J/I;
			windows = new StackWindow[n_chemical];
			for (int s=0; s<n_chemical; s++) {
				ImagePlus imp = new ImagePlus(""+s, stks[s]);
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
		statusLabel.setText("t="+String.format("%3.3f",frm*ht));
	}

}
