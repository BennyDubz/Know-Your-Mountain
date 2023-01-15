package ps5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Hidden Markov Model that tags parts of speech
 *
 * @author Alex Craig
 * @author Ben Williams
 */

public class POSHMM {
    protected ArrayList<POS> posList;               // List of all parts of speech
    protected HashMap<String, Integer> wordFreq;    // Freq of each word in training data
    protected POS start;                            // Starting part of speech of each sentence
    protected Double nonObservationPenalty;
    protected Double notInTwoAheadPenalty;

    public POSHMM() {
        this.posList = new ArrayList<>();
        wordFreq = new HashMap<>();
        start = new POS("#");
        nonObservationPenalty = -100.0;
        notInTwoAheadPenalty = -30.0;
    }

    /**
     * Method which runs the Viterbi algo on an input string to determine parts of speech
     */
    public String viterbi(String input) {
        // Instantiate an ArrayList of HashMaps to serve as our backtrace
        ArrayList<HashMap<POS, Double>> oneAheadPossiblePOS = new ArrayList<>(); // This will be the combination of the transition score from POS to POS, and the observation score of a word for each POS
        ArrayList<HashMap<POS, POS>> oneBehindBackPointer = new ArrayList<>();

        String output = new String();

        String words[] = input.split(" ");

        oneAheadPossiblePOS.add(new HashMap<>());
        oneAheadPossiblePOS.get(0).put(start, 0.0);

        oneBehindBackPointer.add(new HashMap<>());
        oneBehindBackPointer.get(0).put(start, null);

        /**
         * This for loop will fill out our arrayList of HashMap's. Each index of the ArrayList will correspond to a word in the sentence. Index 0 will correspond to # (start). Each HashMap will hold all POS that the word at that index can be, and the log probabilities of it being each POS.
         *
         * This for loop will also fill out an arrayList of HashMap's that corresponds to each word in the same way. Each HashMap will map the POS in the other HashMap to its back pointer.
         */
        for (int i = 0; i < words.length; i++) {
            // Instantiate the HashMap of possible transitions from currPOS to nextPOS as well as the backPointer HashMap
            oneAheadPossiblePOS.add(new HashMap<>());
            oneBehindBackPointer.add(new HashMap<>());
            // For each current possible POS
            for (POS currPOS : oneAheadPossiblePOS.get(i).keySet()) {
                // Look through all possible next POS
                for (POS nextPOS : currPOS.oneAheadTransitions.keySet()) {
                    // Get the score for each of those possible next POS (adding current score, transition score, and observation score)
                    Double scoreForThisTransition = 0.0;
                    // Add the current score
                    scoreForThisTransition += oneAheadPossiblePOS.get(i).get(currPOS);
                    // Add transition score
                    scoreForThisTransition += currPOS.oneAheadTransitions.get(nextPOS);
                    // Add the observation score or observation penalty if word hasn't been observed as POS in training
                    if (nextPOS.observations.containsKey(words[i])) {
                        scoreForThisTransition += nextPOS.observations.get(words[i]);
                    } else {
                        scoreForThisTransition += nonObservationPenalty;
                    }

                    // If this transition score is better than the current score for this POS, then overwrite current (i + 1 because we already added # POS before this loop)
                    boolean contains = false;
                    for (POS candidate : oneAheadPossiblePOS.get(i + 1).keySet()) {
                        // If the same part of speech is already held
                        if (candidate.name.equals(nextPOS.name)) {
                            contains = true;
                            // If the score for that part of speech is lower than score for nextPOS, then replace it
                            if (oneAheadPossiblePOS.get(i + 1).get(candidate) < scoreForThisTransition) {
                                oneAheadPossiblePOS.get(i + 1).put(candidate, scoreForThisTransition);
                                oneBehindBackPointer.get(i + 1).put(candidate, currPOS);
                            }
                        }
                    }
                    // If this POS isn't held in possible next POS's, then add it
                    if (!contains) {
                        oneAheadPossiblePOS.get(i + 1).put(nextPOS, scoreForThisTransition);
                        oneBehindBackPointer.get(i + 1).put(nextPOS, currPOS);
                    }
                }
            }
        }

        // Now get the best guess at the POS of the last word:
        Double currBestScore = -1000000000.0;
        POS pointerPOS = null;
        for (POS best : oneAheadPossiblePOS.get(words.length).keySet()) {
            if (oneAheadPossiblePOS.get(words.length).get(best) > currBestScore) {
                pointerPOS = best;
                currBestScore = oneAheadPossiblePOS.get(words.length).get(best);
            }
        }

        // Now use the backPointer to build out a LinkedList of POS that associate with each word in the sentence
        LinkedList<POS> partsOfSpeech = new LinkedList<>();
        partsOfSpeech.add(0, pointerPOS);
        for (int i = words.length; i > 1; i--) {
            partsOfSpeech.add(0,oneBehindBackPointer.get(i).get(pointerPOS));
            pointerPOS = oneBehindBackPointer.get(i).get(pointerPOS);
        }

        // Now edit output string
        for (POS pos : partsOfSpeech) {
            output += pos.name + " ";
        }
        // Get rid of last space and return
        output = output.substring(0,output.length()-1);
        return output;
    }

