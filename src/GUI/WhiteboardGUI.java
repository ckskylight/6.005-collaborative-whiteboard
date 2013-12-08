package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import ADT.Sketch;

public class WhiteboardGUI extends JFrame {
	
	// ----- OBJECTS TO BE USED IN THE GUI -----
	
	// Sidebar
	// http://stackoverflow.com/questions/2158/creating-a-custom-button-in-java
	private CustomButton clearButton;
	private CustomButton drawButton;
	private CustomButton eraseButton;
	
	// Color picker
	private final JTextField colorTextBox;
	// add buttons of different default colors to the colorPanel
	
	// Stroke weight picker
	private final JComboBox weightDropdown;
	private String[] weightChoices = new String[] {"1","2","4","6","10","20"};
	
	// Main canvas
	private final Canvas canvas;
	
	// JPanels
	private final JPanel topPanel;
	private final JPanel mainPanel;
	private final JPanel buttonsPanel;
	private final JPanel bottomPanel;

	// Brush
	Brush brush = new Brush();
	
	// Model
	Sketch board = new Sketch();
	
	
	// ------- CONSTRUCTOR --------
	public WhiteboardGUI() {
		
		// ----- INITIALIZE GUI ELEMENTS ------

		
		colorTextBox = new JTextField();
		weightDropdown = new JComboBox(weightChoices);
		
		topPanel = new JPanel();
		mainPanel = new JPanel();
		buttonsPanel = new JPanel();
		bottomPanel = new JPanel();
		
		canvas = new Canvas(GUIConstants.CANVAS_WIDTH, GUIConstants.CANVAS_HEIGHT, brush, board);
		// Add border to canvas
		Border blackline = BorderFactory.createLineBorder(Color.black);
		canvas.setBorder(blackline);

		
		// ----- PUT GUI LAYOUT TOGETHER ----- 
		
		// Set up the weight combo box
		weightDropdown.setSelectedIndex(1);
		weightDropdown.addActionListener(new WeightListener());
		
		// Set up the hex color box
		colorTextBox.setPreferredSize(new Dimension(100, 20));
		colorTextBox.addActionListener(new ColorListener());
		
		
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

		bottomPanel.add(colorTextBox);
		bottomPanel.add(weightDropdown);
		bottomPanel.setBackground(Color.decode("#101010"));
		
		// Create and assemble colour palette
		ColorSquare white = new ColorSquare(Color.decode("#FFFFFF"), brush);
		ColorSquare lightgray = new ColorSquare(Color.decode("#D3D3D3"), brush);
		ColorSquare gray = new ColorSquare(Color.decode("#808080"), brush);
		ColorSquare black = new ColorSquare(Color.decode("#000000"), brush);
		ColorSquare yellow = new ColorSquare(Color.decode("#FFFF00"), brush);
		ColorSquare blue = new ColorSquare(Color.decode("#0000FF"), brush);
		ColorSquare cyan = new ColorSquare(Color.decode("#00FFFF"), brush);
		ColorSquare green = new ColorSquare(Color.decode("#008000"), brush);
		ColorSquare lawngreen = new ColorSquare(Color.decode("#7CFC00"), brush);
		ColorSquare red = new ColorSquare(Color.decode("#FF0000"), brush);
		ColorSquare purple = new ColorSquare(Color.decode("#800080"), brush);
		ColorSquare saddlebrown = new ColorSquare(Color.decode("#8B4513"), brush);
		ColorSquare darkorange = new ColorSquare(Color.decode("#FF8C00"), brush);
		ColorSquare teal = new ColorSquare(Color.decode("#008080"), brush);
		ColorSquare goldenrod = new ColorSquare(Color.decode("#DAA520"), brush);

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

		// Add components to the JFrame
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), 1));
		this.getContentPane().add(topPanel);
		this.getContentPane().add(mainPanel);
		this.getContentPane().add(bottomPanel);
		
	}
	
	// ------- HELPER METHODS --------
	

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
		System.out.println("Board size pre: " + board.getSketchSize());
		board.clear();
	}
	
	
	// ------- BRUSH CONTROLS -------
	
	// ------- LISTENERS -------
	class WeightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			float newThickness = Float.parseFloat((String)((JComboBox) e.getSource()).getSelectedItem());
			brush.setThickness(newThickness);
		}
	}
	
	class ColorListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String newColor = e.getActionCommand();
			brush.setColor(Color.decode(newColor));
		}
		
	}
	
	
	// ------- MAIN METHOD ---------
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				// Initialize the GUI
				WhiteboardGUI main = new WhiteboardGUI();
				
				main.pack();
				main.setVisible(true);
			}
		});
	}
	
}
