package ADT;

public class ADT_Test {
    /**
     * This class tests the core functionality of the Sketch and Stroke classes, with tests to be written for 
     * Custom if there is enough time to implement it.  This requires us to test Stroke's constructor,
     * clear, and getImage methods as they fully describe its core functionality.  This will implicitly
     * tests its other setter and getter methods.  I will test creating strokes with positive, negative,
     * absolutely large, small non-zero, and zero values for thickness and x-y start/end coordinates.
     * I will also test clearing Strokes that are or aren't already clear.  Lastly, we test drawing on a
     * blank image and an Image that is already drawn on -- making sure to test the case where a line
     * is drawn over a pre-existing line.
     * 
     * Sketch requires testing the same methods in the same way, but also has a connect() method
     * which adds new Strokes or Sketches to the sketch.  We test Sketch creation to make sure it
     * has an empty list.  We also test connect() with Strokes and Sketches, specifically examining 
     * the overlapping case.  Lastly, we test clearing a Sketch to make sure that all of its contained
     * Drawings become transparent.
     */
    
    /**
     * This tests creating a Stroke with negative values for thickness and for the coordinates of its
     * start and end points.
     */
    public void testStrokeConstructorNegativeValues(){
        
    }
    
    /**
     * This tests creating a Stroke using +/- MAX_INT values for its parameters. 
     */
    public void testStrokeConstructorHugeValues(){
        
    }
    
    /**
     * This tests the normal creation of a Stroke, using expected values for parameters.
     */
    public void testStrokeConstructorNormalVals(){
        
    }
    
    /**
     * Tests clearing a stroke.
     */
    public void testStrokeClearing(){
        
    }
    
    /**
     * Tests a Stroke drawing itself onto a clean image.
     */
    public void testStrokeGetImageBlank(){
        
    }
    
    /**
     * Tests a Stroke drawing itself onto an image which already has things drawn on it, including the 
     * case where the new Stroke overlaps pixels already present on the image.
     */
    public void testStrokeGetImageOverlapping(){
        
    }
    
    /** 
     * This tests that a new Sketch holds an empty List of Drawings.
     */
    public void testSketchConstruction(){
        
    }
    
    /**
     * Tests connecting a Stroke/Sketch to an empty Sketch.
     */
    public void testSketchFirstConnection() {
        
    }
    
    /**
     * Tests adding more Strokes and Sketches to a Sketch that already has some.
     */
    public void testSketchMoreConnections() {
        
    }
    
    public void testSketchClearing() {
        
    }
}
