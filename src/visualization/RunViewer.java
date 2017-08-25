package visualization;

import java.awt.Button;
import java.awt.TextField;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import ij.ImageJ;

public class RunViewer {
	Button btnOpen; TextField tfInfo; Frame frame;
	public RunViewer(){}
	public void prepareGUI(){
		btnOpen = new Button("Open");
		btnOpen.addActionListener(new BtnListener());
		tfInfo = new TextField("1,1,0"); 
		// 1d data: 1(dimension),1(dt),0(column)
		// 2d data middle column: 1(dimension),1(dt), 0.5(column)
		// 2d data: 2(dimension),1(dt)
		
		frame = new Frame("view data");
		frame.setSize(300, 100);
		frame.setLocation(1000,0);
		frame.setLayout(new GridLayout(2,1));
		frame.add(btnOpen); frame.add(tfInfo);
		
		frame.setVisible(true);
	}
	
	public void loadData(){
		String path = "~/Documents/data_working/pde2d/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		JFileChooser chooser = new JFileChooser(new File(path));
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(frame);
		File[] flist = chooser.getSelectedFiles();
		for (File f : flist) {
			double[] info = toDouble(tfInfo.getText());
			if (info[0]==1) new Viewer(new Data1d(f, info[1],info[2]));
			else new Viewer(new Data2d(f, info[1]));
		}
	}
	
	public class BtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Button source = (Button)e.getSource();
			if (source == btnOpen) loadData();
		}
	}
	public static double[] toDouble(String str){
		String[] strarr = str.split(",");
		double[] arr = new double[strarr.length];
		for (int i=0; i<strarr.length; i++) { arr[i] =  Double.parseDouble(strarr[i]); }
		return arr;
	}

	public static void main(String[] args){
		new ImageJ();
		RunViewer gui = new RunViewer();
		gui.prepareGUI();
	}

}
