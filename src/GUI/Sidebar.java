package GUI;

import java.awt.Image;
import javax.swing.JPanel;

/**
 * 
 * Sidebar is a class that is the home of the three buttons: clear, erase and draw.
 * It allows for the GUI to inactivate Draw when Erase is activated and vice versa
 * and serves to make the system more modular.
 *
 */
public class Sidebar {
	CustomButton[] buttons;
	WhiteboardGUI whiteboard;
	
	public Sidebar(WhiteboardGUI whiteboard) {
		this.whiteboard = whiteboard;
		
		// assemble the buttons
		Image drawActive = whiteboard.loadImage("src/GUI/images/DrawActive.png");
		Image drawInactive = whiteboard.loadImage("src/GUI/images/DrawInactive.png");
		Image drawClicked = whiteboard.loadImage("src/GUI/images/DrawClicked.png");
		
		Image eraseActive = whiteboard.loadImage("src/GUI/images/EraseActive.png");
		Image eraseInactive = whiteboard.loadImage("src/GUI/images/EraseInactive.png");
		Image eraseClicked = whiteboard.loadImage("src/GUI/images/EraseClicked.png");
		
		Image clearClicked = whiteboard.loadImage("src/GUI/images/ClearActive.png");
		Image clearInactive = whiteboard.loadImage("src/GUI/images/ClearInactive.png");
		
		CustomButton drawButton = new CustomButton(whiteboard.getBrush(), "draw", whiteboard, this, drawActive, drawInactive, drawClicked, GUIConstants.SIDEBAR_WIDTH);
		CustomButton clearButton = new CustomButton(whiteboard.getBrush(), "clear", whiteboard, this, clearInactive, clearInactive, clearClicked, GUIConstants.SIDEBAR_WIDTH);
		CustomButton eraseButton = new CustomButton(whiteboard.getBrush(), "erase", whiteboard, this, eraseActive, eraseInactive, eraseClicked, GUIConstants.SIDEBAR_WIDTH);
		
		buttons = new CustomButton[] {drawButton, eraseButton, clearButton};
	}
	
	public void addSidebar(JPanel container) {
		for (CustomButton button : buttons) {
			container.add(button);
		}
	}
	
	public void inactivateExcept(String currentButtonAction) {
		for (CustomButton button : buttons) {
			if (button.getAction() != currentButtonAction) {
				button.setStatus(false);
			}
		}
	}
	
}
