package ps2;

import java.util.ArrayList;
import static ps2.Geometry.*;

/**
 * A point quadtree: stores an element at a 2D position,
 * with children at the subdivided quadrants.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 *
 */
public class PointQuadtree<E extends Point2D> {
    private E point;							// the point anchoring this node
    private int x1, y1;							// upper-left corner of the region
    private int x2, y2;							// bottom-right corner of the region
    private PointQuadtree<E> c1, c2, c3, c4;	// children

    /**
     * Initializes a leaf quadtree, holding the point in the rectangle
     */
    public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
        this.point = point;
        this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
    }

    // Getters

    public E getPoint() {
        return point;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    /**
     * Returns the child (if any) at the given quadrant, 1-4
     * @param quadrant	1 through 4
     */
    public PointQuadtree<E> getChild(int quadrant) {
        if (quadrant==1) return c1;
        if (quadrant==2) return c2;
        if (quadrant==3) return c3;
        if (quadrant==4) return c4;
        return null;
    }

    /**
     * Returns whether or not there is a child at the given quadrant, 1-4
     * @param quadrant	1 through 4
     */
    public boolean hasChild(int quadrant) {
        return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
    }

    /**
     * Checks whether tree has children
     * @return true if at least one child, false if no children
     */
    public boolean hasChildren() {
        if (this.hasChild(1) || this.hasChild(2) || this.hasChild(3) || this.hasChild(4)) {
            return true;
        }
        return false;
    }

    /**
     * Inserts the point into the tree
     */
    public void insert(E p2) {

        // Quadrant 1
        if (p2.getX() > this.point.getX() && p2.getY() < this.point.getY()) {
            if (c1 == null){
                c1 = new PointQuadtree<E>(p2, (int)this.point.getX(), this.getY1(), this.getX2(), (int)this.point.getY());
            }
            c1.insert(p2);
        }

        // Quadrant 2
        if (p2.getX() < this.point.getX() && p2.getY() < this.point.getY()) {
            if (c2 == null){
                c2 = new PointQuadtree<E>(p2, this.getX1(), this.getY1(), (int)this.point.getX(), (int)this.point.getY());
            }
            c2.insert(p2);
        }

        // Quadrant 3
        if (p2.getX() < this.point.getX() && p2.getY() > this.point.getY()) {
            if (c3 == null){
                c3 = new PointQuadtree<E>(p2, this.getX1(), (int)this.point.getY(), (int)this.point.getX(), this.getY2());
            }
            c3.insert(p2);
        }

        // Quadrant 4
        if (p2.getX() > this.point.getX() && p2.getY() > this.point.getY()) {
            if (c4 == null){
                c4 = new PointQuadtree<E>(p2, (int)this.point.getX(), (int)this.point.getY(), this.getX2(), this.getY2());
            }
            c4.insert(p2);
        }

    }

    /**
     * Finds the number of points in the quadtree (including its descendants)
     */
    public int size() {
        int sum = 1;

        // Looking for c1
        if (this.hasChild(1)) {
            // Recursively call c1's size method
            sum += c1.size();
        }

        // Looking for c2
        if (this.hasChild(2)) {
            // Recursively call c2's size method
            sum += c2.size();
        }

        // Looking for c3
        if (this.hasChild(3)) {
            // Recursively call c3's size method
            sum += c3.size();
        }

        // Looking for c4
        if (this.hasChild(4)) {
            // Recursively call c4's size method
            sum += c4.size();
        }

        return sum;
    }

    /**
     * Builds a list of all the points in the quadtree (including its descendants)
     */
    public ArrayList<E> allPoints() {
        ArrayList<E> points = new ArrayList<E>();
        points.add(this.point);

        // Looking for c1
        if (this.hasChild(1)) {
            // Recursively call c1's allPoints method
            ArrayList<E> c1Points = c1.allPoints();

            // Add each point within child's arrayList of points to this array list of points
            for (E point : c1Points){
                points.add(point);
            }
        }

        // Looking for c2
        if (this.hasChild(2)) {
            // Recursively call c2's allPoints method
            ArrayList<E> c2Points = c2.allPoints();

            // Add each point within child's arrayList of points to this array list of points
            for (E point : c2Points){
                points.add(point);
            }
        }

        // Looking for c3
        if (this.hasChild(3)) {
            // Recursively call c3's allPoints method
            ArrayList<E> c3Points = c3.allPoints();

            // Add each point within child's arrayList of points to this array list of points
            for (E point : c3Points){
                points.add(point);
            }
        }

        // Looking for c4
        if (this.hasChild(4)) {
            // Recursively call c4's allPoints method
            ArrayList<E> c4Points = c4.allPoints();

            // Add each point within child's arrayList of points to this array list of points
            for (E point : c4Points) {
                points.add(point);
            }

        }
        return points;

    }

    /**
     * Uses the quadtree to find all points within the circle
     * @param cx	circle center x
     * @param cy  	circle center y
     * @param cr  	circle radius
     * @return    	the points in the circle (and the qt's rectangle)
     */
    public ArrayList<E> findInCircle(double cx, double cy, double cr) {
        ArrayList<E> hitPoints = new ArrayList<E>();

        // If circle is within point's rectangle
        if (circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
            // If point is within circle, add it to hitPoints
            if (pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
                hitPoints.add(this.point);
            }

            // Check the first quadrant for points in the circle
            if (this.hasChild(1)) {
                ArrayList<E> c1Points = c1.findInCircle(cx, cy, cr);

                // Add all clicked points (if any) to hitPoints
                for (E p : c1Points) {
                    hitPoints.add(p);
                }
            }

            // Check the second quadrant for points in the circle
            if (this.hasChild(2)) {
                ArrayList<E> c2Points = c2.findInCircle(cx, cy, cr);

                // Add all clicked points (if any) to hitPoints
                for (E p : c2Points) {
                    hitPoints.add(p);
                }
            }

            // Check the third quadrant for points in the circle
            if (this.hasChild(3)) {
                ArrayList<E> c3Points = c3.findInCircle(cx, cy, cr);

                // Add all clicked points (if any) to hitPoints
                for (E p : c3Points) {
                    hitPoints.add(p);
                }
            }

            // Check the fourth quadrant for points in the circle
            if (this.hasChild(4)) {
                ArrayList<E> c4Points = c4.findInCircle(cx, cy, cr);

                // Add all clicked points (if any) to hitPoints
                for (E p : c4Points) {
                    hitPoints.add(p);

                }
            }
        }

        return hitPoints;
    }

    /**
     * @param cx - Center of rectangle x
     * @param cy - Center of rectangle y
     * @param rw - rectangle width
     * @param rh - rectangle height
     * @return ArrayList of points within the rectangle
     */
    public ArrayList<E> findInRectangle(double cx,double cy, int rw, int rh){
        ArrayList<E> hitPoints = new ArrayList<E>();

        // If circle is within point's rectangle
        if (rectangleIntersectsRectangle(cx, cy, rw,rh, x1, y1, x2, y2)) {
            // If point is within rectangle, add it to hitPoints
            if (pointInRectangle(point.getX(), point.getY(), cx, cy, rw, rh)) {
                hitPoints.add(this.point);
            }

            // Check the first quadrant for points in the rectangle
            if (this.hasChild(1)) {
                ArrayList<E> c1Points = c1.findInRectangle(cx, cy, rw, rh);

                // Add all clicked points (if any) to hitPoints
                for (E p : c1Points) {
                    hitPoints.add(p);
                }
            }

            // Check the second quadrant for points in the rectangle
            if (this.hasChild(2)) {
                ArrayList<E> c2Points = c2.findInRectangle(cx, cy, rw, rh);

                // Add all clicked points (if any) to hitPoints
                for (E p : c2Points) {
                    hitPoints.add(p);
                }
            }

            // Check the third quadrant for points in the rectangle
            if (this.hasChild(3)) {
                ArrayList<E> c3Points = c3.findInRectangle(cx, cy, rw, rh);

                // Add all clicked points (if any) to hitPoints
                for (E p : c3Points) {
                    hitPoints.add(p);
                }
            }

            // Check the fourth quadrant for points in the rectangle
            if (this.hasChild(4)) {
                ArrayList<E> c4Points = c4.findInRectangle(cx, cy, rw, rh);

                // Add all clicked points (if any) to hitPoints
                for (E p : c4Points) {
                    hitPoints.add(p);

                }
            }
        }
        return hitPoints;
    }
}