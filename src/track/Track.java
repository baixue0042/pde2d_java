package track;

import java.awt.Font;
import java.util.ArrayList;

import ij.gui.Overlay;
import ij.gui.TextRoi;

public class Track {
	public ArrayList<Blob> allBlobs;
	public Overlay overlay;
	public int trajCount;
	
	public Track(){
		// find largest existed traj label
		this.trajCount = 0;
		this.allBlobs = (new ReadWrite()).reader("detected.txt");
	}
	public float blobOverlap(Blob b1, Blob b2){
		// fraction of pixels in b1 that overlap with b2
		float ratio = 0;
		for (int i=0; i<b1.polygon.npoints; i++){
			if (b2.polygon.contains(b1.polygon.xpoints[i],b1.polygon.ypoints[i])){
				ratio++;
			}
		}
		return ratio/b1.polygon.npoints;
	}
	public int blobChildren(Blob input){
		// find children blob with largest overlap
		float[] overlap = new float[allBlobs.size()];
		for (int i=0; i<allBlobs.size(); i++){
			if (allBlobs.get(i).frm == input.frm+1){
				overlap[i] = blobOverlap(input,allBlobs.get(i));
			}
		}
		return getIndexOfMax(overlap);
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
	public void drawOverlays(){
		overlay = new Overlay();
		Font font = new Font("Arial", Font.PLAIN, 8);
		for (Blob b : this.allBlobs){
			b.draw();
			overlay.add(b.roi);
			TextRoi text = new TextRoi(b.center[0], b.center[1], b.label, font);
			text.setPosition(b.frm);
			overlay.add(text);
		}
	}
	public int getIndexOfMax(float array[]) {
		if (array.length == 0) {
			return -1; // array contains no elements
		}
		float max = array[0];
		int pos = 0;
		for(int i=1; i<array.length; i++) {
			if (max < array[i]) {
				pos = i;
				max = array[i];
			}
		}
		return pos;
	}
}
