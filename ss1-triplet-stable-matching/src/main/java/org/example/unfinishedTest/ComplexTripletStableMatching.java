package org.example.unfinishedTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.problem.AbstractProblem;

public class ComplexTripletStableMatching extends AbstractProblem {

    private static final String[] COMPANIES = {"Microsoft", "Amazon", "Google", "Tesla", "Apple", "Facebook", "Netflix", "Adobe"};
    
    // Preferences of companies for employees (10 options)
    private static final String[][] PREFERENCES_COMPANY_FOR_EMPLOYEE = {
        {"Alice", "Charlie", "Bob", "David", "Eva", "Frank", "Grace", "Hank", "Ivy", "Jack"},  // Microsoft
        {"Bob", "David", "Alice", "Eva", "Charlie", "Grace", "Hank", "Ivy", "Frank", "Jack"},  // Amazon
        {"Charlie", "Eva", "David", "Alice", "Bob", "Frank", "Ivy", "Grace", "Hank", "Jack"},  // Google
        {"David", "Alice", "Bob", "Charlie", "Eva", "Ivy", "Jack", "Frank", "Grace", "Hank"},  // Tesla
        {"Eva", "Bob", "Alice", "Charlie", "David", "Jack", "Ivy", "Grace", "Frank", "Hank"},  // Apple
        {"Frank", "Grace", "Hank", "Ivy", "Jack", "Alice", "Bob", "Charlie", "David", "Eva"},  // Facebook
        {"Grace", "Hank", "Ivy", "Jack", "Alice", "Bob", "Charlie", "David", "Eva", "Frank"},  // Netflix
        {"Hank", "Ivy", "Jack", "Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace"}   // Adobe
    };

    // Preferences of companies for departments (5 options per company)
    private static final String[][] PREFERENCES_COMPANY_FOR_DEPARTMENT = {
        {"Engineering", "HR", "Finance", "Sales", "Marketing"}, // Microsoft
        {"Marketing", "Sales", "IT", "HR", "Engineering"},      // Amazon
        {"IT", "Marketing", "Sales", "Finance", "HR"},          // Google
        {"HR", "IT", "Sales", "Engineering", "Marketing"},      // Tesla
        {"Sales", "Engineering", "Finance", "IT", "HR"},        // Apple
        {"Finance", "IT", "Marketing", "HR", "Sales"},          // Facebook
        {"IT", "Engineering", "Marketing", "HR", "Finance"},    // Netflix
        {"HR", "Sales", "Engineering", "Marketing", "IT"}       // Adobe
    };

    // Preferences of departments for employees (10 options)
    private static final String[][] PREFERENCES_DEPARTMENT_FOR_EMPLOYEE = {
        {"Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Hank", "Ivy", "Jack"},  // Engineering
        {"Charlie", "Eva", "Bob", "Alice", "David", "Frank", "Grace", "Hank", "Ivy", "Jack"},  // Marketing
        {"David", "Charlie", "Alice", "Bob", "Eva", "Grace", "Frank", "Hank", "Ivy", "Jack"},  // IT
        {"Bob", "Alice", "David", "Eva", "Charlie", "Hank", "Ivy", "Grace", "Frank", "Jack"},  // HR
        {"Eva", "David", "Charlie", "Alice", "Bob", "Jack", "Ivy", "Grace", "Hank", "Frank"}   // Sales
    };

    // Preferences of departments for companies (8 options)
    private static final String[][] PREFERENCES_DEPARTMENT_FOR_COMPANY = {
        {"Microsoft", "Google", "Amazon", "Tesla", "Apple", "Facebook", "Netflix", "Adobe"},   // Engineering
        {"Amazon", "Tesla", "Microsoft", "Google", "Apple", "Facebook", "Netflix", "Adobe"},   // Marketing
        {"Google", "Apple", "Microsoft", "Tesla", "Amazon", "Facebook", "Netflix", "Adobe"},   // IT
        {"Tesla", "Amazon", "Apple", "Google", "Microsoft", "Facebook", "Netflix", "Adobe"},   // HR
        {"Apple", "Microsoft", "Google", "Amazon", "Tesla", "Facebook", "Netflix", "Adobe"}    // Sales
    };

    private static final String[] EMPLOYEES = {"Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Hank", "Ivy", "Jack"};
    private static final int NUM_PERSONS = 10;

    public ComplexTripletStableMatching() {
        super(2, 1);  // Hai biến quyết định (cho công ty và ban ngành) và một mục tiêu
    }

