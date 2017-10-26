package track;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.LUT;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.RankFilters;
import ij.plugin.frame.RoiManager;
import ij.gui.PolygonRoi;
import ij.gui.Roi;

import java.awt.Polygon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Detect {
	public Detect(){}

	public void smooth(String infoSmooth){
		ImagePlus imp = IJ.getImage();
		ImageStack stk = imp.getImageStack();
		ImageStack stk2 = new ImageStack(stk.getWidth(),stk.getHeight());
		float THRESHGAUSS = Integer.valueOf(infoSmooth);
		for (int frm=1; frm<stk.getSize()+1; frm++){
			ImageProcessor ip = stk.getProcessor(frm);
			stk2.addSlice(smooth(ip, THRESHGAUSS));
		}
		System.out.println("Smooth "+infoSmooth);
		(new ImagePlus(infoSmooth, stk2)).show();
		stk2 = null;
	}
	
	public void adjust(String infoAdjust){
		ImagePlus imp = IJ.getImage();
		ImageStack stk = imp.getImageStack();
		ImageStack stk2 = new ImageStack(stk.getWidth(),stk.getHeight());
		String[] infoAdjustArr= infoAdjust.split(",");
		int THRESHMAX = Integer.valueOf(infoAdjustArr[0]), THRESHMIN = Integer.valueOf(infoAdjustArr[1]);
		for (int frm=1; frm<stk.getSize()+1; frm++){
			ImageProcessor ip = stk.getProcessor(frm);
			stk2.addSlice(adjust(ip, THRESHMAX, THRESHMIN));
		}
		System.out.println("Adjust "+infoAdjust);
		(new ImagePlus(infoAdjust, stk2)).show();
	}
	
	public void thresh(String infoThresh, String path){
		ImagePlus imp = IJ.getImage();
		ImageStack stk = imp.getImageStack();
		ArrayList<Polygon> stkPolygon = new ArrayList<Polygon>();
		ArrayList<Integer> stkPolygonFrm = new ArrayList<Integer>();
		int THRESHVALUE = Integer.valueOf(infoThresh);
		for (int frm=1; frm<stk.getSize()+1; frm++){
			ImageProcessor ip = stk.getProcessor(frm);
			ByteProcessor bp = ip.convertToByteProcessor();
			bp.threshold(THRESHVALUE);
			for (Polygon p : traceBlobs(bp, frm)) {
				stkPolygon.add(p);
				stkPolygonFrm.add(frm);
			}
		}
		// save threshold result
		try {
			// delete file if already existed
			File f = new File(path+infoThresh+".dat");
			if (f.exists()) f.delete();
			// open output stream
			FileOutputStream fout = new FileOutputStream(f,true);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			// start write
			oout.writeObject(stkPolygon);
			oout.writeObject(stkPolygonFrm); 
			// end write
			oout.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Thresh "+infoThresh);
	}
	
	public void difference(String infoDiff){
		ImagePlus imp = IJ.getImage();
		ImageStack stk = imp.getImageStack();
		ImageStack stk2 = new ImageStack(stk.getWidth(),stk.getHeight());
		String[] infoDiffArr= infoDiff.split(",");
		int STEPSIZE = Integer.valueOf(infoDiffArr[0]), DISPMIN = Integer.valueOf(infoDiffArr[1]), DISPMAX = Integer.valueOf(infoDiffArr[2]);
		
		float[] im1, im2, imsub;
		float v;
		for (int frm=1; frm<stk.getSize()-STEPSIZE; frm+=1){
			im1 = (float[]) stk.getProcessor(frm).convertToFloatProcessor().getPixels();
			im2 = (float[]) stk.getProcessor(frm+STEPSIZE).convertToFloatProcessor().getPixels();
			imsub = new float[im1.length];
			for (int i=0; i<im1.length; i++){
				v = im2[i] - im1[i];
				v = Math.min(v,DISPMAX);
				v = Math.max(v,DISPMIN);
				imsub[i] = v;
			}
			stk2.addSlice(new FloatProcessor(stk.getWidth(),stk.getHeight(),imsub));
		}
		ImagePlus imp2 = new ImagePlus("diff"+infoDiff, stk2);
		imp2.show();
		System.out.println("Time Difference");
	}
	public static void showOverlay(ArrayList<Polygon> stkPolygon,ArrayList<Integer> stkPolygonFrm) {
		ImagePlus imp = IJ.getImage();
		Overlay overlay = new Overlay();
		int N = stkPolygonFrm.size();
		for (int i=0; i<N; i++) {
			Roi roi = new PolygonRoi(stkPolygon.get(i),Roi.TRACED_ROI);
			roi.setPosition(stkPolygonFrm.get(i));
			overlay.add(roi);
		}
		imp.setOverlay(overlay);
		imp.updateImage();
		imp.show();
	}
	
	public static void load(String infoSave, String path){
		
		try {
			File f = new File(path+infoSave+".dat");
			// open input stream
			FileInputStream fin = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(fin);
			// start read
			showOverlay((ArrayList<Polygon>) oin.readObject(), (ArrayList<Integer>) oin.readObject());
			// end read
			oin.close();
			fin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("loaded");
	}
	
	public ImageProcessor smooth(ImageProcessor ip, double sigma){
		ByteProcessor p = ip.convertToByteProcessor();
		if (sigma>0){
			final RankFilters filter = new RankFilters();
			filter.rank(p, sigma, RankFilters.MEAN);
			(new GaussianBlur()).blurGaussian(p, sigma);
			filter.rank(p, sigma, RankFilters.MEAN);
		}
		return p;
	}
	
	public ImageProcessor adjust(ImageProcessor ip, float topValue, float bottomValue){
		ByteProcessor p = ip.convertToByteProcessor();
		p.max(topValue);
		p.min(bottomValue);
		p.subtract(bottomValue);
		p.multiply(255/(topValue-bottomValue));
		return p;
	}

	public ArrayList<Polygon> traceBlobs(ByteProcessor p, int frm){
		ArrayList<Polygon> frmPolygon = new ArrayList<Polygon>();
		ByteProcessor labeledproc = new ByteProcessor(p.getWidth(),p.getHeight());
		int margin = 10;
		int ystart = margin, yend = p.getHeight()-margin, xstart = margin, xend = p.getWidth()-margin;
		int BACKGROUND = 0, OBJECT = 255, NOLABEL = 0;
		int labelCount = 1;
		for (int i =ystart; i < yend; ++i) {
			for (int j = xstart; j < xend; ++j) {	
				if ((p.get(j, i) == OBJECT)&& (labeledproc.get(j, i) == NOLABEL) && (p.get(j, i-1) == BACKGROUND)) {
					Contour c = new Contour(p, labeledproc, new Point(j,i), labelCount, OBJECT);
					labeledproc = c.labeledproc;
					frmPolygon.add(c.contour);
					++labelCount;
				}
			}
		}
		return frmPolygon;
	}
	
}
