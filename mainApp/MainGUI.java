import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

/**
 * @Author Ben Williams, 2022
 * Main GUI class for Ski Patrol map project
 */

public class MainGUI extends JFrame {
    private int screenWidth;
    private int screenHeight;
    private BufferedImage emptyMapImage;
    private BufferedImage fullMapImage;

    // Initial Containers, location information, setup
    private Dimension dimension;
    private Container mainContainer;
    private Container mapContainer;
    private Container learnContainer;
    private Container learnDisplayHolder;
    private MapDisplay displayMapPanel;
    private LearnDisplay learnDisplay;
    private LDIntroScreen ldIntroScreen;

    // Level information
    private HashMap<String, LearnLevel> levels; // This will allow switching of levels using a button on this GUI
    private LearnLevel currLevel;

    // Labels and buttons common across screens
    private PatrolLocation currentlyHovered = null;
    private JLabel hoverLabel;

    public enum appMode {
        TITLE, LEARN, DISPLAY
    }

    private appMode aMode = appMode.TITLE;

    public MainGUI() throws IOException {
        super("Know Your Mountain v1.3");
        this.setResizable(false); // Hopefully I can change this later, but I want to actually work on the application first

        this.emptyMapImage = ImageIO.read(getClass().getResource("emptyMountain.png"));
        this.fullMapImage = ImageIO.read(getClass().getResource("original.png"));

        screenHeight = emptyMapImage.getHeight() / 4;
        screenWidth = emptyMapImage.getWidth() / 4;

        dimension = new Dimension(screenWidth, screenHeight);

        mainContainer = getContentPane();
        mainContainer.setLayout(new CardLayout());

        JPanel titleGUI = (JPanel) titleScreenGUI();
        mainContainer.add(titleGUI, "Title");

        // Create the display map container
        mapContainer = new Container();
        mapContainer.setLayout(new BorderLayout());
        JPanel displayGUI = (JPanel) mapDisplayButtonsGUI();
        mapContainer.add(displayGUI, BorderLayout.NORTH);
        displayMapPanel = initMapPanel(fullMapImage);
        mapContainer.add(displayMapPanel, BorderLayout.CENTER);
        mainContainer.add(mapContainer, "Display");

        // Create Learn container
        learnContainer = new Container();
        learnContainer.setLayout(new BorderLayout());
        JPanel learnGUI = (JPanel) learnButtonsGUI();
        learnContainer.add(learnGUI, BorderLayout.NORTH);

        mainContainer.add(learnContainer, "Learn");

        masterGUIHandler();

        // JFrame initialization
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(dimension);
        pack();
        setVisible(true);
    }

    /**
     * Handles the changing of panels depending on whatever the appMode is.
     * Takes advantage of mainContainer being a cardLayout.
     */
    private void masterGUIHandler() {
        CardLayout cl = (CardLayout) mainContainer.getLayout();
        if (aMode.equals(appMode.TITLE)) {
            cl.show(mainContainer, "Title");
        }
        if (aMode.equals(appMode.DISPLAY)) {
            cl.show(mainContainer, "Display");
        }
        if (aMode.equals(appMode.LEARN)) {
            cl.show(mainContainer, "Learn");
            CardLayout LClayout = (CardLayout) learnContainer.getLayout();
            LClayout.show(learnContainer, "intro");
        }
    }

    private JComponent titleScreenGUI() {
          MainIntroScreen titlescreen = new MainIntroScreen("Know Your Mountain - Jackson Hole Ski Patrol");

          JButton learnButton = titlescreen.addButton(1, 1, 1, "Learn", 100);
          learnButton.setBorder(BorderFactory.createRaisedBevelBorder());
          learnButton.addActionListener(e -> learnButtonHandler());

          JButton displayButton = titlescreen.addButton(2, 1, 1, "Display Map", 100);
          displayButton.setBorder(BorderFactory.createRaisedBevelBorder());
          displayButton.addActionListener(e -> displayButtonHandler());

          return titlescreen;
    }

    private JComponent mapDisplayButtonsGUI() {
        // Back button to title screen
        JButton backButton = new JButton("Back to Title Screen");
        backButton.addActionListener(e -> titleButtonHandler());

        //TODO: Add a "currently hovered over" to Display Map GUI and when Mode is LOOK on LearnDisplay
        JToggleButton swapImage = new JToggleButton("Hide/Show Ski Runs");
        swapImage.addActionListener(e -> swapImage());

        hoverLabel = new JLabel();
        hoverLabel.setFont(new Font(hoverLabel.getFont().getFontName(), Font.BOLD, 20));
        if (currentlyHovered != null) {
            hoverLabel.setText("Your mouse is currently hovering over: "+ currentlyHovered.getName());
        } else {
            hoverLabel.setText("Your mouse is not hovering over any point");
        }

        JComponent gui = new JPanel();
        gui.setLayout(new FlowLayout());
        gui.add(backButton);
        gui.add(swapImage);
        gui.add(hoverLabel);
        gui.setBackground(Color.lightGray);
        return gui;
    }

