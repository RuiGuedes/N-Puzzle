package core;

import java.util.ArrayList;
import java.util.Arrays;

public class State {

    /**
     * Matrix size
     */
    private int N = 3;
    //private int N = 4;

    /**
     * Data structure containing matrix status
     */
    private int[][] matrix;

    /**
     * Data structure containing the problem solution
     */
    private int[][] solution = {{1,2,3}, {4,5,6}, {7,8,0}}; // Prob1|2|3 //
    //private int[][] solution = {{1,2,3,4}, {5,6,7,8}, {9,10,11,12}, {13,14,15,0}}; // Prob4 //

    /**
     * Position of value 0 in matrix
     */
    private int[][] free_cell = {{1}, {1}}; // Prob1|4 //
    //private int[][] free_cell = {{2}, {1}}; // Prob2 //
    //private int[][] free_cell = {{0}, {2}}; // Prob3 //

    /**
     * Path cost so far
     */
    private int pathCost = 0;

    /**
     * Default constructor
     */
    State(int[][] matrix) {
        this.matrix = deepCopy(matrix);
    }

    State(State currState) {
        this.matrix = deepCopy(currState.getMatrix());
        this.pathCost = currState.getPathCost() + 1;
        this.free_cell = deepCopy(currState.getFreeCell());
    }

    /**
     * Get path cost so far
     * @return Path cost
     */
    int getPathCost() {
        return pathCost;
    }

    /**
     * Returns matrix
     * @return Matrix
     */
    public int[][] getMatrix() {
        return matrix;
    }

    /**
     * Returns solution
     * @return Solution
     */
    public int[][] getSolution() {
        return solution;
    }

    /**
     * Returns free cell
     * @return Free cell
     */
    private int[][] getFreeCell() {
        return free_cell;
    }

    /**
     * Get possible actions for a certain state
     * @return Possible actions
     */
    public ArrayList<String> getActions() {
        ArrayList<String> actions = new ArrayList<>();

        int currX = free_cell[0][0];
        int currY = free_cell[1][0];
        int limit = matrix.length - 1;

        if(currX != 0)
            actions.add("LEFT");

        if(currX != limit)
            actions.add("RIGHT");

        if(currY != 0)
            actions.add("UP");

        if(currY != limit)
            actions.add("DOWN");

        return actions;
    }

    /**
     * Executes a certain action
     * @param action Action to be executed
     */
    void executeAction(String action) {
        int incX = 0;
        int incY = 0;

        switch (action) {
            case "UP":
                incY = -1;
                break;
            case "DOWN":
                incY = 1;
                break;
            case "RIGHT":
                incX = 1;
                break;
            case "LEFT":
                incX = -1;
                break;
        }

        matrix[free_cell[1][0]][free_cell[0][0]] = matrix[free_cell[1][0] + incY][free_cell[0][0] + incX];
        matrix[free_cell[1][0] + incY][free_cell[0][0] + incX] = 0;
        free_cell[0][0] += incX;
        free_cell[1][0] += incY;
    }

    /**
     * Gets manhattan distance from a misplaced piece to its correct position
     * @param y Y position
     * @param x X position
     * @return Distance
     */
    double getManhattanDistance(int y, int x)  {
        int num = matrix[y][x];
        double result = 0;

        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                if(solution[i][j] == num)
                    result = Math.abs(y - i) + Math.abs(x - j);
            }
        }

        return result;
    }


    /**
     * Deep copy a bi-dimensional array
     * @param original Original array
     * @return Return the original array copy
     */
    private static int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }

        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }
}
