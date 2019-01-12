/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.util.ArrayList;
import optimization.Configuration;
import optimization.OptimizationAlgorithm;

/**
 *
 * @author Alberto
 */
public class HillClimbing extends OptimizationAlgorithm {

    @Override
    public void search() {
        initSearch();
        
        /* Generates a random configuration as a starting point */
        Configuration currentSolution = this.problem.genRandomConfiguration();
        
        /* Evaluates the previously generated configuration */
        double currentScore = evaluate(currentSolution);
        
        boolean improves = true;

        /* While it improves ... */
        while (improves) {
            improves = false;
            
            /* Obtains the neighbours of the configuration we are working with */
            ArrayList<Configuration> neighbours = generateNeighbours(currentSolution);

            /* For each neighour ... */
            for (Configuration neighbour : neighbours) {
                /* It is evaluated */
                double newScore = evaluate(neighbour);
                /* If its score (total distance) is lower than the previous solution ... */
                if (newScore < currentScore) {
                    currentSolution = neighbour.clone();
                    currentScore = newScore; 
                    improves = true;
                }
            }
        }    
        
        stopSearch();

    }
    
    

    @Override
    public void showAlgorithmStats() {
        return;
    }

    @Override
    public void setParams(String[] args) {
        return;
    }

    private ArrayList<Configuration> generateNeighbours(Configuration currentSolution) {
        ArrayList<Configuration> neighbours = new ArrayList<Configuration>();
        int[] values = currentSolution.getValues().clone();
        /* Permutates the values of the configuration to get a new neighbour */
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