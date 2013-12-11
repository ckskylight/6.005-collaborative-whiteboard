package GUI;

import gson.src.main.java.com.google.gson.Gson;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintWriter;
import javax.swing.JPanel;

import ADT.Sketch;
import ADT.Stroke;

/**
 * Canvas represents a drawing surface that allows the user to draw
 * on it freehand, with the mouse. This class writes to server Strokes
 * as the user draws on the canvas (nothing is displayed until the server 
 * responds).
 */
public class Canvas extends JPanel {
	private static final long serialVersionUID = 1L;
	// image where the user's drawing is stored
	private Image drawingBuffer;
	// Instantiate the ADT
	private Sketch sketch;
	// Instantiate the brush
	private Brush brush;
	private Gson gson;
	private final int id; //ID of the white board Canvas displays.

	// Out to server
	private final PrintWriter out;




	/**
	 * Make a canvas.
	 * @param width width in pixels
	 * @param height height in pixels
	 * @param brush, specifies the thickness and color of what it drawn 
	 * on the canvas
	 * @param board, represents the contents of the canvas. 
	 */
	public Canvas(int width, int height, Brush brush, Sketch board, PrintWriter out, int id) {
		this.brush = brush;
		this.setPreferredSize(new Dimension(width, height));
		this.sketch = board;
		this.out = out;
		this.gson = new Gson();
		this.id = id;
		addDrawingController();
		// note: we can't call makeDrawingBuffer here, because it only
		// works *after* this canvas has been added to a window.  Have to
		// wait until paintComponent() is first called.
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * This method is called during in all repaint calls.
	 * Draws the image of the current sketch over a white background.
	 */
	@Override
	public void paintComponent(Graphics g) {
		makeDrawingBuffer();
		g.drawImage(sketch.getImage(drawingBuffer), 0, 0, null);
	}

	/**
	 * Make the drawing buffer and fill it with white.
	 */
	private void makeDrawingBuffer() {
		drawingBuffer = createImage(getWidth(), getHeight());
		fillWithWhite();

	}

	/**
	 * Make the drawing buffer entirely white.
	 */
	private void fillWithWhite() {
		final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0,  0,  getWidth(), getHeight());

		// IMPORTANT!  every time we draw on the internal drawing buffer, we
		// have to notify Swing to repaint this component on the screen.
		this.repaint();
	}


	/**
	 * Add the mouse listener that supports the user's freehand drawing.
	 */
	private void addDrawingController() {
		DrawingController controller = new DrawingController();
		addMouseListener(controller);
		addMouseMotionListener(controller);
	}

	/**
	 * Sets the Sketch of this canvas to  @param newSketch, and repaints
	 */
	public void setSketch(Sketch newSketch) {
		sketch = newSketch;
		repaint();
	}
	
	/**
	 * Clears the Sketch of this canvas.
	 * This is called when the client receives a clearBoard message.
	 */
	public void clear() {
		sketch.clear();
		repaint();
	}

	/**
	 * DrawingController handles the user's freehand drawing.
	 */
	private class DrawingController implements MouseListener, MouseMotionListener {
		// store the coordinates of the last mouse event, so we can
		// draw a line segment from that last point to the point of the next mouse event.
		private int lastX, lastY; 

		/*
		 * When mouse button is pressed down, start drawing.
		 */
		public void mousePressed(MouseEvent e) {
			lastX = e.getX();
			lastY = e.getY();
		}

		/**
		 * When mouse moves while a button is pressed down,
		 * create a Stroke and send it to the server.
		 */
		public void mouseDragged(MouseEvent e) {
			
			int x = e.getX();
			int y = e.getY();

			// Here store info in the ADT
			Point startPoint = new Point(lastX, lastY);
			Point endPoint = new Point(x, y);
 				Stroke update = new Stroke(startPoint, endPoint, brush.getColor(), brush.getThickness());
				String updateJSon = gson.toJson(update);
				String updateString = id + " addDrawing " + updateJSon;
				//Send this update to the server
				out.println(updateString);
				out.flush();

				lastX = x;
				lastY = y;
				repaint();
			


		}

		// Ignore all these other mouse events.
		public void mouseMoved(MouseEvent e) {
		}
		public void mouseClicked(MouseEvent e) { }
		public void mouseReleased(MouseEvent e) { }
		public void mouseEntered(MouseEvent e) { }
		public void mouseExited(MouseEvent e) { }
	}

	/**
	 * Connect @param update to the Canvas's sketch.
	 * This represents the Server sending an update
	 * to the Canvas.
	 */
	public void connectStroke(Stroke update) {
		sketch.connect(update);
	}

}
