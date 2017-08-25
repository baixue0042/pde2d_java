package visualization;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import ij.process.FloatProcessor;

public class Data1d extends Data{
	public Data1d(File f, double dt, double colum){
		name = f.getName(); dimension = 1;
		try {
			FileInputStream fin = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(fin);
			//******************** read data start ********************
			// read setup info
			hss = (double[]) oin.readObject(); n_chemical = hss.length; 
			ht = (double) oin.readObject(); K = (int) oin.readObject(); kstep = (int) (dt/ht);// default kstep
			hs = (double) oin.readObject(); I = (int) oin.readObject(); J = (int) oin.readObject();
			int JJ = (int) (J*colum);
			// read image data into Array of FloatProcessor
			cmin = hss.clone(); cmax = hss.clone();// initialize min and max pixel value
			fp = new FloatProcessor[n_chemical];
			for (int s=0; s<n_chemical; s++) fp[s] = new FloatProcessor(K/kstep,I);
			for (int k=0; k<K; k+=1) {
				for (int s=0; s<n_chemical; s++) {
					double[][] arr = (double[][]) oin.readObject();
					for (int i=0; i<I; i++){
							if (cmin[s]>arr[i][JJ]) cmin[s]=arr[i][JJ];
							if (cmax[s]<arr[i][JJ]) cmax[s]=arr[i][JJ];// update min and max pixel value
							if (k%kstep==0) fp[s].setf(k/kstep,i,(float) arr[i][JJ]);
					}
				}
			}
			//******************** read data end ********************
			oin.close();
			fin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		for (int s=0; s<n_chemical; s++) 
			System.out.println(printd(hss[s])+"\t \t"+printd(cmin[s])+"\t \t"+printd(cmax[s]));
	}
	public String mouseInfo(int Z, int mouseX, int mouseY, Dimension canvas){
		int x = (int) ((double)mouseX*K/kstep/canvas.width);
		int y = (int) ((double)mouseY*I/canvas.height);
		String[] infoV = new String[n_chemical]; 
		for (int s=0; s<n_chemical; s++) 
			infoV[s] = printd(fp[s].getPixelValue(x,y));
		return "t="+printd(x*ht*kstep)+", x="+printd(y*hs)+"    value="+String.join(",", infoV);
	}
	public String stackInfo(int Z){
		return "";
	}
}