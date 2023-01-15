import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.metal.MetalLabelUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

/**
 * @Author Ben Williams
 * May 3rd, 2022
 * January 4th 2023 - Ended up not using this for every introscreen, so made minor changes. Would probably just make a
 *  specific introscreen manually rather than having customizable methods next time for a project of this size.
 *
 * Basic customizable mainIntroScreen for each Mode with a title and an undefined amount of text
 *
 */
public class MainIntroScreen extends JPanel {
    private JLabel titleLabel; // The JLabel for the title itself
    private int currGridY = 0; // Used so that added text will always go below other added text

    public MainIntroScreen(String title) {
        // Creating JPanel
        super(new GridBagLayout());
        //setBackground(new Color(119, 136, 153));
        //setBackground(new Color(165, 0, 0));
        //setBackground(Color.RED);
        // Creating the title
        this.titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setFont(new Font(titleLabel.getFont().getFontName(), Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        titleLabel.setBackground(new Color(220,220,220));
        //titleLabel.setBackground(Color.LIGHT_GRAY);

        // Uses compound border to add spacing between the title and its border
        Border titleBorder = titleLabel.getBorder();
        Border margin = new EmptyBorder(0,25,0,25);
        titleLabel.setBorder(new CompoundBorder(titleBorder, margin));

        // Adding the constraints so that it is at the top and centered, based off MainGUI
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; // Think of as row, column for x,y
        constraints.gridy = 0;
        currGridY++;
        constraints.gridwidth = 5; // Makes it take up the width of five columns
        constraints.ipady = 100; // Adds padding to the top and bottom of component
        constraints.weighty = 0.7;

        this.add(titleLabel, constraints);

    }


    /**
     *
     * @param gridX - X location on GUI grid
     * @param gridY - Y location on GUI grid
     * @param gridWidth - How many slots it will take in the X grid
     * @param text - The text displayed on the button
     * @return - The button *without* a handler function implemented.
     */
    public JButton addButton(int gridX, int gridY, int gridWidth, String text, int pady)  {
        // Initializing the button
        JButton button = new JButton(text);
        button.setFont(new Font(button.getFont().getFontName(), Font.BOLD, 25));
        button.setPreferredSize(new Dimension(100, 50));
        //button.setBackground(Color.WHITE);
        button.setOpaque(true);
        //button.setUI(new MetalButtonUI());
        button.setBackground(Color.white);

        // Creating the constraints for adding it to GUI
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipady = pady;
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.gridwidth = gridWidth;
        constraints.insets = new Insets(75,50,75,50); // Adds external buffer to other components
        constraints.weightx = 0.5;

        this.add(button, constraints);

        return button;
    }


}
