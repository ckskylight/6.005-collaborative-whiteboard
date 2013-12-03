package GUI;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
	private final JPanel colorPanel;
	// add buttons of different default colors to the colorPanel
	
	// Stroke weight picker
	private final JComboBox weightDropdown;
	
	// Main canvas
	private final JLabel canvas;
	
	
	// ------- CONSTRUCTOR --------
	public WhiteboardGUI() {
		
		
		
		// ----- INITIALIZE GUI ELEMENTS ------
		
		// *Note: Initializing everything with paintbrush temporarily 
		drawButton = new CustomButton(loadImage("src/GUI/images/paintbrush.png"), 60);
		clearButton = new CustomButton(loadImage("src/GUI/images/paintbrush.png"), 60);
		eraseButton = new CustomButton(loadImage("src/GUI/images/paintbrush.png"), 60);
		
		colorTextBox = new JTextArea();
		colorPanel = new JPanel();
		weightDropdown = new JComboBox();
		
		canvas = new JLabel();

		
		// ----- PUT GUI LAYOUT TOGETHER ----- 
		
		this.getContentPane().add(drawButton);

		
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
