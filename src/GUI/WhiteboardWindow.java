package GUI;

import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class WhiteboardWindow extends JFrame {
	
	private static WhiteboardGUI[] whiteboards;
	
	// The tabbed pane that houses all tabs
	private final JTabbedPane tabbedPane;
	
	public WhiteboardWindow(WhiteboardGUI[] whiteboards) {
		this.whiteboards = whiteboards;
		tabbedPane = new JTabbedPane();

	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				// Initialize the GUI. Currently has nothing
				WhiteboardWindow main = new WhiteboardWindow(new WhiteboardGUI[] {new WhiteboardGUI(), new WhiteboardGUI()});
				main.assembleJFrame();
				
				main.pack();
				main.setVisible(true);
			}
		});
	}
	
	private void assembleJFrame() {
		for (WhiteboardGUI whiteboard : whiteboards) {
	        tabbedPane.addTab("Tab 1", whiteboard);
		}
		
		// Add the entire tabbed pane to the jframe
        this.add(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Add the menu bar
        this.setJMenuBar(MenuBar.createMenuBar());
	}
	

}
