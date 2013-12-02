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
		this.color = Color.WHITE;
	}

	/**
	 * 
	 */
	public void updateImage(Image background) {
		Graphics2D g = (Graphics2D) background.getGraphics();
		g.setColor(color);
		g.setStroke(new BasicStroke(thickness));
		g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);	
	}
}

class EmptyStroke implements Drawing{
	public EmptyStroke()  {
	}

	public void clear() {}

	public void updateImage(Image background) {}
}
