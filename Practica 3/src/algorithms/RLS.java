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

    private int iterations = 1000;
    private ArrayList<Configuration> localOptimas = new ArrayList<Configuration>();
    
    @Override
    public void search() {
        
        
        initSearch();

        int i = 0;
        while(i < iterations){
            Configuration randomStart = problem.genRandomConfiguration();
            Configuration localOptima = hillClimbing(randomStart);
            evaluate(localOptima);
            localOptimas.add(localOptima);
            i++;
        }
        
        stopSearch();
    }

    @Override
    public void showAlgorithmStats() {
        System.out.print("Local optimas: ");
        Collections.sort(localOptimas);
        for(Configuration localOptima : localOptimas){
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
                System.out.println("Generating a population of 1000 (\"default\").");
            }
        }
    }
    
    public Configuration hillClimbing(Configuration configuration){
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

    
    private ArrayList<Configuration> generateNeighbours(Configuration currentSolution) {
        ArrayList<Configuration> neighbours = new ArrayList<Configuration>();
        int[] values = currentSolution.getValues().clone();
        /* Swap the values of the configuration to get a new neighbour */
        for(int i = 0; i < currentSolution.getValues().length; i++){
            for(int j = i+1; j < currentSolution.getValues().length; j++){
                values = currentSolution.getValues().clone();
                values[i] = currentSolution.getValues()[j];
                values[j] = currentSolution.getValues()[i];
                neighbours.add(new Configuration(values));
            }
        }
        
        
        return neighbours;
    }

}
