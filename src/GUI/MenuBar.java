package GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.util.Map;

import javax.swing.AbstractButton;
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
	
	Map<Integer,String> boardList;
	
	public MenuBar(Map<Integer,String> boardList, PrintWriter out) {
		
	}
	
	public static JMenuBar createMenuBar(Map<Integer,String> boardList, PrintWriter out) {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;

        // --------- MENU BAR -----------
        menuBar = new JMenuBar();

        // --------- FIRST MENU ---------
        menu = new JMenu("Whiteboard");
        menuBar.add(menu);

        menuItem = new JMenuItem("Create new Whiteboard", KeyEvent.VK_T);
        menuItem.addActionListener(new MenuListener());
        menu.add(menuItem);

        menuItem = new JMenuItem("Rename Whiteboard");
        menuItem.addActionListener(new MenuListener());
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem("About");
        menuItem.addActionListener(new MenuListener());
        menu.add(menuItem);

        // -------- SECOND MENU ----------
        menu = new JMenu("Join/Leave");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Leave Current Whiteboard");
        menuItem.addActionListener(new MenuListener());
        menu.add(menuItem);
        
        submenu = new JMenu("Join Whiteboard");
        menuItem = new JMenuItem("Whiteboard1");
        menuItem.addActionListener(new MenuListener());
        submenu.add(menuItem);
        menuItem = new JMenuItem("Whiteboard2");
        menuItem.addActionListener(new MenuListener());
        submenu.add(menuItem);
        menuItem = new JMenuItem("Whiteboard3");
        menuItem.addActionListener(new MenuListener());
        submenu.add(menuItem);
        menu.add(submenu);

        return menuBar;
    }
}

class MenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = ((JMenuItem) e.getSource()).getText();
		if (command.equals("Rename Whiteboard")) {
			
		}
		else if (command.equals("Create new Whiteboard")) {
			
		}
		else if (command.equals("About")) {
			
		}
		else if (command.equals("Leave Current Whiteboard")) {
			
		}
		else /*if a whiteboard name is chosen*/ {
			
		}
	}
	
}
