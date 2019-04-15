package core;

import agent.Action;
import agent.impl.DynamicAction;
import search.framework.Node;

import java.util.*;
import java.util.function.ToDoubleFunction;

public class Puzzle {

    /**
     * Initial state
     */
    private static int[][] initialMatrix = {{1,2,3}, {5,0,6}, {4,7,8}};   // Prob1 //
    //private static int[][] initialMatrix = {{1,3,6}, {5,2,0}, {4,7,8}}; // Prob2 //
    //private static int[][] initialMatrix = {{1,6,2}, {5,7,3}, {0,4,8}}; // Prob3 //
    //private static int[][] initialMatrix = {{5,1,3,4}, {2,0,7,8}, {10,6,11,12}, {9,13,14,15}}; // Prob4 //

    /**
     * Current game state
     */
    private static State currState;

    /**
     * Holds all information about the visited nodes at a certain moment
     */
    private static Map<String, Integer> searchInfo;

    /**
     * Initializes level class
     */
    Puzzle() {
        currState = new State(initialMatrix);
        searchInfo = new HashMap<>();
    }

    /**
     * Get matrix
     * @return matrix
     */
    int[][] getMatrix() {
        return currState.getMatrix();
    }

    /**
     * Return the game current state
     * @return Current state
     */
    State getCurrState() {
        return currState;
    }

    /**
     * Returns data structure that contains information about the search made
     * @return Search structure
     */
    public static Map<String, Integer> getSearchInfo() {
        return searchInfo;
    }

    /**
     * Resets current state 
     */
    public static void reset() {
        currState = new State(initialMatrix);
        searchInfo = new HashMap<>();
    }

    /**
     * Display matrix in a friendly way
     */
    void display() {
        System.out.println();
        for(int row = 0; row < currState.getMatrix().length; row++) {
            for(int column = 0; column < currState.getMatrix()[row].length; column++) {
                System.out.print(currState.getMatrix()[row][column] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Get possible actions for a certain state
     * @param currState Current state
     * @return List of possible actions
     */
    static List<Action> getActions(State currState) {
        List<Action> actions = new ArrayList<>();

        for(String action : currState.getActions()) {
            State nextState = getResult(currState, new DynamicAction(action));

            if(searchInfo.containsKey(Arrays.deepToString(nextState.getMatrix()))) {
                if(searchInfo.get(Arrays.deepToString(nextState.getMatrix())) > nextState.getPathCost()) {
                    searchInfo.put(Arrays.deepToString(nextState.getMatrix()), nextState.getPathCost());
                    actions.add(new DynamicAction(action));
                }
            }
            else {
                searchInfo.put(Arrays.deepToString(nextState.getMatrix()), nextState.getPathCost());
                actions.add(new DynamicAction(action));
            }
        }

        return actions;
    }

    /**
     * Get the result for a certain action
     * @param currState All agents in a certain map
     * @param action Action to be performed
     * @return The new state reached
     */
    static State getResult(State currState, Action action) {
        State nextState = new State(currState);

        String action_type = ((DynamicAction) action).getName();
        nextState.executeAction(action_type);

        return nextState;
    }

    /**
     * Checks whether level is completed or not
     * @return True if level is complete. False otherwise
     */
    static boolean testGoal(State currState) {
        for(int i = 0; i < currState.getMatrix().length; i++) {
            for(int j = 0; j < currState.getMatrix()[i].length; j++) {
                if(currState.getMatrix()[i][j] != currState.getSolution()[i][j])
                    return false;
            }
        }
        return true;
    }

    /**
     * Creates heuristic function
     * @return New heuristic function
     */
    static ToDoubleFunction<Node<State, Action>> createHeuristicFunction(int heuristicFunction) {
        if(heuristicFunction == 1)
            return new MisplacePieces();
        else
            return new ManhattanDistance();
    }

    
    private static class MisplacePieces implements ToDoubleFunction<Node<State, Action>> {

        @Override
        public double applyAsDouble(Node<State, Action> node) {
            double result = 0;

            for(int i = 0; i < node.getState().getMatrix().length; i++) {
                for(int j = 0; j < node.getState().getMatrix()[i].length; j++) {
                    if(node.getState().getMatrix()[i][j] != node.getState().getSolution()[i][j])
                        result++;
                }
            }

            return result;
        }
    }

    private static class ManhattanDistance implements ToDoubleFunction<Node<State, Action>> {

        @Override
        public double applyAsDouble(Node<State, Action> node) {
            double result = 0;

            for(int i = 0; i < node.getState().getMatrix().length; i++) {
                for(int j = 0; j < node.getState().getMatrix()[i].length; j++) {
                    if(node.getState().getMatrix()[i][j] != node.getState().getSolution()[i][j])
                        result += node.getState().getManhattanDistance(i, j);
                }
            }

            return result;
        }
    }

}