    public String doubleViterbi(String input) {
        // Instantiate an ArrayList of HashMaps to serve as our backtrace
        ArrayList<HashMap<POS, Double>> oneAheadPossiblePOS = new ArrayList<>(); // This will be the combination of the transition score from POS to POS, and the observation score of a word for each POS
        ArrayList<HashMap<POS, POS>> oneBehindBackPointer = new ArrayList<>();

        String output = new String();

        String words[] = input.split(" ");

        oneAheadPossiblePOS.add(new HashMap<>());
        oneAheadPossiblePOS.get(0).put(start, 0.0);

        oneBehindBackPointer.add(new HashMap<>());
        oneBehindBackPointer.get(0).put(start, null);

        /**
         * This for loop will fill out our arrayList of HashMap's. Each index of the ArrayList will correspond to a word in the sentence. Index 0 will correspond to # (start). Each HashMap will hold all POS that the word at that index can be, and the log probabilities of it being each POS.
         *
         * This for loop will also fill out an arrayList of HashMap's that corresponds to each word in the same way. Each HashMap will map the POS in the other HashMap to its back pointer.
         */
        for (int i = 0; i < words.length; i++) {
            // Instantiate the HashMap of possible transitions from currPOS to nextPOS as well as the backPointer HashMap
            oneAheadPossiblePOS.add(new HashMap<>());
            oneBehindBackPointer.add(new HashMap<>());

            // For each current possible POS
            for (POS currPOS : oneAheadPossiblePOS.get(i).keySet()) {
                for (POS nextPOS : currPOS.oneAheadTransitions.keySet()) {
                    // Get the score for each of those possible next POS (adding current score, transition score, and observation score)
                    Double scoreForThisTransition = 0.0;
                    // Add the current score
                    scoreForThisTransition += oneAheadPossiblePOS.get(i).get(currPOS);
                    // Add transition score
                    scoreForThisTransition += currPOS.oneAheadTransitions.get(nextPOS);
                    // If at least at the second word, and POS two ago has a hashmap for currPOS (If currPOS is a period, which is always at the end of the sentence, then it will often have no map)
                    if (i > 0 && oneBehindBackPointer.get(i).get(currPOS).twoAheadTransitions.get(currPOS) != null) {
                       // If POS is not in twoAheadTransitions, add penalty
                        if (!oneBehindBackPointer.get(i).get(currPOS).twoAheadTransitions.get(currPOS).containsKey(nextPOS)) {
                            scoreForThisTransition += notInTwoAheadPenalty;
                        } else { // If it is, add its value multiplied by 0.25 so that it is weighted less
                            scoreForThisTransition += oneBehindBackPointer.get(i).get(currPOS).twoAheadTransitions.get(currPOS).get(nextPOS) * 0.25;
                        }
                    }

                    // Add the observation score or observation penalty if word hasn't been observed as POS in training
                    if (nextPOS.observations.containsKey(words[i])) {
                        scoreForThisTransition += nextPOS.observations.get(words[i]);
                    } else {
                        scoreForThisTransition += nonObservationPenalty;
                    }

                    boolean contains = false;
                    for (POS candidate : oneAheadPossiblePOS.get(i + 1).keySet()) {
                        // If the same part of speech is already held
                        if (candidate.name.equals(nextPOS.name)) {
                            contains = true;
                            // If the score for that part of speech is lower than score for nextPOS, then replace it
                            if (oneAheadPossiblePOS.get(i + 1).get(candidate) < scoreForThisTransition) {
                                oneAheadPossiblePOS.get(i + 1).put(candidate, scoreForThisTransition);
                                oneBehindBackPointer.get(i + 1).put(candidate, currPOS);
                            }
                        }
                    }
                    // If this POS isn't held in possible next POS's, then add it
                    if (!contains) {
                        oneAheadPossiblePOS.get(i + 1).put(nextPOS, scoreForThisTransition);
                        oneBehindBackPointer.get(i + 1).put(nextPOS, currPOS);
                    }
                }
            }
        }
        // Now get the best guess at the POS of the last word:
        Double currBestScore = -1000000000.0;
        POS pointerPOS = null;
        for (POS best : oneAheadPossiblePOS.get(words.length).keySet()) {
            if (oneAheadPossiblePOS.get(words.length).get(best) > currBestScore) {
                pointerPOS = best;
                currBestScore = oneAheadPossiblePOS.get(words.length).get(best);
            }
        }

        // Now use the backPointer to build out a LinkedList of POS that associate with each word in the sentence
        LinkedList<POS> partsOfSpeech = new LinkedList<>();
        partsOfSpeech.add(0, pointerPOS);
        for (int i = words.length; i > 1; i--) {
            partsOfSpeech.add(0, oneBehindBackPointer.get(i).get(pointerPOS));
            pointerPOS = oneBehindBackPointer.get(i).get(pointerPOS);
        }

        // Now edit output string
        for (POS pos : partsOfSpeech) {
            output += pos.name + " ";
        }
        // Get rid of last space and return
        output = output.substring(0, output.length() - 1);
        return output;
    }