    @Override
    public void evaluate(Solution solution) {
        Permutation permForCompanies = (Permutation) solution.getVariable(0);  // Permutation cho công ty
        Permutation permForDepartments = (Permutation) solution.getVariable(1);  // Permutation cho ban ngành

        int[] matchedB = new int[NUM_PERSONS];
        Arrays.fill(matchedB, -1);

        int[] matchedC = new int[NUM_PERSONS];
        Arrays.fill(matchedC, -1);

        List<Integer> freeA = new ArrayList<>();
        for (int i = 0; i < NUM_PERSONS; i++) {
            freeA.add(i);
        }

        int stableMatches = 0;

        // Loop qua hoán vị ngẫu nhiên của các cá thể
        while (!freeA.isEmpty()) {
            int a = freeA.remove(0);  // Chỉ số nhân viên A

            // Bắt đầu loop qua danh sách ưu tiên của nhân viên này
            for (int bIndex = 0; bIndex < COMPANIES.length; bIndex++) {
                int b = permForCompanies.get(bIndex); // Công ty mà A đang xem xét

                // Kiểm tra ưu tiên công ty với nhân viên này
                String[] preferredEmployeesForB = PREFERENCES_COMPANY_FOR_EMPLOYEE[b];

                if (Arrays.asList(preferredEmployeesForB).contains(EMPLOYEES[a])) {
                    
                    // Giai đoạn 1: Match với middle node (ban ngành) trong preflist
                    for (int cIndex = 0; cIndex < PREFERENCES_COMPANY_FOR_DEPARTMENT[b].length; cIndex++) {
                        int departmentIndex = permForDepartments.get(cIndex);  // Ban ngành hiện tại từ hoán vị

                        // Kiểm tra ưu tiên của ban ngành với nhân viên A và công ty B
                        String[] preferredEmployeesForC = PREFERENCES_DEPARTMENT_FOR_EMPLOYEE[departmentIndex];
                        String[] preferredCompaniesForC = PREFERENCES_DEPARTMENT_FOR_COMPANY[departmentIndex];

                        if (Arrays.asList(preferredEmployeesForC).contains(EMPLOYEES[a]) &&
                            Arrays.asList(preferredCompaniesForC).contains(COMPANIES[b])) {

                            // Match A với middle node là công ty B và ban ngành C
                            matchedB[b] = a;
                            matchedC[departmentIndex] = b;
                            stableMatches++;

                            // Giai đoạn 2: Tiếp tục tìm match với right node
                            int padding = PREFERENCES_COMPANY_FOR_DEPARTMENT[b].length;
                            for (int rightNodeIndex = cIndex + padding; rightNodeIndex < PREFERENCES_COMPANY_FOR_DEPARTMENT[b].length; rightNodeIndex++) {
                                int rightNodeDepartmentIndex = permForDepartments.get(rightNodeIndex % PREFERENCES_COMPANY_FOR_DEPARTMENT[b].length);

                                // Kiểm tra ưu tiên right node cho nhân viên A và công ty B
                                String[] preferredRightNodeEmployees = PREFERENCES_DEPARTMENT_FOR_EMPLOYEE[rightNodeDepartmentIndex];
                                String[] preferredRightNodeCompanies = PREFERENCES_DEPARTMENT_FOR_COMPANY[rightNodeDepartmentIndex];

                                if (Arrays.asList(preferredRightNodeEmployees).contains(EMPLOYEES[a]) &&
                                    Arrays.asList(preferredRightNodeCompanies).contains(COMPANIES[b])) {

                                    // Match với right node
                                    matchedC[rightNodeDepartmentIndex] = b;
                                    break;  // Thoát vòng lặp với cá thể này sau khi tìm thấy right node
                                }
                            }
                            break;  // Thoát vòng lặp với middle node
                        }
                    }
                }
            }
        }

        // Tối đa hóa ghép cặp ổn định (sử dụng giá trị âm vì NSGA tối thiểu hóa theo mặc định)
        solution.setObjective(0, -stableMatches);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(2, 1);  // Hai biến quyết định
        solution.setVariable(0, new Permutation(NUM_PERSONS));  // Permutation
        solution.setVariable(1, new Permutation(NUM_PERSONS));  // Permutation cho các ban ngành
        return solution;
    }

    public static void main(String[] args) {
        int numRandomSets = 10;

        for (int setIndex = 0; setIndex < numRandomSets; setIndex++) {
            NondominatedPopulation result = new Executor()
                    .withProblemClass(ComplexTripletStableMatching.class)
                    .withAlgorithm("NSGAIII")
                    .withMaxEvaluations(10000)
                    .withProperty("populationSize", 100)
                    .run();

            System.out.println("Results for random dataset " + (setIndex + 1) + ":");
            System.out.printf("%-10s %-25s\n", "Order", "Triplet Matched");
            System.out.println("-----------------------------------------------------------");

            int order = 1;
            for (Solution solution : result) {
                
                Permutation permForCompanies = (Permutation) solution.getVariable(0);
                Permutation permForDepartments = (Permutation) solution.getVariable(1);

                for (int i = 0; i < NUM_PERSONS; i++) {
                    String employee = EMPLOYEES[i]; // Lấy tên nhân viên
                    int companyIndex = permForCompanies.get(i); // Lấy chỉ số công ty đã ghép cặp
                    int departmentIndex = permForDepartments.get(i); // Lấy chỉ số ban ngành đã ghép cặp

                    // Đảm bảo rằng departmentIndex không bị invalid
                    if (companyIndex >= 0 && companyIndex < PREFERENCES_COMPANY_FOR_DEPARTMENT.length && 
                        departmentIndex >= 0 && departmentIndex < PREFERENCES_COMPANY_FOR_DEPARTMENT[companyIndex].length) {

                        String company = COMPANIES[companyIndex];  // Lấy tên công ty
                        String department = PREFERENCES_COMPANY_FOR_DEPARTMENT[companyIndex][departmentIndex];  // Lấy ban ngành hợp lệ

                        System.out.printf("%-10d (%s, %s, %s)\n", order, employee, company, department);
                    }
                    order++;
                }
            }
            System.out.println();
        }
    }
}
