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
import ij.gui.ImageWindow;
import ij.process.FloatProcessor;

public class Data1d{
	private FloatProcessor[] fp;
	private double ht,hs;
	private double[] c0,cmin, cmax;
	public Dimension canvasSize;
	public int n_chemical, width, height, I, K, kstep;
	public ImageWindow[] windows;

	public Data1d(String fullfilename) {
		loadData(fullfilename);
		prepareGUI();
		
	}
	private void loadData(String fullfilename){
		try {
			FileInputStream fin = new FileInputStream(new File(fullfilename));
			ObjectInputStream oin = new ObjectInputStream(fin);
			//******************** read data start ********************
			c0 = (double[]) oin.readObject(); n_chemical = c0.length; 
			ht = (double) oin.readObject(); 
			hs = (double) oin.readObject(); 
			I = (int) oin.readObject(); K = (int) oin.readObject();
			kstep = (int) (0.1/ht);
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
	}
	private void prepareGUI() {
		width = Viewer.GetScreenWorkingWidth(); height = Viewer.GetScreenWorkingHeight();
		int movieheight = height/n_chemical, moviewidth = movieheight*(K/kstep)/I;
		windows = new ImageWindow[n_chemical];
		System.out.println("pixel range");
		for (int s=0; s<n_chemical; s++) {
			ImagePlus imp = new ImagePlus(""+s, fp[s]);
			System.out.println(printd(c0[s])+","+printd(cmin[s])+","+printd(cmax[s]));
			imp.setDisplayRange(cmin[s], cmax[s]);
			windows[s].setLocationAndSize(0, s*movieheight, moviewidth, movieheight);
			windows[s].getCanvas().addMouseListener(new MyMouseListener());
			windows[s].addWindowListener(new ImageWindowListener());
		}
		canvasSize = windows[0].getCanvas().getSize();

	}

	class MyMouseListener implements MouseListener{
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {
			ImageCanvas ic = (ImageCanvas) e.getSource();
			double t = ((double) e.getY())/canvasSize.width*kstep*ht, x = ((double) e.getX())/canvasSize.height*I*hs;
			if (ic==windows[0].getCanvas()) System.out.println(0+"\t"+printd(t)+","+printd(x));
			if (ic==windows[1].getCanvas()) System.out.println(1+"\t"+printd(t)+","+printd(x));
			if (ic==windows[2].getCanvas()) System.out.println(2+"\t"+printd(t)+","+printd(x));
		}
	}
	String printd(double x){
		return String.format("%.3g",x);
	}
	class ImageWindowListener implements WindowListener{
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {for (int i=0; i<windows.length; i++) windows[i].close();}
	}

}
