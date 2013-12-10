package GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

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

	Map<Integer,String> boardNames;
	PrintWriter out;
	Map<Integer,WhiteboardGUI> whiteboards;
	int currentBoardID;

	public MenuBar(Map<Integer,String> boardList, PrintWriter out, Map<Integer,WhiteboardGUI> whiteboards) {
		this.boardNames = boardList;
		this.out = out;
		this.whiteboards = whiteboards;
	}

	public JMenuBar createMenuBar() {
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


		// Loop to list the different whiteboards in the submenu
		submenu = new JMenu("Join Whiteboard");
		for (Entry<Integer,String> nameEntry : boardNames.entrySet()) {
			// Make the board name the name of the menu item and the ID its description
			menuItem = new JMenuItem(nameEntry.getValue());
			menuItem.getAccessibleContext().setAccessibleDescription(Integer.toString(nameEntry.getKey()));
			// Add a listener to each item
			menuItem.addActionListener(new MenuListener());
			submenu.add(menuItem);
		}
		menu.add(submenu);

		return menuBar;
	}

	public void setBoardList(Map<Integer,String> newBoardList) {
		boardNames = newBoardList;

	}

	public void updateCurrentBoardId(int id)  {
		this.currentBoardID = id;
	}



	class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = ((JMenuItem) e.getSource()).getText();
			if (command.equals("Rename Whiteboard")) {
				String serverRequest = currentBoardID + " setBoardName ";
				serverRequest += "customName";
				out.println(serverRequest);
			}
			else if (command.equals("Create new Whiteboard")) {
				if( out == null)  {
					System.err.println("PRINT WRITER NULL");
				}
				out.println("createBoard");
			}
			else if (command.equals("About")) {

			}
			else if (command.equals("Leave Current Whiteboard")) {
				String serverRequest = currentBoardID + " leaveBoard";
				out.println(serverRequest);
			}
			else /*if a whiteboard name is chosen*/ {
				int selectedBoardID = Integer.parseInt(((JMenuItem) e.getSource()).getAccessibleContext().getAccessibleDescription());
				String serverRequest = selectedBoardID + " joinBoard";
				out.println(serverRequest);
				whiteboards.put(new Integer(selectedBoardID), new WhiteboardGUI(out, selectedBoardID));
				assembleJFrame()


			}
		}

	}
}
