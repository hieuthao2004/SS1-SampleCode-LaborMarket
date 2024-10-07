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

    private static final String[] COMPANIES = {"Microsoft", "Amazon", "Google", "Tesla", "Apple"};
    
    // Preferences of companies for employees (5 options)
    private static final String[][] PREFERENCES_COMPANY_FOR_EMPLOYEE = {
        {"Alice", "Charlie", "Bob", "David", "Eva"},  // Microsoft
        {"Bob", "David", "Alice", "Eva", "Charlie"},  // Amazon
        {"Charlie", "Eva", "David", "Alice", "Bob"},  // Google
        {"David", "Alice", "Bob", "Charlie", "Eva"},  // Tesla
        {"Eva", "Bob", "Alice", "Charlie", "David"}    // Apple
    };

    // Preferences of companies for departments (5 options)
    private static final String[][] PREFERENCES_COMPANY_FOR_DEPARTMENT = {
        {"Engineering", "HR", "Finance", "Sales", "Marketing"}, // Microsoft
        {"Marketing", "Sales", "IT", "HR", "Engineering"},      // Amazon
        {"IT", "Marketing", "Sales", "Finance", "HR"},          // Google
        {"HR", "IT", "Sales", "Engineering", "Marketing"},      // Tesla
        {"Sales", "Engineering", "Finance", "IT", "HR"}         // Apple
    };

    // Preferences of departments for employees (5 options)
    private static final String[][] PREFERENCES_DEPARTMENT_FOR_EMPLOYEE = {
        {"Alice", "Bob", "Charlie", "David", "Eva"},      // Engineering
        {"Charlie", "Eva", "Bob", "Alice", "David"},      // Marketing
        {"David", "Charlie", "Alice", "Bob", "Eva"},      // IT
        {"Bob", "Alice", "David", "Eva", "Charlie"},      // HR
        {"Eva", "David", "Charlie", "Alice", "Bob"}       // Sales
    };

    // Preferences of departments for companies (5 options)
    private static final String[][] PREFERENCES_DEPARTMENT_FOR_COMPANY = {
        {"Microsoft", "Google", "Amazon", "Tesla", "Apple"}, // Engineering
        {"Amazon", "Tesla", "Microsoft", "Google", "Apple"}, // Marketing
        {"Google", "Apple", "Microsoft", "Tesla", "Amazon"}, // IT
        {"Tesla", "Amazon", "Apple", "Google", "Microsoft"}, // HR
        {"Apple", "Microsoft", "Google", "Amazon", "Tesla"}  // Sales
    };

    // New Preferences of employees for companies (5 options)
    private static final String[][] PREFERENCES_EMPLOYEE_FOR_COMPANY = {
        {"Google", "Microsoft", "Tesla", "Amazon", "Apple"}, // Alice
        {"Amazon", "Tesla", "Apple", "Microsoft", "Google"}, // Bob
        {"Tesla", "Apple", "Microsoft", "Amazon", "Google"}, // Charlie
        {"Microsoft", "Apple", "Google", "Amazon", "Tesla"}, // David
        {"Apple", "Google", "Amazon", "Microsoft", "Tesla"}  // Eva
    };

    // New Preferences of employees for departments (5 options)
    private static final String[][] PREFERENCES_EMPLOYEE_FOR_DEPARTMENT = {
        {"Engineering", "Sales", "HR", "Finance", "Marketing"}, // Alice
        {"HR", "IT", "Engineering", "Finance", "Sales"},        // Bob
        {"IT", "Engineering", "Marketing", "Sales", "HR"},      // Charlie
        {"Finance", "Marketing", "Sales", "IT", "Engineering"}, // David
        {"Sales", "Marketing", "IT", "HR", "Finance"}           // Eva
    };

    private static final String[] EMPLOYEES = {"Alice", "Bob", "Charlie", "David", "Eva"};
    private static final int NUM_PERSONS = 5;

    public TripletStableMatchingMOEA() {
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
    
            // Bắt đầu loop qua danh sách ưu tiên của nhân viên này (Employee to Company Preference)
            for (int bIndex = 0; bIndex < COMPANIES.length; bIndex++) {
                int b = permForCompanies.get(bIndex); // Công ty mà A đang xem xét
    
                // Kiểm tra ưu tiên của công ty với nhân viên này (Company to Employee Preference)
                String[] preferredEmployeesForB = PREFERENCES_COMPANY_FOR_EMPLOYEE[b];
    
                // Kiểm tra ưu tiên của nhân viên A với công ty B (Employee to Company Preference)
                if (Arrays.asList(preferredEmployeesForB).contains(EMPLOYEES[a]) &&
                    Arrays.asList(PREFERENCES_EMPLOYEE_FOR_COMPANY[a]).contains(COMPANIES[b])) {
    
                    // Giai đoạn 1: Match với middle node (ban ngành) trong preflist
                    for (int cIndex = 0; cIndex < permForDepartments.size(); cIndex++) {
                        int departmentIndex = permForDepartments.get(cIndex);  // Ban ngành hiện tại từ hoán vị
    
                        // Kiểm tra ưu tiên của ban ngành với nhân viên A và công ty B
                        String[] preferredEmployeesForC = PREFERENCES_DEPARTMENT_FOR_EMPLOYEE[departmentIndex];
                        String[] preferredCompaniesForC = PREFERENCES_DEPARTMENT_FOR_COMPANY[departmentIndex];
    
                        // Kiểm tra ưu tiên của nhân viên A với ban ngành C (Employee to Department Preference)
                        if (Arrays.asList(preferredEmployeesForC).contains(EMPLOYEES[a]) &&
                            Arrays.asList(preferredCompaniesForC).contains(COMPANIES[b]) &&
                            Arrays.asList(PREFERENCES_EMPLOYEE_FOR_DEPARTMENT[a]).contains(PREFERENCES_COMPANY_FOR_DEPARTMENT[b][departmentIndex])) {
    
                            // Match A với middle node là công ty B và ban ngành C
                            matchedB[b] = a;
                            matchedC[departmentIndex] = b;
                            stableMatches++;
    
                            // Giai đoạn 2: Tiếp tục tìm match với right node
                            int padding = PREFERENCES_COMPANY_FOR_DEPARTMENT[b].length;
                            for (int rightNodeIndex = cIndex + padding; rightNodeIndex < permForDepartments.size(); rightNodeIndex++) {
                                int rightNodeDepartmentIndex = permForDepartments.get(rightNodeIndex % NUM_PERSONS);
    
                                // Kiểm tra ưu tiên right node cho nhân viên A và công ty B
                                String[] preferredRightNodeEmployees = PREFERENCES_DEPARTMENT_FOR_EMPLOYEE[rightNodeDepartmentIndex];
                                String[] preferredRightNodeCompanies = PREFERENCES_DEPARTMENT_FOR_COMPANY[rightNodeDepartmentIndex];
    
                                if (Arrays.asList(preferredRightNodeEmployees).contains(EMPLOYEES[a]) &&
                                    Arrays.asList(preferredRightNodeCompanies).contains(COMPANIES[b]) &&
                                    Arrays.asList(PREFERENCES_EMPLOYEE_FOR_DEPARTMENT[a]).contains(PREFERENCES_COMPANY_FOR_DEPARTMENT[b][rightNodeDepartmentIndex])) {
    
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
        solution.setVariable(0, new Permutation(NUM_PERSONS));  // Permutation cho các công ty
        solution.setVariable(1, new Permutation(NUM_PERSONS));  // Permutation cho các ban ngành
        return solution;
    }

    public static void main(String[] args) {
        int numRandomSets = 10;

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
