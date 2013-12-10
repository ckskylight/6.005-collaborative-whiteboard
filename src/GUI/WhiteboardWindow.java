package GUI;
import gson.src.main.java.com.google.gson.Gson;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import ADT.Sketch;

public class WhiteboardWindow extends JFrame {
	
	//Server Elements
	private final int SERVER_PORT = 4444;
	private final String host = "127.0.0.1";
	private final Socket server;
	private static PrintWriter serverOut;
	private Gson gson;
	private Map<Integer, String> boardNames;
	private UpdateWerker listner;
	
	private static Map<Integer, WhiteboardGUI> whiteboards;
	// The tabbed pane that houses all tabs
	private JTabbedPane tabbedPane;
	
	// Background image
	Image backgroundImage = loadImage("src/GUI/images/Background.jpg");
	
	// IPanel that has the background image that everything is painted on
	JPanel mainPanel = new IPanel(backgroundImage);
	

	// Menu bar
	private final MenuBar menuBar;
	
	public WhiteboardWindow() throws IOException {
		this.whiteboards = new HashMap<Integer, WhiteboardGUI>();
		tabbedPane = new JTabbedPane();
		this.setPreferredSize(GUIConstants.WINDOW_DIMENSIONS);
		
		//Connect to Server
		server = new Socket();
		server.connect(new InetSocketAddress(SERVER_PORT));
		gson = new Gson();
		serverOut = new PrintWriter( server.getOutputStream(), true);
		serverOut.println("getBoardList");
		BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
		String boardListString = serverIn.readLine().substring(6); //TDO: magic number
		Map<Integer, String> boardList = gson.fromJson(boardListString, Map.class);
		listner = new UpdateWerker(server);
		
		this.whiteboards = new HashMap<Integer, WhiteboardGUI>();
		tabbedPane = new JTabbedPane();
		menuBar = new MenuBar(GUIConstants.EMPTY_BOARDS, serverOut, whiteboards);
		this.setBoardList(boardList);
		listner.execute();

	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				WhiteboardWindow main;
				try {
					main = new WhiteboardWindow();
					main.assembleJFrame();
					
					main.pack();
					main.setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
	}
	
	public Image loadImage(String filePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	private void assembleJFrame() {
		for (Integer id : whiteboards.keySet()) {
	        tabbedPane.addTab(boardNames.get(id), whiteboards.get(id));
		}
		
		// Add the entire tabbed pane to the jframe
        mainPanel.add(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Add the menu bar
        
        this.add(mainPanel);
        this.setJMenuBar(menuBar.createMenuBar());
	}
	
	public void setBoardList(Map<Integer,String> newBoardList) {
		this.boardNames = newBoardList;
		menuBar.setBoardList(boardNames);
		
	}
	/**
	 * Takes in a message from the board, as specified in the Server Protocol,
	 * and updates the GUI accordingly. 
	 * @param string, message from the server. 
	 */
	private void parseServerMessage(String string) {
		System.out.println("server message is ");
		System.out.println(string);
		System.out.println();

		if(string.contains("BOARD "))  {
			String boardString = string.substring("BOARD ".length()); //TODO:Magic number
			String sketchString = boardString.substring(6);
			int id = Integer.parseInt(boardString.substring(0, 6).trim());
			Sketch sketch = gson.fromJson(sketchString, Sketch.class);
			this.whiteboards.get(new Integer(id)).setSketch(sketch);
			

		}else  {
			if(string.contains("BLIST"))  {
				String boardListString = string.substring(6); //TODO:Magic number
				@SuppressWarnings("unchecked")
				Map<Integer, String> boardList = gson.fromJson(boardListString, Map.class);
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
	public WhiteboardGUI getCurrentWhiteboard() {
		return (WhiteboardGUI) tabbedPane.getSelectedComponent();
	}

}
