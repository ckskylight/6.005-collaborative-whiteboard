package ADT;

import java.awt.Image;
import java.util.ArrayList;

public class Sketch implements Drawing {

	private ArrayList<Drawing> sketch;
	private Drawing lastDrawing;

	public Sketch()  {

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Image background) {
		// TODO Auto-generated method stub
		return null;
	}

}

class EmptySketch extends Sketch  {
	public EmptySketch()  {

	}
}
