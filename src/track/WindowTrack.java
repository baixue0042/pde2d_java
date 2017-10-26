package track;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Roi;

public class WindowTrack extends Frame {
private TextField tfInput;
private Button btnSeed, btnAdd, btnRemove, btnSave;
private String info;
private static ImagePlus imp;
private Track data;
// Constructor to setup the GUI components and event handlers
public WindowTrack () {
	data = new Track();
	data.drawOverlays();
	System.out.println(data.allBlobs.size()+" Blobs imported");
	
	imp = IJ.openImage();
	new ImageJ();
	Roi.setColor(Color.green);
	imp.setOverlay(data.overlay);
	imp.show();

	setLayout(new GridLayout(5, 1, 4, 1));
	tfInput = new TextField("", 20);
	add(tfInput);
	btnSeed = new Button("Seed");
	add(btnSeed);
	btnAdd = new Button("Add");
	add(btnAdd);
	btnRemove = new Button("Remove");
	add(btnRemove);
	btnSave = new Button("Save");
	add(btnSave);
 
	// Allocate an instance of inner class BtnListener.
	BtnListener listener = new BtnListener();
	// Use the same listener instance to all the 3 Buttons.
	btnSeed.addActionListener(listener);
	btnAdd.addActionListener(listener);
	btnRemove.addActionListener(listener);
	btnSave.addActionListener(listener);

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
		info = tfInput.getText();
		// Need to determine which button has fired the event.
		Button source = (Button)evt.getSource();
		// Get a reference of the source that has fired the event.
		// getSource() returns a java.lang.Object. Downcast back to Button.
		if (source == btnSeed) {
			data.seed(info);
			System.out.println("Seed "+info);
		} else if (source == btnAdd) {
			data.add(info);
			System.out.println("Add "+info);
		} else if (source == btnRemove) {
			data.remove(info);
			System.out.println("Remove "+info);
		} else if (source == btnSave) {
			data.save(info);
			System.out.println("Save "+info);
		}
		data.drawOverlays();
		imp.setOverlay(data.overlay);
		tfInput.setText("");
	}
}

	public static void main(String[] args) {
		new WindowTrack();
	}

}