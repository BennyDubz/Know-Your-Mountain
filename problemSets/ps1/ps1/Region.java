package ps1;

/**
 * Region class holds an ArrayList of points
 *
 * @author Alex Craig, Winter 2022
 * @author Ben Williams, Winter 2022
 */

import java.awt.*;
import java.util.ArrayList;

public class Region {

    private ArrayList<Point> points;

    public Region() {
        points = new ArrayList<Point>();
    }

    public void addPoint(Point point){
        points.add(point);
    }

    public int getSize(){
        return points.size();
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public Point getPoint(int i) {
        return points.get(i);
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    // Allows a region to be reset when appropriate
    public void wipeRegion(){ points = new ArrayList<Point>();}

}

