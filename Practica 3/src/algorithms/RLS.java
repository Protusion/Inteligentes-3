/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import optimization.Configuration;
import optimization.OptimizationAlgorithm;

/**
 *
 * @author Alberto
 */
public class RLS extends OptimizationAlgorithm {

    /* Number of iterations obtained as a parameter (default 20) */
    private int iterations = 20;
    /* List of all Local Optimas */
    private ArrayList<Configuration> localOptimas = new ArrayList<Configuration>();

    @Override
    public void search() {

        initSearch();

        int i = 0;
        while (i < iterations) {
            System.out.println("Iteration "+i);
            /* Obtains a random configuration as a starting point */
            Configuration randomStart = problem.genRandomConfiguration();
            /* Obtains the best solution possible with the previously generated configuration applying HillClimbing */
            Configuration localOptima = hillClimbing(randomStart);
            /* Evaluates the new Local Optima */
            evaluate(localOptima);
            /* Add it to the list of Local Optimas */
            localOptimas.add(localOptima);
            i++;
        }

        stopSearch();
    }

    /*
        Shows the Local Optimas obtained throughrout all iterations
     */
    @Override
    public void showAlgorithmStats() {
        System.out.print("Local optimas: ");
        Collections.sort(localOptimas); // Sorts them by score
        for (Configuration localOptima : localOptimas) {
            System.out.print(localOptima.score() + ", ");
        }
        System.out.println();
    }

    /*
        Sets the parameters for this algorithm 
     */
    @Override
    public void setParams(String[] args) {
        if (args.length > 0) {
            try {
                iterations = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("Iterating 20 times (\"default\").");
            }
        }
    }

    /*
        Hill Climbing Algorithm 
     */
    private Configuration hillClimbing(Configuration configuration) {
        Configuration currentSolution = configuration.clone();
        double currentScore = evaluate(currentSolution);
        boolean improves = true;

        while (improves) {
            improves = false;
            ArrayList<Configuration> neighbours = generateNeighbours(currentSolution);

            for (Configuration neighbour : neighbours) {
                double newScore = evaluate(neighbour);
                if (newScore < currentScore) {
                    currentSolution = neighbour.clone();
                    currentScore = newScore;
                    improves = true;
                }
            }
        }
        return currentSolution;
    }

    /* 
        Axuiliary function for Hill Climbing (generates neighberhood) 
     */
    private ArrayList<Configuration> generateNeighbours(Configuration currentSolution) {
        ArrayList<Configuration> neighbours = new ArrayList<Configuration>();
        int[] values = currentSolution.getValues().clone();
        for (int i = 0; i < currentSolution.getValues().length; i++) {
            for (int j = i + 1; j < currentSolution.getValues().length; j++) {
                values = currentSolution.getValues().clone();
                values[i] = currentSolution.getValues()[j];
                values[j] = currentSolution.getValues()[i];
                neighbours.add(new Configuration(values));
            }
        }

        return neighbours;
    }

}
