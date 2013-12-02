package ADT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

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
	 * 
	 */
	public void clear()  {
		this.color = new Color(0,0,0,0); //Sets the color to be transparent. 
	}

	/**
	 * 
	 */
	public Image getImage(Image background) {
		Graphics2D g = (Graphics2D) background.getGraphics();
		g.setColor(color);
		g.setStroke(new BasicStroke(thickness));
		g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);	
		return background;
	}
}

class EmptyStroke extends Stroke {

	public EmptyStroke(Point startPoint, Point endPoint, float pixels) {
		super(startPoint, endPoint, new Color(0,0,0,0), pixels);
	}

}
