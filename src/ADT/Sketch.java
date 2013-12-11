package ADT;

import gson.src.main.java.com.google.gson.Gson;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Sketch is represents top level of our ADT, it is a composition of 
 * other drawings. 
 * 
 * Abstraction Function: 
 * This maps free-hand drawings to an ArrayList of Drawings (in practice Strokes).
 *  
 * Thread Safety Argument:
 * This class is thread-safe through the monitor pattern. Only one thread may mutate or observe the Sketch at a time.
 * This allows us to defend ourselves from unwanted interleaving. 
 *
 */
public class Sketch implements Drawing, Serializable {


	private static final long serialVersionUID = 1L;
	private ArrayList<Drawing> sketch;


	public Sketch()  {
		sketch = new ArrayList<Drawing>();
	}
	
	public Sketch(ArrayList<Drawing> sketch) {
		this.sketch = sketch;
	}


	/**
	 *   This adds a new drawing to the Sketch.
	 *   <Sketch, Drawing> --> <Sketch>
	 *   
	 **/ 
	public synchronized void connect(Drawing drawing)  {
		sketch.add(drawing);
	}


	/**
	 *   Erase the entire drawing.
	 **/
	public synchronized void clear() {
		sketch.clear();
	}

	/**
	 * @return the size of the sketch (number of strokes)
	 */
	public synchronized int getSketchSize() {
		return sketch.size();
	}

	/**
	 *   Returns associated Java Graphics image associated with this
	 *   Drawing.  This represents the bridge from the ADT to the GUI.
	 *   Prerequisite: background must be a blank canvas (white canvas).
	 **/ 
	public synchronized Image getImage(Image background) {
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
	public synchronized boolean equals(Object other)  {
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
	public synchronized int hashCode()  {
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
	public synchronized ArrayList<Drawing> getDrawings()  {
		return this.sketch;
	}


	/**
	 * @return JSON String representing this instance.
	 */
	public synchronized String getJSON() {
		Gson gson = new Gson();
		return gson.toJson(this) ;
	}

}
