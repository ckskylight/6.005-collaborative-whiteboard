package ADT;

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
 */
public class Stroke implements Drawing {

	private Color color;
	private float thickness;
	private Point startPoint;
	private Point endPoint;


	public Stroke(Point startPoint, Point endPoint, Color c, float pixels){
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
}

