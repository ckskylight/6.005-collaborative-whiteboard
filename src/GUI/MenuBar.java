package GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class MenuBar {
	

	public static JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;

        // --------- MENU BAR -----------
        menuBar = new JMenuBar();

        // --------- FIRST MENU ---------
        menu = new JMenu("Whiteboard");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);

        menuItem = new JMenuItem("Create new Whiteboard", KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem("Rename Whiteboard");
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem("About");
        menu.add(menuItem);

        // -------- SECOND MENU ----------
        menu = new JMenu("Join/Leave");
        menu.setMnemonic(KeyEvent.VK_N);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Leave Current Whiteboard");
        menu.add(menuItem);
        
        submenu = new JMenu("Join Whiteboard");
        menuItem = new JMenuItem("Whiteboard1");
        submenu.add(menuItem);
        menuItem = new JMenuItem("Whiteboard2");
        submenu.add(menuItem);
        menuItem = new JMenuItem("Whiteboard3");
        submenu.add(menuItem);
        menu.add(submenu);

        return menuBar;
    }


    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MenuLookDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
