package server;

import java.util.ArrayList;

import ADT.Drawing;
import ADT.Sketch;

public class WhiteboardModel {

	private ArrayList<String> subscribers;
	// Implementation note: I changed the ID to an int, it's best practice.
	private final int boardID;
	private String boardName;
	private Drawing drawing;
	
	public WhiteboardModel(String boardName, int boardID)  {
		this.drawing = new Sketch();
		this.subscribers = new ArrayList<String>();
		this.boardID = boardID;
		this.boardName = boardName;
	}
	
	public int getBoardID(){
		return boardID;
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
	
	public void addSubscriber(String user)  {
		subscribers.add(user);
	}
	
	public void removeSubscriber(String user)  {
		subscribers.remove(user);
	}
	
	
}
