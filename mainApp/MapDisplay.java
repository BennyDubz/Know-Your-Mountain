import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @Author Some of ZoomablePanel Author Thanasis1101 on github/StackOverflow
 * @Author Ben Williams, 2022
 */

public class MapDisplay extends ZoomablePanel {
    protected ArrayList<PatrolLocation> locations;
    protected MainGUI parentGUI;
    protected HashMap<String, LearnLevel> levels;
    protected boolean isOnlyMapDisplay;
    protected BufferedReader locationsReader;

    public MapDisplay(BufferedImage image, MainGUI parentGUI) throws IOException {
        super(image);
        locations = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream("/patrolLocations");
        //this.locationsReader = new BufferedReader(new FileReader(Objects.requireNonNull(getClass().getResource("patrolLocations")).getFile()));
        locationsReader = new BufferedReader(new InputStreamReader(is));
        locations = patrolLocationArrayList();
        initLevels();
        this.parentGUI = parentGUI;
        this.setLayout(null);
        this.isOnlyMapDisplay = true;
    }

    /**
     *
     * Uses the file with the names of points and their locations on the map as x, y coordinates and
     *  returns an arraylist of patrolLocations
     * @throws IOException
     */
    private ArrayList<PatrolLocation> patrolLocationArrayList() throws IOException {
        ArrayList<PatrolLocation> result = new ArrayList<>();
        String line = locationsReader.readLine();

        while (line != null) {
            // Split the line into half for each part of the location
            String[] halves = line.split("; ");
            String name = halves[0];

            // Get the coordinates by again splitting the second half
            String[] coordinates = halves[1].split(", ");
            Point point = new Point(Integer.valueOf(coordinates[0]), Integer.valueOf(coordinates[1]));

            // Create PatrolLocation and add it to ArrayList
            PatrolLocation location = new PatrolLocation(name, point);
            result.add(location);
            line = locationsReader.readLine();

        }
        return result;
    }

    /**
     * Initialize all 10 levels. The first 9 have 15 points, while the last has 13 points.
     */
    private void initLevels() {
        levels = new HashMap<>();
        LearnLevel previousLevel = null;
        // Creating levels 1-10
        for (int level = 1; level < 11; level++) {
            ArrayList<PatrolLocation> levelPoints = new ArrayList<>();
            for (int point = 0; point < 15; point++) {
                // The 10th level only has 13 points, do not want to go over
                if (level == 10 && point == 13) {
                    break;
                }
                levelPoints.add(locations.get(15 * (level - 1) + point));
            }
            // Creating the level
            LearnLevel newLevel = new LearnLevel(levelPoints, this, previousLevel, "Area "+ level);
            levels.put("Area "+ level, newLevel);
            // Adding it to the map
            previousLevel = newLevel;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        // Do all necessary work in parent class
        super.paintComponent(g);

        // This allows learnDisplay to customize paintComponent without drawing all its locations
        if (isOnlyMapDisplay) {
            // Loop through and draw all locations
            for (int i = 0; i < locations.size(); i++) {
                locations.get(i).draw(g, parentGUI, this, false, false);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        boolean changed = false;
        Point imagePoint = new Point((int) ((e.getPoint().getX() / getZoomFactor()) - getxOffset() / getZoomFactor()), (int) ((e.getPoint().getY() / getZoomFactor()) - getyOffset() / getZoomFactor()));
        // Loop through all locations
        for (int i = 0; i < locations.size(); i++) {
            // If the location's circle contains the mouse point
            if (locations.get(i).contains(imagePoint, getZoomFactor())) {
                // This will display the hover text even if the point has already been clicked
                parentGUI.setHoveredLocation(locations.get(i), true);
                // If it has not already been clicked
                if (!locations.get(i).isClicked()) {
                    // If its not toggled as hovered over, toggle it
                    if (!locations.get(i).isHovered()) {
                        locations.get(i).toggleHovered();
                        changed = true;
                    }
                }
            } else { // If the location does not contain the mouse coordinates, but is hovered, toggle it back
                if (locations.get(i).isHovered()) {
                    locations.get(i).toggleHovered();
                    parentGUI.setHoveredLocation(locations.get(i), false);
                    changed = true;

                }
            }
        }
        // Only repaint if any changes have been made
        if (changed) repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        Point imagePoint = new Point((int) ((e.getPoint().getX() / getZoomFactor()) - getxOffset() / getZoomFactor()), (int) ((e.getPoint().getY() / getZoomFactor()) - getyOffset() / getZoomFactor()));

        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).contains(imagePoint, getZoomFactor())) {
                locations.get(i).toggleClicked();
            }
        }
    }

    public MainGUI getParentGUI() {
        return parentGUI;
    }
}