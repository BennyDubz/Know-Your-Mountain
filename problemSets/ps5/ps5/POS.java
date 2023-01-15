package ps5;

import java.util.*;

/**
 * Simple class that holds a part of speech and associated data
 *
 * @author Alex Craig
 * @author Ben Williams
 */

public class POS {
    protected String name;                                                  // Name of part of speech (ex: NP for proper noun)
    protected HashMap<POS, Double> oneAheadTransitions;                     // Map holding parts of speech that can be transitioned to from this one, and the logarithmic probability of that transition
    protected HashMap<POS, HashMap<POS, Double>> twoAheadTransitions;                     // Map holding parts of speech that can transition to any POS and then to this specific POS, and the logarithmic probability of that transition


    protected HashMap<String, Double> observations;                         // Map holding words that can be this part of speech, and the logarithmic probability that those words are this part of speech
    protected HashMap<POS, Integer> onceRemovedTransitionsCount;            // Map holding a POS, and how many times that POS has directly transitioned to this POS during training
    protected HashMap<POS, HashMap<POS, Integer>> twiceRemovedTransitionsCount;           // Map holding a POS, and how many times that POS has transitioned to another POS and then to this POS during training

    protected HashMap<String, Integer> observationsCount;                   // Map holding words and how many times those words have been this POS during training
    protected Integer oneAheadTransitionsCount;                             // Count holding total number of times this POS transitions to another POS (whenever it's not the last word in the sentence)

    public POS(String name) {
        // HMM data:
        this.name = name;
        this.oneAheadTransitions = new HashMap<>();
        this.twoAheadTransitions = new HashMap<>();
        this.observations = new HashMap<>();

        // Training data:
        this.onceRemovedTransitionsCount = new HashMap<>();
        this.observationsCount = new HashMap<>();
        this.oneAheadTransitionsCount = 0;
        this.twiceRemovedTransitionsCount = new HashMap<>();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
