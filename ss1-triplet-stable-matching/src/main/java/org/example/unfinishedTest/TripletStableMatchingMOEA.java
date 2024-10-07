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
    private static final String[] EMPLOYEES = {"Alice", "Bob", "Charlie", "David", "Eva"};
    private static final String[] DEPARTMENTS = {"Engineering", "Sales", "HR", "Finance", "Marketing"};
    private static final int NUM_PERSONS = 5;

    // Preferences of employees for companies and departments (3D array)
    private static final String[][][] PREFERENCES_EMPLOYEE = {
        {{"Google", "Microsoft", "Tesla", "Apple", "Amazon"}, {"Engineering", "Sales", "HR", "Finance", "Marketing"}}, // Alice
        {{"Amazon", "Tesla", "Apple", "Google", "Microsoft"}, {"HR", "IT", "Engineering", "Sales", "Marketing"}}, // Bob
        {{"Tesla", "Apple", "Microsoft", "Google", "Amazon"}, {"IT", "Engineering", "Marketing", "HR", "Finance"}}, // Charlie
        {{"Microsoft", "Apple", "Google", "Amazon", "Tesla"}, {"Finance", "Marketing", "HR", "IT", "Engineering"}}, // David
        {{"Apple", "Google", "Amazon", "Microsoft", "Tesla"}, {"Sales", "Marketing", "IT", "HR", "Finance"}} // Eva
    };

    // Preferences of companies for employees and departments (3D array)
    private static final String[][][] PREFERENCES_COMPANY = {
        {{"Alice", "Charlie", "Bob", "David", "Eva"}, {"Engineering", "HR", "Finance", "Sales", "Marketing"}}, // Microsoft
        {{"Bob", "David", "Alice", "Eva", "Charlie"}, {"Marketing", "Sales", "IT", "HR", "Engineering"}}, // Amazon
        {{"Charlie", "Eva", "David", "Alice", "Bob"}, {"IT", "Marketing", "Sales", "Finance", "HR"}}, // Google
        {{"David", "Alice", "Bob", "Charlie", "Eva"}, {"HR", "Engineering", "Marketing", "Sales", "Finance"}}, // Tesla
        {{"Eva", "Bob", "Alice", "Charlie", "David"}, {"Sales", "Engineering", "Finance", "IT", "HR"}} // Apple
    };

    // Preferences of departments for employees and companies (3D array)
    private static final String[][][] PREFERENCES_DEPARTMENT = {
        {{"Alice", "Bob", "Charlie", "David", "Eva"}, {"Microsoft", "Google", "Tesla", "Apple", "Amazon"}}, // Engineering
        {{"Charlie", "Eva", "David", "Alice", "Bob"}, {"Amazon", "Tesla", "Apple", "Google", "Microsoft"}},    // Sales
        {{"David", "Alice", "Bob", "Charlie", "Eva"}, {"Google", "Apple", "Microsoft", "Amazon", "Tesla"}}, // HR
        {{"Bob", "Eva", "Charlie", "Alice", "David"}, {"Tesla", "Microsoft", "Amazon", "Google", "Apple"}},  // Finance
        {{"Alice", "Charlie", "Eva", "Bob", "David"}, {"Amazon", "Apple", "Tesla", "Google", "Microsoft"}}    // Marketing
    };

    public TripletStableMatchingMOEA() {
        super(2, 1);  // Hai biến quyết định (cho công ty và ban ngành) và một mục tiêu
    }

    @Override
    public void evaluate(Solution solution) {
        Permutation permForCompanies = (Permutation) solution.getVariable(0);  // Hoán vị cho công ty
        Permutation permForDepartments = (Permutation) solution.getVariable(1);  // Hoán vị cho ban ngành

        int[] matchedB = new int[NUM_PERSONS];
        Arrays.fill(matchedB, -1);

        int[] matchedC = new int[NUM_PERSONS];
        Arrays.fill(matchedC, -1);

        List<Integer> freeA = new ArrayList<>();
        for (int i = 0; i < NUM_PERSONS; i++) {
            freeA.add(i);
        }

        int stableMatches = 0;

        // Vòng lặp qua hoán vị ngẫu nhiên của các cá thể
        while (!freeA.isEmpty()) {
            int a = freeA.remove(0);  // Chỉ số nhân viên A

            // Bắt đầu vòng lặp qua danh sách ưu tiên của nhân viên này (Employee to Company Preference)
            for (int bIndex = 0; bIndex < COMPANIES.length; bIndex++) {
                int b = permForCompanies.get(bIndex); // Công ty mà A đang xem xét

                // Kiểm tra ưu tiên của công ty với nhân viên này (Company to Employee Preference)
                String[] preferredEmployeesForB = PREFERENCES_COMPANY[b][0];
                String[] preferredDepartmentsForB = PREFERENCES_COMPANY[b][1];  // Công ty cũng có các ban ngành ưu tiên

                // Kiểm tra ưu tiên của nhân viên A với công ty B (Employee to Company Preference)
                if (Arrays.asList(preferredEmployeesForB).contains(EMPLOYEES[a]) &&
                    Arrays.asList(PREFERENCES_EMPLOYEE[a][0]).contains(COMPANIES[b])) {

                    // Giai đoạn 1: Match với middle node (ban ngành) trong preflist
                    for (int cIndex = 0; cIndex < permForDepartments.size(); cIndex++) {
                        int departmentIndex = permForDepartments.get(cIndex);  // Ban ngành hiện tại từ hoán vị

                        // Kiểm tra ưu tiên của ban ngành với nhân viên A và công ty B
                        String[] preferredEmployeesForC = PREFERENCES_DEPARTMENT[departmentIndex][0];
                        String[] preferredCompaniesForC = PREFERENCES_DEPARTMENT[departmentIndex][1];

                        // Kiểm tra ưu tiên của nhân viên A với ban ngành C (Employee to Department Preference)
                        if (Arrays.asList(preferredEmployeesForC).contains(EMPLOYEES[a]) &&
                            Arrays.asList(preferredCompaniesForC).contains(COMPANIES[b]) &&
                            Arrays.asList(PREFERENCES_EMPLOYEE[a][1]).contains(DEPARTMENTS[departmentIndex]) &&
                            Arrays.asList(preferredDepartmentsForB).contains(DEPARTMENTS[departmentIndex])) {  // Thêm kiểm tra ở đây

                            // Match A với middle node là công ty B và ban ngành C
                            matchedB[b] = a;
                            matchedC[departmentIndex] = b;
                            stableMatches++;

                            // Giai đoạn 2: Tiếp tục tìm match với right node
                            for (int cRightIndex = 0; cRightIndex < permForDepartments.size(); cRightIndex++) {
                                int rightDepartmentIndex = permForDepartments.get(cRightIndex);  // Ban ngành hiện tại từ hoán vị (right node)

                                // Kiểm tra ưu tiên của ban ngành C (right node) với nhân viên A và công ty B
                                String[] preferredEmployeesForCRight = PREFERENCES_DEPARTMENT[rightDepartmentIndex][0];
                                String[] preferredCompaniesForCRight = PREFERENCES_DEPARTMENT[rightDepartmentIndex][1];

                                // Kiểm tra tính hợp lệ của việc ghép cặp với right node
                                if (Arrays.asList(preferredEmployeesForCRight).contains(EMPLOYEES[a]) &&
                                    Arrays.asList(preferredCompaniesForCRight).contains(COMPANIES[b]) &&
                                    Arrays.asList(PREFERENCES_EMPLOYEE[a][1]).contains(DEPARTMENTS[rightDepartmentIndex]) &&
                                    Arrays.asList(preferredDepartmentsForB).contains(DEPARTMENTS[rightDepartmentIndex])) {

                                    // Thực hiện ghép cặp A với middle và right node
                                    matchedC[rightDepartmentIndex] = b;
                                    stableMatches++;
                                    break;  // Thoát vòng lặp với right node khi đã có ghép cặp ổn định
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
        solution.setVariable(0, new Permutation(NUM_PERSONS));  // Hoán vị cho các công ty
        solution.setVariable(1, new Permutation(NUM_PERSONS));  // Hoán vị cho các ban ngành
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
                
                    // Kiểm tra tính hợp lệ của chỉ số và in ra kết quả tương ứng
                    if (companyIndex >= 0 && companyIndex < COMPANIES.length &&
                        departmentIndex >= 0 && departmentIndex < DEPARTMENTS.length) {
                
                        String company = COMPANIES[companyIndex];  // Lấy tên công ty
                        String department = DEPARTMENTS[departmentIndex];  // Lấy tên ban ngành
                
                        // In ra thông tin cặp ghép giữa nhân viên, công ty và ban ngành
                        System.out.printf("%-10d (%s, %s, %s)\n", order, employee, company, department);
                    } else {
                        // Nếu ghép cặp không hợp lệ, in ra thông tin nhân viên với chỉ số không hợp lệ
                        System.out.printf("%-10d (%s, INVALID MATCH)\n", order, employee);
                    }
                    order++;
                }
                
            }
            System.out.println();
        }
    }
}
