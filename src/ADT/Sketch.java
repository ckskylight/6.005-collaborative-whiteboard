package ADT;

import java.awt.Image;
import java.util.ArrayList;

/**
 * Sketch is represents top level of our ADT, it is a composition of 
 * other drawings. 
 *
 */
public class Sketch implements Drawing {


	private ArrayList<Drawing> sketch;

	public Sketch()  {
		sketch = new ArrayList<Drawing>();
	}
	

	/**
	 *   This adds a new drawing to the Sketch.
	 *   <Sketch, Drawing> --> <Sketch>
	 **/ 
	public void connect(Drawing drawing)  {
		sketch.add(drawing);
	}

	/**
	 *   Erase the entire drawing.
	 **/
	public void clear() {
		for (Drawing drawing: sketch)  {
			drawing.clear();
		}
	}

	/**
	 *   Returns associated Java Graphics image associated with this
	 *   Drawing.  This represents the bridge from the ADT to the GUI.
	 *   Prerequisite: background must be a blank canvas (white canvas).
	 **/ 
	public Image getImage(Image background) {
		for (Drawing drawing: sketch)  {
			drawing.getImage(background);
		}	
		return background;	
	}


}
