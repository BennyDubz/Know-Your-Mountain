package ps3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Frequency table of CharacterData using a HashMap
 *
 * @author Alex Craig
 * @author Ben Williams
 */
public class FrequencyTable {
    private HashMap<Character, Integer> freqMap;
    private BufferedReader input;

    public FrequencyTable(String pathName) throws Exception {
        freqMap = new HashMap<Character, Integer>();
        this.setPath(pathName);
        this.readFrequencies();
    }

    // Loops through all characters and adds them to frequency map
    public void readFrequencies() throws Exception {
        if (input == null) {
            throw new Exception("Not reading a file");
        }
        int readChar = input.read();
        while(readChar != -1) {
            this.addChar((char)readChar);
            readChar = input.read();
        }
        input.close();
    }

    public void addChar(char c) {
        // If current character is not in map, add it to map with frequency 1
        if (freqMap.get(c) == null) {
            freqMap.put(c, 1);
        } else { // Else, add 1 to its frequency
            freqMap.put(c,freqMap.get(c)+1);
        }
    }

    public HashMap<Character, Integer> getFreqMap() {
        return freqMap;
    }

    public void setPath(String pathName) throws FileNotFoundException {
        this.input = new BufferedReader(new FileReader(pathName));
    }

}
