package rd2d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReadWrite {
	String filename;
	public ReadWrite(String fileName) {this.filename = filename;}
	public void writer(Integrate2d m){
	}
	public Integrate2d reader(){
		Model0 m = new Model0();
		try {
			FileInputStream fi = new FileInputStream(new File(this.filename));
			ObjectInputStream oi = new ObjectInputStream(fi);
			m = (Model0) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//System.out.println(m.toString());
		return m;
	}

}
