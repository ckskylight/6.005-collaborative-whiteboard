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

	/**
	 * This returns true if two sketches are behaviorally equal, eg they contain all
	 * the same drawings. False otherwise. 
	 */
	@Override
	public boolean equals(Object other)  {
		if(!(other instanceof Sketch))  
			return false;
		Sketch otherSketch = (Sketch) other;
		ArrayList<Drawing> otherDrawing = otherSketch.getDrawings();
		//Check both sketches have the same number of drawings. 
		if(!(this.sketch.size() == otherDrawing.size()))
			return false;
		//Returns false if any drawings are not equal or not in the correct order.
		for (int i = 0; i < this.sketch.size(); i++)  { 
			if(!(sketch.get(i).equals(otherDrawing.get(i))))
				return false;
		}
		//Returns true otherwise. 
		return true;
	}
	
	@Override
	public int hashCode()  {
		int hash = 0;
		for(Drawing drawing: sketch)  {
			hash += drawing.hashCode();
		}
		return hash;
	}


	/**
	 * This returns the list Drawings that the sketch is composed of.
	 * This is used for testing equality.  
	 */
	public ArrayList<Drawing> getDrawings()  {
		return this.sketch;
	}

}
