package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

public class ColorSquare extends JComponent implements MouseListener {
	
	int height = 20;
	int width = 20;
	Color color;
	
	public ColorSquare(Color color) {
		this.color = color;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
