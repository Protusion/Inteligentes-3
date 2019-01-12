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
public class GeneticAlgorithm extends OptimizationAlgorithm {

    private int population = 1000; // Number of individuals in each generation (set by parameter | default 1000)
    private int generations = 300; // Number of iterations (set by parameter | default 300)
    private double probMutation = 0.1; // Probability of mutating an individual (set by parameter | default 0.1)
    private double probCrossover = 1; // Probability of crossing two individuals to generate 
    private int replacement = 0; // Type of replacement (0 | replacement, 1 | elitism, 2 | truncation, default | replacement)

    private Random r = new Random();

    @Override
    public void search() {
        
        initSearch();

        ArrayList<Configuration> currentPopulation = new ArrayList<Configuration>();
        
        /* Generates the population */
        currentPopulation = this.generatePopulation();
        /* Evaluates the population */
        this.evaluate(currentPopulation);

        int i = 0;
        while (i < generations) {
            System.out.println("Iteration "+i);
            ArrayList<Configuration> newPopulation = this.selectPopulation(currentPopulation); // Selects the new population using ranks selection
            newPopulation = this.crossPopulation(newPopulation); // Crosses the invidiuals of the population applying 2PCS crossover
            newPopulation = this.mutatePopulation(newPopulation); // Mutates the individuals by swaping the values of 2 random positions of values array
            this.evaluate(newPopulation); // Evaluates the new generated population
            currentPopulation = this.replacement(currentPopulation, newPopulation); // Combines both populations depending on the replacent type selected
            i++;
        }

        stopSearch();

    }

    /*
        Generates a new random population
     */
    public ArrayList<Configuration> generatePopulation() {
        ArrayList<Configuration> generatedPopulation = new ArrayList<Configuration>();

        /* Loops generating random individuals */ 
        for (int i = 0; i < this.population; i++) {
            generatedPopulation.add(problem.genRandomConfiguration());
        }

        return generatedPopulation;
    }

    /*
        Evaluates a population
     */
    public void evaluate(ArrayList<Configuration> population) {
        /* Loops through all individuals evaluating them */
        for (Configuration individual : population) {
            evaluate(individual);
        }

    }

    /*
        Selects the population apllying ranks selection
     */
    public ArrayList<Configuration> selectPopulation(ArrayList<Configuration> population) {
        /* New population with the new individuals selected */
        ArrayList<Configuration> newPopulation = new ArrayList<Configuration>();
        
        /* Clone of the given population so modifications dont affect the population passed through parameters */
        ArrayList<Configuration> currentPopulation = (ArrayList<Configuration>) population.clone();

        /* Sort all individuals by score */
        Collections.sort(currentPopulation);

        /* Get an array with all of the ranks */
        int[] ranks = new int[currentPopulation.size()];
        ranks[0] = 1;
        int totalRank = 1;

        for (int i = 1; i < currentPopulation.size(); i++) {
            ranks[i] = i + 1;
            totalRank += i + 1;
        }

        /* Get the cumulative absolute frequencies */
        double[] frequencies = new double[currentPopulation.size()];
        frequencies[0] = (1.0 / totalRank) * (currentPopulation.size() - ranks[0] + 1);
        for (int i = 1; i < currentPopulation.size(); i++) {

            frequencies[i] = (1.0 / totalRank) * (currentPopulation.size() - ranks[i] + 1);
            frequencies[i] += frequencies[i - 1];

        }

        /* Select the new population */
        boolean found;
        for (int i = 0; i < currentPopulation.size(); i++) {
            found = false;
            for (int j = 0; j < frequencies.length; j++) {
                if (frequencies[j] > r.nextDouble() && !found) {
                    newPopulation.add(currentPopulation.get(j));
                    found = true;
                }
            }
        }

        return newPopulation;
    }

    /*
        Based on 2PCS crossover (https://www.youtube.com/watch?v=4YjNe3qvVlI)
     */
    public ArrayList<Configuration> crossPopulation(ArrayList<Configuration> population) {
        ArrayList<Configuration> crossedPopulation = new ArrayList<Configuration>();
        
        int m = 0;
        while(crossedPopulation.size() != population.size()){
            if (probCrossover >= r.nextDouble()) {
                
                int p1 = m;
                int p2 = m + 1;
                
                if(m == population.size() - 1){p2 = 0;}
                
                int[] parent1 = population.get(p1).getValues();
                int[] parent2 = population.get(p2).getValues();

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
            m++;
            if(m > population.size()){m = 0;}
        }
        return crossedPopulation;
    }

    /*
        Mutates a population based on swapping
     */
    public ArrayList<Configuration> mutatePopulation(ArrayList<Configuration> population) {
        ArrayList<Configuration> mutatedPopulation = new ArrayList<Configuration>();
        for (Configuration individual : population) {
            if (r.nextDouble() <= probMutation) {
                int[] values = individual.getValues();
                int r1 = r.nextInt(values.length);
                int r2 = r.nextInt(values.length);

                int temp = values[r1];
                values[r1] = values[r2];
                values[r2] = temp;

                mutatedPopulation.add(new Configuration(values));
            }else{
                mutatedPopulation.add(individual.clone());
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
            case 0: // Replacement
                generatedPopulation = newPopulation;
                break;
            case 1: // Elitism
                Collections.sort(formerPopulation);
                Collections.sort(newPopulation);
                for(int i = 0; i < formerPopulation.size()-1; i++){
                    generatedPopulation.add(newPopulation.get(i));
                }
                generatedPopulation.add(formerPopulation.get(formerPopulation.size()-1));
                break;
            case 2: // Truncation
                ArrayList<Configuration> mergedPopulations = (ArrayList<Configuration>) formerPopulation.clone();
                mergedPopulations.addAll(newPopulation);
                Collections.sort(mergedPopulations);
                for(int i = 0; i < formerPopulation.size(); i++){
                    generatedPopulation.add(mergedPopulations.get(i));
                }
                break;
            default:
                generatedPopulation = (ArrayList<Configuration>)newPopulation.clone();
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
                        System.out.println("Probability of crossover: 1 (\"default\").");
                    }
                    if (args.length > 3) {
                        try {
                            probMutation = Integer.parseInt(args[3]);
                        } catch (Exception e) {
                            System.out.println("Probability of mutation: 0.1 (\"default\").");
                        }
                        if (args.length > 4) {
                            try {
                                int typeOfReplac = Integer.parseInt(args[4]);
                                if (typeOfReplac >= 0 && typeOfReplac < 4) {
                                    replacement = typeOfReplac;
                                }
                            } catch (Exception e) {
                                System.out.println("Type of replacement: new population used (\"default\").");
                            }
                        }
                    }
                }
            }
        }
    }

    /* ----------- Auxuliary functions ------------ */

 /*
        Checks if the value is inside the array 
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
        Shifts the values of a given array to the right, the number of times specified
     */
    private int[] rotate(int[] array, int number) {
        int[] copy = array.clone();

        for (int j = 0; j < number; j++) {
            int temp = copy[copy.length - 1]; // stores the last element
            for (int i = copy.length - 1; i > 0; i--) {
                copy[i] = copy[i - 1];   // do the switch
            }
            copy[0] = temp; // restore the last element into the first one
        }

        return array;
    }
}
