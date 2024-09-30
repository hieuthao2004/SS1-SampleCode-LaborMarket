package org.example.unfinishedTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.problem.AbstractProblem;

public class TripletStableMatchingMOEA extends AbstractProblem {

    private static final String[][] preferencesB = {
        {"Microsoft", "Amazon", "Google", "Tesla", "Apple"},
        {"Google", "Apple", "Tesla", "Amazon", "Microsoft"},
        {"Amazon", "Microsoft", "Apple", "Google", "Tesla"},
        {"Apple", "Tesla", "Google", "Microsoft", "Amazon"},
        {"Tesla", "Google", "Microsoft", "Amazon", "Apple"}
    };

    private static final String[][] preferencesC = {
        {"Engineering", "Marketing", "IT", "HR", "Sales"},
        {"Marketing", "Engineering", "HR", "Sales", "IT"},
        {"Sales", "HR", "Marketing", "Engineering", "IT"},
        {"HR", "Sales", "IT", "Engineering", "Marketing"},
        {"IT", "HR", "Engineering", "Marketing", "Sales"}
    };

    private static final String[] employees = {"Alice", "Bob", "Charlie", "David", "Eva"};
    private static final int NUM_PERSONS = 5;

    public TripletStableMatchingMOEA() {
        super(1, 1);  // One decision variable and one objective (stable matches)
    }

    @Override
    public void evaluate(Solution solution) {
        Permutation perm = (Permutation) solution.getVariable(0);
        int[] preferencesA = perm.toArray();  // Preferences of A as indices
    
        int[] matchedB = new int[NUM_PERSONS];
        Arrays.fill(matchedB, -1);
    
        int[] matchedC = new int[NUM_PERSONS];
        Arrays.fill(matchedC, -1);
    
        List<Integer> freeA = new ArrayList<>();
        for (int i = 0; i < NUM_PERSONS; i++) {
            freeA.add(i);
        }
    
        int stableMatches = 0;
    
        // Iterate over employees (A), trying to match them with companies (B) and departments (C)
        while (!freeA.isEmpty()) {
            int a = freeA.remove(0);  // Employee index A
    
            for (int b : preferencesA) {  // Iterate over employee A's preferences for companies
                int c = a;  // Assume C corresponds to A for simplicity
    
                // Only match if both company and department are free
                if (matchedB[b] == -1 && matchedC[c] == -1) {
                    matchedB[b] = a;
                    matchedC[c] = b;
                    stableMatches++;
    
                    break;
                }
            }
        }
    
        // Maximize stable matches (use negative value since NSGA minimizes by default)
        solution.setObjective(0, -stableMatches);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);  // One decision variable and one objective
        solution.setVariable(0, new Permutation(NUM_PERSONS));  // Permutation for preferences of A set
        return solution;
    }

    public static void main(String[] args) {
        int numRandomSets = 8;

        for (int setIndex = 0; setIndex < numRandomSets; setIndex++) {
            NondominatedPopulation result = new Executor()
                    .withProblemClass(TripletStableMatchingMOEA.class)
                    .withAlgorithm("NSGAII")
                    .withMaxEvaluations(10000)
                    .withProperty("populationSize", 100)
                    .run();

            System.out.println("Results for random dataset " + (setIndex + 1) + ":");
            System.out.printf("%-10s %-25s\n", "Order", "Triplet Matched");
            System.out.println("-----------------------------------------------------------");

            int order = 1;
            for (Solution solution : result) {
                int stableMatches = -1 * (int) solution.getObjective(0);
                Permutation perm = (Permutation) solution.getVariable(0);

                for (int i = 0; i < NUM_PERSONS; i++) {
                    String employee = employees[perm.get(i)];
                    String company = preferencesB[i][perm.get(i)];
                    String department = preferencesC[i][perm.get(i)];

                    // Print the results in table format
                    System.out.printf("%-10d (%s, %s, %s)\n",
                            order, employee, company, department);

                    order++;
                }
            }
            System.out.println();
        }
    }
}
