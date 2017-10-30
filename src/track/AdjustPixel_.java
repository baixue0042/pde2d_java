package track;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class AdjustPixel_ {
	public static void adjust(int topValue, int bottomValue){
		ImagePlus imp = IJ.getImage();
		ImageStack stk = imp.getImageStack();
		ImageStack stk2 = new ImageStack(stk.getWidth(),stk.getHeight());
		for (int frm=1; frm<stk.getSize()+1; frm++){
			ImageProcessor ip = stk.getProcessor(frm);
			ByteProcessor p = ip.convertToByteProcessor();
			p.max(topValue);
			p.min(bottomValue);
			p.subtract(bottomValue);
			p.multiply(255/(topValue-bottomValue));
			stk2.addSlice(p);
		}
		(new ImagePlus( imp.getShortTitle()+bottomValue+"_"+topValue, stk2)).show();
	}
	public static void main(String[] args) {
		adjust(120,50);
	}
}
