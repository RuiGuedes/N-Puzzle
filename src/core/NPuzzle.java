package core;

import agent.Action;
import search.agent.SearchAgent;
import search.framework.Metrics;
import search.framework.SearchForActions;
import search.framework.problem.GeneralProblem;
import search.framework.problem.Problem;
import search.framework.qsearch.TreeSearch;
import search.informed.AStarSearch;
import search.informed.GreedyBestFirstSearch;
import search.uninformed.BreadthFirstSearch;

import java.io.IOException;
import java.util.*;

public class NPuzzle {

    /**
     * Scanner to read user input
     */
    private static final Scanner reader = new Scanner(System.in);

    /**
     * Min depth for Depth-Limited Search
     */
    private static int MIN_DEPTH = 1;

    /**
     * Max depth for Depth-Limited Search
     */
    private static int MAX_DEPTH = 1000;

    public static void main(String[] args) throws Exception {

        // Read filename and initialize map
        new Puzzle().display();

        // Display interface
        mainMenu();
    }

    /**
     * Displays main menu
     */
    private static void mainMenu() throws Exception {

        ArrayList<String> displayOptions = new ArrayList<>() {
            {
                add("1 - Breadth-First Search");
                add("2 - Greedy-Best-First Search");
                add("3 - A-StarSearch");
                add("4 - Exit\n");
            }
        };

        ArrayList<String> possibleHeuristics = new ArrayList<>() {
            {
                add("\nHeuristic Functions\n");
                add("1 - Misplaced Pieces");
                add("2 - Manhattan Distance\n");
            }
        };

        while(true) {
            displayTitle();
            String option = read_input(displayOptions, "Select an algorithm: ", "Invalid option. Try again !", 1, displayOptions.size());

            switch (option) {
                case "1":
                    uninformedSearch(new Puzzle(), "Breadth-First Search");
                    break;
                case "2":
                    String greedyHeuristic = read_input(possibleHeuristics, "Select an heuristic: ", "Invalid option. Try again !", 1, possibleHeuristics.size() - 1);
                    informedSearch(new Puzzle(), "Greedy-Best-First Search", Integer.parseInt(greedyHeuristic));
                    break;
                case "3":
                    String aStarHeuristic = read_input(possibleHeuristics, "Select an heuristic: ", "Invalid option. Try again !", 1, possibleHeuristics.size() - 1);
                    informedSearch(new Puzzle(), "A-StarSearch", Integer.parseInt(aStarHeuristic));
                    break;
                case "4":
                    return;
            }
        }
    }

    /**
     * Read options
     * @return Selected option
     */
    private static String read_input(ArrayList<String> displayOptions, String displayQuestion, String displayInvalid, int lowerLimit, int upperLimit) {
        String option;

        for(String displayOption : displayOptions)
            System.out.println(displayOption);

        while (true) {
            System.out.print(displayQuestion);
            option = reader.nextLine();

            try {
                if(Integer.parseInt(option) >= lowerLimit && Integer.parseInt(option) <= upperLimit)
                    break;
                else
                    throw new Exception();
            }
            catch (Exception e) {
                System.out.println(displayInvalid + "\n");
            }
        }

        return option;
    }

    /**
     * Performs uninformed search
     * @param map Puzzle chosen
     * @param algorithm Algorithm to be used
     */
    private static void uninformedSearch(Puzzle map, String algorithm) throws IOException, InterruptedException {
        Problem<State, Action> problem = new GeneralProblem<>(map.getCurrState(), Puzzle::getActions, Puzzle::getResult,  Puzzle::testGoal);
        SearchForActions<State, Action> search = new BreadthFirstSearch<>(new TreeSearch<>());

        long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long start = System.currentTimeMillis();

        Optional<List<Action>> actions = search.findActions(problem);

        long elapsedTime = System.currentTimeMillis() - start;
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        if(actions.isPresent())
            displayAlgorithmInformation(map, actions.get(), search.getMetrics(), null, elapsedTime, memoryUsage);
        else
            displayAlgorithmInformation(map, new ArrayList<>(), search.getMetrics(), null, elapsedTime, memoryUsage);
    }

