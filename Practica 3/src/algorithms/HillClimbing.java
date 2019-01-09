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

        Configuration currentSolution = problem.genRandomConfiguration();
        evaluate(currentSolution);
        boolean improves = true;

        while (improves) {
            improves = false;
            ArrayList<Configuration> neighbours = generateNeighbours(currentSolution);

            for (Configuration neighbour : neighbours) {
                double score = evaluate(neighbour);
                if (score < bestScore) {
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
