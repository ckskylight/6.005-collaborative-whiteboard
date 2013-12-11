package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import server.WhiteboardServer.Listner;
import server.WhiteboardServer.Writer;
import gson.src.main.java.com.google.gson.Gson;
import ADT.Drawing;
import ADT.Stroke;


/**
 * @authors John O'Sullivan, Adam Yala, C.K. Ong
 * WhiteboardServer coordinates the server side of the Project 2 white board application.  Our server has
 * functionality to:
 *   - store boards
 *   - receive a message connect new strokes to said boards
 *   - receive messages to set and change the user-defined name of boards
 *   - receive messages to allow users to join/leave boards
 *   - receive messages for users to create new boards
 *   - send a message to a client containing the most recent state of the board
 *   - send a message to every client containing an update to the board. 
 *   - send a message to a client containing a Map of every currently created board ID and Name
 * 

 *	Thread Safety Argument:
 *   s
 *   
 *   REP INVARIANT:
 *   All of the key values in boardMembers are present in boards, and all of the entry values 
 *   in boardMembers (or rather, the integers that are members of the list held as the entry value)
 *   are present in connections.
 */
public class WhiteboardServer {
	private Map<Integer, WhiteboardModel> boards; 
	private Map<Integer, Socket> connections;
	private Map<Integer, Thread> readThreads;
	private Map<Integer, Writer> writerRunnables;

	private Map<Integer, List<Integer>> boardMembers;
	private ServerSocket serverSocket;

	/**
	 * Initializes the server at the specified port. 
	 * @throws IOException
	 */
	public WhiteboardServer(int port) throws IOException {
		this.boards = Collections.synchronizedMap(new HashMap<Integer, WhiteboardModel>());
		this.connections = Collections.synchronizedMap(new HashMap<Integer, Socket>());
		this.boardMembers = Collections.synchronizedMap(new HashMap<Integer, List<Integer>>());
		this.serverSocket = new ServerSocket(port);
		this.readThreads = Collections.synchronizedMap(new HashMap<Integer, Thread>());
		this.writerRunnables = Collections.synchronizedMap(new HashMap<Integer, Writer>());
	}

	/**
	 * Accepts a client, and spawns a new Thread to read and a new Thread to write to the client
	 * Each user receives a unique user ID and is recorded in the list of connections,
	 * and read and write runnables. 
	 * @throws IOException
	 */
	public void serve() throws IOException {
		while(true) {
			Socket socket = serverSocket.accept();
			//Obtain unique user ID
			int userID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
			while (this.connections.containsKey(userID)) {
				userID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
			}
			//Spawn new threads to read and write to/from this client
			Thread newListner = new Thread(new Listner(socket, this, userID));
			Writer writer = new Writer(socket, this, userID);
			Thread newWriter = new Thread( writer);
			synchronized(this.connections) { //Update maps
				this.connections.put(userID, socket);
				readThreads.put(userID, newListner);
				writerRunnables.put(userID, writer);
			}
			newListner.start();
			newWriter.start();
			
		}
	}