    /**
     * Method to train the weights of the HMM
     *
     * @param sentencesPath File with sentences
     * @param tagsPath File with parts of speech associated with each word in sentences
     */
    public void train(String sentencesPath, String tagsPath) throws Exception {
        BufferedReader trainingSentencesReader = new BufferedReader(new FileReader(sentencesPath));
        BufferedReader trainingTagsReader = new BufferedReader(new FileReader(tagsPath));

        String sentenceLine = trainingSentencesReader.readLine().toLowerCase();
        String tagLine = trainingTagsReader.readLine();

        // Add a blank start POS to our POS list
        posList.add(start);

        while (sentenceLine != null && tagLine != null) {
            String[] words = sentenceLine.split(" ");
            String[] tags = tagLine.split(" ");

            POS prevPOS = start;
            POS twoAgoPos = null;

            for (int i = 0; i < words.length; i++) {
                POS currPOS = new POS(tags[i]);
                boolean included = false;
                // Look  through our current posList, and set currPOS equal to correct POS
                for (POS pos : posList) {
                    if (pos.name.equals(tags[i])) {
                        included = true;
                        currPOS = pos;
                    }
                }

                // If the tag isn't already contained in our list of parts of speech, add it
                if (!included) {
                    posList.add(currPOS);
                }

                // Update or add new transition from prevPOS to currPOS
                if (!prevPOS.onceRemovedTransitionsCount.containsKey(currPOS)) {
                    prevPOS.onceRemovedTransitionsCount.put(currPOS, 1);
                } else {
                    prevPOS.onceRemovedTransitionsCount.put(currPOS, prevPOS.onceRemovedTransitionsCount.get(currPOS) + 1);
                }
                // Update the currPos count
                prevPOS.oneAheadTransitionsCount++;

                // Update or add twiceRemovedTransitions if twoAgoPOS is not null, as we are at least at the 2nd word of a sentence
                if (twoAgoPos != null) {
                    // If the POS two words ago has not seen this POS one ahead, add it to its twiceRemovedTransitions map.
                    if (!twoAgoPos.twiceRemovedTransitionsCount.containsKey(prevPOS)) {
                        twoAgoPos.twiceRemovedTransitionsCount.put(prevPOS, new HashMap<>());
                        // Add currPOS to prevPOS's hashmap in twoAgoPOS's twiceRemovedTransitionsCount
                        twoAgoPos.twiceRemovedTransitionsCount.get(prevPOS).put(currPOS,1);
                    } else {
                        // If the prevPOS has not seen the currPOS while twoAgoPOS is behind it, add currPOS to prevPOS's hashmap in twoAgoPOS's twiceRemovedTransitionsCount
                        if (!twoAgoPos.twiceRemovedTransitionsCount.get(prevPOS).containsKey(currPOS)) {
                            twoAgoPos.twiceRemovedTransitionsCount.get(prevPOS).put(currPOS,1);
                        } else { // Otherwise, increment it by one.
                            twoAgoPos.twiceRemovedTransitionsCount.get(prevPOS).put(currPOS,twoAgoPos.twiceRemovedTransitionsCount.get(prevPOS).get(currPOS) + 1);
                        }
                    }
                }

                // Update or add new word for currPOS
                if (!currPOS.observationsCount.containsKey(words[i])) {
                    currPOS.observationsCount.put(words[i], 1);
                } else {
                    currPOS.observationsCount.put(words[i], currPOS.observationsCount.get(words[i]) + 1);
                }

                // Update or add the overall word frequency
                if (!wordFreq.containsKey(words[i])) {
                    wordFreq.put(words[i], 1);
                } else {
                    wordFreq.put(words[i], wordFreq.get(words[i]) + 1);
                }

                // Update the twoAgoPOS
                twoAgoPos = prevPOS;
                // Update the previous POS
                prevPOS = currPOS;

            }
            sentenceLine = trainingSentencesReader.readLine();
            tagLine = trainingTagsReader.readLine();
            if (sentenceLine != null) sentenceLine.toLowerCase(Locale.ROOT);
        }

        // Calculates the probabilities for everything
        CalculateProbabilities();
    }

