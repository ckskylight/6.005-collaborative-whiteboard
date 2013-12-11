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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Spring;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ADT.Drawing;
import ADT.Sketch;
import ADT.Stroke;

/**
 * 
 * @author Yala
 * 
 */
public class WhiteboardWindow extends JFrame {

	/**
	 * 
	 */
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

	@SuppressWarnings("unchecked")
	public WhiteboardWindow() throws IOException, ClassNotFoundException {
		this.whiteboards = new HashMap<Integer, WhiteboardGUI>();
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new TabListener());
		this.setPreferredSize(GUIConstants.WINDOW_DIMENSIONS);

		// Connect to Server
		server = new Socket();
		server.connect(new InetSocketAddress(SERVER_PORT));

		// Get boardList from server and start listening for updates
		gson = new Gson();
		sketchgson = new GsonBuilder().registerTypeAdapter(Sketch.class,
				new SketchDeserializer()).create();
		serverOut = new PrintWriter(server.getOutputStream(), true);
		serverOut.println("getBoardList");
		in = new ObjectInputStream(server.getInputStream());
		String boardListString = (String) in.readObject();
		boardListString = boardListString.substring(6);

		Map<Integer, String> boardList = gson.fromJson(boardListString,
				Map.class);
		listner = new Worker(server, in);

		this.whiteboards = new HashMap<Integer, WhiteboardGUI>();
		menuBar = new MenuBar(GUIConstants.EMPTY_BOARDS, serverOut, whiteboards, currentBoardID);
		this.setBoardList(boardList);
		new Thread(listner).start();

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
				} catch (ClassNotFoundException e) {
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
		tabbedPane.addChangeListener(new TabListener());
		for (Integer id : whiteboards.keySet()) {
			tabbedPane.addTab(boardNames.get(Integer.toString(id)),
					whiteboards.get(id));
		}

		// Add the entire tabbed pane to the jframe
		mainPanel.add(tabbedPane);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		if (tabbedPane.getComponentCount() > 0) {
			currentBoardID = ((WhiteboardGUI) tabbedPane.getSelectedComponent()).getID();
		}

		// Add the menu bar

		this.add(mainPanel);
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
		if (string == null || string.equals("null"))
			return;

		if (string.contains("BOARD ")) {

			String boardString = string.substring("BOARD ".length()); // TODO:Magic
																		// number
			int id = Integer.parseInt(boardString.substring(0, 6).trim());
			Integer idInteger = new Integer(id);

			String sketchString = boardString.substring(6);
			Sketch sketch = sketchgson.fromJson(sketchString, Sketch.class);

			if (!this.whiteboards.containsKey(idInteger)) {
				this.whiteboards.put(idInteger,
						new WhiteboardGUI(serverOut, id));
			}
			this.whiteboards.get(idInteger).setSketch(sketch);
			assembleJFrame();

		} else if (string.contains("MSG ")) {
			String updateString = string.substring("MSG ".length());
			int id = Integer.parseInt(updateString.substring(0, 6).trim());
			Integer idInteger = new Integer(id);
			if (string.contains("clearBoard")) {
				this.whiteboards.get(idInteger).clear();
			} else {
				String sketchString = updateString.substring(6);
				Stroke stroke = gson.fromJson(sketchString, Stroke.class);
				this.whiteboards.get(idInteger).connectStroke(stroke);
			}

		} else {
			if (string.contains("BLIST")) {
				;
				String boardListString = string.substring(6); // TODO:Magic
																// number
				@SuppressWarnings("unchecked")
				Map<Integer, String> boardList = gson.fromJson(boardListString,
						Map.class);
				this.setBoardList(boardList);
				assembleJFrame();
			}

			// This should take care of all cases.
			else {
				throw new RuntimeException("Unrecognized Server Message: "
						+ string);
			}

		}

		this.repaint();

	}

	//
	public class Worker implements Runnable {

		private final Socket server;
		private ObjectInputStream serverIn;

		/**
		 * This Swing worker handel's the receiving and processing of messages
		 * from the server. It is always waiting for a new message from the
		 * server. Once it processes a new message, it creates the next worker
		 * worker to continue listening to the server.
		 * 
		 * @throws IOException
		 */
		public Worker(Socket serverConnection, ObjectInputStream serverIn) {
			server = serverConnection;
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
					response = "" + (String) serverIn.readObject();
					final String message = response;
					if (message != null) {
						SwingUtilities.invokeAndWait(new Runnable() {
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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
				currentBoardID = ((WhiteboardGUI) ((JTabbedPane) e.getSource()).getSelectedComponent()).getID();
				menuBar.updateCurrentBoardId(currentBoardID);
			} catch (Exception ex) {
			}
		}
		
	}

}
