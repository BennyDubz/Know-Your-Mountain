package ps3;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Queue of CharacterTree's that prioritizes smallest frequency first
 * Contains method to construct the compression tree
 *
 * @author Alex Craig
 * @author Ben Williams
 */
public class CharacterPriorityQueue {
    private PriorityQueue treeMaker;
    private Comparator<CharacterTree> treeCompare;
    private CharacterTree finalTree;

    public CharacterPriorityQueue(FrequencyTable freqTable) throws Exception {
        treeCompare = new TreeComparator();
        treeMaker = new PriorityQueue<CharacterTree>(treeCompare);

        // Adds a characterTree to the priority queue for each character in the frequency table
        for (Character c : freqTable.getFreqMap().keySet()) {
            // Instantiate a characterData for each character and its frequency, create a tree with that data
            CharacterTree newTree = new CharacterTree(new java.lang.CharacterData(c,freqTable.getFreqMap().get(c)));
            treeMaker.add(newTree);
        }

        finalTree = makeCompressionTree(freqTable);
    }

    public CharacterTree makeCompressionTree(FrequencyTable freqTable) throws Exception {
        CharacterTree resultTree;
        PriorityQueue<CharacterTree> treeQueue = treeMaker;

        // If there aren't any characters in text, just return a tree with null data
        if (treeQueue.size() <= 0) {
            resultTree = new CharacterTree(null);
            return resultTree;
        }

        while (treeQueue.size() > 1) {
            // Get character trees with two lowest frequencies
            CharacterTree t1 = treeQueue.remove();
            CharacterTree t2 = treeQueue.remove();

            // Make a new character data with null character but with frequency as the sum of two trees
            java.lang.CharacterData newData = new java.lang.CharacterData(null, t1.getData().getFreq() + t2.getData().getFreq());
            // Make new node with null character but frequency of newData
            CharacterTree mergedTree = new CharacterTree(newData, t1, t2);

            // Add new node to Priority Queue
            treeQueue.add(mergedTree);
        }

        // The final tree in treeMaker will be the completed tree
        resultTree = treeQueue.remove();
        return resultTree;
    }

    public PriorityQueue getTreeMaker() {
        return treeMaker;
    }

    public CharacterTree getFinalTree() {
        return finalTree;
    }
}
