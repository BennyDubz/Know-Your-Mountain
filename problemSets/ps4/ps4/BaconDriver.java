package ps4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

public class BaconDriver {

    public static void main(String[] args) throws Exception {
        String actorPath = new String("problemSets/ps4/data/actors.txt");
        String moviePath = new String("problemSets/ps4/data/movies.txt");
        String movieActorPath = new String("problemSets/ps4/data/movie-actors.txt");

        HashMap<Integer,String> actorIDs = parseIntegerStringMap(actorPath);
        HashMap<Integer,String> movieIDs = parseIntegerStringMap(moviePath);
        HashMap<Integer,HashSet<Integer>> movieActorIDs = parseIntegerToIntegerSetMap(movieActorPath);

        // Creates a new game with Kevin Bacon as the first center of the universe
        BaconGame baconGame = new BaconGame(new BaconGraph(actorIDs, movieIDs, movieActorIDs, "Kevin Bacon"));
        baconGame.runGame();
    }

    /**
     * Method to parse input data and create a map of movies or actors to their ID's
     */
    public static HashMap<Integer,String> parseIntegerStringMap(String path) throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(path));
        HashMap<Integer,String> result = new HashMap<>();

        String readLine = fileReader.readLine();
        while (readLine != null) {
            String currLine = readLine;
            String[] split = currLine.split("\\|");
            result.put(Integer.valueOf(split[0]), split[1]);
            readLine = fileReader.readLine();
        }

        return result;
    }

    /**
     * Method to parse input data and map movie ID's to the set of actor ID's that appeared in them
     */
    public static HashMap<Integer, HashSet<Integer>> parseIntegerToIntegerSetMap(String path) throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(path));
        HashMap<Integer, HashSet<Integer>> result = new HashMap<>();

        String readLine = fileReader.readLine();
        while (readLine != null) {
            String currLine = readLine;
            String[] split = currLine.split("\\|");

            if (result.containsKey(Integer.valueOf(split[0]))) {
                result.get(Integer.valueOf(split[0])).add(Integer.valueOf(split[1]));
            } else {
                result.put(Integer.valueOf(split[0]), new HashSet<Integer>());
                result.get(Integer.valueOf(split[0])).add(Integer.valueOf(split[1]));
            }

            readLine = fileReader.readLine();
        }

        return result;
    }


}
