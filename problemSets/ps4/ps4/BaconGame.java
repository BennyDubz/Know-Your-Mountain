package ps4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Class that holds and runs the game
 * @author Alex Craig
 * @author Ben Williams
 */
public class BaconGame {
    boolean running;
    BaconGraph baconGraph;
    Scanner input;

    public BaconGame(BaconGraph baconGraph) {
        this.baconGraph = baconGraph;
        input = new Scanner(System.in);
        running = true;
    }

    public void runGame() {
        System.out.println("\n\tCommands:\n\t\tc <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n\t\td <low> <high>: list actors sorted by degree, with degree between low and high\n\t\ti: list actors with infinite separation from the current center\n\t\tp <name>: find path from <name> to current center of the universe\n\t\ts <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n\t\tu <name>: make <name> the center of the universe\n\t\tq: quit game");
        printNewCenter();
        while (running) {
            String userInput = input.nextLine();
            handleInput(userInput);
        }
        input.close();
        System.out.println("\n\tGame Closed");
    }

    /**
     * Handles user input and calls appropriate method
     */
    public void handleInput(String input) {
        String[] parts = input.split(" ", 2);
        // parts[0] should always be the command letter
        if (parts[0].equals("c")) {
            averageSeparationSorter(Integer.parseInt(parts[1]));
        } else if (parts[0].equals("d")) {
            // parts[1] will encompass the rest of the string, so need to split it again into the two numbers
            String[] numbers = parts[1].split(" ");
            sortActorsByDegree(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
        } else if (parts[0].equals("i")) {
            listUnreachable();
        } else if (parts[0].equals("p")) {
            findPath(parts[1]);
        } else if (parts[0].equals("s")) {
            // parts[1] will encompass the rest of the string, so need to split it again into the two numbers
            String[] numbers = parts[1].split(" ");
            sortActorsBySeparation(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
        } else if (parts[0].equals("u")) {
            newCenter(parts[1]);
        } else if (parts[0].equals("q")) {
            running = false;
        } else {
            System.out.println("\n\tInvalid Input");
            // Re-prints the commands
            System.out.println("\n\tCommands:\n\t\tc <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n\t\td <low> <high>: list actors sorted by degree, with degree between low and high\n\t\ti: list actors with infinite separation from the current center\n\t\tp <name>: find path from <name> to current center of the universe\n\t\ts <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n\t\tu <name>: make <name> the center of the universe\n\t\tq: quit game");
        }
    }

    /**
     * @param low - lower bound for degree
     * @param high - upper bound for degree
     * @Return ArrayList of Actors sorted by degree, but only between low and high.
     */
     public ArrayList<String> sortActorsByDegree(int low, int high) {
         ArrayList<String> result = new ArrayList<>();
         HashMap<String,Integer> intermediate = new HashMap<>();

         // Loops through every actor, adds them to the result ArrayList if their degree is within the range
         for (String actor : baconGraph.getBaconGameGraph().vertices()) {
             int outDegree = baconGraph.getBaconGameGraph().outDegree(actor);
             if (outDegree > low && outDegree < high) {
                 result.add(actor);
                 intermediate.put(actor,outDegree);
             }
         }

         // Sorts the result ArrayList by degree, with the highest degrees at the beginning of the ArrayList
         result.sort((String s1, String s2) -> baconGraph.getBaconGameGraph().outDegree(s2) - baconGraph.getBaconGameGraph().outDegree(s1));
         System.out.println("\tActors sorted by degree between " + low + " and " + high);

         for (int i = 0; i < result.size(); i++) {
             System.out.println("\t\tActor " + result.get(i) + " in place " + i + " with degree " + intermediate.get(result.get(i)));
         }

         return result;
     }

    /**
     * Sorts actors by degree of separation from current center of universe
     * @param low -Lower bound of separation to be listed
     * @param high -Upper bound of separation to be listed
     * @return Returns an ArrayList of actors sorted by their separation from the center of the universe
     */
     public ArrayList<String> sortActorsBySeparation(Integer low, Integer high) {
         ArrayList<String> result = new ArrayList<>();
         HashMap<String,Integer> intermediate = new HashMap<>();
         System.out.println("\tSorting actors by degree of separation between " + low + " and " + high);

         // Loops through every actor, adds them to the ArrayList and their pathLength to the map
         for (String actor : baconGraph.shortestPaths.vertices()) {
             int pathLength = baconGraph.getPath(baconGraph.getShortestPaths(), actor).size() - 1;
             if (pathLength > low && pathLength < high) {
                 result.add(actor);
                 intermediate.put(actor,pathLength);
             }
         }

         // Sorts the ArrayList by actor's separation from the center of the universe, with the lowest separations at the beginning
         result.sort((String a1, String a2) -> (intermediate.get(a1) - intermediate.get(a2)));

         for (int i = 0; i < result.size(); i++) {
            System.out.println("\t\tActor " + result.get(i) + " in place " + i + " with " + intermediate.get(result.get(i)) + " separations from " + baconGraph.getCenterOfUniverse());
         }

         return result;
     }

    /**
     * Lists all nodes that are unreachable from current center of universe
     */
    public ArrayList<String> listUnreachable() {
        ArrayList<String> result = new ArrayList<>();
        System.out.println("\n\tThese Actors are unreachable from the current center of the universe:");
        // Loops through all actors who are missing from current center of the universe
        for (String actor : baconGraph.missingVertices(baconGraph.getBaconGameGraph(),baconGraph.getShortestPaths())) {
            System.out.println("\t\t" + actor);
            result.add(actor);
        }

        return result;
    }

    /**
     * Lists the shortest path from a given actor to the current center of the universe
     * Also prints whichever movies each actor played a part together in
     */
    public void findPath(String actor) {
        // If the actor is in the missing vertices, do not continue
        if (baconGraph.missingVertices(baconGraph.getBaconGameGraph(), baconGraph.getShortestPaths()).contains(actor)) {
            System.out.println("\n\t" + actor + "'s separation number is infinity");
            return;
        }
        // Finds the path between actor and center of the universe and prints it along the way
        if (baconGraph.getBaconGameGraph().hasVertex(actor)) {
            ArrayList<String> pathList = (ArrayList<String>) baconGraph.getPath(baconGraph.getShortestPaths(), actor);
            String number = String.valueOf(pathList.size() - 1);
            System.out.println("\n\t" + actor + "'s separation number is " + number);
            for (int i = 0; i < pathList.size() - 1; i++) {
                ArrayList<String> moviesTogether = new ArrayList<>();
                for (String movie : baconGraph.getBaconGameGraph().getLabel(pathList.get(i), pathList.get(i + 1))) {
                    moviesTogether.add(movie);
                }
                System.out.println("\t\t" + pathList.get(i) + " played a role with " + pathList.get(i + 1) + " in " + moviesTogether);
            }
        } else {
            System.out.println("\n\tNo such actor exists... cannot find path.");
        }
    }

    public void averageSeparationSorter(int numberOfActors) {
        HashMap<String, Double> intermediate = new HashMap<>();
        ArrayList<String> result = new ArrayList<>();
        // Creates a set of exclusions from actors who are un-accessible wth the current center of the universe. Makes it more efficient
        HashSet<String> exclusions = (HashSet<String>) baconGraph.missingVertices(baconGraph.getBaconGameGraph(),baconGraph.getShortestPaths());

        // Loops through every actor
        for (String actor : baconGraph.getBaconGameGraph().vertices()) {
            // If actor is not in excluded set, add it to the results, and add its average seperation to the map
            if (!exclusions.contains(actor)) {
                AdjacencyMapGraph<String,HashSet<String>> tree = (AdjacencyMapGraph<String, HashSet<String>>) baconGraph.bfs(baconGraph.getBaconGameGraph(),actor);
                intermediate.put(actor, baconGraph.averageSeparation(tree, actor, 0));
                result.add(actor);
            }
        }

        // Sort the results by actor's average separations, with the highest average separation at the beginning
        result.sort((String s1, String s2) -> ((int)((intermediate.get(s2) - intermediate.get(s1))*10)));

        // Prints from the end of the list if user gives a negative number
        if (numberOfActors < 0) {
            System.out.println("\tPrinting actors in ascending order who have the least average separation when they are the center");
            int idx = result.size()-1;
            int counter = numberOfActors;
            while (counter < 0) {
                if (idx < 0) return; // Prevents it from going too far if user gives too negative of a number
                System.out.println("\t\tActor " + result.get(idx) + " in place " + idx + " with an average of " + intermediate.get(result.get(idx)) + " separations");
                idx--;
                counter++;
            }
        }

        // Prints from the beginning of the list if user gives a positive number
        if (numberOfActors > 0) {
            System.out.println("\tPrinting actors in descending order who have the most average separation when they are the center");
            int idx = 0;
            int counter = numberOfActors;
            while (counter > 0) {
                if (idx == result.size()) return; // Prevents it from going too far if user gives too big of a number
                System.out.println("\t\tActor " + result.get(idx) + " in place " + idx + " with an average of " + intermediate.get(result.get(idx)) + " separations");
                idx++;
                counter--;
            }
        }
    }

    /**
     * @param newCenter - Actor's name who will become the new center of the universe
     */
    public void newCenter(String newCenter) {
        // If the actor exists
        if (baconGraph.getBaconGameGraph().hasVertex(newCenter)) {
            baconGraph.setCenterOfUniverse(newCenter);
            printNewCenter();
        } else {
            System.out.println("\n\tNo such actor exists... cannot set new center of universe.");
        }
    }

    /**
     * Prints the information of a new center of the universe
     */
    public void printNewCenter() {
        int connection = baconGraph.getBaconGameGraph().numVertices() - baconGraph.missingVertices(baconGraph.getBaconGameGraph(), baconGraph.getShortestPaths()).size();
        System.out.println("\n\t" + baconGraph.getCenterOfUniverse() + " is now the center of the acting universe, connected to " + connection + "/" + baconGraph.getBaconGameGraph().numVertices() + " actors with average separation " + baconGraph.getAverageSeparation());
    }
}
