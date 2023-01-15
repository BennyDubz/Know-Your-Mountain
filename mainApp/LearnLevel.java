import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Ben Williams, April 5th 2022
 *
 * A class for an individual level containing only a small/digestable amount of patrol locations.
 * Also contains all previous & current locations in a seperate list to be able to practice all of them.
 */
public class LearnLevel {
    String name;
    ArrayList<PatrolLocation> levelLocations; // This level's locations
    ArrayList<PatrolLocation> previousAndCurrentLocations; // This level's AND previous level's locations
    MapDisplay parentLearnPanel; // Parent LearnDisplay or MapDisplay
    Mode MODE = Mode.LOOK; // Initialize Mode as LOOK
    LearnLevel previous; // Previous level
    HashSet<PatrolLocation> clicked;
    boolean showNamesInLook;

    ArrayList<PatrolLocation> solutionsMC;
    ArrayList<PatrolLocation> solutionsCumulative;


    public enum Mode {
        LOOK, EASYMC, HARDMC, CUMULATIVE
    }

    public LearnLevel(ArrayList<PatrolLocation> locations, MapDisplay parent, LearnLevel previous, String name) {
        this.levelLocations = locations;
        this.parentLearnPanel = parent;
        this.previous = previous;
        this.name = name;
        showNamesInLook = true;
        previousAndCurrentLocations = initPreviousAndCurrentLocations(this);
        shuffleSolution();
    }

    public void draw(Graphics g) {
        // If only considering the level's own locations
        if (!MODE.equals(Mode.CUMULATIVE)) {
            if (!MODE.equals(Mode.LOOK)) {
                for (PatrolLocation location : levelLocations) {
                    location.draw(g, parentLearnPanel.getParentGUI(), parentLearnPanel, false, false);
                }
            } else if (showNamesInLook) {
                for (PatrolLocation location : levelLocations) {
                    location.draw(g, parentLearnPanel.getParentGUI(), parentLearnPanel, true, true);
                }
            } else {
                for (PatrolLocation location : levelLocations) {
                    location.draw(g, parentLearnPanel.getParentGUI(), parentLearnPanel, false, true);
                }
            }

        } else { // If also considering previous levels locations
            for (PatrolLocation location : previousAndCurrentLocations) {
                location.draw(g, parentLearnPanel.getParentGUI(), parentLearnPanel, false, false);
            }
        }
    }

    /**
     * Shuffles the solutions ArrayList for the level. can be easily called to re-shuffle whenever the modes are changed
     */
    public void shuffleSolution() {
        solutionsMC = levelLocations;
        Collections.shuffle(solutionsMC);

        solutionsCumulative = previousAndCurrentLocations;
        Collections.shuffle(solutionsCumulative);
    }

    public void namelessDrawMC (Graphics g) {
        // Iterate through all the points in the solution
        for (int i = 0; i < solutionsMC.size(); i++) {
            solutionsMC.get(i).draw(g, parentLearnPanel.getParentGUI(), parentLearnPanel, false, false);
        }
    }

    public void namelessDrawCumulative (Graphics g) {
        // Iterate through all the points in the solution
        for (int i = 0; i < solutionsCumulative.size(); i++) {
            solutionsCumulative.get(i).draw(g, parentLearnPanel.getParentGUI(), parentLearnPanel, false, false);
        }
    }

    /**
     * A recursive function to get all the current and previous locations.
     * @param level
     * @return An ArrayList of the current level's and previous levels' locations
     */
    private ArrayList<PatrolLocation> initPreviousAndCurrentLocations (LearnLevel level) {
        ArrayList<PatrolLocation> result = new ArrayList<>();
        // If the previous level is not null/at the first level, do not recurse
        if (level.getPrevious() != null) {
            // Recursively build the ArrayList
            result = initPreviousAndCurrentLocations(level.getPrevious());
        }
        // Add all of level parameter's locations to list
        result.addAll(level.getLevelLocations());

        return result;
    }

    public LearnLevel getPrevious() {
        return previous;
    }

    public ArrayList<PatrolLocation> getLevelLocations() {
        return levelLocations;
    }

    public void setMODE(Mode MODE) {
        this.MODE = MODE;
    }

    public String getName() {
        return name;
    }

    /**
     * Used when switching between levels so that JLabels do not stay after points disappear.
     */
    public void hide() {
        for (PatrolLocation patrolLocation : levelLocations) {
            patrolLocation.setClicked(false);
            patrolLocation.hideLabel(parentLearnPanel);
            patrolLocation.setCorrectMarked(false);
            patrolLocation.setIncorrectMarked(false);
        }
    }

    public void hideCumulative() {
        for (PatrolLocation patrolLocation : previousAndCurrentLocations) {
            patrolLocation.setClicked(false);
            patrolLocation.hideLabel(parentLearnPanel);
            patrolLocation.setCorrectMarked(false);
            patrolLocation.setIncorrectMarked(false);
        }
    }

    public void setShowNamesInLook(boolean showNamesInLook) {
        this.showNamesInLook = showNamesInLook;
    }

    public ArrayList<PatrolLocation> getSolutionsMC() {
        return solutionsMC;
    }

    public ArrayList<PatrolLocation> getSolutionsCumulative() {
        return solutionsCumulative;
    }

    public HashSet<PatrolLocation> getClicked() {
        return clicked;
    }

    @Override
    public String toString() {
        return "LearnLevel{" +
                "name='" + name + '\'' +
                '}';
    }

}
