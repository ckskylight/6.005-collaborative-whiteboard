package GUI;
import gson.src.main.java.com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import ADT.Drawing;
import ADT.Sketch;

public class WhiteboardWindow extends JFrame {
	
	//Server Elements
	private final int SERVER_PORT = 4444;
	private final String host = "127.0.0.1";
	private final Socket server;
	private static PrintWriter serverOut;
	private Gson gson;
	private BufferedReader serverIn;
	private Map<Integer, String> boardNames;
	private UpdateWerker listner;
	
	private static WhiteboardGUI[] whiteboards;
	// The tabbed pane that houses all tabs
	private final JTabbedPane tabbedPane;
	

	// Menu bar
	private final MenuBar menuBar = new MenuBar(GUIConstants.EMPTY_BOARDS, serverOut);
	
	public WhiteboardWindow(WhiteboardGUI[] whiteboards) throws IOException {
		this.whiteboards = whiteboards;
		tabbedPane = new JTabbedPane();
		
		//Connect to Server
		server = new Socket();
		server.connect(new InetSocketAddress(SERVER_PORT));
		gson = new Gson();
		listner = new UpdateWerker(server);
		listner.execute();
		serverOut = new PrintWriter( server.getOutputStream(), true);
		serverOut.println("getBoardList");


	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				WhiteboardWindow main;
				try {
					main = new WhiteboardWindow(new WhiteboardGUI[] {new WhiteboardGUI(serverOut), new WhiteboardGUI(serverOut)});
					main.assembleJFrame();
					
					main.pack();
					main.setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}

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

        this.setJMenuBar(menuBar.createMenuBar());
	}
	
	public void setBoardList(Map<Integer,String> newBoardList) {
		menuBar.setBoardList(newBoardList);
		
	}
	/**
	 * Takes in a message from the board, as specified in the Server Protocol,
	 * and updates the GUI accordingly. 
	 * @param string, message from the server. 
	 */
	private void parseServerMessage(String string) {
		if(string.contains("BOARD "))  {
			String boardString = string.substring(16); //TODO:Magic number
			Drawing sketch = gson.fromJson(boardString, Sketch.class);
			

		}else  {
			if(string.contains("BLIST"))  {
				String boardListString = string.substring(6); //TODO:Magic number
				Map boardList = gson.fromJson(boardListString, Map.class);
				this.setBoardList(boardList);

			}
			else  { //In this cases, no  changes are necessary. 
				if(string.contains("LEAVE"))  {
					return;
				}
				else  {
					if(string.contains("UPDATE ACK"))  {
						return;
					} //This should take care of all cases.
					else  {
						throw new RuntimeException("Unrecognized Server Message!");
					}
					
				}
			}
		}
		
	}

	//
	public class UpdateWerker  extends SwingWorker<String, String>{

		private final Socket server;
		private BufferedReader serverIn;
		/**
		 * This Swing worker handel's the receiving and 
		 * processing of messages from the server. It is always waiting for a new 
		 * message from the server. Once it processes a new message, it creates the next worker
		 * worker to continue listening to the server. 
		 * @throws IOException 
		 */
		public UpdateWerker(Socket serverConnection)  {
			server = serverConnection;
			try {
				serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		@Override
		protected String doInBackground() throws IOException {
			String response = serverIn.readLine();
			return response;

		}

		@Override
		protected void done()  {

			try {
				parseServerMessage(get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			UpdateWerker listner;
			listner = new UpdateWerker(server);
			listner.execute();

		}
	}


}
