package GUI;

import gson.src.main.java.com.google.gson.Gson;
import gson.src.main.java.com.google.gson.GsonBuilder;
import gson.src.main.java.com.google.gson.JsonArray;
import gson.src.main.java.com.google.gson.JsonDeserializationContext;
import gson.src.main.java.com.google.gson.JsonDeserializer;
import gson.src.main.java.com.google.gson.JsonElement;
import gson.src.main.java.com.google.gson.JsonObject;
import gson.src.main.java.com.google.gson.JsonParseException;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ADT.Drawing;
import ADT.Sketch;
import ADT.Stroke;

/**
 * WhiteboardWindow represents the client in our System. It is the top level of
 * our GUI and is the point from which the client connects to the server.
 * 
 * TODO: Thread Safety, Invariants, Testing, More details
 * 
 * 
 * 
 */
public class WhiteboardWindow extends JFrame {

	private static final long serialVersionUID = 6651504303207410645L;
	// Server Elements
	private final int SERVER_PORT = 4444;
	private final Socket server;
	private static PrintWriter serverOut;
	private Gson gson;
	private Gson sketchgson;
	private Map<Integer, String> boardNames;
	private Worker listner;
	private ObjectInputStream in;

	// Represents the whiteboard the user has joined.
	private Map<Integer, WhiteboardGUI> whiteboards;

	// The tabbed pane that houses all tabs
	private JTabbedPane tabbedPane;

	// Background image
	Image backgroundImage = loadImage("src/GUI/images/BackgroundResized.jpg");

	// IPanel that has the background image that everything is painted on
	JPanel mainPanel = new IPanel(backgroundImage);

	// Tracker for the tab being currently displayed
	int currentBoardID;

	// Menu bar
	private final MenuBar menuBar;

	/**
	 * Upon the creating a {@link WhiteboardWindow}, the client connects to the
	 * server, obtains the board list and creates the GUI.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public WhiteboardWindow() throws IOException, ClassNotFoundException {
		this.whiteboards = new HashMap<Integer, WhiteboardGUI>();
		this.setPreferredSize(GUIConstants.WINDOW_DIMENSIONS);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle("Collaborative Whiteboardzz");
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new TabListener());
		
		// Add the entire tabbed pane to the jframe
		mainPanel.add(tabbedPane);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		this.add(mainPanel);

		// Connect to Server
		server = new Socket();
		server.connect(new InetSocketAddress(SERVER_PORT));

		// Get boardList from server and start listening for updates
		gson = new Gson();
		sketchgson = new GsonBuilder().registerTypeAdapter(Sketch.class,
				new SketchDeserializer()).create();
		serverOut = new PrintWriter(server.getOutputStream(), true);
		serverOut.println("getBoardList");
		serverOut.flush();
		in = new ObjectInputStream(server.getInputStream());
		String boardListString = (String) in.readObject();
		boardListString = boardListString.substring(6);

		Map<Integer, String> boardList = gson.fromJson(boardListString,
				Map.class);
		listner = new Worker(in);

		this.whiteboards = new HashMap<Integer, WhiteboardGUI>();
		menuBar = new MenuBar(GUIConstants.EMPTY_BOARDS, serverOut,
				whiteboards, currentBoardID);
		this.setBoardList(boardList);
		new Thread(listner).start();

	}

	/**
	 * Launches new Client (all of which connect to sever upon construction).
	 * 
	 * @param args
	 */
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
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Loads an Image from specified file path.
	 * 
	 * @param filePath
	 *            , must be valid or IOException will occur.
	 * @return Image at file Path
	 */
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
		if (tabbedPane.getComponentCount() > 0) {
			tabbedPane.removeAll();
		}
		for (Integer id : whiteboards.keySet()) {
			tabbedPane.addTab(boardNames.get(Integer.toString(id)),
					whiteboards.get(id));
		}
		
