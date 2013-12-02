package ADT;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

public class Stroke implements Drawing {
	
	private Color color;
	private double thickness;
	private Point startPoint;
	private Point endPoint;

	
	public Stroke(Point startPoint, Point endPoint, Color c, double pixels){
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.color = c;
		this.thickness = pixels;
	}
	
	public void clear()  {
		
	}
	
	public Image getImage()  {
		return null;
	}
}
