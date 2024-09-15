package org.example;

import java.text.DecimalFormat;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class LMPMain {
        private static final DecimalFormat df = new DecimalFormat("0.00");
        public static void main(String[] args) {

                int numEmployee = 5;
                int numCompany = 5;

                double[][] salary = {
                        {5000, 6000, 5500, 4000, 4800},
                        {4800, 5500, 6000, 5000, 5200},
                        {6000, 5500, 4000, 6500, 5000},
                        {5200, 6200, 4000, 5300, 5400},
                        {4500, 5000, 5200, 4700, 5800}
                };
                // đơn vị là đô la

                double[][] distant= {
                        {9, 8, 5, 6, 10},
                        {6, 7, 8.5, 6, 9},
                        {7, 9, 6, 5, 8},
                        {6, 9, 4, 6.5, 7},
                        {8, 6, 7, 7.5, 9}
                };
                // đơn vị là km

                double[][] skill= {
                        {0.8, 0.9, 0.85, 0.6, 0.7},
                        {0.7, 0.85, 0.9, 0.8, 0.8},
                        {0.9, 0.85, 0.6, 0.9, 0.8},
                        {0.8, 0.9, 0.6, 0.8, 0.8},
                        {0.65,0.8, 0.8, 0.7, 0.85}
                };
                // thước đo tối đa là 1

                NondominatedPopulation result = new Executor()
                        .withProblemClass(LaborMarketProblem.class,numEmployee, numCompany, salary, distant, skill)
                        .withAlgorithm("PESA2")
                        .withMaxEvaluations(10000)
                        .run();

                // output :

                // solution.setObjective(0, -totalSalary); // tối đa hóa mức lương (tối đa hóa -> giảm thiểu giá trị âm)
                // solution.setObjective(1, totalDistance); // Giảm thiểu khoảng cách
                // solution.setObjective(2, -totalSkill); // tối đa hóa kỹ năng (tối đa hóa -> giảm thiểu giá trị âm)

                for (Solution solution : result){
                        System.out.println("------------------------------------------------");
                        System.out.println("Maximize salary   company  -> employee: " + df.format(-solution.getObjective(0)) + " USD");
                        System.out.println("Minimize distance employee -> company : " + df.format(solution.getObjective(1)) + " km");
                        System.out.println("Maximine skill    employee -> company : " + df.format(-solution.getObjective(2)) + " [0-1]");
                        System.out.println("------------------------------------------------");
        }
    }
}
