package GUI;

import java.awt.Color;

/**
 * This class represents the user's brush as they are drawing
 * It tracks the users brush color and stroke.
 * It can also act an eraser (by setting the color to white). 
 *
 */
public class Brush {
	
	private Color color = GUIConstants.DEFAULT_COLOR;
	private float thickness = GUIConstants.DEFAULT_BRUSH_THICKNESS;
	
	public Brush()  {
	}
	/**
	 * Set's the brush's color to  @param c
	 */
	public void setColor(Color c)  {
		this.color = c;
	}
	
	/**
	 * @return Color of brush
	 */
	public Color getColor()  {
		return this.color;
	}
	
	/**
	 * Set the thickness of the brush to @param t
	 */
	public void setThickness(float t)  {
		this.thickness = t;
	}
	
	/**
	 * @return Thickness of brush
	 */
	public float getThickness()  {
		return this.thickness;
	}

}