	/**
	 * Handles requests from a client (with userID) as specified
	 * by the Protocol. For a more explicit description of the protocol, 
	 * please see Design Doc. 
	 * If appropriate, the server prompts a writing thread to return a message
	 * to the client. 
	 * @param input, User request
	 * @param userID, ID of User.
	 */
	private void handleRequest(String input, int userID) {
		Writer clientWriter = writerRunnables.get(userID); //Gets writer for this client
		if (input.startsWith("createBoard")) { //creatBoard Message
			String boardName = input.substring("createBoard".length() +1 );
			this.createBoard(userID, boardName);
			updateClientsBoardList(); //Sends everyone a updated boardList
		} else if (input.startsWith("getBoardList")) {//Get board list message
			clientWriter.put( "BLIST " + this.getBoardList());//Sends client current boardList	
		} else { //This is some sort of message beginning with a boardID
			int boardID = Integer.parseInt(input.substring(0,  5));  //Parse boardID
			input = input.substring(6);
			if (input.startsWith("joinBoard")) { //joinBoard Message
				joinBoard(boardID, userID);
				//Sends server a current copy of the board's sketch
				clientWriter.put(  "BOARD "+ Integer.toString(boardID) + " " + this.boards.get(boardID).getSketch().getJSON());
			} else if (input.startsWith("leaveBoard")) { //Leave message
				leaveBoard(boardID, userID);
				clientWriter.put("LEAVE "+ boardID); //Sends user confirmation that they left
			} else if (input.startsWith("addDrawing")) {//addDrawing message
				String drawingJSON = input.substring("addDrawing".length() + 1); 
				connectDrawing(boardID, drawingJSON);//Update internal model with drawing
				updateClientsBoards(boardID, drawingJSON);//Send all subscribed clients to this board the same update			
			} else if (input.startsWith("setBoardName")) {//setBoardName message
				String newName = input.substring("setBoardName".length() + 1);
				changeBoardName(boardID, newName); //Updates board name
				updateClientsBoardList(); //Sends updated board list to all users
			} else if(input.startsWith("clearBoard"))  { //clearBoard message
				boards.get(boardID).clear(); //Clears server's master copy of board
				updateClientsBoards(boardID, "clearBoard");//Sends message to all subsrbied clients to clear board. 
			}
			else {
				clientWriter.put( "ERROR"); // invalid request, this should cover all of 'em.
			}
		}
	}



	/**
	 * Takes a board id and the JSON for a Stroke object to add to it. Updates Sketch of board.
	 * @param boardID
	 * @param drawingJSON
	 */
	private void connectDrawing(int boardID, String drawingJSON) {
		WhiteboardModel board;
		synchronized (board = this.boards.get(boardID)) {
			Gson gson = new Gson();
			Drawing drawObj = gson.fromJson(drawingJSON, Stroke.class);
			board.connectDrawing(drawObj);
		}
	}

	/**
	 * Takes a board ID and a String representing a new human-readable name for the board,
	 * and sets the name to that value.
	 * @param boardID
	 * @param newName
	 */
	private void changeBoardName(int boardID, String newName) {
		WhiteboardModel board = this.boards.get(boardID);
		synchronized (board) {
			this.boards.get(boardID).setBoardName(newName);
			updateClientsBoardList();
		}
	}

	/**
	 * Takes a boardID and a userID and adds the user to the board's members.
	 * @param boardID
	 * @param userID
	 */
	private void joinBoard(int boardID, int userID) {
		this.boardMembers.get(boardID).add(userID);
	}

	/**
	 * Given a boardID and userID, removes that user from the board's members.
	 * @param boardID
	 * @param userID
	 */
	private void leaveBoard(int boardID, int userID) {
		this.boardMembers.get(boardID).remove(userID);
	}

	/**
	 * Given a board ID, sends the most current JSON of the board's state to each member
	 * of the board.
	 * This is done through placing the message on each clients respective BlockingQueue.
	 * @param boardID
	 */
	private  void updateClientsBoards(int boardID) {
		List<Integer> clients = this.boardMembers.get(boardID);
		String boardState = "BOARD " + Integer.toString(boardID) + " " + this.boards.get(boardID).getSketch().getJSON();
		for (Integer clientID : clients) {
			Writer client = this.writerRunnables.get(clientID);
			client.put(boardState); //Puts message on blocking queue for each writer thread. 
		}
	}

	/**
	 * Given a board ID, sends the specified message to each member of the board.
	 * This is done through placing the message on each clients respective BlockingQueue.
	 */
	private void updateClientsBoards(int boardID, String message) {
		List<Integer> clients = this.boardMembers.get(boardID);
		String msg = "MSG " + Integer.toString(boardID) + " " + message;
		for (Integer clientID : clients) {
			Writer client = this.writerRunnables.get(clientID);
			client.put(msg);//Puts message on blocking queue for each writer thread.
		}		
	}

	/**
	 * Sends an updated version of the boardsName list to all clients connected to the server. 
	 * This is done through placing the message on each clients respective BlockingQueue.
	 */
	private  void updateClientsBoardList() {
		String boardListJSON = "BLIST " + this.getBoardList();
		for(Integer clientID: connections.keySet())  {
			Writer client = this.writerRunnables.get(clientID);
			client.put(boardListJSON); //Puts message on blocking queue for each writer thread.

		}
	}

