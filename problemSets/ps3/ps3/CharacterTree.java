package ps3;

import java.util.HashMap;

/**
 * Binary Tree of type CharacterData
 *
 * @author Alex Craig
 * @author Ben Willaims
 */
public class CharacterTree extends BinaryTree<java.lang.CharacterData> {
    private CharacterTree left;
    private CharacterTree right;
    /**
     * Constructs leaf node -- left and right are null
     */
    public CharacterTree(java.lang.CharacterData data) {
        super(data);
        right = null;
        left = null;
    }

    /**
     * Constructs inner node
     */
    public CharacterTree(CharacterData data, CharacterTree tree1, CharacterTree tree2) {
        super(data, tree1, tree2);
        left = tree1;
        right = tree2;
    }

    /**
     * Traverses CharacterTree, keeping track of where it is. Once a leaf is hit then put that character in map with value as the path to get to it (0 for left, 1 for right). Once all leaves are found return final map.
     */
    public HashMap<Character, String> makeMap(String currPath, HashMap<Character, String> retMap) {
        // If current node is a leaf, put it in the character map with a value of current binary path
        if (this.isLeaf()) {
            retMap.put(this.data.getCharacter(), currPath);
        }
        // If current node has a left child, recursively add 0 to path and traverse to it
        if (this.left != null) {
            retMap = this.left.makeMap(currPath + "0", retMap);
        }
        // If current node has a left child, recursively add 1 to path and traverse to it
        if (this.right != null) {
            retMap = this.right.makeMap(currPath + "1", retMap);
        }

        return retMap;
    }

    /**
     * Only meant to be used when one character is in text. Simply maps that character to 0.
     */
    public HashMap<Character, String> makeMapSingleChar(HashMap<Character, String> retMap) {
        if (this.data != null) retMap.put(this.data.getCharacter(), "0");
        return retMap;
    }

    @Override
    public CharacterTree getRight() {
        return right;
    }

    @Override
    public CharacterTree getLeft() {
        return left;
    }
}
