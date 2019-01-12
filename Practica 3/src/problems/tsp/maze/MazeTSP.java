package problems.tsp.maze;

import optimization.Configuration;
import problems.tsp.TSP;
import utils.Position;
import visualization.*;

/**
 * Extends the TSP to represent it in a maze where movements are either horizontal or vertical, and uses manhattan as distance.
 */
public class MazeTSP extends TSP implements ProblemVisualizable {

    /**
     * Constructors
     */
    public MazeTSP() {
        generateInstance(20, 10, 0);
    }

    public MazeTSP(int rangeXY, int numCities) {
        generateInstance(rangeXY, numCities, 0);
    }

    public MazeTSP(int rangeXY, int numCities, int seed) {
        generateInstance(rangeXY, numCities, seed);
    }

    /**
     * Returns a view of the problem.
     */
    @Override
    public ProblemView getView() {
        MazeTSPView mazeView = new MazeTSPView(this, 800);
        return mazeView;
    }

    /**
     * Calculates the score of a configuration as the sum of the path.
     */
    @Override
    public double score(Configuration configuration) {

        /* Obtains the order of the cities */
        int[] values = configuration.getValues().clone();

        double score = 0;

        /* Calculates the distance between the starting point and the first city */
        score += dist(posAgent, posCities.get(values[0]));

        /* Calculates the addition of the distances between cities in order */
        for (int i = 0; i < values.length - 1; i++) {
            score += dist(posCities.get(values[i]), posCities.get(values[i + 1]));
        }

        /* Calculates the distance between the last city and the final postion */
        score += dist(posCities.get(values[values.length - 1]), posExit);

        return score;
    }

    /**
     * Calculates the distance between two points (Manhattan)
     */
    private double dist(Position from, Position to) {
        return Math.abs(from.x - to.x) + Math.abs(from.y - to.y);
    }
}
