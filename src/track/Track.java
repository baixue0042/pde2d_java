package track;

import java.awt.Font;
import java.awt.Polygon;
import java.awt.List;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.TextRoi;

public class Track {
	public ArrayList<Blob> allBlobs;
	public Overlay overlay;
	public int trajCount;
	
	public Track(){
		// find largest existed traj label
		this.trajCount = 0;
		RoiManager manager = RoiManager.getInstance();
		if (manager != null) {
		    String[] labels = manager.getList().getSelectedItems();
		    Hashtable<String, Roi> table = (Hashtable<String, Roi>)manager.getROIs();
		    for (String label : labels) {
		        int slice = manager.getSliceNumber(label);
		        Roi roi = table.get(label);
		        roi.getPolygon();
		    }
		}
	}
	
	public void blobChildren(Roi currentRoi){
		// find Roi: (1) in the next frame, (2) largest overlap
		float[] overlap = new float[allBlobs.size()];
		for (int index: manager.getIndexes()){
			if (manager.getRoi(index) == currentRoi.getPosition()+1){
				overlap[i] = blobOverlap(input,allBlobs.get(i));
			}
		}
	}
	public ArrayList<Integer> getIndex(String name){
		ArrayList<Integer> index = new ArrayList<Integer>();
		for (int i=0; i<this.allBlobs.size(); i++) {
			if (this.allBlobs.get(i).label.contains(name)) {
				index.add(i);
			}
		}
		return index;
	}
	public void seed(String name){
		ArrayList<Integer> index = getIndex(name);
		if (index.size()>0) {
			int i = index.get(0);
			Blob b = this.allBlobs.get(i);
			int stepCount = 0;
			int nextIndex = 0;
			do {
				b.setTrackID(this.trajCount, stepCount);
				nextIndex = blobChildren(b);
				b.children.add(nextIndex);
				b = this.allBlobs.get(nextIndex);
				stepCount++;
				} while ((nextIndex>0)&&(stepCount<50));
			this.trajCount++;
		}
	}
	public void add(String name){
		String[] names = name.split(",");// example input: 0_,2_ (append traj 2 to traj 0)
		ArrayList<Integer> index0 = getIndex(names[0]);// index of blobs that start with 0_
		Blob b0 = this.allBlobs.get(index0.get(index0.size()-1));// last blob of trajectory 0_
		int count = Integer.valueOf(b0.label.split("_")[1])+1;
		ArrayList<Integer> index1 = getIndex(names[1]);// index of blobs that start with 0_
		for (int i:index1) {// add trajectory 2_ to trajectory 0_
			Blob b1 = this.allBlobs.get(i);// blob to be added to trajectory 0_
			b1.label = names[0] + "_" + count;
			b0.children.add(i);
			count ++;
		}
	}
	public void remove(String name){
		ArrayList<Integer> index = getIndex(name);
		if (index.size()>0) {
			int i = index.get(0);
			Blob b = this.allBlobs.get(i);
			b.label = b.backup;
		}		
	}
	public void save(String name){
		(new ReadWrite()).writer("tracked"+name+".txt", allBlobs);
	}
	public static void drawLabel(){
		ImagePlus imp = IJ.getImage();
		Overlay overlay = new Overlay();
		Font font = new Font("Arial", Font.PLAIN, 8);
		for (Blob b : this.allBlobs){
			b.draw();
			overlay.add(b.roi);
			TextRoi text = new TextRoi(b.center[0], b.center[1], b.label, font);
			text.setPosition(b.frm);
			overlay.add(text);
		}
	}
}
