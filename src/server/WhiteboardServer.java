package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *   - send a message to every client containing the most recent state of the board
 *   - send a message to a client containing a list of every currently created board
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
	private Map<Integer, WhiteboardUser> threads;

	private Map<Integer, List<Integer>> boardMembers;
	private ServerSocket serverSocket;

	public WhiteboardServer(int port) throws IOException {
		this.boards = Collections.synchronizedMap(new HashMap<Integer, WhiteboardModel>());
		this.connections = Collections.synchronizedMap(new HashMap<Integer, Socket>());
		this.boardMembers = Collections.synchronizedMap(new HashMap<Integer, List<Integer>>());
		this.serverSocket = new ServerSocket(port);
		this.threads = Collections.synchronizedMap(new HashMap<Integer, WhiteboardUser>());
	}

	public void serve() throws IOException {
		while(true) {
			Socket socket = serverSocket.accept();
			int userID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
			while (this.connections.containsKey(userID)) {
				userID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
			}
			WhiteboardUser newUser = new WhiteboardUser(socket, this, userID);
			synchronized(this.connections) {
				this.connections.put(userID, socket);
				threads.put(userID, newUser);
			}
			newUser.start();
			
		}
	}

	private String handleRequest(String input, int userID) {

		if (input.startsWith("createBoard")) {
			String boardName = input.substring("createBoard".length() +1 );
			this.createBoard(userID, boardName);
			updateClientsBoardList();
			return "UPDATE ACK";
		} else if (input.startsWith("getBoardList")) {
			return "BLIST " + this.getBoardList();
		} else {
			int boardID = Integer.parseInt(input.substring(0,  5));
			input = input.substring(6);
			if (input.startsWith("joinBoard")) {
				joinBoard(boardID, userID);
				return "BOARD "+ Integer.toString(boardID) + " " + this.boards.get(boardID).getSketch().getJSON();
			} else if (input.startsWith("leaveBoard")) {
				leaveBoard(boardID, userID);
				return "LEAVE You left Board #" + Integer.toString(boardID); 
			} else if (input.startsWith("addDrawing")) {
				String drawingJSON = input.substring("addDrawing".length() + 1);
				connectDrawing(boardID, drawingJSON);
				updateClientsBoards(boardID, drawingJSON);
				return "UPDATE ACK";
			} else if (input.startsWith("setBoardName")) {
				String newName = input.substring("setBoardName".length() + 1);
				changeBoardName(boardID, newName);
				updateClientsBoardList();
				return "UPDATE ACK";
			} else if(input.startsWith("clearBoard"))  {
				boards.get(boardID).clear();
				updateClientsBoards(boardID, "clearBoard");
				return "UPDATE ACK";

			}
			else {
				return "ERROR"; // invalid request, this should cover all of 'em.
			}
		}
	}



	/**
	 * Takes a board id and the JSON for a Stroke object to add to it, and adds it on.
	 * Also updates all members of the board, so that it doesn't have to be done elsewhere.
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
			return;
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
	 * @param boardID
	 */
	private  void updateClientsBoards(int boardID) {
		List<Integer> clients = this.boardMembers.get(boardID);
		String boardState = "BOARD " + Integer.toString(boardID) + " " + this.boards.get(boardID).getSketch().getJSON();
		for (Integer clientID : clients) {
			WhiteboardUser client = this.threads.get(clientID);
			client.write(boardState);
		}
	}
	
	private void updateClientsBoards(int boardID, String message) {
		List<Integer> clients = this.boardMembers.get(boardID);
		String msg = "MSG " + Integer.toString(boardID) + " " + message;
		for (Integer clientID : clients) {
			WhiteboardUser client = this.threads.get(clientID);
			client.write(msg);
		}		
	}

	/**
	 * Sends an updated version of the boardsName list to all clients connected to the server. 
	 */
	private  void updateClientsBoardList() {
		String boardListJSON = "BLIST " + this.getBoardList();
		for(Integer clientID: connections.keySet())  {
			WhiteboardUser client = this.threads.get(clientID);
			client.write(boardListJSON);

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
	 * Thread class to watch a given user for input.  All input gets directed to the handleRequest
	 * method of the main server.  Upon the client ending the connection, sockets are closed and the
	 * class removes itself from the parent's connections map.
	 * @author John
	 *
	 */
	class WhiteboardUser extends Thread{
		private final Socket socket;
		private final WhiteboardServer parentServer;
		private final int userID;
		ObjectOutputStream outStream;
		
		public WhiteboardUser(Socket socket, WhiteboardServer parentServer, int userID) {
			this.socket = socket;
			this.parentServer = parentServer;
			this.userID = userID;
			try {
				outStream = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void write(String message)  {
			try {
				outStream.writeObject(message);
				outStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				try{
					while (true)  {
					for (String line = in.readLine(); line != null; line = in.readLine()){
						String output = this.parentServer.handleRequest(line, userID);
						if (!output.equals("UPDATE ACK"))  {
						outStream.writeObject(output);
						outStream.flush();
						}
						
					}
	
					} 
				} finally {
					
					// Get the userID out of the connections listing.
					synchronized(this.parentServer.connections) {
						this.parentServer.connections.remove(userID);
						try {
							this.parentServer.threads.get(userID).join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						this.parentServer.threads.remove(userID);
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
					outStream.close();
					in.close();
					this.socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
