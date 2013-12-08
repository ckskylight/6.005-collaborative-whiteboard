package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;

public class ColorSquare extends JComponent implements MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int height = 20;
	int width = 20;
	Color color;
	Brush brush;

	
	public ColorSquare(Color color, Brush brush) {
		super();
		this.color = color;
		this.brush = brush;
	}
	
	// --------- GRAPHICS ---------
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		Dimension arc = new Dimension((int)Math.sqrt(width), (int)Math.sqrt(height));
		graphics.setColor(color);
		graphics.fillRoundRect(0, 0, width, height, arc.width, arc.height);
	}
	
	// ------------ SIZE INFORMATION -------------
	
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
	
	// ---------------- LISTENERS -------------

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("setting color");
		brush.setColor(color);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("pressed");
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println("ENTERED");
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}