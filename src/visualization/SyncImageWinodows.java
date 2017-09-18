package visualization;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.gui.ImageCanvas;

public class SyncImageWinodows{
	public OpenStackWindow[] iw; 
	public Data data;
	public int n_chemical;
	
	public SyncImageWinodows(Data data){
		this.data = data; n_chemical = data.n_chemical;
		ImageWindowListener iwl = new ImageWindowListener(); ImageMouseListener ml = new ImageMouseListener(); StkSyncListener ssl = new StkSyncListener();
		iw = new OpenStackWindow[n_chemical];
		for (int s=0; s<n_chemical; s++) {
			if (data.dimension==1) {
				ImagePlus imp = new ImagePlus(data.name+s, data.fp[s]);
				imp.setDisplayRange(data.cmin[s], data.cmax[s]);
				iw[s]= new OpenStackWindow(imp);
			} else {
				ImagePlus imp = new ImagePlus(data.name+s, data.stks[s]);
				imp.setDisplayRange(data.cmin[s], data.cmax[s]);
				iw[s]= new OpenStackWindow(imp);
				iw[s].addDisplayChangeListener(ssl);
			}
			iw[s].setLocation(0, s*GetScreenWorkingHeight()/data.n_chemical);
			iw[s].getCanvas().addMouseListener(ml);iw[s].getCanvas().addMouseMotionListener(ml);
			iw[s].addWindowListener(iwl);iw[s].addComponentListener(iwl);
		}
	}
	public void displayXY(MouseEvent e){
		ImageCanvas canvas =(ImageCanvas) (e.getSource());
		IJ.showStatus(data.mouseInfo(canvas.getImage().getSlice(), e.getX(), e.getY(), canvas.getSize()));
	}
	public void displayZ(DisplayChangeEvent e){
		IJ.showStatus(data.stackInfo(e.getValue()));
		if(e.getType()==DisplayChangeEvent.Z) {
			for(OpenStackWindow w : iw) 
				if( !w.equals(e.getSource())) w.showSlice(e.getValue());
		}
	}
	public void alignWindows(ComponentEvent e){
		ImageWindow w = (ImageWindow) e.getComponent();
		for (int s=0; s<n_chemical; s++) 
			if (!iw[s].equals(w)) iw[s].setLocation((int) (w.getLocation().getX()),(int)(iw[s].getLocation().getY()));
	}
	public void closeWindows(){
		for (ImageWindow w : iw) w.close(); 
		IJ.showStatus("");
	}
	public class StkSyncListener implements DisplayChangeListener{
		public void displayChanged(DisplayChangeEvent e) {displayZ(e);}
	}
	class ImageMouseListener implements MouseListener, MouseMotionListener{
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {displayXY(e);}
		public void mouseDragged(MouseEvent e) {}
	}
	class ImageWindowListener implements WindowListener, ComponentListener{
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {closeWindows();}
		public void componentHidden(ComponentEvent e) {}
		public void componentMoved(ComponentEvent e) {alignWindows(e);}
		public void componentResized(ComponentEvent e) {}
		public void componentShown(ComponentEvent e) {}
	}
	public static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}
	public static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}

}
