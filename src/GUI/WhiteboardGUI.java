package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import ADT.Sketch;
import ADT.Stroke;
import GUI.Brush;
import GUI.Canvas;
import GUI.ColorSquare;
import GUI.GUIConstants;
import GUI.Sidebar;

/**
 * 
 * The WhiteboardGUI class takes care of each of the individual whiteboards
 * present in the main window. Each tab is an instance of WhiteboardGUI that 
 * houses a different board. The WhiteboardGUI also contains the interfaces
 * for drawing, erasing, choosing weight and color. 
 *
 */
public class WhiteboardGUI extends JPanel {
	
	// ----- OBJECTS TO BE USED IN THE GUI -----

	/**
	 * 
	 */
	private static final long serialVersionUID = 193838065573701861L;

	// Brush
	Brush brush = new Brush();

	// Model
	Sketch board = new Sketch();


	// Color picker
	private final JTextField colorTextBox;
	// add buttons of different default colors to the colorPanel
	private final ColorSquare white = new ColorSquare(Color.decode("#FFFFFF"), brush);
	private final ColorSquare lightgray = new ColorSquare(Color.decode("#D3D3D3"), brush);
	private final ColorSquare gray = new ColorSquare(Color.decode("#808080"), brush);
	private final ColorSquare black = new ColorSquare(Color.decode("#000000"), brush);
	private final ColorSquare yellow = new ColorSquare(Color.decode("#FFFF00"), brush);
	private final ColorSquare blue = new ColorSquare(Color.decode("#0000FF"), brush);
	private final ColorSquare cyan = new ColorSquare(Color.decode("#00FFFF"), brush);
	private final ColorSquare green = new ColorSquare(Color.decode("#008000"), brush);
	private final ColorSquare lawngreen = new ColorSquare(Color.decode("#7CFC00"), brush);
	private final ColorSquare red = new ColorSquare(Color.decode("#FF0000"), brush);
	private final ColorSquare purple = new ColorSquare(Color.decode("#800080"), brush);
	private final ColorSquare saddlebrown = new ColorSquare(Color.decode("#8B4513"), brush);
	private final ColorSquare darkorange = new ColorSquare(Color.decode("#FF8C00"), brush);
	private final ColorSquare teal = new ColorSquare(Color.decode("#008080"), brush);
	private final ColorSquare goldenrod = new ColorSquare(Color.decode("#DAA520"), brush);

	// Bottom panel labels
	private JLabel weightLabel = new JLabel("Weight:");
	// Stroke weight picker
	@SuppressWarnings("rawtypes")
	private final JComboBox weightDropdown;

	// Main canvas
	private final Canvas canvas;

	// JPanels
	@SuppressWarnings("unused")
	private final JPanel topPanel;
	private final JPanel mainPanel;
	private final JPanel buttonsPanel;
	private final JPanel bottomPanel;
	
	// Output stream to server
	private final PrintWriter out;

	//ID of the board represented by this whiteboardGUI
	private final int id;

	// ------- CONSTRUCTOR --------
	@SuppressWarnings({ "unchecked", "rawtypes" })

	public WhiteboardGUI(PrintWriter out, int id) {
		this.out = out;
		this.id = id;


		// ----- INITIALIZE GUI ELEMENTS ------


		colorTextBox = new JTextField("Hex Color");
		weightDropdown = new JComboBox(GUIConstants.WEIGHT_CHOICES);

		topPanel = new JPanel();
		mainPanel = new JPanel();
		buttonsPanel = new JPanel();
		bottomPanel = new JPanel();

		canvas = new Canvas(GUIConstants.CANVAS_WIDTH, GUIConstants.CANVAS_HEIGHT, brush, board, out, id);
		// Add border to canvas
		Border blackline = BorderFactory.createLineBorder(Color.black);
		canvas.setBorder(blackline);


		// ----- PUT GUI LAYOUT TOGETHER ----- 

		// Set up the weight combo box
		weightDropdown.setSelectedIndex(4);
		weightDropdown.addActionListener(new WeightListener());

		// Set up the hex color box
		colorTextBox.setPreferredSize(new Dimension(100, 20));
		colorTextBox.addActionListener(new ColorListener());
		colorTextBox.addMouseListener(new ColorMouseListener());
		colorTextBox.setBackground(GUIConstants.HONEYDEW);


		// Assemble the main panel
		Sidebar sidebar = new Sidebar(this);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, 1));

		sidebar.addSidebar(buttonsPanel);
		buttonsPanel.setSize(GUIConstants.SIDEBAR_WIDTH, GUIConstants.CANVAS_WIDTH);

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		buttonsPanel.setAlignmentY(TOP_ALIGNMENT);
		buttonsPanel.setBackground(Color.decode("#F5DEB3"));

		mainPanel.add(buttonsPanel);
		mainPanel.add(canvas);
		mainPanel.setBackground(Color.decode("#8B4513"));


		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		weightLabel.setForeground(Color.WHITE);
		bottomPanel.add(weightLabel);
		bottomPanel.add(weightDropdown);
		bottomPanel.add(colorTextBox);
		bottomPanel.setBackground(Color.decode("#505050"));

		bottomPanel.add(white);
		bottomPanel.add(lightgray);
		bottomPanel.add(gray);
		bottomPanel.add(black);
		bottomPanel.add(red);
		bottomPanel.add(darkorange);
		bottomPanel.add(yellow);
		bottomPanel.add(lawngreen);
		bottomPanel.add(green);
		bottomPanel.add(teal);
		bottomPanel.add(blue);
		bottomPanel.add(cyan);
		bottomPanel.add(purple);
		bottomPanel.add(saddlebrown);
		bottomPanel.add(goldenrod);

		
        
        // Currently working with single tab so add all contents to that tab
		this.setLayout(new BoxLayout(this, 1));
		//this.add(topPanel);
		this.add(mainPanel);
		this.add(bottomPanel);
		
	}

	// ------- HELPER METHODS --------


	public void setSketch(Sketch newSketch) {
		canvas.setSketch(newSketch);
	}
	
	public void connectStroke(Stroke update)  {
		canvas.connectStroke(update);
	}

	public Image loadImage(String filePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public int getWeight() {
		return Integer.parseInt(weightDropdown.toString());
	}

	public Brush getBrush() {
		return brush;
	}

	public void clear() {
		canvas.clear();
		repaint();
	}
	
	public int getID() {
		return id;
	}


	// ------- BRUSH CONTROLS -------

	// ------- LISTENERS -------
	class ColorMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			((JTextField) e.getSource()).setText("");
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

	class WeightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("rawtypes")
			float newThickness = Float.parseFloat((String)((JComboBox) e.getSource()).getSelectedItem());
			brush.setThickness(newThickness);
		}
	}

	class ColorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String newColor = e.getActionCommand();
			JTextField currentField = ((JTextField) e.getSource());
			try {
				brush.setColor(Color.decode(newColor));
				currentField.setBackground(GUIConstants.HONEYDEW);
			} catch (Exception ex) {
				currentField.setText("Invalid");
				currentField.setBackground(GUIConstants.MISTYROSE);
			}
		}

	}

	public void requestClear() {
			out.println(id + " clearBoard");
			out.flush();
	}

	
}
