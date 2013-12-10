package ADT;

import java.awt.Image;

/**
 * Drawing represents the contents of whiteboard. Classes that implement 
 * drawing are Stroke, Custom and Sketch. 
 * Pre-req: This class cannot not be mutated from the GUI!

 * Abstraction function: This maps drawings to objects that implement Drawing
 *
 */
public interface Drawing {

	/**
	 *   Erase the entire drawing.
	 **/
	public void clear();

	/**
	 *   Returns associated Java Graphics image associated with this
	 *   Drawing.  This represents the bridge from the ADT to the GUI.
	 **/ 
	public Image getImage(Image background);

	public String getJSON();
}
