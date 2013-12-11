package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MenuBar {

	Map<Integer,String> boardNames;
	PrintWriter out;
	Map<Integer,WhiteboardGUI> whiteboards;
	int currentBoardID;

	public MenuBar(Map<Integer,String> boardList, PrintWriter out, Map<Integer,WhiteboardGUI> whiteboards, int currentBoardID) {
		this.boardNames = boardList;
		this.out = out;
		this.whiteboards = whiteboards;
		this.currentBoardID = currentBoardID;
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
			menuItem.getAccessibleContext().setAccessibleDescription(nameEntry.getKey() + "");
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
			String serverRequest;
			if (command.equals("Rename Whiteboard")) {
				String newName = JOptionPane.showInputDialog("Enter a new name");
				if (newName != null) {
					serverRequest = currentBoardID + " setBoardName ";
					serverRequest += newName; 
					out.println(serverRequest);
				}
			}
			else if (command.equals("Create new Whiteboard")) {
				String boardName = JOptionPane.showInputDialog("Enter a whiteboard name");
				if (boardName.trim().equals("")) {
					boardName = "Untitled";
				}
				else {
					boardName = boardName.trim();
				}
				serverRequest = "createBoard";
				serverRequest += " " + boardName;
				out.println(serverRequest);
			}
			else if (command.equals("About")) {
				JOptionPane.showMessageDialog(null, "Made by: \n Adam Yala, John O'Sullivan, CK Ong \n Because we're awesome", "Collaborative Whiteboard", JOptionPane.INFORMATION_MESSAGE);
			}
			else if (command.equals("Leave Current Whiteboard")) {
				serverRequest = currentBoardID + " leaveBoard";
				out.println(serverRequest);
			}
			else /*if a whiteboard name is chosen*/ {
				int selectedBoardID = Integer.parseInt(((JMenuItem) e.getSource()).getAccessibleContext().getAccessibleDescription());
				if(!whiteboards.containsKey(new Integer(selectedBoardID)))  {
					serverRequest = selectedBoardID + " joinBoard";
					whiteboards.put(new Integer(selectedBoardID), new WhiteboardGUI(out, selectedBoardID));
					out.println(serverRequest);
				}

			}
			out.flush();
		}

	}
}
