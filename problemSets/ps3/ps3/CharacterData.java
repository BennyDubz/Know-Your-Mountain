package ps3;

/**
 * Data type that holds character and its frequency
 *
 * @author Alex Craig
 * @author Ben Williams
 */
public class CharacterData {
    private Integer freq;
    private Character character;

    public CharacterData(Character c, Integer freq) {
        this.character = c;
        this.freq = freq;
    }

    @Override
    public String toString() {
        return "CharacterData{" +
                "c=" + character +
                ", freq=" + freq +
                '}';
    }

    public Integer getFreq() {
        return freq;
    }

    public Character getCharacter() {
        return character;
    }
}
