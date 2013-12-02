package GUI;

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
	private final JButton clearButton;
	private final JButton drawButton;
	private final JButton eraseButton;
	
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
