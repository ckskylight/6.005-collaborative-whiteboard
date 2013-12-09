package server;

import gson.src.main.java.com.google.gson.Gson;

import ADT.Drawing;
import ADT.Sketch;

public class WhiteboardModel {

	// Implementation note: I changed the ID to an int, it's best practice.
	private final int boardID;
	private String boardName;
	private Sketch drawing;
	
	public WhiteboardModel(String boardName, int boardID)  {
		this.drawing = new Sketch();
//		this.subscribers = new ArrayList<String>();
		this.boardID = boardID;
		this.boardName = boardName;
	}
	
	public int getBoardID(){
		return boardID;
	}
	
	public String getJSON() {
	    Gson gson = new Gson();
	    String json = gson.toJson(this);
	    return json;
	}
	
	public String getBoardName()  {
		return boardName;
	}
	
	public void setBoardName(String name)  {
		boardName = name;
	}
	
	public Drawing getDrawing()  {
		return drawing;
	}
	
	public void connectDrawing(Drawing newDrawing) {
	    this.drawing.connect(newDrawing);
	}
	
	
}
