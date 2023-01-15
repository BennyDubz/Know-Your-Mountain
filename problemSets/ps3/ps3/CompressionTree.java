package ps3;

import java.util.HashMap;

/**
 * Class that holds characterTree for compression and map of all characters to leafs in tree
 *
 * @author Alex Craig
 * @author Ben Williams
 */
public class CompressionTree {
    private CharacterTree compressionTree;                          // Character tree that holds compression code for each character/leaf
    private HashMap<Character,String> mapOfCharacters;              // Hashmap with characters as key and compression code as value
    private HashMap<String, Character> mapOfCharactersReversed;     // Hashmap with compression code as key and character as value

    public CompressionTree(CharacterTree tree) {
        compressionTree = tree;
        String path = new String();
        HashMap<Character,String> retMap = new HashMap<>();
        if(compressionTree.size() == 1) mapOfCharacters = compressionTree.makeMapSingleChar(retMap);
        else mapOfCharacters = compressionTree.makeMap(path, retMap);
        mapOfCharactersReversed = this.makeMapReversed();
    }

    public HashMap<String, Character> makeMapReversed() {
        HashMap<String, Character> retMap = new HashMap<String, Character>();
        for (Character c : mapOfCharacters.keySet()) {
            retMap.put(mapOfCharacters.get(c), c);
        }
        return retMap;
    }

    public CharacterTree getTree() {
        return compressionTree;
    }

    public HashMap<Character, String> getMapOfCharacters() {
        return mapOfCharacters;
    }

    public HashMap<String, Character> getMapOfCharactersReversed() {
        return mapOfCharactersReversed;
    }
}
