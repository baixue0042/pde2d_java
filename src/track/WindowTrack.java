package track;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager; 

public class WindowTrack extends Frame {
private TextField tfInput;
private Button btnSeed, btnAdd, btnRemove, btnSave;
private RoiManager manager; 
// Constructor to setup the GUI components and event handlers
public WindowTrack () {
	new ImageJ();
	setLayout(new GridLayout(5, 1, 4, 1));
	tfInput = new TextField("", 20);
	add(tfInput);
	btnSeed = new Button("Seed");
	add(btnSeed);
	btnAdd = new Button("Add");
	add(btnAdd);
	btnRemove = new Button("Remove");
	add(btnRemove);
	btnSave = new Button("Save");
	add(btnSave);
 
	// Allocate an instance of inner class BtnListener.
	BtnListener listener = new BtnListener();
	// Use the same listener instance to all the 3 Buttons.
	btnSeed.addActionListener(listener);
	btnAdd.addActionListener(listener);
	btnRemove.addActionListener(listener);
	btnSave.addActionListener(listener);

	setTitle("Master");
	setSize(100, 600);
	setVisible(true);
}

/**
 * BtnListener is a named inner class used as ActionEvent listener for all the Buttons.
 */
private class BtnListener implements ActionListener {
@Override
	public void actionPerformed(ActionEvent evt) {
		String info = tfInput.getText();
		Button source = (Button)evt.getSource();
		if (source == btnSeed) {
		} else if (source == btnAdd) {
			System.out.println("Add "+info);
		} else if (source == btnRemove) {
			System.out.println("Remove "+info);
		} else if (source == btnSave) {
			System.out.println("Save "+info);
		}
		tfInput.setText("");
	}
public int nextRoi(Roi currentRoi){
	// find Roi: (1) in the next frame, (2) largest overlap
	//measure overlap for all Roi in the next frame
	float max = 0; int pos = 0;
	for (int index=0; index<manager.getCount(); index++)
		if (manager.getRoi(index).getPosition() == currentRoi.getPosition()+1){
			float overlap = polygonOverlap(manager.getRoi(index).getPolygon(),currentRoi.getPolygon());
			if (max < overlap) {pos = index;max = overlap;}//update max index/value
		}
	if (max<0.001) return -1;//didn't find overlapping Roi
	else return pos;//return index of Roi with largest overlap
}
public void growSeedRoi(){
	RoiManager manager = RoiManager.getInstance();
	int seedIndex = manager.getSelectedIndex();
	System.out.println("Seed "+seedIndex);
	String trajIndex = "t"+seedIndex;
	int nextIndex = -1;
	do{
		manager.rename(seedIndex,trajIndex);
		nextIndex = nextRoi(manager.getRoi(seedIndex));
		seedIndex = nextIndex;
	} while (nextIndex>0);
}
}
public static float polygonOverlap(Polygon p1, Polygon p2){
	// fraction of pixels in p1 that overlap with p2
	// overlap = # overlap pixels/ # pixels in p1
	float ratio = 0;
	for (int i=0; i<p1.npoints; i++){
		if (p2.contains(p1.xpoints[i],p1.ypoints[i])){
			ratio++;
		}
	}
	return ratio/p1.npoints;
}

	public static void main(String[] args) {
		new WindowTrack();
	}

}