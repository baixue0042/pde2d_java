package rd2d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReadWrite {
	public ReadWrite(){}
	public void writer(String fileName, Model_rd2d m){
		try {
			FileOutputStream f = new FileOutputStream(new File(fileName));
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(m);
			//System.out.println(this.toString());
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} 
	}
	public Model_rd2d reader(String fileName){
		Model_rd2d m = new Model_rd2d();
		try {
			FileInputStream fi = new FileInputStream(new File(fileName));
			ObjectInputStream oi = new ObjectInputStream(fi);
			m = (Model_rd2d) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("End of file");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return m;
	}

}
