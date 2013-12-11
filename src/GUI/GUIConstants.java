package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

public class GUIConstants {

	public static final int CANVAS_WIDTH = 800;
	public static final int CANVAS_HEIGHT = 600;
	public static final int SIDEBAR_WIDTH = 40;
	public static final Color DEFAULT_COLOR = Color.BLACK;
	public static final float DEFAULT_BRUSH_THICKNESS = 5;
	public static final String[] WEIGHT_CHOICES = new String[] {"1","2","3","4","5","6","8","10","12","14","16","18","20"};
	public static final Color HONEYDEW = Color.decode("#F0FFF0");
	public static final Color MISTYROSE = Color.decode("#FFE4E1");
	public static final HashMap<Integer, String> EMPTY_BOARDS = new HashMap<Integer,String>();
	public static final Dimension WINDOW_DIMENSIONS = new Dimension(850, 750);
}
