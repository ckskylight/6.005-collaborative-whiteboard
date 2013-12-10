package GUI;
import gson.src.main.java.com.google.gson.Gson;
import gson.src.main.java.com.google.gson.GsonBuilder;
import gson.src.main.java.com.google.gson.InstanceCreator;
import gson.src.main.java.com.google.gson.JsonArray;
import gson.src.main.java.com.google.gson.JsonDeserializationContext;
import gson.src.main.java.com.google.gson.JsonDeserializer;
import gson.src.main.java.com.google.gson.JsonElement;
import gson.src.main.java.com.google.gson.JsonObject;
import gson.src.main.java.com.google.gson.JsonParseException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import ADT.Drawing;
import ADT.Sketch;
import ADT.Stroke;

/**
 * 
 * @author Yala
 *
 */
public class WhiteboardWindow extends JFrame {

	//Server Elements
	private final int SERVER_PORT = 4444;
	private final Socket server;
	private static PrintWriter serverOut;
	private Gson gson;
	private Gson sketchgson;
	private Map<Integer, String> boardNames;
	private UpdateWerker listner;

	//Represents the whiteboard the user has joined. 
	private static Map<Integer, WhiteboardGUI> whiteboards;

	// The tabbed pane that houses all tabs
	private JTabbedPane tabbedPane;

	// Background image
	Image backgroundImage = loadImage("src/GUI/images/BackgroundResized.jpg");

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

		//Get boardList from server and start listening for updates
		gson = new Gson();
		sketchgson = new GsonBuilder().registerTypeAdapter(Sketch.class, new SketchDeserializer()).create();
		serverOut = new PrintWriter( server.getOutputStream(), true);
		serverOut.println("getBoardList");
		BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
		String boardListString = serverIn.readLine().substring(6); //TDO: magic number
		Map<Integer, String> boardList = gson.fromJson(boardListString, Map.class);
		listner = new UpdateWerker(server);

		this.whiteboards = new HashMap<Integer, WhiteboardGUI>();
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

	/**
	 * This helper sets up the subelements of this GUI.
	 */
	private void assembleJFrame() {
		tabbedPane = new JTabbedPane();
		for (Integer id : whiteboards.keySet()) {
	        tabbedPane.addTab(boardNames.get(Integer.toString(id)), whiteboards.get(id));
		}

		// Add the entire tabbed pane to the jframe
		mainPanel.add(tabbedPane);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		// Add the menu bar

		this.add(mainPanel);
		this.setJMenuBar(menuBar.createMenuBar());
	}

	/**
	 * Updates the current boardList for the whiteboard window and 
	 * menubar. 
	 * 
	 * @param newBoardList, a list mapping the unique board ID's currently at the server
	 * and their names.
	 */
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
		if (string == null || string.equals("null"))  
			return;

		System.out.println("MESSAGE:");
		System.out.println(string);
		System.out.println();
		if(string.contains("BOARD "))  {
			System.out.println("BOARD MESSAGE RECEIVED");
			System.out.println(string);
			String boardString = string.substring("BOARD ".length()); //TODO:Magic number
			String sketchString = boardString.substring(6);
			int id = Integer.parseInt(boardString.substring(0, 6).trim());
			Integer idInteger = new Integer(id);
			Sketch sketch = sketchgson.fromJson(sketchString, Sketch.class);
			if(!this.whiteboards.containsKey(idInteger))  {
				this.whiteboards.put(idInteger, new WhiteboardGUI(serverOut, id));
				assembleJFrame();
			}
			this.whiteboards.get(idInteger).setSketch(sketch);
			System.out.println("new map size " + whiteboards.size());
			System.out.println(sketchString);
			for (int boardid : whiteboards.keySet()) {
				System.out.println(boardNames.get(new Integer(boardid)));
			}
			
			this.repaint();


		}else  {
			if(string.contains("BLIST"))  {
				System.out.println("RECEIVED BLIST");
				System.out.println(string);
				String boardListString = string.substring(6); //TODO:Magic number
				@SuppressWarnings("unchecked")
				Map<Integer, String> boardList = gson.fromJson(boardListString, Map.class);
				this.setBoardList(boardList);
				
				assembleJFrame();
				this.repaint();

			}
			else  { //In this cases, no  changes are necessary. 
				if(string.contains("LEAVE") || string.contains("UPDATE ACK"))  {
					return;
				}
				//This should take care of all cases.
				else  {
					throw new RuntimeException("Unrecognized Server Message: " + string);
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

		/**
		 * The worker blocks and wait for the server's response in background.
		 * @return server response
		 */
		@Override
		protected String doInBackground() throws IOException {
			String response = serverIn.readLine();
			return response;

		}

		/**
		 * Parses and handles server message when done and creates a new 
		 * UpdateWerker to keep listening for updates. 
		 */
		@Override
		protected void done()  {

			try {
				String message = get();
				if(message != null){
					parseServerMessage(message);
				}
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

	/**
	 * @return instance of whiteboardGUI currently selected by used via tab interface. 
	 */
	public WhiteboardGUI getCurrentWhiteboard() {
		return (WhiteboardGUI) tabbedPane.getSelectedComponent();
	}
	
	class SketchDeserializer implements JsonDeserializer<Sketch> {

		@Override
		public Sketch deserialize(JsonElement json,
				java.lang.reflect.Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			ArrayList<Drawing> strokeArray = new ArrayList<Drawing>();
			

			JsonObject object = (JsonObject) json;
			JsonArray sketchArray = object.getAsJsonArray("sketch");
			for (JsonElement stroke : sketchArray) {
				JsonObject strokeObject = (JsonObject) stroke;
				Color color = gson.fromJson(strokeObject.get("color"), Color.class);
				int thickness = strokeObject.get("thickness").getAsInt();
				Point startPoint = gson.fromJson(strokeObject.get("startPoint"), Point.class);
				Point endPoint = gson.fromJson(strokeObject.get("endPoint"), Point.class);
				System.err.println(startPoint.toString());
				System.err.println(endPoint.toString());
				strokeArray.add(new Stroke(startPoint, endPoint, color, thickness));
			}
			//ArrayList<Drawing> sketchList = gson.fromJson(object.get("sketch"), ArrayList.class);
			return new Sketch(strokeArray);
		}
		
	}

}
