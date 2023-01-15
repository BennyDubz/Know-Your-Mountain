import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A JPanel specifically made for the different modes in Learn
 * December 7th, 2022
 * Author: Ben Williams
 */
public class LearnLabelPanel extends JPanel {
    LearnLevel currLevel;
    LearnDisplay LD;
    LearnDisplay.Mode currMode;
    JPanel promptPanel;
    JLabel titleLabel;
    JLabel promptLabel;
    JLabel scoreLabel;
    int score;
    int scoreOutOf;

    // All having to do with the multiple choice setup
    int indexMC;
    int incorrectIndex1;
    int incorrectIndex2;
    int incorrectIndex3;
    boolean indexChanged;

    // Keys
    boolean spacePressed;

    private boolean incorrectLimbo;
    private boolean correctLimbo;

    public LearnLabelPanel(LearnDisplay LD) {
        super();
        this.setBackground(Color.lightGray);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        currMode = LD.getCurrMode(); // Set as default, but will be changed.
        currLevel = LD.getCurrLevel();
        this.LD = LD;
        initLabels();

        incorrectLimbo = false;
        correctLimbo = false;
        indexMC = 0;
        score = 0;
        scoreOutOf = 0;
        indexChanged = true;
        incrementScoreLabel();

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spacePressed = true;
                LD.repaint();
            }
        };

        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spaceAction");

        ActionMap actionMap = this.getActionMap();
        actionMap.put("spaceAction", action);
        setFocusable(true);
        requestFocusInWindow();

    }

    public void initLabels() {
        titleLabel = new JLabel();
        promptLabel = new JLabel();
        scoreLabel = new JLabel();

        titleLabel.setBorder(new LineBorder(Color.black));
        titleLabel.setFont(new Font(titleLabel.getFont().getFontName(), Font.BOLD, 15));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        promptPanel = new JPanel();
        promptPanel.setLayout(new GridBagLayout());
        promptLabel.setHorizontalAlignment(SwingConstants.CENTER);

        promptLabel.setBorder(new LineBorder(Color.black));
        promptLabel.setMaximumSize(new Dimension(220,200));
        promptLabel.setFont(new Font(promptLabel.getFont().getFontName(), Font.BOLD, 14));
        promptPanel.add(promptLabel);

        scoreLabel.setBorder(new LineBorder(Color.black));
        scoreLabel.setFont(new Font(scoreLabel.getFont().getFontName(), Font.BOLD, 15));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //titleLabel.
        //titleLabel.setBorder(new CompoundBorder(new EmptyBorder(titleLabel.getX(), titleLabel.getY(), )));

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(promptPanel, BorderLayout.CENTER);
        this.add(scoreLabel, BorderLayout.SOUTH);
    }

    public void modeChange() {
        indexMC = 0;
        score = 0;
        scoreOutOf = 0;
        incrementScoreLabel();
        LD.currLevel.shuffleSolution();

        if (LD.getCurrMode() == LearnDisplay.Mode.EASYMC) {
            titleLabel.setText("Easy Multiple Choice");
            currLevel.hide();
        }

        if (LD.getCurrMode() == LearnDisplay.Mode.HARDMC) {
            titleLabel.setText("Difficult Multiple Choice");
            currLevel.hide();
        }

        if (LD.getCurrMode() == LearnDisplay.Mode.CUMULATIVE) {
            titleLabel.setText("Cumulative");
            currLevel.hide();
        }

    }

    public void levelChange() {
        currLevel = LD.getCurrLevel();
        incorrectLimbo = false;
        correctLimbo = false;
        score = 0;
        indexMC = 0;
        scoreOutOf = 0;
        incrementScoreLabel();
    }

    public void handleDraw(Graphics g) {

        if (LD.getCurrMode() == LearnDisplay.Mode.EASYMC) {
            easyMCHandler(g);
        } else if (LD.getCurrMode() == LearnDisplay.Mode.HARDMC) {
            difficultMCHandler(g);
        } else if (LD.getCurrMode() == LearnDisplay.Mode.CUMULATIVE) {
            cumulativeHandler(g);
        }
    }

    /**
     * Handles the drawing, prompting, and scoring for the easy MC mode
     * @param g
     */
    private void easyMCHandler(Graphics g) {
        requestFocus();

        // Iterate through all the points in the solution
        if (indexMC < currLevel.getSolutionsMC().size()) {

            // Only run once
            if (indexChanged) {
                incorrectIndex1 = (int) Math.floor(Math.random() * currLevel.getSolutionsMC().size());
                incorrectIndex2 = (int) Math.floor(Math.random() * currLevel.getSolutionsMC().size());
                incorrectIndex3 = (int) Math.floor(Math.random() * currLevel.getSolutionsMC().size());

                // No overlap!
                while (incorrectIndex1 == indexMC || incorrectIndex2 == indexMC || incorrectIndex3 == indexMC || incorrectIndex1 == incorrectIndex2 || incorrectIndex1 == incorrectIndex3 || incorrectIndex2 == incorrectIndex3 ) {
                    incorrectIndex1 = (int) Math.floor(Math.random() * currLevel.getSolutionsMC().size());
                    incorrectIndex2 = (int) Math.floor(Math.random() * currLevel.getSolutionsMC().size());
                    incorrectIndex3 = (int) Math.floor(Math.random() * currLevel.getSolutionsMC().size());
                }
                indexChanged = false;
            }
            currLevel.getSolutionsMC().get(indexMC).draw(g, LD.getParentGUI(), LD, false, false);
            currLevel.getSolutionsMC().get(incorrectIndex1).draw(g, LD.getParentGUI(), LD, false, false);
            currLevel.getSolutionsMC().get(incorrectIndex2).draw(g, LD.getParentGUI(), LD, false, false);
            currLevel.getSolutionsMC().get(incorrectIndex3).draw(g, LD.getParentGUI(), LD, false, false);

            if (! incorrectLimbo && ! correctLimbo) {
                promptLabel.setText("<html>Click on:<br/> " + currLevel.getSolutionsMC().get(indexMC).getName() + "</html>");
            }

            // Correct limbo allows user to "unclick" the correct point and still go into this if statement before they press space
            if (!incorrectLimbo && (correctLimbo || (currLevel.getSolutionsMC().get(indexMC).isClicked() && !currLevel.getSolutionsMC().get(incorrectIndex1).isClicked() && !currLevel.getSolutionsMC().get(incorrectIndex2).isClicked() && !currLevel.getSolutionsMC().get(incorrectIndex3).isClicked()))) {
                // For correct answer
                currLevel.getSolutionsMC().get(indexMC).correctMark(g, LD);
                currLevel.getSolutionsMC().get(incorrectIndex1).incorrectMark(g, LD);
                currLevel.getSolutionsMC().get(incorrectIndex2).incorrectMark(g, LD);
                currLevel.getSolutionsMC().get(incorrectIndex3).incorrectMark(g, LD);

                // Only run once when correct point is picked
                if (! correctLimbo) {
                    promptLabel.setText("<html>Correct<br>Press space to continue</html>");
                    score++;
                    scoreOutOf++;
                    incrementScoreLabel();
                    correctLimbo = true;
                }

                // When space is pressed, move on
                if (spacePressed) {
                    indexMC++;
                    indexChanged = true;
                    currLevel.hide();
                    spacePressed = false;
                    correctLimbo = false;
                    LD.repaint();
                }

            } else if (!correctLimbo && (incorrectLimbo || (currLevel.getSolutionsMC().get(incorrectIndex1).isClicked() || currLevel.getSolutionsMC().get(incorrectIndex2).isClicked() || currLevel.getSolutionsMC().get(incorrectIndex3).isClicked()))) {
                // For incorrect answers
                currLevel.getSolutionsMC().get(indexMC).correctMark(g, LD);
                currLevel.getSolutionsMC().get(incorrectIndex1).incorrectMark(g, LD);
                currLevel.getSolutionsMC().get(incorrectIndex2).incorrectMark(g, LD);
                currLevel.getSolutionsMC().get(incorrectIndex3).incorrectMark(g, LD);
                incrementScoreLabel();

                // Only run once when incorrect point is picked
                if (! incorrectLimbo) {
                    promptLabel.setText("<html>Incorrect<br>Press space to continue</html>");
                    scoreOutOf++;
                    incrementScoreLabel();
                    incorrectLimbo = true;
                }

                if (spacePressed) {
                    indexMC++;
                    indexChanged = true;
                    currLevel.hide();
                    spacePressed = false;
                    incorrectLimbo = false;
                    LD.repaint();
                }
            }
            // Don't let the spacePressed carry over and have unintended effects
            if (spacePressed) spacePressed = false;
        } else {
            promptLabel.setText("Completed all points");
        }
    }

    /**
     * Handles the drawing, prompting, and scoring for the difficult MC mode
     * @param g
     */
    private void difficultMCHandler(Graphics g) {
        requestFocus();

        // If not all points have been looped over
        if (indexMC < currLevel.getSolutionsMC().size()) {
            currLevel.namelessDrawMC(g);

            if (indexChanged) {
                promptLabel.setText("<html>Click on:<br/> " + currLevel.getSolutionsMC().get(indexMC).getName() + "</html>");
                indexChanged = false;
            }

            if (! incorrectLimbo && ! correctLimbo) {
                for (PatrolLocation location : currLevel.getSolutionsMC()) {
                    if (location.isClicked()) {
                        if (location == currLevel.getSolutionsMC().get(indexMC)) {
                            if (! correctLimbo) {
                                promptLabel.setText("<html>Correct<br>Press space to continue</html>");
                                score++;
                                scoreOutOf++;
                                incrementScoreLabel();
                                correctLimbo = true;
                                currLevel.getSolutionsMC().get(indexMC).correctMark(g, LD);
                            }
                        } else {
                            if (! incorrectLimbo) {
                                promptLabel.setText("<html>Incorrect<br>Press space to continue</html>");
                                scoreOutOf++;
                                incrementScoreLabel();
                                incorrectLimbo = true;
                                location.incorrectMark(g, LD);
                                currLevel.getSolutionsMC().get(indexMC).correctMark(g, LD);
                            }
                        }
                    }
                }
            }

            if (spacePressed && (incorrectLimbo || correctLimbo)) {
                indexMC++;
                indexChanged = true;
                currLevel.hide();
                spacePressed = false;
                incorrectLimbo = false;
                correctLimbo = false;
                LD.repaint();
            } else if (spacePressed) {
                // Don't let the spacePressed carry over and have unintended effects
                spacePressed = false;
            }
        } else {
            promptLabel.setText("Completed all points");
        }
    }

    /**
     * Handles the drawing, prompting, and scoring for the cumulative mode
     * @param g
     */
    private void cumulativeHandler(Graphics g) {
        requestFocus();

        if (indexMC < currLevel.getSolutionsCumulative().size()) {
            currLevel.namelessDrawCumulative(g);

            if (indexChanged) {
                promptLabel.setText("<html>Click on:<br/> " + currLevel.getSolutionsCumulative().get(indexMC).getName() + "</html>");
                indexChanged = false;
            }

            if (! incorrectLimbo && ! correctLimbo) {
                for (PatrolLocation location : currLevel.getSolutionsCumulative()) {
                    if (location.isClicked()) {
                        if (location == currLevel.getSolutionsCumulative().get(indexMC)) {
                            if (! correctLimbo) {
                                promptLabel.setText("<html>Correct<br>Press space to continue</html>");
                                score++;
                                scoreOutOf++;
                                incrementScoreLabel();
                                correctLimbo = true;
                                currLevel.getSolutionsCumulative().get(indexMC).correctMark(g, LD);
                            }
                        } else {
                            if (! incorrectLimbo) {
                                promptLabel.setText("<html>Incorrect<br>Press space to continue</html>");
                                scoreOutOf++;
                                incrementScoreLabel();
                                incorrectLimbo = true;
                                location.incorrectMark(g, LD);
                                currLevel.getSolutionsCumulative().get(indexMC).correctMark(g, LD);
                            }
                        }
                    }
                }
            }

            if (spacePressed && (incorrectLimbo || correctLimbo)) {
                indexMC++;
                indexChanged = true;
                currLevel.hideCumulative();
                spacePressed = false;
                incorrectLimbo = false;
                correctLimbo = false;
                LD.repaint();
            } else if (spacePressed) {
                // Don't let the spacePressed carry over and have unintended effects
                spacePressed = false;
            }


        } else {
            promptLabel.setText("Completed all points");
        }
    }

    /**
     * Changes the text of the score label
     */
    private void incrementScoreLabel() {
        scoreLabel.setText("Score: " + score + "/" + scoreOutOf);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void setSpacePressed(boolean spacePressed) {
        this.spacePressed = spacePressed;
    }
}
