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
		frame = new Frame("view data");
		frame.setSize(300, 100);
		frame.setLocation(1000,0);
		frame.setLayout(new GridLayout(2,1));
		frame.add(btnOpen); frame.add(tfInfo);
		frame.setVisible(true);
	}
	
	public File[] chooseFile(){
		String path = "~/Documents/data_working/pde/";
		path = path.replaceFirst("^~", System.getProperty("user.home"));
		JFileChooser chooser = new JFileChooser(new File(path));
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(frame);
		File[] flist = chooser.getSelectedFiles();
		return flist;
	}
	public Data loadData(File f, double[] info ){
		// construct Data instance from file f with info
		Data data;
		if (info[0]==1) 
			data = new Data1d(f, info[1],info[2]);
			// 1d data: 1(dimension),1(dt),0(column)
			// 2d data middle column: 1(dimension),1(dt), 0.5(column)
		else 
			data = new Data2d(f, info[1]);
			// 2d data: 2(dimension),1(dt)
		return data;
	}
	
	public void openFunction(){
		File[] flist = chooseFile();
		CurveWindow curvewindow = new CurveWindow();
		double[] info = toDouble(tfInfo.getText());
		for (File f : flist) {
			Data data = loadData(f,info);
			Curve_xt c_mid=new Curve_xt(data,0.5);
			int classified;
			if (c_mid.whether_magnified()) {//whether center magnified
				Curve_xt c_edge=new Curve_xt(data,0);
				curvewindow.add_curve(c_edge); 
				if (c_edge.ymax<1.5) {//whether edge is excited
					classified=2;//center magnified & edge is not excited: localized pulse
					new SyncImageWinodows(data);
				} else {
					classified=1;//center magnified & edge is excited: traveling pulse
				}
			} else {
				classified=0;//center not magnified: nothing
			}
			System.out.println(data.k_R[0]+","+data.k_R[1]+","+data.k_R[2]+","+classified);
		}
		curvewindow.displayPlotWindow();
	}

	public class BtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Button source = (Button)e.getSource();
			if (source == btnOpen) openFunction();
		}
	}
	public static double[] toDouble(String str){
		String[] strarr = str.split(",");
		double[] arr = new double[strarr.length];
		for (int i=0; i<strarr.length; i++)  arr[i] =  Double.parseDouble(strarr[i]); 
		return arr;
	}

	public static void main(String[] args){
		new ImageJ();
		RunViewer gui = new RunViewer();
		gui.prepareGUI();
	}

}
