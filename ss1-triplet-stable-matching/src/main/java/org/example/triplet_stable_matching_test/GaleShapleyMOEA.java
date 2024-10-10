package org.example.triplet_stable_matching_test;

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

    private List<String[]> allMatchedTriples;
    private Random random = new Random();

    public GaleShapleyMOEA() {
        super(1, 1);  // Một biến quyết định và một mục tiêu
        this.allMatchedTriples = new ArrayList<>();
    }

    @Override
    public void evaluate(Solution solution) {
        Permutation matching = (Permutation) solution.getVariable(0);
        double satisfaction = 0.0;

        int[] employeeOrder = matching.toArray();

        // Sử dụng GaleShapleyTwoStage với dữ liệu từ Dataset
        GaleShapleyTwoStage galeShapley = new GaleShapleyTwoStage(Dataset.EMPLOYEES, Dataset.COMPANIES, Dataset.DEPARTMENTS, 
            Dataset.PREFERENCES_EMPLOYEE, Dataset.PREFERENCES_COMPANY, Dataset.PREFERENCES_DEPARTMENT);
        galeShapley.match();

        int[] companyMatches = galeShapley.getCompanyMatches();  // Lấy kết quả ghép giữa nhân viên và công ty

        // Lưu các bộ ba Employee - Department - Company
        String[] matchedTriples = new String[Dataset.NUM_PERSONS];
        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            int empIndex = employeeOrder[i];
            int compIndex = companyMatches[empIndex];  // Sử dụng companyMatches để lấy công ty tương ứng
            matchedTriples[i] = String.format("%s - %s - %s", Dataset.EMPLOYEES[empIndex], Dataset.DEPARTMENTS[empIndex], Dataset.COMPANIES[compIndex]);
        }

        allMatchedTriples.add(matchedTriples);

        double[] pairSatisfaction = new double[Dataset.NUM_PERSONS];

        // Tính toán mức độ hài lòng của từng nhân viên
        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            int employeeIndex = employeeOrder[i];
            int companyIndex = companyMatches[employeeIndex];  // Chỉ sử dụng companyMatches

            // Tìm chỉ số hài lòng cho mỗi giai đoạn
            int satisfactionRankEmployee = Arrays.asList(Dataset.PREFERENCES_EMPLOYEE[employeeIndex][0]).indexOf(Dataset.DEPARTMENTS[employeeIndex]);
            int satisfactionRankDepartment = Arrays.asList(Dataset.PREFERENCES_DEPARTMENT[employeeIndex][1]).indexOf(Dataset.COMPANIES[companyIndex]);

            // Tính hài lòng cho nhân viên, kết hợp cả 2 giai đoạn
            pairSatisfaction[i] = (5 - satisfactionRankEmployee) + (5 - satisfactionRankDepartment);
            pairSatisfaction[i] += random.nextDouble() * 2;  // Thêm yếu tố ngẫu nhiên

            satisfaction += pairSatisfaction[i];
        }

        solution.setObjective(0, -satisfaction);

        System.out.println("Matched Triples and Satisfaction:");
        for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
            System.out.printf("Employee: %s - Department: %s - Company: %s | Satisfaction: %.2f\n",
                    Dataset.EMPLOYEES[employeeOrder[i]], 
                    Dataset.DEPARTMENTS[employeeOrder[i]],  // Ghép Department từ employeeOrder
                    Dataset.COMPANIES[companyMatches[employeeOrder[i]]], 
                    pairSatisfaction[i]);
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

    public List<String[]> getAllMatchedTriples() {
        return allMatchedTriples;
    }

    // public static void main(String[] args) {
    //     int numOfRandomSets = 2; // Số lần chạy
    //     GaleShapleyMOEA problemInstance = new GaleShapleyMOEA();
    
    //     for (int run = 0; run < numOfRandomSets; run++) {
    //         System.out.printf("========== Run %d ==========\n", run + 1);
    //         NondominatedPopulation result = new Executor()
    //             .withProblemClass(GaleShapleyMOEA.class)
    //             .withAlgorithm("NSGAIII")
    //             .withMaxEvaluations(1)
    //             .withProperty("populationSize", 1)
    //             .run();
    
    //         // Tiêu đề của kết quả
    //         System.out.printf("%-10s %-15s %-30s\n", "Solution", "Satisfaction", "Matched Triples (Employee - Department - Company)");
    //         System.out.println("-------------------------------------------------------------");
    
    //         for (Solution solution : result) {
    //             // Tính toán hài lòng cho từng solution
    //             problemInstance.evaluate(solution);
    
    //             // Lấy các bộ ba ghép của solution cuối cùng
    //             List<String[]> allMatchedTriples = problemInstance.getAllMatchedTriples();
    //             String[] matchedTriples = allMatchedTriples.get(allMatchedTriples.size() - 1);
    
    //             // In ra kết quả theo định dạng
    //             System.out.printf("%-10d %-15.2f ", 
    //                 result.indexOf(solution) + 1, 
    //                 -solution.getObjective(0)); // Hài lòng (ngược dấu vì tối ưu hóa là minimization)
    
    //             // In bộ ba ghép giữa nhân viên, ban ngành, và công ty
    //             for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
    //                 System.out.printf("%s", matchedTriples[i]);
    //                 if (i < Dataset.NUM_PERSONS - 1) {
    //                     System.out.print(", "); // Dấu phẩy phân cách các bộ ba
    //                 }
    //             }
    //             System.out.println(); // Xuống dòng sau mỗi solution
    //         }
    //         System.out.println(); // Dòng trống giữa các lần chạy
    //     }
    // }

    public static void main(String[] args) {
        int numOfRandomSets = 1; // Số lần chạy
        GaleShapleyMOEA problemInstance = new GaleShapleyMOEA();
    
        // Biến để lưu kết quả cuối cùng
        NondominatedPopulation finalResult = null;
    
        for (int run = 0; run < numOfRandomSets; run++) {
            System.out.printf("========== Run %d ==========\n", run + 1);
            finalResult = new Executor()
                .withProblemClass(GaleShapleyMOEA.class)
                .withAlgorithm("NSGAIII")
                .withMaxEvaluations(2)
                .withProperty("populationSize", 1)
                .run();
        }
    
        // In kết quả cuối cùng sau tất cả các lần chạy
        if (finalResult != null) {
            System.out.printf("%-10s %-15s %-30s\n", "Solution", "Satisfaction", "Matched Triples (Employee - Department - Company)");
            System.out.println("-------------------------------------------------------------");
    
            for (Solution solution : finalResult) {
                // Tính toán hài lòng cho từng solution
                problemInstance.evaluate(solution);
    
                // Lấy các bộ ba ghép của solution cuối cùng
                List<String[]> allMatchedTriples = problemInstance.getAllMatchedTriples();
                String[] matchedTriples = allMatchedTriples.get(allMatchedTriples.size() - 1);
    
                // In ra kết quả theo định dạng yêu cầu
                System.out.printf("%-10d %-15.2f ", 
                    finalResult.indexOf(solution) + 1, 
                    -solution.getObjective(0)); // Hài lòng (ngược dấu vì tối ưu hóa là minimization)
    
                // In bộ ba ghép giữa nhân viên, ban ngành, và công ty
                for (int i = 0; i < Dataset.NUM_PERSONS; i++) {
                    System.out.printf("%s", matchedTriples[i]);
                    if (i < Dataset.NUM_PERSONS - 1) {
                        System.out.print(", "); // Dấu phẩy phân cách các bộ ba
                    }
                }
                System.out.println(); // Xuống dòng sau mỗi solution
            }
        }
    
        System.out.println(); // Dòng trống giữa các lần chạy
    }
    
    
}
