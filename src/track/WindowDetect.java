package track;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Roi;

public class WindowDetect extends Frame {
	private TextField tfSmooth, tfAdjust, tfThresh, tfDiff, tfLoad;
	private Button btnSmooth, btnAdjust, btnThresh, btnDiff ,btnLoad;
	private Detect data;
	private ImagePlus imp;
	// Constructor to setup the GUI components and event handlers
	public WindowDetect() {
		new ImageJ();
		//imp = IJ.openImage();imp.show();
		Roi.setColor(Color.green);

		data = new Detect();
		
		setLayout(new GridLayout(5, 2, 4, 1));
		tfSmooth = new TextField("6", 20);
		add(tfSmooth);
		btnSmooth = new Button("Smooth");
		add(btnSmooth);

		tfAdjust = new TextField("255,0", 20);
		add(tfAdjust);
		btnAdjust = new Button("Adjust");
		add(btnAdjust);
		
		tfThresh = new TextField("0", 20);
		add(tfThresh);
		btnThresh = new Button("Thresh");
		add(btnThresh);
		
		tfDiff = new TextField("1,5,20", 20);
		add(tfDiff);
		btnDiff = new Button("Diff");
		add(btnDiff);
		
		tfLoad = new TextField("", 20);
		add(tfLoad);
		btnLoad = new Button("Load");
		add(btnLoad);
		
	 
		BtnListener listener = new BtnListener();
		btnSmooth.addActionListener(listener);
		btnAdjust.addActionListener(listener);
		btnThresh.addActionListener(listener);
		btnDiff.addActionListener(listener);
		btnLoad.addActionListener(listener);
		
		setTitle("Detect");
		setSize(200, 200);
		setVisible(true);
	}

	/**
	 * BtnListener is a named inner class used as ActionEvent listener for all the Buttons.
	 */
	private class BtnListener implements ActionListener {
	@Override
		public void actionPerformed(ActionEvent evt) {
			String path = "~/Documents/data_working/";
			path = path.replaceFirst("^~", System.getProperty("user.home"));
			Button source = (Button)evt.getSource();// Need to determine which button has fired the event.
			if (source == btnSmooth) {
				data.smooth(tfSmooth.getText());
			} else if (source == btnAdjust) {
				data.adjust(tfAdjust.getText());
			} else if (source == btnThresh) {
				data.thresh(tfThresh.getText(),path);
			} else if (source == btnDiff) {
				data.difference(tfDiff.getText());
			} else if (source == btnLoad) {
				data.load(tfLoad.getText(),path);
			}
			System.gc();
		}
	}

		public static void main(String[] args) {
			new WindowDetect();
		}


}
