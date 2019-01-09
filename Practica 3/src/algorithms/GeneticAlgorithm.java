/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import optimization.Configuration;
import optimization.OptimizationAlgorithm;

/**
 *
 * @author Alberto
 */
public class GeneticAlgorithm extends OptimizationAlgorithm {

    private int population = 1000;
    private int generations = 1000;
    private double probMutation = 0.1;
    private double probCrossover = 1;
    private int replacement = 0;

    @Override
    public void search() {
        ArrayList<Configuration> population = new ArrayList<Configuration>();
        HashMap<Configuration, Double> scores = new HashMap<Configuration, Double>();

        initSearch();

        population = this.generatePopulation();
        scores = this.evaluate(population);

        int i = 0;
        while (i < generations) {
            ArrayList<Configuration> newPopulation = this.selectPopulation(scores);
            newPopulation = this.crossPopulation(newPopulation);
            newPopulation = this.mutatePopulation(newPopulation);
            scores = this.evaluate(newPopulation);
            population = this.replacement(population, newPopulation);
            i++;
        }

    }

    /*
        Generates a new random population
     */
    public ArrayList<Configuration> generatePopulation() {
        ArrayList<Configuration> generatedPopulation = new ArrayList<Configuration>();

        for (int i = 0; i < this.population; i++) {
            generatedPopulation.add(problem.genRandomConfiguration());
        }

        return generatedPopulation;
    }

    /*
        Evaluates a population
     */
    public HashMap<Configuration, Double> evaluate(ArrayList<Configuration> population) {
        HashMap<Configuration, Double> scores = new HashMap<Configuration, Double>();
        for (Configuration individual : population) {
            scores.put(individual, evaluate(individual));
        }

        return scores;
    }

    /*
        Selects the best individuals based on the lowest scores
     */
    public ArrayList<Configuration> selectPopulation(HashMap<Configuration, Double> scores) {
        ArrayList<Configuration> optimalPopulation = new ArrayList<Configuration>();
        HashMap<Configuration, Double> currentScores = (HashMap<Configuration, Double>) scores.clone();

        for (int i = 0; i < scores.size() / 2; i++) {
            double minScore = (Collections.min(currentScores.values()));  // Returns the max value in the Hashmap
            for (Entry<Configuration, Double> entry : scores.entrySet()) {  // Iterates through hashmap
                if (entry.getValue() == minScore) { // Finds the configuration with the maxScore
                    optimalPopulation.add(entry.getKey()); // Adds the configuration
                    currentScores.remove(entry); // Removes the configuration from the set of possible configurations
                }
            }
        }

        return optimalPopulation;
    }

    /*
        Based on Ordered Crossover
     */
    public ArrayList<Configuration> crossPopulation(ArrayList<Configuration> population) {
        ArrayList<Configuration> crossedPopulation = new ArrayList<Configuration>();
        Random r = new Random();

        for (int m = 0; m < population.size() - 1; m++) {
            if (probCrossover > r.nextDouble()) {
                int[] parent1 = population.get(m).getValues();
                int[] parent2 = population.get(m + 1).getValues();

                int l = parent1.length;
                // Get 2 random ints between 0 and the size of the array
                int r1 = r.nextInt(l);
                int r2 = r.nextInt(l);
                // To make sure r1 < r2
                while (r1 >= r2) {
                    r1 = r1 = r.nextInt(l);
                    r2 = r.nextInt(l);
                }
                // Creates the child .... initial elements are -1
                int[] child = new int[l];
                for (int i = 0; i < l; i++) {
                    child[i] = -1;
                }
                // Copy the elements between r1, r2 from parent1 into child
                for (int i = r1; i <= r2; i++) {
                    child[i] = parent1[i];
                }
                // Aux array to hold elements of parent1 which are not in child yet
                int[] y = new int[l - (r2 - r1) - 1];
                int j = 0;
                for (int i = 0; i < l; i++) {
                    if (!arrayContains(child, parent1[i])) {
                        y[j] = parent1[i];
                        j++;
                    }
                }
                // rotate parent2
                // Number of places is the same as the number of elements after r2
                int[] copy = parent2.clone();
                copy = rotate(copy, l - r2 - 1);

                // Now order the elements in y according to their order in parent2
                int[] y1 = new int[l - (r2 - r1) - 1];
                j = 0;
                for (int i = 0; i < l; i++) {
                    if (arrayContains(y, copy[i])) {
                        y1[j] = copy[i];
                        j++;
                    }
                }

                // Now we copy the ramaining elments into child
                // According to their order in parent 2 .... starting after r2!
                j = 0;
                for (int i = 0; i < y1.length; i++) {
                    int ci = (r2 + i + 1) % l;
                    child[ci] = y1[i];
                }

                crossedPopulation.add(new Configuration(child));

            }
        }
        return crossedPopulation;
    }

    /*
        Mutates a population based on swapping
     */
    public ArrayList<Configuration> mutatePopulation(ArrayList<Configuration> population) {
        ArrayList<Configuration> mutatedPopulation = new ArrayList<Configuration>();
        Random r = new Random();

        for (Configuration individual : population) {
            if (r.nextDouble() < probMutation) {
                int[] values = individual.getValues();
                int r1 = r.nextInt(values.length);
                int r2 = r.nextInt(values.length);

                int temp = values[r1];
                values[r1] = values[r2];
                values[r2] = temp;
                
                mutatedPopulation.add(new Configuration(values));
            }
        }

        return mutatedPopulation;
    }

    /*
        Combines former population with the new population applying Truncation
     */
    public ArrayList<Configuration> replacement(ArrayList<Configuration> formerPopulation, ArrayList<Configuration> newPopulation) {
        ArrayList<Configuration> generatedPopulation = new ArrayList<Configuration>();

        switch (replacement) {
            case 0:
                generatedPopulation = newPopulation;
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                generatedPopulation = newPopulation;
        }
        return generatedPopulation;
    }

    @Override
    public void showAlgorithmStats() {
        return;
    }

    @Override
    public void setParams(String[] args) {
        if (args.length > 0) {
            try {
                population = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("Generating a population of 1000 (\"default\").");
            }
            if (args.length > 1) {
                try {
                    generations = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Applying the algorithm for 1000 generations (\"default\").");
                }
                if (args.length > 2) {
                    try {
                        probCrossover = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        System.out.println("Probability of  (\"default\").");
                    }
                    if (args.length > 3) {
                        try {
                            probMutation = Integer.parseInt(args[3]);
                        } catch (Exception e) {
                            System.out.println("Generating a population of 1000 (\"default\").");
                        }
                        if (args.length > 4) {
                            try {
                                int typeOfReplac = Integer.parseInt(args[4]);
                                if (typeOfReplac >= 0 && typeOfReplac < 4) {
                                    replacement = typeOfReplac;
                                }
                            } catch (Exception e) {
                                System.out.println("Generating a population of 1000 (\"default\").");
                            }
                        }
                    }
                }
            }
        }
    }

    /*
        Checks if the value is inside the array (aux function to work with arrays)
     */
    private boolean arrayContains(int[] array, int value) {

        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }

        return false;
    }

    /*
        Rotates the array (aux function to work with arrays)
     */
    private int[] rotate(int[] array, int number) {
        int[] copy = array.clone();

        for (int j = 0; j < number; j++) {
            int temp = copy[copy.length - 1]; // store the last element
            for (int i = copy.length - 1; i > 0; i--) {
                copy[i] = copy[i - 1];   // do the switch
            }
            copy[0] = temp; // restore the last element into the first one
        }

        return array;
    }

}
