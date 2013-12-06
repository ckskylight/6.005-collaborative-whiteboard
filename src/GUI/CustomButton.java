package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;

public class CustomButton extends JComponent implements MouseListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	private Image icon;
	private int height;
	private int width;
	private Brush brush;
	private String action;
	private WhiteboardGUI whiteboard;
	
	public CustomButton(Brush brush, String action, WhiteboardGUI whiteboard, Image icon) {
		super();
		enableInputMethods(true);
		addMouseListener(this);
		this.icon = icon;
		height = icon.getHeight(null);
		width = icon.getWidth(null);
		this.brush = brush;
		this.action = action;
		this.whiteboard = whiteboard;
	}
	
	/**
	 * 
	 * @param brush
	 * @param action one of: "draw", "erase", "clear"
	 * @param icon
	 * @param dimension
	 */
	public CustomButton(Brush brush, String action, WhiteboardGUI whiteboard, Image icon, int dimension) {
		super();
		enableInputMethods(true);
		addMouseListener(this);
		this.icon = icon;
		height = dimension;
		width = dimension;
		this.brush = brush;
		this.action = action;
		this.whiteboard = whiteboard;
	}
	
	public CustomButton(Brush brush, String action, WhiteboardGUI whiteboard, Image icon, int h, int w) {
		super();
		enableInputMethods(true);
		addMouseListener(this);
		this.icon = icon;
		height = h;
		width = w;
		//this.brush = brush;
		this.action = action;
		this.whiteboard = whiteboard;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(height, width);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		// Cast to Graphics2D
		Graphics2D graphics = (Graphics2D) g;
		
		// Draw the icon
		graphics.drawImage(icon, 0, 0, height, width, null);
		
	}
	
	public void addActionListener(ActionListener listener)
    {
        listeners.add(listener);
    }
	
	private void notifyListeners(MouseEvent e)
    {
        ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, new String(), e.getWhen(), e.getModifiers());
        synchronized(listeners)
        {
            for (int i = 0; i < listeners.size(); i++)
            {
                ActionListener tmp = listeners.get(i);
                tmp.actionPerformed(evt);
            }
        }
    }
	
	
	// --- OTHER THINGS ---

	@Override
	public void mouseClicked(MouseEvent e) {
		if (action.equals("draw")) {
			brush.setColor( new Color(0,0,0) );
			System.out.println("draw");
		}
		else if (action.equals("erase")) {
			brush.setColor( new Color(255,255,255) );
			System.out.println("erase");
		}
		else if (action.equals("clear")) {
			whiteboard.clear();
			System.out.println("clear");

		}
		
	}
	
	//Ignore all other mouse events
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
