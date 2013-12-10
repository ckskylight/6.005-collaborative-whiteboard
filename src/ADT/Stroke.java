package ADT;

import gson.src.main.java.com.google.gson.Gson;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

/**
 * Stroke represents an atomic drawing. Strokes can be composed into 
 * Sketches and serve as a method of passing updates to the server when
 * free hand drawing. 
 * 
 * Abstraction Function: This maps straight lines to an Image representing that straight 
 * line.
 * 
 * Representation Invariant:
 * Thickness must be >= 1
 * 
 * Thread Safety Argument:
 * Since this class is only mutated from a single thread, (server when clear is called)
 * this class is thread-safe. 
 *
 */
public class Stroke implements Drawing {

	private Color color;
	private final float thickness;
	private final Point startPoint;
	private final Point endPoint;


	public Stroke(Point startPoint, Point endPoint, Color c, float pixels){
		if(pixels < 1)  {
			throw new IllegalArgumentException("Thickness must be minimum 1 pixel wide!");
		}

		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.color = c;
		this.thickness = pixels;
	}

	/**
	 * Erases all drawings from the Whiteboard. 
	 * This is done by the setting the color of the stroke to be
	 * Transparent. 
	 */
	public void clear()  {
		this.color = new Color(0,0,0,0); //Sets the color to be transparent. 

	}


	/**
	 *   Imprints this Drawing on the Image passed in.
	 *   This method mutates background. 
	 *   Returns the mutated background. 
	 */ 	 
	public Image getImage(Image background) {
		Graphics2D g = (Graphics2D) background.getGraphics();
		g.setColor(color);
		g.setStroke(new BasicStroke(thickness));
		g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);	
		return background;
	}
	
	/**
	 * The following methods are observers for Strokes instance variables.
	 * These are only used to test for equality. 
	 */
	public Color getColor()  {
		return this.color;
	}
	
	public Point getStartPoint()  {
		return this.startPoint;
	}
	
	public Point getEndPoint()  {
		return this.endPoint;
	}
	
	public float getThickness()  {
		return this.thickness;
	}
	
	/**
	 * Returns true if two Strokes are behaviorally equal. False otherwise. 
	 */
	@Override
	public boolean equals(Object other)  {
		if(!(other instanceof Stroke))
			return false;
		Stroke otherStroke = (Stroke) other;
		return getColor().equals(otherStroke.getColor()) && getThickness() == otherStroke.getThickness()
				&&  getStartPoint().equals(otherStroke.getStartPoint()) && getEndPoint().equals(otherStroke.getEndPoint());
	}
	
	@Override
	public int hashCode()  {
		return color.hashCode() + startPoint.hashCode() + endPoint.hashCode() + (int) thickness;
	}
	
	@Override
	public String getJSON() {
		Gson gson = new Gson();
		return gson.toJson(this) ;
	}
}