    /**
     * Method to take the trained observation and transition counts and use those to calculate the probabilities for the HMM
     */
    public void CalculateProbabilities() {
        // For each POS that we have
        for (POS pos : posList) {
            // Calculate the probability of each word being that POS
            for (String word : pos.observationsCount.keySet()) {
                Double probOfWordBeingThisPOS = (double) pos.observationsCount.get(word) / wordFreq.get(word);
                pos.observations.put(word, Math.log(probOfWordBeingThisPOS));
            }

            // Calculate the probability of each POS being transitioned to from this POS
            for (POS nextPOS : pos.onceRemovedTransitionsCount.keySet()) {
                Double probOfTransitionToNext = (double) pos.onceRemovedTransitionsCount.get(nextPOS) / pos.oneAheadTransitionsCount;
                pos.oneAheadTransitions.put(nextPOS, Math.log(probOfTransitionToNext));
            }

            // Calculate the probability of each POS being transitioned to after it already transitions to a POS, but specifically for the POS ahead of it.
            for (POS nextPOS : pos.twiceRemovedTransitionsCount.keySet()) {
                for (POS twoAheadPOS : pos.twiceRemovedTransitionsCount.get(nextPOS).keySet()) {
                    // Will run only the first time going through this second for loop,
                    if (!pos.twoAheadTransitions.containsKey(nextPOS)) {
                        pos.twoAheadTransitions.put(nextPOS, new HashMap<>());
                    }
                    Double probOfTransition = (double) pos.twiceRemovedTransitionsCount.get(nextPOS).get(twoAheadPOS) / pos.onceRemovedTransitionsCount.get(nextPOS);
                    pos.twoAheadTransitions.get(nextPOS).put(twoAheadPOS, Math.log(probOfTransition));
                }
            }
        }
    }
}