package track;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.TextRoi;
import ij.plugin.frame.RoiManager; 

public class WindowTrack extends Frame {
private TextField tfInput;
private Button btnSeed, btnMerge, btnRemove;
private RoiManager manager; 
// Constructor to setup the GUI components and event handlers
public WindowTrack () {
	new ImageJ();
	setLayout(new GridLayout(4, 1, 3, 1));
	tfInput = new TextField("", 20);
	add(tfInput);
	btnSeed = new Button("Seed");
	add(btnSeed);
	btnMerge = new Button("Merge");
	add(btnMerge);
	btnRemove = new Button("Remove");
	add(btnRemove);
 
	// Allocate an instance of inner class BtnListener.
	BtnListener listener = new BtnListener();
	// Use the same listener instance to all the 3 Buttons.
	btnSeed.addActionListener(listener);
	btnMerge.addActionListener(listener);
	btnRemove.addActionListener(listener);

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
		if  (source == btnSeed) Seed(); 
		else if (source == btnMerge) Merge(info);
		else if (source == btnRemove) Remove();
		showRoiName();
		tfInput.setText("");
	}
public void Seed(){
	// use mouse to select a Roi, then click button
	// create a trajectory from the selected Roi
	manager = RoiManager.getInstance();
	int seedIndex = manager.getSelectedIndex();
	System.out.println("Seed "+seedIndex);
	String trajIndex = ""+seedIndex;
	int nextIndex = 0;
	do{
		manager.rename(seedIndex,trajIndex);
		nextIndex = nextRoi(seedIndex);
		seedIndex = nextIndex;
	} while (nextIndex>0);
}

public void Merge(String info) {
	// type TWO trajectory indexes to be merged together, separated by ","  eg: 12,13
	// then click button
	// the resulting trajectory is the input trajectories concatenated together
	// approach: change name of Roi 13->12
	String[] mergeIndex = info.split(",");
	manager = RoiManager.getInstance();
	for (int index=0; index<manager.getCount(); index++)
		if (manager.getRoi(index).getName()==mergeIndex[1]) manager.rename(index, mergeIndex[0]);
	System.out.println("Seed "+info);
}
public void Remove() {
	// use mouse to select a Roi, then click button
	// remove selected Roi from trajectory and label as untracked
	manager = RoiManager.getInstance();
	int index = manager.getSelectedIndex();
	System.out.println("Remove "+index);
	manager.rename(index,"-");
}
public int nextRoi(int currentIndex){
	// find Roi: (1) in the next frame, (2) largest overlap
	Roi currentRoi = manager.getRoi(currentIndex);
	float max = 0; int maxindex = 0;
	for (int index=0; index<manager.getCount(); index++)
		if (manager.getRoi(index).getPosition() == currentRoi.getPosition()+1){
			float overlap = polygonOverlap(currentRoi,manager.getRoi(index));
			if (max < overlap) {maxindex = index;max = overlap;}//update max index/value
		}
	System.out.println(currentIndex+","+max+","+maxindex);
	return maxindex;//return index of Roi with largest overlap
}
public void showRoiName() {
	Font font = new Font("Arial", Font.PLAIN, 12);
	Overlay overlay = new Overlay();
	for (int index=0; index<manager.getCount(); index++) {
		Roi currentRoi = manager.getRoi(index);
		if (!currentRoi.getName().contains("-")) {//Roi that contains "-" is not included in any track
			TextRoi text = new TextRoi(currentRoi.getBounds().x, currentRoi.getBounds().y, currentRoi.getName(), font);
			text.setPosition(currentRoi.getPosition());
			text.setStrokeColor(Color.GREEN);
			currentRoi.setStrokeColor(Color.GREEN);
			overlay.add(text);
			overlay.add(currentRoi);
		}
	}
	ImagePlus imp = IJ.getImage();
	imp.setOverlay(overlay);
}
}
public static float polygonOverlap(Roi p1, Roi p2){
	// # pixels inside of p2 contained in p1/ # pixels inside of p1
	float p2inp1 = 0;
	Point[] p1InsidePoints = p1.getContainedPoints(),p2InsidePoints = p2.getContainedPoints();
	for (Point p: p2InsidePoints)
		if (p1.contains(p.x,p.y)) p2inp1++;
	return p2inp1/p1InsidePoints.length;
}

	public static void main(String[] args) {
		new WindowTrack();
	}

}