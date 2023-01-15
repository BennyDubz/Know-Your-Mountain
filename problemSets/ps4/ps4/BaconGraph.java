package ps4;


import java.util.*;

/**
 * Class that holds a graph of all people and a set of all their movie connections as label
 * Also holds a graph of shortest paths to center of universe. Shortest paths graph only contains directed edges that point towards center of universe.
 * @author Alex Craig
 * @author Ben Williams
 */
public class BaconGraph {
    AdjacencyMapGraph<String, HashSet<String>> baconGameGraph;
    AdjacencyMapGraph<String, HashSet<String>> shortestPaths;
    String centerOfUniverse;
    HashMap<Integer, String> actorIDs, movieIDs;
    HashMap<Integer, HashSet<Integer>> movieToActorIDs;

    public BaconGraph(HashMap<Integer, String> actorIDs, HashMap<Integer, String> movieIDs, HashMap<Integer, HashSet<Integer>> movieToActorIds, String center) {
        this.actorIDs = actorIDs;
        this.movieIDs = movieIDs;
        this.movieToActorIDs = movieToActorIds;
        baconGameGraph = makeGameGraph();
        centerOfUniverse = center;
        shortestPaths = (AdjacencyMapGraph<String, HashSet<String>>) bfs(baconGameGraph, centerOfUniverse);
    }

    public AdjacencyMapGraph<String, HashSet<String>> makeGameGraph() {
        AdjacencyMapGraph<String, HashSet<String>> ret = new AdjacencyMapGraph<>();

        for (String actorName : actorIDs.values()) {
            ret.insertVertex(actorName);
        }

        // Look through all movies
        for (Integer movieID : movieToActorIDs.keySet()) {
            // Look through all actors in each movie
            for (Integer currentActorID : movieToActorIDs.get(movieID)) {
                // For each actor in each movie, make or add the current movie to the set of connections between all other actors in movie if the connection doesn't already exist
                for (Integer addingActorID : movieToActorIDs.get(movieID)) {
                    // Make sure we won't add a connection between the same actor
                    if (!Objects.equals(currentActorID, addingActorID)) {
                        if (!ret.hasEdge(actorIDs.get(currentActorID), actorIDs.get(addingActorID))) { // If the connection between actors doesn't already exist, make a new edge with this movie as first connection in set
                            HashSet<String> newConnection = new HashSet<String>();
                            newConnection.add((movieIDs.get(movieID)));
                            ret.insertUndirected(actorIDs.get(currentActorID), actorIDs.get(addingActorID), newConnection);
                        } else { // If there already exists a connection between the actors, add this new movie to the set of connections (edge label)
                            ret.getLabel(actorIDs.get(currentActorID), actorIDs.get(addingActorID)).add(movieIDs.get(movieID));
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Does a breadth first search of all other actors to find shortest paths to them
     */
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
        Queue<V> queue = new LinkedList<>();
        queue.add(source);
        HashSet<V> visited = new HashSet<>();
        visited.add(source);
        // Result graph that contains shortest paths in form of directed edges to source
        AdjacencyMapGraph<V,E> result = new AdjacencyMapGraph<>();
        result.insertVertex(source);
        // BFS Algorithm
        while (!queue.isEmpty()) {
            V node = queue.remove();
            for (V adjNode : g.inNeighbors(node)) {
                if (!visited.contains(adjNode)) {
                    // Add neighbor vertices to graph
                    result.insertVertex(adjNode);
                    // Insert edge that points towards the root
                    result.insertDirected(adjNode, node, g.getLabel(adjNode, node));
                    visited.add(adjNode);
                    queue.add(adjNode);
                }
            }
        }

        return result;
    }

    /**
     * @param tree must be a graph with directed edges where all edges point towards root
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        ArrayList<V> result = new ArrayList<>();
        V currNode = v;
        // Runs until the root is found
        while (tree.outDegree(currNode) > 0) {
            // Adds node to the ArrayList
            result.add(currNode);
            // Changes current node to the next node towards the root
            for (V newNode : tree.outNeighbors(currNode)) currNode = newNode;
        }
        result.add(currNode);
        return result;
    }

    /**
     * Returns set of vertices that have no connection to center of universe
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subGraph) {
        HashSet<V> result = new HashSet<>();
        // Loops through all vertices in big graph
        for (V node : graph.vertices()) {
            // If vertex is missing from the small graph, add it to result
            if (!subGraph.hasVertex(node)) result.add(node);
        }
        return result;
    }

    public static <V,E> double averageSeparation(Graph<V,E> tree, V root, int lengthTracker) {
        double average = 0;
        // Base case when at end of path
        if (tree.inDegree(root) == 0) {
            // Creates average along the way rather than dividing at the end
            return (double) lengthTracker / tree.numVertices();
        }

        // Recursively calls with in neighbors
        for (V node : tree.inNeighbors(root)) {
            average += averageSeparation(tree, node, lengthTracker +1) + ((double) lengthTracker / tree.numVertices());
        }

        return average;
    }

    /**
     * Changes who the center of universe is
     */
    public void setCenterOfUniverse(String newCenter) {
        centerOfUniverse = newCenter;
        shortestPaths = (AdjacencyMapGraph<String, HashSet<String>>) bfs(baconGameGraph, centerOfUniverse);
    }

    public String getCenterOfUniverse() {
        return centerOfUniverse;
    }

    public AdjacencyMapGraph<String, HashSet<String>> getBaconGameGraph() {
        return baconGameGraph;
    }

    public AdjacencyMapGraph<String, HashSet<String>> getShortestPaths() {
        return shortestPaths;
    }

    public HashMap<Integer, String> getActorIDs() {
        return actorIDs;
    }

    public HashMap<Integer, String> getMovieIDs() {
        return movieIDs;
    }

    public HashMap<Integer, HashSet<Integer>> getMovieToActorIDs() {
        return movieToActorIDs;
    }

    public Double getAverageSeparation() {
        return averageSeparation(shortestPaths,centerOfUniverse,0);
    }
}
