package model;

import java.util.ArrayList;

import ADT.Drawing;
import ADT.Sketch;

public class WhiteboardModel {

	private ArrayList<String> subscribers;
	private final String boardID;
	private String boardName;
	private Sketch drawing;
	
	public WhiteboardModel(String boardName, String boardID)  {
		this.drawing = new Sketch();
		this.subscribers = new ArrayList<String>();
		this.boardID = boardID;
		this.boardName = boardName;
	}
	
	public String getBoardID(){
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
