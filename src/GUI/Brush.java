package GUI;

import java.awt.Color;

/**
 * This class represents the user's brush as they are drawing
 * It tracks the users brush color and stroke.
 * It can also act an eraser. 
 *
 */
public class Brush {
	
	private Color color = GUIConstants.DEFAULT_COLOR;
	private float thickness = GUIConstants.DEFAULT_BRUSH_THICKNESS;
	
	public Brush()  {
	}
	
	public void setColor(Color c)  {
		this.color = c;
	}
	
	public Color getColor()  {
		return this.color;
	}
	
	public void setThickness(float t)  {
		this.thickness = t;
	}
	
	public float getThickness()  {
		return this.thickness;
	}

}