    private JComponent learnButtonsGUI() throws IOException {
        // Back button to title screen
        JButton titleButton = new JButton("Back to Title Screen");
        titleButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) learnContainer.getLayout();
            cl.show(learnContainer, "intro");
            ldIntroScreen.reset();
            titleButtonHandler();
        });

        learnDisplayHolder = new Container();
        learnDisplayHolder.setLayout(new BorderLayout());
        learnDisplay = initLearnDisplay(fullMapImage);
        learnDisplayHolder.add(learnDisplay, BorderLayout.CENTER);

        learnContainer.setLayout(new CardLayout());
        learnContainer.add(learnDisplayHolder, "learnDisplay");
        ldIntroScreen = new LDIntroScreen(this, learnDisplay);
        learnContainer.add(ldIntroScreen, "intro");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBounds(0, 0, screenWidth, screenHeight / 20);
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.setBackground(Color.lightGray);
        buttonsPanel.add(titleButton);
        ldIntroScreen.add(buttonsPanel, BorderLayout.NORTH);

        CardLayout cl = (CardLayout) learnContainer.getLayout();
        cl.show(learnContainer, "intro");


        // Finally, create the gui, add all buttons/components
        JComponent gui = new JPanel();
        gui.setLayout(new FlowLayout());
        //gui.add(titleButton);
        //gui.add(levelB);
        gui.setBackground(Color.lightGray);

        return gui;
    }

    public MapDisplay initMapPanel(BufferedImage image) throws IOException {
        return new MapDisplay(image, this);
    }

    public LearnDisplay initLearnDisplay(BufferedImage image) throws IOException {
        return new LearnDisplay(image, this);
    }

    /**
     * Handles the press of the "back to title screen" button
     */
    public void titleButtonHandler() {
        aMode = appMode.TITLE;
        masterGUIHandler();
    }


    /**
     * Handles the press of the "Display Map" button
     */
    public void displayButtonHandler() {
        aMode = appMode.DISPLAY;
        masterGUIHandler();
    }

    /**
     * Handles the press of the "Learn" button
     */
    public void learnButtonHandler() {
        aMode = appMode.LEARN;
        ldIntroScreen.reset();
        masterGUIHandler();
    }

    /**
     * Swaps the image between the blank map and the map with all the runs
     */
    public void swapImage() {

        // Swaps image for display's map panel
        if (aMode.equals(appMode.DISPLAY)) {
            if (displayMapPanel.getImage().equals(emptyMapImage)) {
                displayMapPanel.setImage(fullMapImage);
            } else {
                displayMapPanel.setImage(emptyMapImage);
            }
        }

        // Swaps image for learn panel
        if (aMode.equals(appMode.LEARN)) {
            if (learnDisplay.getImage().equals(emptyMapImage)) {
                learnDisplay.setImage(fullMapImage);
            } else {
                learnDisplay.setImage(emptyMapImage);
            }
        }

    }

    /**
     * Method that handles what location is being hovered over, as well as the label on the top
     * @param location
     * @param OnOrOff
     */
    public void setHoveredLocation(PatrolLocation location, Boolean OnOrOff) {
        if (OnOrOff) currentlyHovered = location;
        else currentlyHovered = null;

        if (currentlyHovered != null) {
            hoverLabel.setText("Your mouse is currently hovering over: "+ currentlyHovered.getName());
        } else {
            hoverLabel.setText("Your mouse is not hovering over any point");
        }
    }

    public Container getLearnContainer() {
        return learnContainer;
    }

    public Container getLearnDisplayHolder() {
        return learnDisplayHolder;
    }

    public appMode getaMode() {
        return aMode;
    }

    public LearnLevel getCurrLevel() {
        return currLevel;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");
//        BufferedImage emptyMapImage = ImageIO.read(new File("Assets/emptyMountain.png"));
//        //BufferedImage emptyMapImage = ImageIO.read(new File(MainGUI.class.getResource("Assets/emptyMountain.png").getPath()));
//        BufferedImage fullMapImage = ImageIO.read(new File("Assets/original.png"));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
