package ps6;

import java.awt.*;
import java.util.ArrayList;

import static ps6.Segment.pointToSegmentDistance;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
	private ArrayList<Point> points = new ArrayList<>();
	private Color color;
	private int strokeWeight;
	private int containsBuffer;

	/**
	 * Initial Polyline with only one point
	 */
	public Polyline(int x, int y, int strokeWeight, Color color) {
		Point firstPoint = new Point(x,y);
		points.add(firstPoint);
		this.color = color;
		this.strokeWeight = strokeWeight;
		this.containsBuffer = strokeWeight + 5;
	}

	/**
	 * Polyline with multiple points
	 */
	public Polyline(ArrayList points, int strokeWeight, Color color) {
		this.points = points;
		this.color = color;
		this.strokeWeight = strokeWeight;
		this.containsBuffer = strokeWeight + 5;
	}

	public void addPoint(int x, int y) {
		Point newPoint = new Point(x, y);
		points.add(newPoint);
	}

	/**
	 * @return a string of the type of shape that this is
	 * In this case, changed to "freehand" to match editor
	 */
	@Override
	public String getType() {
		return "freehand";
	}

	@Override
	public void moveBy(int dx, int dy) {
		for (Point point : points) {
			point.x += dx;
			point.y += dy;
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public int getStrokeWeight() {
		return strokeWeight;
	}

	@Override
	public boolean contains(int x, int y) {
		// Loop through all points, if the x and y parameters are within a certain range of any of the points
		for (int i = 0; i < points.size()-1; i++) {
			if (pointToSegmentDistance(x, y, points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y) < containsBuffer) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(strokeWeight));
		for (int i = 0; i < points.size()-1; i++) {
			g2.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
		}
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	@Override
	public String toString() {
		String coords = new String();
		for (Point p : points) {
			coords += " " + p.x + "," + p.y;
		}
		return ("polyline: " + coords);
	}
}