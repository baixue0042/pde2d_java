package visualization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.Dimension;

import ij.ImageStack;
import ij.process.FloatProcessor;

public class Data2d extends Data{
	public Data2d(File f, double dt){
		name = f.getName(); dimension = 2;
		try {
			FileInputStream fin = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(fin);
			//******************** read data start ********************
			// read setup info
			hss = (double[]) oin.readObject(); n_chemical = hss.length; 
			ht = (double) oin.readObject(); K = (int) oin.readObject(); kstep = (int) (dt/ht);// default kstep
			hs = (double) oin.readObject(); I = (int) oin.readObject(); J = (int) oin.readObject();
			
			// read image data into Array of FloatProcessor
			cmin = hss.clone(); cmax = hss.clone();// initialize min and max pixel value
			stks = new ImageStack[n_chemical];
			for (int s=0; s<n_chemical; s++) {
				stks[s] = new ImageStack(I,J,K/kstep);
				for (int kk=0; kk<K/kstep; kk++)
					stks[s].setProcessor(new FloatProcessor(I,J),kk+1);
			}
			for (int k=0; k<K; k+=1) {
				for (int s=0; s<n_chemical; s++) {
					double[][] arr = (double[][]) oin.readObject();
					for (int i=0; i<I; i++)
						for (int j=0; j<J; j++){
							if (cmin[s]>arr[i][J/2]) cmin[s]=arr[i][J/2];
							if (cmax[s]<arr[i][J/2]) cmax[s]=arr[i][J/2];// update min and max pixel value
							if (k%kstep==0) stks[s].getProcessor(k/kstep+1).setf(i,j,(float) arr[i][j]);
						}
				}
			}
			//******************** read data end********************
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
		int x = (int) ((double)mouseX*I/canvas.height);
		int y = (int) ((double)mouseY*J/canvas.width);
		String[] infoV = new String[n_chemical]; 
		for (int s=0; s<n_chemical; s++) 
			infoV[s] = printd(stks[s].getProcessor(Z).getPixelValue(x,y));
		return "t="+printd(Z*ht*kstep)+", x="+printd(x*hs)+", y="+printd(y*hs)+"    value="+String.join(",", infoV);
	}
	public String stackInfo(int Z){
		return "t="+printd(Z*ht*kstep);
	}
}
