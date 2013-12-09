package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 */
public class WhiteboardServer {
    private Map<Integer, WhiteboardModel> boards; 
    private Map<Integer, Socket> connections;
    private Map<Integer, List<Integer>> boardMembers;
    private ServerSocket serverSocket;
    
    public WhiteboardServer(int port) throws IOException {
        this.boards = Collections.synchronizedMap(new HashMap<Integer, WhiteboardModel>());
        this.connections = Collections.synchronizedMap(new HashMap<Integer, Socket>());
        this.boardMembers = Collections.synchronizedMap(new HashMap<Integer, List<Integer>>());
        this.serverSocket = new ServerSocket(port);
    }
    
    public void serve() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            int userID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
            while (this.connections.containsKey(userID)) {
                userID = 10000 + (int)(Math.random() * ((99999 - 10000) - 1));
            }
            synchronized(this.connections) {
                this.connections.put(userID, socket);
            }
            Thread newUser = new Thread(new WhiteboardUser(socket, this, userID));
            newUser.start();
        }
    }
    
    private String handleRequest(String input, int userID) {
        if (input.startsWith("createBoard")) {
            String boardName = input.substring(12);
            WhiteboardModel newBoard = this.createBoard(userID, boardName);
            return (Integer.toString(newBoard.getBoardID())) + " " + newBoard.getJSON();
        } else if (input.startsWith("getBoardList")) {
            return this.getBoardList();
        } else {
            int boardID = Integer.parseInt(input.substring(0,  6));
            input = input.substring(7);
            if (input.startsWith("joinBoard")) {
                joinBoard(boardID, userID);
             return Integer.toString(boardID) + " " + this.boards.get(boardID).getJSON();
            } else if (input.startsWith("leaveBoard")) {
                leaveBoard(boardID, userID);
             return "You left Board #" + Integer.toString(boardID); 
            } else if (input.startsWith("addDrawing")) {
                String drawingJSON = input.substring(11);
                connectStroke(boardID, drawingJSON);
                return Integer.toString(boardID) + " " + this.boards.get(boardID).getJSON();
            } else if (input.startsWith("setBoardName")) {
                String newName = input.substring(13);
                changeBoardName(boardID, newName);
                return Integer.toString(boardID) + " " + this.boards.get(boardID).getJSON();
            } else if (input.startsWith("getBoardName")) {
                return Integer.toString(boardID) + " " + this.boards.get(boardID).getBoardName();
            } else {
                return null; // invalid request, this should cover all of 'em.
            }
        }
    }
    
    /**
     * Takes a board id and the JSON for a Stroke object to add to it, and adds it on.
     * Also updates all members of the board, so that it doesn't have to be done elsewhere.
     * @param boardID
     * @param strokeJSON
     */
    private synchronized void connectStroke(int boardID, String strokeJSON) {
        WhiteboardModel board = this.boards.get(boardID);
        Gson gson = new Gson();
        Drawing strokeObj = gson.fromJson(strokeJSON, Stroke.class);
        board.connectDrawing(strokeObj);
        updateClients(boardID);
    }
    
    /**
     * Takes a board ID and a String representing a new human-readable name for the board,
     * and sets the name to that value.
     * @param boardID
     * @param newName
     */
    private synchronized void changeBoardName(int boardID, String newName) {
        this.boards.get(boardID).setBoardName(newName);
        return;
    }
    
    /**
     * Takes a boardID and a userID and adds the user to the board's members.
     * @param boardID
     * @param userID
     */
    private synchronized void joinBoard(int boardID, int userID) {
        this.boardMembers.get(boardID).add(userID);
    }
    
    /**
     * Given a boardID and userID, removes that user from the board's members.
     * @param boardID
     * @param userID
     */
    private synchronized void leaveBoard(int boardID, int userID) {
        this.boardMembers.get(boardID).remove(userID);
    }
    
    /**
     * Given a board ID, sends the most current JSON of the board's state to each member
     * of the board.
     * @param boardID
     */
    private synchronized void updateClients(int boardID) {
        List<Integer> clients = this.boardMembers.get(boardID);
        String boardState = this.boards.get(boardID).getJSON();
        for (Integer i : clients) {
            Socket socket = this.connections.get(i);
            try{
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(boardState);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Given a userID and a human-readable name for the board, creates a unique five
     * digit number for the board's ID, creates and stores the board, and adds the user 
     * as a member to said board.
     * @param userID
     * @param boardName
     */
    private synchronized WhiteboardModel createBoard(int userID, String boardName) {
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
     * Thread class to watch a given user for input.  All input gets directed to the handleRequest
     * method of the main server.  Upon the client ending the connection, sockets are closed and the
     * class removes itself from the parent's connections map.
     * @author John
     *
     */
    class WhiteboardUser implements Runnable{
        private final Socket socket;
        private final WhiteboardServer parentServer;
        private final int userID;
        
        public WhiteboardUser(Socket socket, WhiteboardServer parentServer, int userID) {
            this.socket = socket;
            this.parentServer = parentServer;
            this.userID = userID;
        }
        
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                try{
                    for (String line = in.readLine(); line != null; line = in.readLine()){
                        String output = this.parentServer.handleRequest(line, userID);
                        out.println(output);
                        out.flush();
                            } 
                    } finally {
                        out.close();
                        in.close();
                    this.socket.close();
                        synchronized(this.parentServer.connections) {
                            this.parentServer.connections.remove(userID);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
