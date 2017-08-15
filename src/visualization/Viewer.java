package visualization;
import java.io.File;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFileChooser;

import ij.ImagePlus;
import ij.gui.ImageWindow;

public class Viewer{
	public Button btnOpen;
	public Frame frame;
	public static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}
	public static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}
	public Viewer(){
		frame = new Frame("view data");
		frame.setSize(200, 200);
		frame.setLocation(100,0);
		frame.setLayout(new GridLayout(2,1));
		
		btnOpen = new Button("Open");
		btnOpen.addActionListener(new BtnListener());
		frame.add(btnOpen);
		frame.setVisible(true);
	}
	public class BtnListener implements ActionListener {
		@Override
			public void actionPerformed(ActionEvent e) {
				Button source = (Button)e.getSource();// Need to determine which button has fired the event.
				if (source == btnOpen) {
					String path = "~/Documents/data_working/pde2d/";
					path = path.replaceFirst("^~", System.getProperty("user.home"));

					JFileChooser chooser = new JFileChooser(new File(path));
					chooser.setMultiSelectionEnabled(true);
					chooser.showOpenDialog(frame);
					File[] flist = chooser.getSelectedFiles();
					for (File f : flist) System.out.println(f.getName());
				}
			}
		}
	public static void main(String[] args){
		new Viewer();
	}
}
