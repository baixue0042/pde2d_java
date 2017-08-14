package visualization;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;
import ij.process.FloatProcessor;

public class Data1d {
	private FloatProcessor[] fp;
	private StackWindow[] windows;
	private Frame frame;
	private Label statusLabel;
	private double ht,hs;
	private double[] c0,cmin, cmax;
	private int I,K, n_chemical, width, height, kstep;
	private Dimension canvasSize;
	
	public Data1d(String fullfilename, int n) {
		kstep = n;
		try {
			FileInputStream fin = new FileInputStream(new File(fullfilename));
			ObjectInputStream oin = new ObjectInputStream(fin);
			//******************** read data start ********************
			c0 = (double[]) oin.readObject(); n_chemical = c0.length; 
			ht = (double) oin.readObject(); 
			hs = (double) oin.readObject(); 
			I = (int) oin.readObject(); K = (int) oin.readObject();

			cmin = c0.clone(); cmax = c0.clone();// initialize min and max pixel value
			fp = new FloatProcessor[n_chemical];
			for (int s=0; s<n_chemical; s++) fp[s] = new FloatProcessor(K/kstep,I);
			for (int k=0; k<K; k+=1) {
				for (int s=0; s<n_chemical; s++) {
					double[] arr = (double[]) oin.readObject();
					for (int i=0; i<I; i++){
							if (cmin[s]>arr[i]) cmin[s]=arr[i];
							if (cmax[s]<arr[i]) cmax[s]=arr[i];
							if (k%kstep==0) fp[s].setf(k/kstep,i,(float) arr[i]);
					}
				}
			}
			//******************** read data end********************
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
	private void prepareGUI() {
		width = GetScreenWorkingWidth(); height = GetScreenWorkingHeight();
		int movieheight = height/n_chemical, moviewidth = movieheight*(K/kstep)/I;
		windows = new StackWindow[n_chemical];
		System.out.println("pixel range");
		for (int s=0; s<n_chemical; s++) {
			ImagePlus imp = new ImagePlus(""+s, fp[s]);
			System.out.println(printd(c0[s])+","+printd(cmin[s])+","+printd(cmax[s]));
			imp.setDisplayRange(cmin[s], cmax[s]);
			windows[s] = new StackWindow(imp);
			windows[s].setLocationAndSize(0, s*movieheight, moviewidth, movieheight);
		}
		canvasSize = windows[0].getCanvas().getSize();
		
		statusLabel = new Label();
		
		frame = new Frame("scroll bar");
		frame.setSize(width/2, 80);
		frame.setLocation(width/4,0);
		frame.setLayout(new GridLayout(2,1));
		frame.add(statusLabel);
		frame.setVisible(true);
		frame.addWindowListener(new MyWindowListener());
		for (int i=0; i<windows.length; i++){
			windows[i].getCanvas().addMouseListener(new MyMouseListener());
		}

	}
	class MyWindowListener implements WindowListener{
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {
			frame.dispose(); 
			for (int i=0; i<windows.length; i++) windows[i].close();
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
			ImageCanvas ic = (ImageCanvas) e.getSource();
			double t = 0, x = ((double) e.getX())/canvasSize.height*I*hs;
			if (ic==windows[0].getCanvas()) System.out.println(0+"\t"+printd(t)+","+printd(x));
			if (ic==windows[1].getCanvas()) System.out.println(1+"\t"+printd(t)+","+printd(x));
			if (ic==windows[2].getCanvas()) System.out.println(2+"\t"+printd(t)+","+printd(x));
		}
	}
	private static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}
	private static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}
	String printd(double x){
		return String.format("%.3g",x);
	}

}
