import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @Author Ben Williams, March 2022
 * A class representing a specific location on the map
 */
public class PatrolLocation {
    public Point coordinates;
    public String name;
    private int diameter = 36;
    private boolean hovered = false;
    private boolean clicked = false;
    private JLabel label;
    private boolean labelActive = false;
    private boolean incorrectMarked;
    private boolean correctMarked;

    public PatrolLocation(String name, Point coordinates) {
        this.name = name;
        this.coordinates = coordinates;

        // Initializing the label that will only be shown in certain circumstances
        label = new JLabel();
        label.setOpaque(true);
        label.setBackground(new Color(210,210,210));
        label.setText(name);
        label.setBorder(new LineBorder(Color.BLACK, 2));
        label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, 30));
        //label.setBackground(new Color(215, 215, 215));
        Dimension size = label.getPreferredSize();
        label.setBounds((int) coordinates.getX() -5, (int) coordinates.getY() - 50, (int) size.getWidth() + 5, (int) size.getHeight());

        correctMarked = false;
        incorrectMarked = false;
    }

    /**
     * For drawing the location
     */
    public void draw(Graphics g, MainGUI parentGUI, JPanel parentPanel, boolean showNames, boolean lookMode) {
        if (correctMarked) {
            correctMark(g, parentPanel);
            return;
        }

        if (incorrectMarked) {
            incorrectMark(g, parentPanel);
            return;
        }

        if (clicked) { // Set color to green if it is clicked
            g.setColor(new Color(0,150,255));
            g.fillOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);

            // Will only show the location's name above the point if in Display Map Mode
            if (parentGUI.getaMode().equals(MainGUI.appMode.DISPLAY) || lookMode) {
                // Draws a light rectangle as a background to the text
                parentPanel.add(label);
                labelActive = true;
            }

        } else if (hovered) { // Set color to yellow if it is hovered over
            g.setColor(new Color(210, 210, 29));
            g.fillOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);

            //TODO: Add a "currently hovered over" to Display Map GUI and when Mode is LOOK on LearnDisplay
            //Will only show the location's name above the point if in Display Map Mode
            if (parentGUI.getaMode().equals(MainGUI.appMode.DISPLAY) || lookMode) {
                parentPanel.add(label);
                labelActive = true;
            }

        } else { // Otherwise, leave the point red
            //g.setColor(new Color(225, 50, 17));
            //g.setColor(Color.black);
            g.setColor(new Color(225, 50, 17));
            g.fillOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);

            // If there is an unwanted label here
            if (labelActive) {
                parentPanel.remove(label);
            }
        }
        // At the end so that it will always be drawn if this is true.
        if (showNames) {
            parentPanel.add(label);
            labelActive = true;
        }

        // Draw a black border around the point
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.black);
        g2.drawOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);
    }

    // TODO: Add "Draw X" and "Draw Box" to show the incorrect and correct points.

    /**
     * Draws an X over the point to mark it as incorrect and re-draws the point as red
     * Shows the label
     * @param g
     */
    public void incorrectMark(Graphics g, JPanel parentPanel) {
        // Draws the point red
        g.setColor(new Color(225, 50, 17));
        g.fillOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);

        // Draws the X over the point
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(5));
        g2.drawLine((int) (coordinates.getX() -25), (int)coordinates.getY()-25, (int)coordinates.getX()+25, (int)coordinates.getY()+25);
        g2.drawLine((int) coordinates.getX() - 25, (int) coordinates.getY()+25, (int)coordinates.getX()+25, (int) coordinates.getY()-25);

        // Draw the border over the point that is normally there
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.black);
        g2.drawOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);

        showLabel(parentPanel);
        incorrectMarked = true;
    }

    /**
     * Draws the point green and draws a box over it to show that it is correct
     * Shows the label
     * @param g
     */
    public void correctMark(Graphics g, JPanel parentPanel) {

        // Draws the point green
        g.setColor(new Color(50,180,50));
        g.fillOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);

        // Draws the box
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(5));
        g2.drawRect((int) coordinates.getX()-25, (int) coordinates.getY()-25, 50, 50);
        showLabel(parentPanel);

        // Draws the border around the point that is normally there
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.black);
        g2.drawOval((int) coordinates.getX() - diameter/2, (int) coordinates.getY() - diameter/2, diameter, diameter);

        correctMarked = true;
    }

    /**
     * @param p - Point in question (most likely mouse coordinates)
     * @param p - Zoomfactor for scaling a small buffer in the click
     * @return boolean of if point p is in the location's oval
     */
    public boolean contains(Point p, double zoomFactor) {
        return (coordinates.getX()-p.getX())*(coordinates.getX()-p.getX()) + (coordinates.getY()-p.getY())*(coordinates.getY()-p.getY()) <= ((diameter + 3)/2 * (diameter + 3)/2);
    }

    public void showLabel(JPanel parentPanel) {
        if (!labelActive) {
            parentPanel.add(label);
            labelActive = true;
        }
    }

    public void hideLabel(JPanel parentPanel) {
        if (labelActive) {
            parentPanel.remove(label);
            labelActive = false;
        }
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

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public String getName() {
        return name;
    }

    public void setCorrectMarked(boolean correctMarked) {
        this.correctMarked = correctMarked;
    }

    public void setIncorrectMarked(boolean incorrectMarked) {
        this.incorrectMarked = incorrectMarked;
    }
}
