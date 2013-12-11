package GUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

public class ColorSquare extends JComponent implements MouseListener {
	
	/**
	 * This class represents the colored squares in the color picker interface..
	 */
	private static final long serialVersionUID = 1L;
	Color color;
	Brush brush;

	
	public ColorSquare(Color color, Brush brush) {
		super();
		this.addMouseListener(this);
		this.color = color;
		this.brush = brush;
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	
	// --------- GRAPHICS ---------
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		Dimension arc = new Dimension((int)Math.sqrt(GUIConstants.COLORSQUARE_WIDTH),
				(int)Math.sqrt(GUIConstants.COLORSQUARE_HEIGHT));
		graphics.setColor(color);
		graphics.fillRoundRect(0, 0, GUIConstants.COLORSQUARE_WIDTH, GUIConstants.COLORSQUARE_HEIGHT, 
				arc.width, arc.height);
	}
	
	// ------------ SIZE INFORMATION -------------
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(GUIConstants.COLORSQUARE_HEIGHT, GUIConstants.COLORSQUARE_WIDTH);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	// ---------------- LISTENERS -------------

	@Override
	public void mouseClicked(MouseEvent e) {
		brush.setColor(color);
	}

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
}
