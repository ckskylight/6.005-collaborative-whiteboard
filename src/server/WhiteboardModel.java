package server;

import ADT.Drawing;
import ADT.Sketch;

/**
 * 	Represents the server model of a single  thread-safe white board.
 * Each white board has a unique, final ID, and a mutable name and drawing.
 * User's can update the drawing (a Sketch) through connectDrawing and clear.
 * 
 * RI:
 * boardID is immutable
 * 
 * Thread Safety:
 * 	This class is thread-safe via the monitor pattern. The server must obtain a lock on
 * the model in order to mutate it. Only one client can mutate the state at a time, assuring a 
 * consistent model for a all clients.
 * 
 *
 */
public class WhiteboardModel {

	private final int boardID;
	private String boardName;
	private Sketch drawing;
	
	/**
	 * Initializes Model with mutable name and final ID.
	 * @param boardName, mutable name
	 * @param boardID, final ID, must be 5 digits long
	 */
	public WhiteboardModel(String boardName, int boardID)  {
		this.drawing = new Sketch();
		this.boardID = boardID;
		this.boardName = boardName;
	}
	
	/**
	 * @return 5 digit long ID of board
	 */
	public int getBoardID(){
		return boardID;
	}
	
	/**
	 * @return user defined white board name
	 */
	public synchronized String getBoardName()  {
		return boardName;
	}
	
	/**
	 * Sets white board name to user specified @param name.
	 */
	public synchronized void setBoardName(String name)  {
		boardName = name;
	}
	
	/**
	 * @return Sketch representing the contents of the white board. 
	 */
	public  synchronized Sketch getSketch()  {
		return drawing;
	}
	
	/**
	 * Updates the Sketch representing the contents of the white board by adding
	 * @param newDrawing to the sketch.
	 */
	public synchronized void connectDrawing(Drawing newDrawing) {
	    this.drawing.connect(newDrawing);
	}
	
	/**
	 * Clears the Sketch.
	 */
	public synchronized void clear()  {
		this.drawing.clear();
	}
	
	
}