		// Add the menu bar
		this.setJMenuBar(menuBar.createMenuBar());
		
	}

	/**
	 * Updates the current boardList for the whiteboard window and menubar.
	 * 
	 * @param newBoardList
	 *            , a list mapping the unique board ID's currently at the server
	 *            and their names.
	 */
	public void setBoardList(Map<Integer, String> newBoardList) {
		this.boardNames = newBoardList;
		menuBar.setBoardList(boardNames);

	}

	/**
	 * Takes in a message from the board, as specified in the Server Protocol,
	 * and updates the GUI accordingly.
	 * 
	 * @param string
	 *            , message from the server.
	 */
	private void parseServerMessage(String string) {

		if (string == null || string.equals("null")) // Skip invalid messages.
			return;
		if (string.contains ("LEAVE")) {
			String idString = string.substring("LEAVE ".length());
			int id = Integer.parseInt(idString); // IDs are 5 digits long
			whiteboards.remove(id);
			assembleJFrame();

		} else if (string.contains("BOARD ")) { // Indicates server sent a Sketch representing a whiteboard.
			String boardString = string.substring("BOARD ".length());
			int id = Integer.parseInt(boardString.substring(0, 6).trim()); // IDs are 5 digits long
			Integer idInteger = new Integer(id);
			String sketchString = boardString.substring(6); // 6 is used to get past the ID in the string.
			Sketch sketch = sketchgson.fromJson(sketchString, Sketch.class); // Convert the string to a sketch

			if (!this.whiteboards.containsKey(idInteger)) {
				this.whiteboards.put(idInteger,
						new WhiteboardGUI(serverOut, id));
			}
			this.whiteboards.get(idInteger).setSketch(sketch); // update sketch of a whiteboard
			assembleJFrame(); // Update tabs

		} else if (string.contains("MSG ")) { // Indicates server sent an update in the form of a Stroke or a clear message

			String updateString = string.substring("MSG ".length());
			
			int id = Integer.parseInt(updateString.substring(0, 6).trim()); // IDs are 5 digits long
			Integer idInteger = new Integer(id);

			if (string.contains("clearBoard")) { // Server sent a clear message
				this.whiteboards.get(idInteger).clear();
			} else { // Server sent a Stroke update message (Free hand drawing and erasing)
				String sketchString = updateString.substring(6);
				Stroke stroke = gson.fromJson(sketchString, Stroke.class);
				this.whiteboards.get(idInteger).connectStroke(stroke);
			}

		} else if (string.contains("BLIST ")) {// Indicates server sent a an updated Boards List (<ID,Name>)
			String boardListString = string.substring("BLIST ".length());
			@SuppressWarnings("unchecked")
			Map<Integer, String> boardList = gson.fromJson(boardListString,
					Map.class);
			this.setBoardList(boardList);
			assembleJFrame();
		}

		// This should take care of all cases. Should never reach here.
		else {
			throw new RuntimeException("Unrecognized Server Message: " + string);
		}

		this.repaint();

	}

	/**
	 * This Runnable handles reading messages from the server. Once it receives
	 * a message from the server, it pushes the processing of the response onto
	 * the EventQueue
	 * 
	 * @throws IOException
	 */

	public class Worker implements Runnable {

		private ObjectInputStream serverIn;

		/**
		 * @param serverIn
		 *            , {@link ObjectInputStream} used to read from the socket
		 *            (connected to WhiteboardServer). Must be the only socket
		 *            used to read from this socket or header data will be
		 *            corrupted.
		 */

		public Worker(ObjectInputStream serverIn) {
			this.serverIn = serverIn;

		}

		/**
		 * The worker blocks and wait for the server's response in background.
		 * Then tells the GUI to process the response.
		 */
		@Override
		public void run() {
			while (true) {
				String response;
				try {
					response = "" + (String) serverIn.readObject(); // get
																	// server
																	// message
					final String message = response;

					if (message != null) {
						SwingUtilities.invokeAndWait(new Runnable() { // process
																		// in
																		// GUI
									public void run() {
										parseServerMessage(message);
									}
								});

					} // TODO: Close the the thread or something.
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * @return instance of whiteboardGUI currently selected by used via tab
	 *         interface.
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
				Color color = gson.fromJson(strokeObject.get("color"),
						Color.class);
				int thickness = strokeObject.get("thickness").getAsInt();
				Point startPoint = gson.fromJson(
						strokeObject.get("startPoint"), Point.class);
				Point endPoint = gson.fromJson(strokeObject.get("endPoint"),
						Point.class);
				strokeArray.add(new Stroke(startPoint, endPoint, color,
						thickness));
			}
			return new Sketch(strokeArray);
		}

	}

	class TabListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				currentBoardID = ((WhiteboardGUI) ((JTabbedPane) e.getSource())
						.getSelectedComponent()).getID();
				menuBar.updateCurrentBoardId(currentBoardID);
			} catch (Exception ex) {
			}
		}

		/**
		 * @return instance of whiteboardGUI currently selected by used via tab
		 *         interface.
		 */
		public WhiteboardGUI getCurrentWhiteboard() {
			return (WhiteboardGUI) tabbedPane.getSelectedComponent();
		}

		class SketchDeserializer implements JsonDeserializer<Sketch> {

			@Override
			public Sketch deserialize(JsonElement json,
					java.lang.reflect.Type typeOfT,
					JsonDeserializationContext context)
					throws JsonParseException {
				ArrayList<Drawing> strokeArray = new ArrayList<Drawing>();

				JsonObject object = (JsonObject) json;
				JsonArray sketchArray = object.getAsJsonArray("sketch");
				for (JsonElement stroke : sketchArray) {
					JsonObject strokeObject = (JsonObject) stroke;
					Color color = gson.fromJson(strokeObject.get("color"),
							Color.class);
					int thickness = strokeObject.get("thickness").getAsInt();
					Point startPoint = gson.fromJson(
							strokeObject.get("startPoint"), Point.class);
					Point endPoint = gson.fromJson(
							strokeObject.get("endPoint"), Point.class);
					strokeArray.add(new Stroke(startPoint, endPoint, color,
							thickness));
				}
				return new Sketch(strokeArray);
			}

		}

	}
}
