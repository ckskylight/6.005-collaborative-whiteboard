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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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
 * Whiteboard window is a class that extends JFrame, and this creates the window
 * that all the other components of the GUI depend upon. A new class WhiteboardWindow
 * had to be created instead of using a regular JFrame because the main window needs 
 * to handle the different tabs corresponding to the different whiteboards that can
 * be created. 
 * 
 * The main representation of these tabs are in the Maps named boardNames
 * and whiteboards. boardNames is a map of all the boards on the server represented by
 * their ID's and their assigned names. whiteboards is a map that contains the whiteboards
 * that the user is connected to (as WhiteboardGUI instances) as well as their ID's. 
 * This allows the WhiteboardWindow to instantiate these instances of WhiteboardGUI 
 * within the tabs, represented by the Swing class JTabbedPane. 
 * 
 * The JTabbedPane is contained within an IPanel (Image Panel), which was a class created
 * to extend a JPanel that has a background image. In this way, by default, when there 
 * are no tabs (no connected whiteboards) the background image / welcome message
 * appears on the screen.
 * 
 * Since WhiteboardWindow is the top class that all the other GUI classes depend on
 * and/or are spawned from, it contains the methods that read the server responses
 * and make changes to the GUI based on how the server responded.
 * 
 * 
 * 
 * ======================== TESTING STRATEGY ============================
 * assembleJFrame
 * setBoardList
 * parseServerMessage
 * getCurrentWhiteboard
 * 
 * In testing the GUI, our approach was to list all the features and capabilities
 * of the GUI and test all permutations of input to test the response of the window.
 * These features and tests are listed below:
 * 
 * Create new whiteboard
 * 	- Pass in:
 *		- Empty name
 *		- Name with only letters
 *		- Name with letters and spaces
 *		- Name with other characters (numbers,punctuation,etc)
 *		- Name with other characters with spaces
 *		- Name with letters, other chars and spaces
 *	Here we are looking for the GUI to create a new tab with the name of the 
 *	whiteboard specified by the user. We are also looking for the GUI to still
 *	be functioning as expected after the creation of the new whiteboard.
 * 
 * Rename current whiteboard
 * 	- Pass in:
 *		- Empty name
 *		- Name with only letters
 *		- Name with letters and spaces
 *		- Name with other characters (numbers,punctuation,etc)
 *		- Name with other characters with spaces
 *		- Name with letters, other chars and spaces
 *	For all of these, the name of the whiteboard in the tab should change to
 *	what the user specified, including if it's an empty string.
 *	- Pass in a name when not connected to any whiteboards
 *	This should effectively not do anything on the whiteboard. The GUI should
 *	send nothing to the server and the state of the GUI would remain the same.
 * 
 * Leave current whiteboard
 * 	- Situations to leave whiteboard:
 * 		- No connected boards
 * 		- One connected board
 * 		- More than one connected board
 * 	When there are no connected boards, clicking on leave will do nothing. 
 * 	When there is one connected board, clicking on leave should close the tab
 * 		and return you to the welcome screen
 * 	When there is more than one connected board, exit the current board and 
 * 		enter one of the other boards that the user is connected to
 * 
 * Join whiteboard
 * 	- Situations:
 * 		- Connected to no whiteboards
 * 		- Connected to 1 or more whiteboards
 * 		- Join a board that you just left
 * 		- Join a board already joined
 * 	When joining a whiteboard that's not currently joined, the GUI would add 
 * 		a new tab corresponding to the joined whiteboard.
 * 	When joining a whiteboard that's already joined (perhaps by accident) the 
 * 		GUI should do nothing and continue to work as expected.
 * 
 * Switching whiteboards (between tabs)
 * 	- Situations:
 * 		- One tab
 * 		- More than one tab
 * 	When there is one tab and that tab is clicked, the GUI should do nothing
 * 		and continue working as expected. When more than one tab is open, when
 * 		user clicks on the tab he is on, nothing should happen. When clicking on 
 * 		another tab, the GUI should switch to the whiteboard corresponding to 
 * 		the name on the tab. 
 * 
 * Draw
 * 	- Situations:
 * 		- Draw is active
 * 		- Erase is active
 * 		- Clear just clicked
 * 	When Draw is active, the icon background is green. On mousedown, this turns
 * 		dark blue. On mouseup, Draw remains active (green). The user should still
 * 		be able to draw on the whiteboard.
 * 	When Erase is active (and Draw is inactive - light blue), the icon still 
 * 		turns dark blue on mousedown but Draw becomes active (green) on mouseup.
 * 		The user should now be able to draw (turn off erase)
 * 	Clear being clicked should not affect anything since Clear is not a toggle 
 * 		button.
 * 
 * Erase
 * 	The tests for erase mimic those of Draw, except replace Draw with Erase
 * 	and vice versa.
 * 
 * Clear
 * 	- Clear clicked, nothing on board
 * 	- Clear clicked, drawing on board
 * 	When clear is clicked, whether or not there is something on the board,
 * 		the board should become or remain empty (white).
 * 
 * Change stroke weight
 * 	Changing the weight to any number from the dropdown should change the thickness
 * 	of any line drawn. The color of the line should remain the same.
 * 
 * Change stroke color (hex/decimal box)
 * 	- Input:
 * 		- Standard hex
 * 		- Standard decimal
 * 		- Non-standard numerical value
 * 		- Letters and/or spaces
 * 	Entering a standard numerical value for a color should change the color of the brush
 * 		and is evident when using the brush. If previous value was invalid, the color of
 * 		the box should turn light green. 
 * 	Entering a non-standard numerical value or letters/spaces would print "Invalid" 
 * 		inside the box and change the box fill color to light red to indicate an error.
 * 
 * Change stroke color (color palate)
 * 	- Situations:
 * 		- Click the same color as current color
 * 		- Click a different color
 * 	The brush color should change to the color of the clicked button. If it's the same
 * 		as the current color that means the color should stay the same.
 * 
 * Multiple clients
 * 		- Create a board on one, join on the other
 * 		- Join the same board that is already on the server
 * 		- Create boards with the same time on both (should act as different boards)
 * 		- Use different brush colors
 * 		- Use different brush weights
 * 		- One draws and one erases
 * 		- One draws and one clears the board
 * 
 * Mouseover (change cursor)
 * 	- Mouse over draw, erase, clear
 * 	- Mouse over color palate buttons
 * 	On mouseover all these buttons, the cursor should change from an arrow to the standard
 * 		hand that appears when a button is to be clicked in most operating systems.
 * 
 * 
 * ========================== THREAD SAFETY =============================
 * 	Each client, an instance of WhiteboardWindow spawns a background thread to 
 * listen to the server. We ensure the thread-safety of our client side by never 
 * mutating the GUI from the background thread, and using SwingUttilities.invokeAndWait
 * to pass messages to the GUI from the Server; the thread ensures the liveness
 * of the GUI by only doing blocking operations in the background thread. For all
 * freehand drawing actions, clear actions or menu selections, a message is sent to
 * the Server and the GUI is only updated after the GUI receives a response. 
 * This assures all whiteboards and Board lists are consistent for all clients.
 * 	
 * 
 */
