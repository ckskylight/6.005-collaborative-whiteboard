package GUI;

import java.awt.Color;
import java.awt.Cursor;
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
	private Image activeIcon;
	private Image inactiveIcon;
	private Image clickedIcon;
	private boolean isMouseDown = false;
	private boolean isActive;
	private int height;
	private int width;
	private Brush brush;
	private String action;
	private WhiteboardGUI whiteboard;
	private Sidebar sidebar;
	
	
	/**
	 * precondition: activeIcon, inactiveIcon and clickedIcon all have to be the same size
	 * @param brush
	 * @param action one of: "draw", "erase", "clear"
	 * @param icon
	 * @param dimension
	 */
	public CustomButton(Brush brush, String action, WhiteboardGUI whiteboard, Sidebar sidebar, Image activeIcon, Image inactiveIcon, Image clickedIcon, int dimension) {
		super();
		enableInputMethods(true);
		addMouseListener(this);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.activeIcon = activeIcon;
		this.inactiveIcon = inactiveIcon;
		this.clickedIcon = clickedIcon;
		height = dimension;
		width = dimension;
		this.brush = brush;
		this.action = action;
		this.whiteboard = whiteboard;
		this.sidebar = sidebar;
		if (action.equals("draw")) {
			isActive = true;
		}
		else {
			isActive = false;
		}
	}
	
	
	// --------- SIZE INFORMATION ----------
	
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
	
	
	// --------- GRAPHICS ----------
	
	@Override
	public void paintComponent(Graphics g) {
		
		// Cast to Graphics2D
		Graphics2D graphics = (Graphics2D) g;
		
		// Choose the necessary icon
		Image currentIcon = null;
		if (isMouseDown) {
			currentIcon = clickedIcon;
		}
		else if (isActive) {
			currentIcon = activeIcon;
		}
		else if (!isActive) {
			currentIcon = inactiveIcon;
		}
		graphics.drawImage(currentIcon, 0, 0, height, width, null);
		
	}
	
	// ---------- LISTENERS ---------
	
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
	

	@Override
	public void mouseClicked(MouseEvent e) {
		if (action.equals("draw")) {
			brush.setColor( new Color(0,0,0) );
		}
		else if (action.equals("erase")) {
			brush.setColor( new Color(255,255,255) );
		}
		else if (action.equals("clear")) {
			whiteboard.clear();
		}
		
	}
	
	//Ignore all other mouse events
	@Override
	public void mousePressed(MouseEvent e) {
		isMouseDown = true;
		this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isMouseDown = false;
		// If it's a toggle button (not "clear") switch between active and inactive
		if (!action.equals("clear")) {
			sidebar.inactivateExcept(action);
			if (!isActive) {
				isActive = !isActive;
			}
		}
		this.repaint();
	}


	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	
	
	// ----------- GETTERS / SETTERS -----------
	public void setStatus(boolean active) {
		isActive = active;
		this.repaint();
	}
	
	public String getAction() {
		return action;
	}
	
}
