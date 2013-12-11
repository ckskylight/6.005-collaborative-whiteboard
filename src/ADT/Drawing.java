package ADT;

import java.awt.Image;

/**
 * Drawing represents the contents of white board. Classes that implement 
 * drawing are Stroke and Sketch. 
 * 
 * Abstraction function: This maps drawings to objects that implement Drawing
 *
 */
public interface Drawing {


	/**
	 *   Returns associated Java Graphics image associated with this
	 *   Drawing.  This represents the bridge from the ADT to the GUI.
	 **/ 
	public Image getImage(Image background);

}
