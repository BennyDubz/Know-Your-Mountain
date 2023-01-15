package ps5;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Driver class to train and then run the parts of speech Hidden Markov Model
 *
 * @author Alex Craig
 * @author Ben Williams
 */

public class SudiDriver {
    static POSHMM sudi;
    static boolean running = true;
    static Scanner input;

    static String trainingSentences = new String("problemSets/ps5/texts/brown-train-sentences.txt");
    static String trainingTags = new String("problemSets/ps5/texts/brown-train-tags.txt");

    static String testingSentences = new String("problemSets/ps5/texts/brown-test-sentences.txt");
    static String testingTags = new String("problemSets/ps5/texts/brown-test-tags.txt");

    public static void main(String[] args) throws Exception {
        sudi = new POSHMM();
        input = new Scanner(System.in);
        sudi.train(trainingSentences, trainingTags);

        System.out.println("\n\tSudi is trained!");
        printInstructions();

        while (running) {
            String userInput = input.nextLine();
            handleInput(userInput);
        }

    }

    public static void handleInput(String input) throws IOException {
        if (input.equals("1")) {
            consoleInput(true);
        }
        else if (input.equals("2")) {
            fileTest(true);
        }
        else if (input.equals("3")) {
            consoleInput(false);
        }
        else if (input.equals("4")) {
            fileTest(false);
        }
        else if (input.equals("q")) {
            System.out.println("\n\tQuitting Sudi");
            running = false;
        } else {
            printInstructions();
        }
    }

    // Handles a console input
    public static void consoleInput(boolean isBigram) {
        System.out.println("\n\tConsole input selected. Please type in your sentence (put a space before punctuation).\n");

        Scanner in = new Scanner(System.in);
        String input = in.nextLine();

        if (input.trim().length() <= 0) {
            System.out.println("\n\tInput is empty!");
            return;
        }

        String[] inputParts = input.split(" ");
        String[] speechParts;
        String partsOfSpeech;

        if (isBigram) {
            partsOfSpeech = sudi.viterbi(input.toLowerCase(Locale.ROOT));
        } else {
            partsOfSpeech = sudi.doubleViterbi(input.toLowerCase(Locale.ROOT));
        }

        speechParts = partsOfSpeech.split(" ");

        System.out.print("\n\t");
        for (int i = 0; i < inputParts.length; i++) {
            System.out.print(inputParts[i] + "/" + speechParts[i] + " ");
        }
        System.out.println("\n");
    }

    // Tests Sudi against a specified file
    public static void fileTest(boolean isBigram) throws IOException {
        System.out.println("\n\tFile test selected. Comparing Sudi's output to specified test files...");

        BufferedReader testSentenceReader = new BufferedReader(new FileReader(testingSentences));
        BufferedReader testTagReader = new BufferedReader(new FileReader(testingTags));

        int correctCount = 0;
        int totalCount = 0;

        String sentenceLine = testSentenceReader.readLine();
        String tagLine = testTagReader.readLine();
        while (sentenceLine != null && tagLine != null) {
            String tagGuess;
            if (isBigram) {
                tagGuess = sudi.viterbi(sentenceLine);
            } else {
                tagGuess = sudi.doubleViterbi(sentenceLine);
            }

            String[] guesses = tagGuess.split(" ");
            String[] tags = tagLine.split(" ");

            for (int i = 0; i < tags.length; i++) {
                totalCount++;
                if (tags[i].equals(guesses[i])) {
                    correctCount++;
                }
            }
            sentenceLine = testSentenceReader.readLine();
            tagLine = testTagReader.readLine();
        }

        Double accuracy = (double) 100 * correctCount / totalCount;
        int wrongCount = totalCount - correctCount;
        System.out.println("\n\t\tGot " + correctCount + "/" + totalCount + " tags correct. Got " + wrongCount + " tags wrong. Accuracy %: " + accuracy);
    }

    public static void printInstructions() {
        System.out.println("\n\tSudi Instructions:\n\t\t1 Input sentence through console using bigram model\n\t\t2 Test on hard coded file using bigram model\n\t\t3 Input sentence through console using trigram Mode\n\t\t4 Test on hard coded file using trigram model\n\t\tq Quit Sudi");
    }
}