    /**
     * Performs informed search
     * @param map Puzzle chosen
     * @param algorithm Algorithm to be used
     */
    private static void informedSearch(Puzzle map, String algorithm, int heuristic) throws Exception {
        Problem<State, Action> problem = new GeneralProblem<>(map.getCurrState(), Puzzle::getActions, Puzzle::getResult,  Puzzle::testGoal);
        SearchForActions<State, Action> search = null;

        switch (algorithm) {
            case "Greedy-Best-First Search":
                search = new GreedyBestFirstSearch<>(new TreeSearch<>(), Puzzle.createHeuristicFunction(heuristic));
                break;
            case "A-StarSearch":
                search = new AStarSearch<>(new TreeSearch<>(), Puzzle.createHeuristicFunction(heuristic));
                break;
        }

        long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long start = System.currentTimeMillis();

        SearchAgent<State, Action> agent = new SearchAgent<>(problem, search);

        long elapsedTime = System.currentTimeMillis() - start;
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        displayAlgorithmInformation(map, agent.getActions(), null, agent.getInstrumentation(), elapsedTime, memoryUsage);
    }

    /**
     * Clears screen
     */
    private static void clearScreen() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    /**
     * Displays game title
     */
    private static void displayTitle() throws IOException, InterruptedException {
        clearScreen();
        System.out.println("##############");
        System.out.println("## N-Puzzle ##");
        System.out.println("##############\n");
    }

    /**
     * Displays algorithm detailed information
     * @param actions Actions made
     * @param metrics Algorithm metrics
     * @param properties Algorithm metrics
     * @param elapsedTime Time elapsed
     */
    private static void displayAlgorithmInformation(Puzzle map, List<Action> actions, Metrics metrics, Properties properties, long elapsedTime, long memoryUsage) throws IOException, InterruptedException {
        displayTitle();
        map.display();
        displaySolution(actions);
        displayStatistics(metrics, properties, elapsedTime, memoryUsage);
        blockUntil();
    }

    /**
     * Displays level solution
     * @param actions Actions made
     */
    private static void displaySolution(List<Action> actions) {
        System.out.println("##################");
        System.out.println("# Puzzle solution #");
        System.out.println("##################\n");

        if(actions.isEmpty())
            System.out.println("No solution available to be displayed.");

        for(int i = 0; i < actions.size(); i++) {
            System.out.println(i + " - " + actions.get(i));
        }
    }

    /**
     * Displays algorithm metrics
     * @param metrics Algorithm metrics
     * @param properties Algorithm metrics
     * @param elapsedTime Time elapsed
     */
    private static void displayStatistics(Metrics metrics, Properties properties, long elapsedTime, long memoryUsage) {

        ArrayList<String> stats = new ArrayList<>();
        ArrayList<String> info = new ArrayList<>() {
            {
                add("maxQueueSize");
                add("nodesExpanded");
                add("pathCost");
                add("queueSize");
            }
        };

        for (String name : info) {
            if (metrics != null)
                stats.add(metrics.get(name));
            else
                stats.add(properties.get(name).toString());
        }

        System.out.println("\n##############");
        System.out.println("# Statistics #");
        System.out.println("##############\n");
        System.out.println("Maximum Queue Size = " + stats.get(0));
        System.out.println("Nodes Expanded = " + stats.get(1));
        System.out.println("Path Cost = " + stats.get(2));
        System.out.println("Queue Size = " + stats.get(3));
        System.out.println("Time Spent = " + elapsedTime + " ms");
        System.out.println("Memory Usage = " + humanReadableByteCount(memoryUsage, true));
        System.out.println();
    }

    /**
     * Blocks until user press any key to continue
     */
    private static void blockUntil() {
        System.out.print("Press any key to continue ... ");
        while (true) {
            if(reader.hasNextLine()) {
                reader.nextLine();
                break;
            }

        }
    }

    /**
     * Displays memory usage in a friendly way.
     * @param bytes Amount of bytes
     * @param si Display mode
     * @return Bytes formatted
     * @author aioobe
     */
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
