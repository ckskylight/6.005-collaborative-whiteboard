package ADT;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.junit.Test;

public class ADT_Test {
    /**
     * This class tests the core functionality of the Sketch and Stroke classes.  This requires us to test Stroke's constructor and
     * getImage methods as they fully describe its core functionality.  This will implicitly
     * tests its other setter and getter methods.  I will test creating strokes with positive, negative,
     * absolutely large, small non-zero, and zero values for thickness and x-y start/end coordinates.
     * We also test when a Stroke's start and end points are equal.Lastly, we test drawing on a blank image and an Image that is 
     * already drawn on -- making sure to test the case where a line is drawn over a pre-existing line.
     * 
     * Sketch requires testing the same methods in the same way, but also has a connect() and clear() method
     * which adds new Strokes or Sketches to the sketch and removes them all respectively.  We test Sketch 
     * creation to make sure it has an empty list.  We also test connect() with Strokes and Sketches, specifically examining 
     * the overlapping case.  Lastly, we test clearing a Sketch to make sure that all of its contained
     * Drawings are removed.
     */
    
    /*
     * Stroke construction helper method to save me some keystrokes. 
     */
    public Stroke makeStroke(int x1, int y1, int x2, int y2, Color c, float pixels) {
        return new Stroke(new Point(x1, y1), new Point(x2, y2), c, pixels);
    }
    
    /**
     * This tests creating a Stroke with negative values for thickness 
     */
    @Test
    public void testStrokeConstructorNegativeValues(){
        
        try {
            makeStroke(10, 10, 10, 10, Color.GRAY, (float) -10);
            throw new RuntimeException("Accepted negative thickness.");
        } catch (IllegalArgumentException e) {
            System.out.println("Negative thickness caught.");
        }
    }
    
    /**
     * This tests creating a Stroke using +/- MAX_INT values for its parameters. 
     */
    @Test
    public void testStrokeConstructorHugeValues(){
        Stroke hugeStroke = makeStroke(Integer.MAX_VALUE, Integer.MAX_VALUE, 
                                                          Integer.MAX_VALUE, Integer.MAX_VALUE, 
                                                          Color.GRAY, Float.MAX_VALUE);
        Point hugePoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assert(hugeStroke.getStartPoint().equals(hugePoint));
        assert(hugeStroke.getEndPoint().equals(hugePoint));
        assert(hugeStroke.getThickness() == (Float.MAX_VALUE));
    }
    
    /**
     * This tests the normal creation of a Stroke, using expected values for parameters.
     */
    @Test
    public void testStrokeConstructorNormalVals(){
        Stroke normalStroke = makeStroke(10, 20, 30, 40, Color.GRAY, 20);
        assert(normalStroke.getColor().equals(Color.GRAY));
        assert(normalStroke.getStartPoint().equals(new Point(10, 20)));
        assert(normalStroke.getEndPoint().equals(new Point(30, 40)));
        assert(normalStroke.getThickness() == 20);
    }
    
    /**
     * Tests creating a Stroke whose start and end points are in the same spot.
     */
    @Test
    public void testStrokeEqualStartAndEnd(){
        Stroke pointStroke = makeStroke(10, 10, 10, 10, Color.GRAY, 10);
        Point tenPoint = new Point(10, 10);
        assert(pointStroke.getStartPoint().equals(tenPoint));
        assert(pointStroke.getEndPoint().equals(tenPoint));
        assert(pointStroke.getColor().equals(Color.GRAY));
        assert(pointStroke.getThickness() == 10);
    }
    
    /**
     * Tests a Stroke drawing itself onto a clean image.
     */
    @Test
    public void testStrokeGetImageBlank(){
        Stroke testStroke = makeStroke(0, 0, 100, 0, Color.BLACK, 1);
        BufferedImage background = new BufferedImage(100, 100, 5);
        BufferedImage drawnOn = (BufferedImage) testStroke.getImage(background);
        for (int i = 0; i < 100; i++) {
            Color pixelColor = new Color(drawnOn.getRGB(i, 0), true);
            assert(pixelColor.equals(Color.BLACK));
        }
    }
    
    /**
     * Tests a Stroke drawing itself onto an image which already has things drawn on it, including the 
     * case where the new Stroke overlaps pixels already present on the image.
     */
    @Test
    public void testStrokeGetImageOverlapping(){
        BufferedImage background = new BufferedImage(100, 100, 5);
        for (int i = 0; i < 20; i ++) {
            Stroke whatever = makeStroke(i, 0, i * 2, i * 3, Color.BLACK, 1);
            background = (BufferedImage) whatever.getImage(background);
        }
        Stroke overStroke = makeStroke(0, 10, 50, 10, Color.BLUE, 1);
        BufferedImage testableImage = (BufferedImage) overStroke.getImage(background);
        for (int i = 0; i <  50; i++) {
            Color pixelColor = new Color(testableImage.getRGB(i, 10), true);
            assert(pixelColor.equals(Color.BLUE));
        }
    }
    
    /** 
     * This tests that a new Sketch holds an empty List of Drawings.
     */
    @Test
    public void testSketchConstruction(){
        Sketch testSketch = new Sketch();
        int methodSize = testSketch.getSketchSize();
        assert(methodSize == testSketch.getDrawings().size());
    }
    
    /**
     * Tests connecting a Stroke/Sketch to an empty Sketch.
     */
    @Test
    public void testSketchFirstConnection() {
        Sketch testSketch = new Sketch();
        Stroke testStroke = makeStroke(0,  0,  100,  100,  Color.GRAY,  5);
        testSketch.connect(testStroke);
        assert(testSketch.getSketchSize() == 1);
        Sketch blankSketch = new Sketch();
        testSketch.connect(blankSketch);
        assert(testSketch.getSketchSize() == 2);
        Sketch another = new Sketch();
        Stroke anotherStroke = makeStroke(1, 1, 5, 5, Color.GRAY, 4);
        another.connect(anotherStroke);
        testSketch.connect(another);
        assert(testSketch.getSketchSize() == 3);
        
    }
    
    /**
     * Tests adding more Strokes and Sketches to a Sketch that already has some.
     */
    @Test
    public void testSketchMoreConnections() {
        Sketch testSketch = new Sketch();
        for (int i = 0; i < 20; i++) {
            testSketch.connect(makeStroke(i, 2* i, 3 * i, 4 * i, Color.GRAY, 3));
        }
        assert (testSketch.getSketchSize() == 20);
        testSketch.connect(makeStroke(0, 0, 100, 100, Color.GRAY, 10));
        assert(testSketch.getSketchSize() == 21);
        testSketch.connect(new Sketch());
        assert(testSketch.getSketchSize() == 22);
    }
    
    /**
     * Tests that clearing a sketch, with or without drawings on it, will render every pixel white.
     */
    @Test
    public void testSketchClearing() {
        Sketch testSketch = new Sketch();
        for (int i = 0; i < 20; i++) {
            testSketch.connect(makeStroke(i, 2* i, 3 * i, 4 * i, Color.GRAY, 3));
        }
        testSketch.clear();
        BufferedImage background = new BufferedImage(100, 100, 5);
        testSketch.getImage(background);
        Color testColor = new Color(0, 0, 0, 0);
        for (int i = 0; i < 100; i ++) {
            for (int j = 0; j < 100; j++) {
                assert(new Color(background.getRGB(i, j), true).equals(testColor));
            }
        }
    }
}
