package org.example.double_stable_matching;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GaleShapleyMOEA extends AbstractProblem {

    private List<String[]> allMatchedCompanies;
    private Random random = new Random();

    public GaleShapleyMOEA() {
        super(1, 1);  // Một biến quyết định và một mục tiêu
        this.allMatchedCompanies = new ArrayList<>();
    }

    @Override
    public void evaluate(Solution solution) {
        Permutation matching = (Permutation) solution.getVariable(0);
        double satisfaction = 0.0;

        int[] employeeOrder = matching.toArray();

        GaleShapley galeShapley = new GaleShapley(Dataset.EMPLOYEES, Dataset.COMPANIES, Dataset.PREFERENCES_EMPLOYEE, Dataset.PREFERENCES_COMPANY);
        galeShapley.match();
        int[] employeeMatches = galeShapley.getEmployeeMatches();

        String[] matchedCompanies = new String[Dataset.NUM_PERSONS];
        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            matchedCompanies[i] = Dataset.COMPANIES[employeeMatches[employeeOrder[i]]];
        }

        allMatchedCompanies.add(matchedCompanies);

        double[] pairSatisfaction = new double[Dataset.NUM_PERSONS];

        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            int employeeIndex = employeeOrder[i];
            int companyIndex = employeeMatches[employeeIndex];

            int satisfactionRank = Arrays.asList(Dataset.PREFERENCES_EMPLOYEE[employeeIndex]).indexOf(Dataset.COMPANIES[companyIndex]);
            pairSatisfaction[i] = 5 - satisfactionRank;

            // Thêm yếu tố ngẫu nhiên
            pairSatisfaction[i] += random.nextDouble() * 2; // Ngẫu nhiên giữa 0 và 2 điểm

            satisfaction += pairSatisfaction[i];
        }

        solution.setObjective(0, -satisfaction);

        System.out.println("Pair Satisfaction:");
        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            System.out.printf("Employee: %s - Company: %s | Satisfaction: %.2f\n",
                    Dataset.EMPLOYEES[employeeOrder[i]], matchedCompanies[i], pairSatisfaction[i]);
        }
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        int[] permutation = new int[Dataset.NUM_PERSONS];
        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            permutation[i] = i;
        }

        List<Integer> list = Arrays.asList(Arrays.stream(permutation).boxed().toArray(Integer[]::new));
        Collections.shuffle(list);
        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            permutation[i] = list.get(i);
        }
        solution.setVariable(0, new Permutation(permutation));
        return solution;
    }

    public List<String[]> getAllMatchedCompanies() {
        return allMatchedCompanies;
    }

    public static void main(String[] args) {
        int numOfRandomSets = 1; // Số lần chạy
        GaleShapleyMOEA problemInstance = new GaleShapleyMOEA();
    
        for (int run = 0; run < numOfRandomSets; run++) {
            System.out.printf("========== Run %d ==========\n", run + 1);
            NondominatedPopulation result = new Executor()
                .withProblemClass(GaleShapleyMOEA.class)
                .withAlgorithm("NSGAIII")
                .withMaxEvaluations(2)
                .withProperty("populationSize", 1)
                .run();
    
            // Tiêu đề của kết quả
            System.out.printf("%-10s %-15s %-30s\n", "Solution", "Satisfaction", "Matched Pairs (Employee - Company)");
            System.out.println("-------------------------------------------------------------");
    
            for (Solution solution : result) {
                // Tính toán hài lòng cho từng solution
                problemInstance.evaluate(solution);
    
                // Lấy các cặp ghép của solution cuối cùng
                List<String[]> allMatchedCompanies = problemInstance.getAllMatchedCompanies();
                String[] matchedCompanies = allMatchedCompanies.get(allMatchedCompanies.size() - 1);
    
                // In ra kết quả theo định dạng
                System.out.printf("%-10d %-15.2f ", 
                    result.indexOf(solution) + 1, 
                    -solution.getObjective(0)); // Hài lòng (ngược dấu vì tối ưu hóa là minimization)
    
                // In cặp ghép giữa nhân viên và công ty
                for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
                    System.out.printf("%s - %s", Dataset.EMPLOYEES[i], matchedCompanies[i]);
                    if (i < Dataset.NUM_PERSONS - 1) {
                        System.out.print(", "); // Dấu phẩy phân cách các cặp
                    }
                }
                System.out.println(); // Xuống dòng sau mỗi solution
            }
            System.out.println(); // Dòng trống giữa các lần chạy
        }
    }    
}
