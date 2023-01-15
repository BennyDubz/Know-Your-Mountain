package ps2;
/**
 * Geometry helper methods
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Fall 2016, separated from quadtree, instrumented to count calls
 * 
 */
public class Geometry {
	private static int numInCircleTests = 0;			// keeps track of how many times pointInCircle has been called
	private static int numCircleRectangleTests = 0;		// keeps track of how many times circleIntersectsRectangle has been called
		
	public static int getNumInCircleTests() {
		return numInCircleTests;
	}

	public static void resetNumInCircleTests() {
		numInCircleTests = 0;
	}

	public static int getNumCircleRectangleTests() {
		return numCircleRectangleTests;
	}

	public static void resetNumCircleRectangleTests() {
		numCircleRectangleTests = 0;
	}

	/**
	 * Returns whether or not the point is within the circle
	 * @param px		point x coord
	 * @param py		point y coord
	 * @param cx		circle center x
	 * @param cy		circle center y
	 * @param cr		circle radius
	 */
	public static boolean pointInCircle(double px, double py, double cx, double cy, double cr) {
		numInCircleTests++;
		return (px-cx)*(px-cx) + (py-cy)*(py-cy) <= cr*cr;
	}

	/**
	 * Returns whether or not the point is within a rectangle.
	 * @param px - point x
	 * @param py - point y
	 * @param rx - rectangle center x
	 * @param ry - rectangle center y
	 * @param rw - rectangle width
	 * @param rh - rectangle height
	 */
	public static boolean pointInRectangle(double px, double py, double rx, double ry, int rw, int rh) {
		// Could have done this in return statement, but this is more readable
		int rLeftX = (int)(rx - (rw/2));
		int rRightX = (int)(rx + (rw/2));

		int rTopY = (int)(ry-(rh/2));
		int rBottomY = (int)(ry + (rh/2));

		return (rLeftX < px && rRightX > px && rTopY < py && rBottomY > py);

	}

	/**
	 * Returns whether or not the circle intersects the rectangle
	 * Based on discussion at http://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
	 * @param cx	circle center x
	 * @param cy	circle center y
	 * @param cr	circle radius
	 * @param x1 	rectangle min x
	 * @param y1  	rectangle min y
	 * @param x2  	rectangle max x
	 * @param y2  	rectangle max y
	 */
	public static boolean circleIntersectsRectangle(double cx, double cy, double cr, double x1, double y1, double x2, double y2) {
		numCircleRectangleTests++;
		double closestX = Math.min(Math.max(cx, x1), x2);
		double closestY = Math.min(Math.max(cy, y1), y2);
		return (cx-closestX)*(cx-closestX) + (cy-closestY)*(cy-closestY) <= cr*cr;
	}

	/**
	 *
	 * @param rx - Questioned rectangle's center x
	 * @param ry - Questioned rectangle's center y
	 * @param rw - Questioned rectangle's width
	 * @param rh - Questioned rectangle's height
	 *
	 * @param x1 - Point's x1
	 * @param y1 - Point's y1
	 * @param x2 - Point's x2
	 * @param y2 - Point's y2
	 * @return whether or not the questioned rectangle intersects the Point's rectangle
	 *
	 */
	public static boolean rectangleIntersectsRectangle(double rx,double ry, int rw, int rh, int x1, int y1, int x2, int y2){
		int rQuestionedLeftX = (int)(rx - (rw/2));
		int rQuestionedRightX = (int)(rx + (rw/2));
		int rQuestionedTopY = (int)(ry-(rh/2));
		int rQuestionedBottomY = (int)(ry + (rh/2));

		// If Either x value falls within range
		if ((rQuestionedLeftX > x1 && rQuestionedLeftX < x2) || (rQuestionedRightX > x1 && rQuestionedRightX < x2)){
			// If either y value falls within range
			if ((rQuestionedTopY > y1 && rQuestionedTopY < y2) || (rQuestionedBottomY > y1 && rQuestionedBottomY < y2)){
				return true;
			}
		}
		return false;
	}
}
