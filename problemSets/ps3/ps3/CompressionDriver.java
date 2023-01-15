package ps3;

import java.io.*;

/**
 * Driver class to encode, compress, and decompress a .txt file
 *
 * @author Alex Craig
 * @author Ben Williams
 */
public class CompressionDriver {
    public static void main(String[] args) throws Exception {
        /**
         * Set path
         */
        // Specify which .txt file you want to compress and where you want to decompress it to:
        String textName = new String("test");
        String path = new String("problemSets/ps3/assets/inputs/" + textName + ".txt");
        String comPath = new String("problemSets/ps3/assets/compressions/" + textName + "_compressed.txt");
        String deComPath = new String("problemSets/ps3/assets/decompressions/" + textName + "_decompressed.txt");


        /**
         * Encode
         */
        try{
            // Make a frequency table of all characters and their frequency in specified .txt file
            FrequencyTable freqTable = new FrequencyTable(path);

            // Make a priorityQueue of characterTrees
            CharacterPriorityQueue charQueue = new CharacterPriorityQueue(freqTable);

            // Merge the characterTrees to make final compressionTree and map all characters to unique binary code
            CompressionTree compressionTree = new CompressionTree(charQueue.getFinalTree());

            /**
             * Compress
             */
            try {
                // Instantiate a file reader and writer
                BufferedReader fileReader = new BufferedReader(new FileReader(path));
                BufferedBitWriter fileCompressor = new BufferedBitWriter(comPath);
                int readChar = fileReader.read();

                // While there are still characters left to read
                while (readChar != -1) {
                    char currChar = (char) readChar;
                    // Write current character's compression code into compressed file in bits
                    String compressionCode = compressionTree.getMapOfCharacters().get(currChar);
                    for (int i = 0; i < compressionCode.length(); i++) {
                        Boolean bit = true;
                        if (compressionCode.substring(i, i + 1).equals("0")) bit = false;
                        fileCompressor.writeBit(bit);
                    }
                    // Set current character to next character
                    readChar = fileReader.read();
                }

                fileReader.close();
                fileCompressor.close();

                /**
                 * Decompress
                 */
                try{
                    // Instantiate a compressed file reader and a decompressed file writer
                    BufferedBitReader compressedFileReader = new BufferedBitReader(comPath);
                    BufferedWriter fileDeCompressor = new BufferedWriter(new FileWriter(deComPath));

                    // Instantiate a current bit and current string
                    boolean readBit = compressedFileReader.readBit();
                    String currBitString = new String();

                    // While there are still bits left to read
                    while (compressedFileReader.hasNext()) {
                        // Get the current 1 or 0 and add it to currBitString
                        String currBit = "1";
                        if (!readBit) currBit = "0";
                        currBitString += currBit;
                        // If currBitString is stored as a value in the compression HashMap then write the corresponding character to the decompressed file and reset currBitString
                        if (compressionTree.getMapOfCharacters().containsValue(currBitString)) {
                            Character writeChar = compressionTree.getMapOfCharactersReversed().get(currBitString);
                            fileDeCompressor.write(writeChar);
                            currBitString = new String();
                        }
                        // Set readBit to next bit
                        readBit = compressedFileReader.readBit();
                    }

                    compressedFileReader.close();
                    fileDeCompressor.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
