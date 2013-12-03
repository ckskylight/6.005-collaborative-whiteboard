package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class WhiteboardGUI extends JFrame {
	
	// ----- OBJECTS TO BE USED IN THE GUI -----
	
	// Sidebar
	// http://stackoverflow.com/questions/2158/creating-a-custom-button-in-java
	private final CustomButton clearButton;
	private final CustomButton drawButton;
	private final CustomButton eraseButton;
	
	// Color picker
	private final JTextArea colorTextBox;
	// add buttons of different default colors to the colorPanel
	
	// Stroke weight picker
	private final JComboBox weightDropdown;
	
	// Main canvas
	private final JLabel canvas;
	
	// JPanels
	private final JPanel topPanel;
	private final JPanel mainPanel;
	private final JPanel buttonsPanel;
	private final JPanel bottomPanel;

	
	
	// ------- CONSTRUCTOR --------
	public WhiteboardGUI() {
		
		
		
		// ----- INITIALIZE GUI ELEMENTS ------
		
		// *Note: Initializing everything with paintbrush temporarily 
		drawButton = new CustomButton(loadImage("src/GUI/images/paintbrush.png"), WhiteBoardGUIConstants.SIDEBAR_WIDTH);
		clearButton = new CustomButton(loadImage("src/GUI/images/paintbrush.png"), WhiteBoardGUIConstants.SIDEBAR_WIDTH);
		eraseButton = new CustomButton(loadImage("src/GUI/images/paintbrush.png"), WhiteBoardGUIConstants.SIDEBAR_WIDTH);
		
		colorTextBox = new JTextArea();
		weightDropdown = new JComboBox();
		
		topPanel = new JPanel();
		mainPanel = new JPanel();
		buttonsPanel = new JPanel();
		bottomPanel = new JPanel();
		
		canvas = new JLabel();

		
		// ----- PUT GUI LAYOUT TOGETHER ----- 
		
		// create the canvas
		ImageIcon canvasIcon = new ImageIcon(loadImage("src/GUI/images/800x600.gif"));
		canvas.setIcon(canvasIcon);
		
		// Assemble the main panel
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, 1));
		
		buttonsPanel.add(drawButton);
		buttonsPanel.add(clearButton);
		buttonsPanel.setBackground(Color.BLUE);
		buttonsPanel.setSize(WhiteBoardGUIConstants.SIDEBAR_WIDTH, WhiteBoardGUIConstants.CANVAS_HEIGHT);
		
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.setBackground(Color.YELLOW);
		mainPanel.add(buttonsPanel);
		mainPanel.add(canvas);
		
		
		colorTextBox.setPreferredSize(new Dimension(100, 20));
		bottomPanel.setBackground(Color.GREEN);
		bottomPanel.setAlignmentX(RIGHT_ALIGNMENT);
		bottomPanel.add(colorTextBox);
		bottomPanel.add(weightDropdown);
		
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
	
	
	// ------- BRUSH CONTROLS -------
	
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
