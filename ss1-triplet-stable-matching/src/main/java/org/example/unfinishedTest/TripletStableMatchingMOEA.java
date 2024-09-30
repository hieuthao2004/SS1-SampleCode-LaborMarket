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

    private static final String[][] preferencesB_forA = {
        {"Alice", "Charlie"},  // Microsoft
        {"Bob", "David"},      // Amazon
        {"Charlie", "Eva"},    // Google
        {"David", "Alice"},    // Tesla
        {"Eva", "Bob"}         // Apple
    };

    private static final String[][] preferencesB_forC = {
        {"Engineering", "HR"}, // Microsoft
        {"Marketing", "Sales"},// Amazon
        {"IT", "Marketing"},   // Google
        {"HR", "IT"},          // Tesla
        {"Sales", "Engineering"}// Apple
    };

    private static final String[][] preferencesC_forA = {
        {"Alice", "Bob"},      // Engineering
        {"Charlie", "Eva"},    // Marketing
        {"David", "Charlie"},  // IT
        {"Bob", "Alice"},      // HR
        {"Eva", "David"}       // Sales
    };

    private static final String[][] preferencesC_forB = {
        {"Microsoft", "Google"},// Engineering
        {"Amazon", "Tesla"},    // Marketing
        {"Google", "Apple"},    // IT
        {"Tesla", "Amazon"},    // HR
        {"Apple", "Microsoft"}  // Sales
    };

    private static final String[] employees = {"Alice", "Bob", "Charlie", "David", "Eva"};
    private static final int NUM_PERSONS = 5;

    public TripletStableMatchingMOEA() {
        super(1, 1);  // Một biến quyết định và một mục tiêu (ghép cặp ổn định)
    }

    @Override
    public void evaluate(Solution solution) {
        Permutation permForCompanies = (Permutation) solution.getVariable(0);  // Permutation cho công ty
        Permutation permForDepartments = (Permutation) solution.getVariable(1);  // Permutation cho ban ngành
    
        int[] preferencesA_forB = permForCompanies.toArray();  // Ưu tiên của A cho các công ty
        int[] preferencesA_forC = permForDepartments.toArray();  // Ưu tiên của A cho các ban ngành
    
        int[] matchedB = new int[NUM_PERSONS];
        Arrays.fill(matchedB, -1);
    
        int[] matchedC = new int[NUM_PERSONS];
        Arrays.fill(matchedC, -1);
    
        List<Integer> freeA = new ArrayList<>();
        for (int i = 0; i < NUM_PERSONS; i++) {
            freeA.add(i);
        }
    
        int stableMatches = 0;
    
        // Lặp qua các nhân viên (A), cố gắng ghép cặp với các công ty (B) và bộ phận (C)
        while (!freeA.isEmpty()) {
            int a = freeA.remove(0);  // Chỉ số nhân viên A
    
            // Xét ưu tiên của A cho các công ty
            for (int bIndex : preferencesA_forB) {  // Lặp qua các ưu tiên của A cho các công ty
                int b = bIndex; // Công ty mà A đang xem xét
    
                // Kiểm tra xem công ty B có ưu tiên nhân viên A không
                if (matchedB[b] == -1) {
                    // Kiểm tra xem công ty B có thích nhân viên A và bộ phận C không
                    String[] preferredEmployeesForB = preferencesB_forA[b];
                    String[] preferredDepartmentsForB = preferencesB_forC[b];
                    
                    // Tìm các bộ phận mà B ưu tiên và xem bộ phận C có tương thích không
                    for (int cIndex : preferencesA_forC) {  // Lặp qua các ưu tiên của A cho các ban ngành
                        if (matchedC[cIndex] == -1) {
                            // Xét xem nhân viên A có nằm trong danh sách ưu tiên của C không
                            String[] preferredEmployeesForC = preferencesC_forA[cIndex];
                            String[] preferredCompaniesForC = preferencesC_forB[cIndex];
    
                            if (Arrays.asList(preferredEmployeesForB).contains(employees[a]) &&
                                Arrays.asList(preferredDepartmentsForB).contains(preferencesC_forA[cIndex][0]) &&
                                Arrays.asList(preferredEmployeesForC).contains(employees[a]) &&
                                Arrays.asList(preferredCompaniesForC).contains(preferencesB_forA[b][0])) {
                                
                                // Ghép A với B và C
                                matchedB[b] = a;
                                matchedC[cIndex] = b;
                                stableMatches++;
                                break;
                            }
                        }
                    }
                    if (matchedB[b] != -1) {
                        break; // Dừng nếu đã ghép
                    }
                }
            }
        }
    
        // Tối đa hóa ghép cặp ổn định (sử dụng giá trị âm vì NSGA tối thiểu hóa theo mặc định)
        solution.setObjective(0, -stableMatches);
    }
    
    @Override
    public Solution newSolution() {
        Solution solution = new Solution(2, 1);  // Hai biến quyết định: một cho công ty, một cho ban ngành
        solution.setVariable(0, new Permutation(NUM_PERSONS));  // Permutation cho các công ty
        solution.setVariable(1, new Permutation(NUM_PERSONS));  // Permutation cho các ban ngành
        return solution;
    }
    

    public static void main(String[] args) {
        int numRandomSets = 5;

        for (int setIndex = 0; setIndex < numRandomSets; setIndex++) {
            NondominatedPopulation result = new Executor()
                    .withProblemClass(TripletStableMatchingMOEA.class)
                    .withAlgorithm("NSGAIII")
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
                    int bIndex = perm.get(i);
                    String company = preferencesB_forA[bIndex][0]; // Chọn công ty đầu tiên từ ưu tiên
                    String department = preferencesC_forA[bIndex][0]; // Chọn bộ phận đầu tiên từ ưu tiên

                    // In kết quả theo định dạng bảng
                    System.out.printf("%-10d (%s, %s, %s)\n", order, employee, company, department);
                    order++;
                }
            }
            System.out.println();
        }
    }
}
