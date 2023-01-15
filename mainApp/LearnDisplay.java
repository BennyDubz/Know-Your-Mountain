import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * A special MapDisplay for learn Mode
 * July 8th, 2022
 * Ben Williams
 */
public class LearnDisplay extends MapDisplay {
    private MainGUI parentGUI;
    LearnLevel currLevel;
    private JPanel learnIntro;
    Mode currMode;
    boolean showNamesInLook;
    private LearnLabelPanel labelPanel; // For the "Click on point X", and amount correct label

    public enum Mode {
        LOOK, EASYMC, HARDMC, CUMULATIVE
    }

    public LearnDisplay(BufferedImage image, MainGUI parentGUI) throws IOException {
        super(image, parentGUI);
        this.parentGUI = parentGUI;
        currLevel = levels.get("Area 1");
        currMode = Mode.LOOK;

        // TODO: Make the background of the rest of the panel transparent so that it doesn't need to take up as much space

        labelPanel = new LearnLabelPanel(this);
        labelPanel.setPreferredSize(new Dimension(200, 400));
        labelPanel.setOpaque(false);

        modeButtons();

        this.isOnlyMapDisplay = false;

    }

    /**
     * Creates all the buttons on top of the screen in learn mode and their functions.
     */
    private void modeButtons() {
        JPanel modesPanel = new JPanel();
        modesPanel.setBounds(0, 0, parentGUI.getScreenWidth() * 4, parentGUI.getScreenHeight() / 5);
        modesPanel.setLayout(new FlowLayout());
        modesPanel.setBackground(Color.lightGray);
        this.add(modesPanel, BorderLayout.NORTH);

        ButtonGroup modes = new ButtonGroup();

        /**
         * Defining hide names button earlier, so that switching modes outside "look" can
         * hide this button as it is not relevant.
         */
        JToggleButton toggleNames = new JToggleButton("Hide Names");
        toggleNames.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (toggleNames.isSelected()) {
                    toggleNames.setText("Show Names");
                } else {
                    toggleNames.setText("Hide Names");
                }
            }
        });
        toggleNames.addActionListener(e -> {
            showNamesInLook = !showNamesInLook;
            currLevel.setShowNamesInLook(showNamesInLook);
            repaint();
        });
        showNamesInLook = true;

        JRadioButton lookButton = new JRadioButton("Look");
        lookButton.setFont(new Font(lookButton.getFont().getFontName(), Font.PLAIN, 16));
        lookButton.setSize(20, 20);
        lookButton.addActionListener(e -> {
            currLevel.setMODE(LearnLevel.Mode.LOOK);
            currMode = Mode.LOOK;
            toggleNames.setVisible(true);
            parentGUI.getLearnDisplayHolder().remove(labelPanel);
            repaint();
        });
        lookButton.setSelected(true);

        JRadioButton easyMCButton = new JRadioButton("Easy Multiple Choice");
        easyMCButton.addActionListener(e -> {
            if (currMode != Mode.EASYMC) {
                currLevel.setMODE(LearnLevel.Mode.EASYMC);
                currMode = Mode.EASYMC;
                currLevel.hide();
                parentGUI.getLearnDisplayHolder().add(labelPanel, BorderLayout.EAST);
                labelPanel.modeChange();
                toggleNames.setVisible(false);
                repaint();
            }
        });

        JRadioButton hardMCButton = new JRadioButton("Difficult Multiple Choice");
        hardMCButton.addActionListener(e -> {
            currLevel.setMODE(LearnLevel.Mode.HARDMC);
            currMode = Mode.HARDMC;
            currLevel.hide();
            parentGUI.getLearnDisplayHolder().add(labelPanel, BorderLayout.EAST);
            labelPanel.modeChange();
            toggleNames.setVisible(false);
            repaint();
        });

        JRadioButton cumulativeButton = new JRadioButton("Cumulative");
        cumulativeButton.addActionListener(e -> {
            currLevel.setMODE(LearnLevel.Mode.CUMULATIVE);
            currLevel.hide();
            currMode = Mode.CUMULATIVE;
            parentGUI.getLearnDisplayHolder().add(labelPanel, BorderLayout.EAST);
            labelPanel.modeChange();
            toggleNames.setVisible(false);
            repaint();
        });

        JButton titleScreenButton = new JButton("Back to Title Screen");
        titleScreenButton.addActionListener(e -> {
            parentGUI.titleButtonHandler();
        });

        // Add toggle button for showing ski runs
        JToggleButton toggleRuns = new JToggleButton("Hide Ski Runs");
        toggleRuns.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (toggleRuns.isSelected()) {
                    toggleRuns.setText("Show Ski Runs");
                } else {
                    toggleRuns.setText("Hide Ski Runs");
                }
            }
        });
        toggleRuns.addActionListener(e -> {
            parentGUI.swapImage();
        });

        // Levels dropdown lists all levels
        String[] levelNames = {"Area 1", "Area 2", "Area 3",
                "Area 4", "Area 5", "Area 6", "Area 7", "Area 8", "Area 9", "Area 10"};

        // Creates the combo-box button/dropdown for the levels
        JComboBox levelsButton = new JComboBox(levelNames);
        levelsButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String currentSelection = (String)((JComboBox)e.getSource()).getSelectedItem();
                currLevel.hide();
                currLevel = levels.get(currentSelection);
                currLevel.setShowNamesInLook(showNamesInLook);
                labelPanel.levelChange();
                repaint();
            }
        });

        // Add the buttons to the button group
        modes.add(lookButton);
        modes.add(easyMCButton);
        modes.add(hardMCButton);
        modes.add(cumulativeButton);

        // Add the buttons to the panel
        modesPanel.add(lookButton);
        modesPanel.add(easyMCButton);
        modesPanel.add(hardMCButton);
        modesPanel.add(cumulativeButton);
        modesPanel.add(titleScreenButton);
        modesPanel.add(toggleRuns);
        modesPanel.add(toggleNames);
        modesPanel.add(levelsButton);

        parentGUI.getLearnDisplayHolder().add(modesPanel, BorderLayout.NORTH);

    }

    public Mode getCurrMode() {
        return currMode;
    }

    public LearnLevel getCurrLevel() {
        return currLevel;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currMode == Mode.LOOK) {
            currLevel.draw(g);
        } else {
            labelPanel.handleDraw(g);
        }
    }


}
