package server;

import gson.src.main.java.com.google.gson.Gson;

import ADT.Drawing;
import ADT.Sketch;

/**
 * 
 * @author Yala
 *
 */
public class WhiteboardModel {

	private final int boardID;
	private String boardName;
	private Sketch drawing;
	
	public WhiteboardModel(String boardName, int boardID)  {
		this.drawing = new Sketch();
		this.boardID = boardID;
		this.boardName = boardName;
	}
	
	public int getBoardID(){
		return boardID;
	}
	
	public synchronized String getJSON() {
	    Gson gson = new Gson();
	    String json = gson.toJson(this);
	    return json;
	}
	
	public synchronized String getBoardName()  {
		return boardName;
	}
	
	public synchronized void setBoardName(String name)  {
		boardName = name;
	}
	
	public  synchronized Sketch getSketch()  {
		return drawing;
	}
	
	public synchronized void connectDrawing(Drawing newDrawing) {
	    this.drawing.connect(newDrawing);
	}
	
	public synchronized void clear()  {
		this.drawing.clear();
	}
	
	
}