	/**
	 * Given a userID and a human-readable name for the board, creates a unique five
	 * digit number for the board's ID, creates and stores the board, and adds the user 
	 * as a member to said board.
	 * @param userID
	 * @param boardName
	 */
	private WhiteboardModel createBoard(int userID, String boardName) {
		int newBoardID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
		while (this.boards.containsKey(newBoardID)) {
			newBoardID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
		}
		WhiteboardModel newBoard = new WhiteboardModel(boardName, newBoardID);
		synchronized(this.boards) {
			this.boards.put(newBoardID, newBoard);
		}
		synchronized(this.boardMembers) {
			this.boardMembers.put(newBoardID, new ArrayList<Integer>());
			this.boardMembers.get(newBoardID).add(userID);
		}
		updateClientsBoards(newBoardID);
		return newBoard;
	}

	/**
	 * Returns a string representing a JSON of a map between boardID's and boardName's.
	 * @return
	 */
	private synchronized String getBoardList() {  
		HashMap<Integer, String> listing = new HashMap<Integer, String>();
		for (int boardID : this.boards.keySet()) {
			listing.put(boardID, this.boards.get(boardID).getBoardName());
		}
		Gson gson = new Gson();
		String answer = gson.toJson(listing);
		return answer;
	}

	/**
	 * Starts the server at a specific port
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Server starting...");
		try {
			WhiteboardServer server = new WhiteboardServer(4444);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Thread class to listen a given user for input.  All input gets directed to the handleRequest
	 * method of the main server.  Upon the client ending the connection, sockets are closed and the
	 * class removes itself from the parent's connections map.
	 * 
	 * It also closes the Writer class responsible for the User using the Writer.kill() method.
	 *
	 */
	class Listner implements Runnable{
		private final Socket socket;
		private final WhiteboardServer parentServer;
		private final int userID;
		
		public Listner(Socket socket, WhiteboardServer parentServer, int userID) {
			this.socket = socket;
			this.parentServer = parentServer;
			this.userID = userID;

		}

		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				try{
					while (true)  {
					for (String line = in.readLine(); line != null; line = in.readLine()){ //Read from client
						this.parentServer.handleRequest(line, userID); //Sends client request to central server
						}		
					break;
					}
				} finally {
					// Get the userID out of the connections listing.
					synchronized(this.parentServer.connections) {
						this.parentServer.connections.remove(userID);
						try {
							this.parentServer.readThreads.get(userID).join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						this.parentServer.readThreads.remove(userID);
					} 
					// gets the userID out of all the boardMembers listings.
					synchronized(this.parentServer.boardMembers) {
						for(Integer boardID: this.parentServer.boardMembers.keySet())  {
							for(Integer userID: this.parentServer.boardMembers.get(boardID)) {
								if(userID.equals(new Integer(this.userID)))  {
									this.parentServer.boardMembers.get(boardID).remove(userID);
								}
							}

						}
					}
					socket.close();
					this.parentServer.writerRunnables.get(userID).kill();
					// The termination statement is triggered upon reading a message, so we add one final
					// message to make sure that the thread ends.
					this.parentServer.writerRunnables.get(userID).put("Die, thread, die!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Runnable to write to a given user. To add messages to this runnable,
	 * we add messages to a blocking Queue.
	 *
	 */
	class Writer implements Runnable{
		private final Socket socket;
		private ObjectOutputStream outStream;
		private BlockingQueue<String> messages;
		private Boolean alive;
		
		public Writer(Socket socket, WhiteboardServer parentServer, int userID) {
			this.socket = socket;
			messages = new ArrayBlockingQueue<String>(10000);
			this.alive = true;
			
			try {
				outStream = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Add element to BlockingQueue of Writer. This will be sent to the 
		 * client as soon as possible. 
		 * @param message, message to client from server. 
		 */
		public void put(String message)  {
			try {
				messages.put(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void kill() {
		    this.alive = false;
		}

		public void run() {
			try {
				try{
					while (true)  {
						String output = messages.take();
						if (alive) {
        		                      outStream.writeObject(output); //Send message to client
        		                      outStream.flush();
						}
		                          else {
		                              break;
		                          }
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					outStream.close();
					this.socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