public class WhiteboardWindow extends JFrame {

	private static final long serialVersionUID = 6651504303207410645L;
	// Server Elements
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
	public WhiteboardWindow(int port) throws IOException, ClassNotFoundException {
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
		server.connect(new InetSocketAddress(port));

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
	 * Starts a new Whiteboard client using the given arguments.
	 * 
	 * Usage: WhiteboardWindow [--port PORT]
	 * 
	 * PORT is an optional integer argument which starts the client listening on that port.  It must
	 * be a value between 0 and 65535, and if it is omitted the client starts listening on port 4444.
	 * @param args
	 */
	public static void main(String[] args) {
	    final PortCarrier port = new PortCarrier();
	    Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
	    while (! arguments.isEmpty()) {
	        String flag = arguments.remove();
	        try {
	            if (flag.equals("--port")) {
	                port.setPort(Integer.parseInt(arguments.remove()));
	                if (port.getPort() < 0 || port.getPort() > 65535) {
	                    throw new IllegalArgumentException("Port not within valid range.");
	                }
	            } else {
	                throw new IllegalArgumentException(flag + " is not a recognized argument.");
	            }
	        } catch (IllegalArgumentException e) {
	            System.err.println(e.getMessage());
	            e.printStackTrace();
	        }
	    }
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				WhiteboardWindow main;
				try {
				        main = new WhiteboardWindow(port.getPort());				        
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

					}
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
