package ADT;

import java.awt.Image;

/**
 * Drawing represents the contents of whiteboard. Classes that implement 
 * drawing are stroke, path and sketch. 
 *
 */
public interface Drawing {

	/**
	 *   Deletes all components from the Drawing.
	 **/
	public void clear();

	/**
	 *   Returns associated Java Graphics image associated with this
	 *   Drawing.  This represents the bridge from the ADT to the GUI.
	 **/ 
	public Image getImage(Image background);
}
