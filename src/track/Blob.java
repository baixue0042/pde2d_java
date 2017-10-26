package track;


import java.io.Serializable;
import java.util.ArrayList;
import java.awt.Polygon;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.FloatPolygon;

public class Blob implements Serializable {
	/*
	 * blob
	 */
	private static final long serialVersionUID = 1L;
	public Polygon polygon;
	public Roi roi;
	public String label,backup;
	public int[] pixels;
	public int size,frm;
	public float[] xpoints,ypoints;
	public double[] center;
	public ArrayList<Integer> children;
	
	public Blob(Polygon p, String label, int frm){
		this.polygon = p;
		this.label = label;
		this.backup = label;
		this.frm = frm;
		this.children = new ArrayList<Integer>();
	}
	public void draw(){
		float[] x = new float[this.polygon.npoints];
		float[] y = new float[this.polygon.npoints];
		for (int j=0; j<this.polygon.npoints; j++) {
			x[j] = this.polygon.xpoints[j]+0.5f;
			y[j] = this.polygon.ypoints[j]+0.5f;
		}
		Roi roi = new PolygonRoi(x,y,this.polygon.npoints,Roi.TRACED_ROI);
		roi.setPosition(this.frm);//setPosition(int channel,int slice,int frame)
		this.roi = roi;
		this.center = roi.getContourCentroid();
	}
	public void setTrackID(int id, int seq){
		this.label = id+"_"+seq;
	}
	public void measure(){
		FloatPolygon fp = this.roi.getContainedFloatPoints();
		this.size = fp.npoints;
		this.xpoints = fp.xpoints;
		this.ypoints = fp.ypoints;		
	}
	@Override
	public String toString() {
		String x = "";
		String y = "";
		int n = 0;
		for (int xx : this.polygon.xpoints){
			if (xx>0) {x += (xx+","); n++;}
		}
		for (int yy : this.polygon.xpoints){
			if (yy>0) y += (yy+",");
		}
		return "polygonx:" + x + "\npolygony:" + y + "\npolygonn:" + n + "\nfrm: " + this.frm + "\nlabel: " + this.label;
	}
}
