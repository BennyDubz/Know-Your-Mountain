import java.awt.*;

public class SkiRun {
    public Point start; // Start point of the run
    public Point end; // End point of the run
    public String name; // Name of the run
    private boolean hovered = false; // Boolean for whether the run is being hovered over
    private boolean clicked = false; // Boolean for whether the run has been clicked or not

    public SkiRun(String name, String difficulty, Point start, Point end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    /**
     * Draws the run on the map, different color depending on difficulty
     */
    public void Draw(Graphics g) {

    }
    public boolean Contains(Point p, double zoomFactor) {
        return 10 / zoomFactor > pointToSegmentDistance((int) p.getX(), (int) p.getY(), (int) start.getX(), (int) start.getY(), (int) end.getX(),(int) end.getY());
    }

    /**
     * Taken from CS10 problem set 6
     * Helper method to compute the distance between a point (x,y) and a segment (x1,y1)-(x2,y2)
     * http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
     */
    public static double pointToSegmentDistance(int x, int y, int x1, int y1, int x2, int y2) {
        double l2 = dist2(x1, y1, x2, y2);
        if (l2 == 0) return Math.sqrt(dist2(x, y, x1, y1)); // segment is a point
        // Consider the line extending the segment, parameterized as <x1,y1> + t*(<x2,y2> - <x1,y1>).
        // We find projection of point <x,y> onto the line.
        // It falls where t = [(<x,y>-<x1,y1>) . (<x2,y2>-<x1,y1>)] / |<x2,y2>-<x1,y1>|^2
        double t = ((x-x1)*(x2-x1) + (y-y1)*(y2-y1)) / l2;
        // We clamp t from [0,1] to handle points outside the segment.
        t = Math.max(0, Math.min(1, t));
        return Math.sqrt(dist2(x, y, x1+t*(x2-x1), y1+t*(y2-y1)));
    }

    /**
     * Taken from CS10 problem set 6
     * Euclidean distance squared between (x1,y1) and (x2,y2)
     */
    public static double dist2(double x1, double y1, double x2, double y2) {
        return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
    }

    /**
     * Allows toggling of the hovered boolean
     */
    public void toggleHovered() {
        hovered = !hovered;
    }

    public boolean isHovered() {
        return hovered;
    }

    /**
     * Allows toggling of the clicked boolean
     */
    public void toggleClicked() {
        clicked = !clicked;
    }

    public boolean isClicked() {
        return clicked;
    }
}
