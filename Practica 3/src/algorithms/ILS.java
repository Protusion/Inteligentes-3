/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import optimization.Configuration;
import optimization.OptimizationAlgorithm;

/**
 *
 * @author Alberto
 */
public class ILS extends OptimizationAlgorithm {

    /* Number of iterations obtained as a parameter (default 20) */
    private int iterations = 20;
    /* List of all Local Optimas */
    private ArrayList<Configuration> localOptimas = new ArrayList<Configuration>();

    private Random r = new Random();

    @Override
    public void search() {

        initSearch();

        /* Generates a random starting solution */
        Configuration currentSolution = problem.genRandomConfiguration();

        int i = 0;
        while (i < iterations) {
            /* Perturbates the configuration by permutating 3 of its values */
            currentSolution = perturbate(currentSolution);
            /* Applys Hill Climbing to the permutated solution to obtain the local Optima */
            currentSolution = hillClimbing(currentSolution);
            /* Add it to the list of Local Optimas */
            localOptimas.add(currentSolution);
            i++;
        }

        stopSearch();
    }

    /*
        Shows the Local Optimas obtains thwrout all iterations
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
        Perturbates a configuration permutating 3 random values 
     */
    private Configuration perturbate(Configuration configuration) {
        int[] pert = configuration.getValues().clone();
        if (configuration.getValues().length < 3) {
            return configuration;
        }
        int bound = configuration.getValues().length - 1;

        int r1 = r.nextInt(bound);
        int r2 = r.nextInt(bound);
        int r3 = r.nextInt(bound);

        while (r1 == r2 || r1 == r3 || r2 == r3) {
            r1 = r.nextInt(bound);
            r2 = r.nextInt(bound);
            r3 = r.nextInt(bound);
        }

        pert[r1] = configuration.getValues()[r3];
        pert[r2] = configuration.getValues()[r1];
        pert[r3] = configuration.getValues()[r2];

        return new Configuration(pert);
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
