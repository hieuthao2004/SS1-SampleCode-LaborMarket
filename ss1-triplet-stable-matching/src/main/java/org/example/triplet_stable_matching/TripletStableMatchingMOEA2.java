package org.example.triplet_stable_matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.problem.AbstractProblem;

public class TripletStableMatchingMOEA2 extends AbstractProblem {

    private static final String[] COMPANIES = {
        "Microsoft", "Amazon", "Google", "Tesla", "Apple",
        "Facebook", "Netflix", "Intel", "NVIDIA", "IBM"
    };

    private static final String[] EMPLOYEES = {
        "Alice", "Bob", "Charlie", "David", "Eva",
        "Frank", "Grace", "Hannah", "Ivan", "Julia"
    };

    private static final String[] DEPARTMENTS = {
        "Engineering", "Sales", "HR", "Finance", "Marketing",
        "Operations", "IT", "Legal", "R&D", "Customer Support"
    };

    private static final int NUM_PERSONS = 10;

    // Preferences of employees for companies and departments (3D array)
    private static final String[][][] PREFERENCES_EMPLOYEE = {
        {{"Google", "Microsoft", "Tesla", "Apple", "Amazon", "Facebook", "Netflix", "Intel", "NVIDIA", "IBM"}, 
         {"Engineering", "Sales", "HR", "Finance", "Marketing", "Operations", "IT", "Legal", "R&D", "Customer Support"}}, // Alice
        {{"Amazon", "Tesla", "Apple", "Google", "Microsoft", "NVIDIA", "Facebook", "IBM", "Netflix", "Intel"}, 
         {"IT", "HR", "Engineering", "Sales", "Marketing", "Operations", "Legal", "R&D", "Customer Support", "Finance"}}, // Bob
        {{"Tesla", "Apple", "Microsoft", "Google", "Amazon", "Facebook", "Intel", "NVIDIA", "IBM", "Netflix"}, 
         {"IT", "Engineering", "Marketing", "HR", "Finance", "R&D", "Operations", "Legal", "Sales", "Customer Support"}}, // Charlie
        {{"Microsoft", "Apple", "Google", "Amazon", "Tesla", "NVIDIA", "IBM", "Facebook", "Intel", "Netflix"}, 
         {"Finance", "Marketing", "HR", "IT", "Engineering", "R&D", "Operations", "Legal", "Sales", "Customer Support"}}, // David
        {{"Apple", "Google", "Amazon", "Microsoft", "Tesla", "Facebook", "Netflix", "Intel", "IBM", "NVIDIA"}, 
         {"Sales", "Marketing", "IT", "HR", "Finance", "Operations", "Legal", "R&D", "Customer Support", "Engineering"}}, // Eva
        {{"Facebook", "Tesla", "Google", "Amazon", "Apple", "Microsoft", "Netflix", "Intel", "IBM", "NVIDIA"}, 
         {"Marketing", "Sales", "HR", "Customer Support", "Finance", "R&D", "Engineering", "IT", "Operations", "Legal"}}, // Frank
        {{"Netflix", "Google", "Amazon", "Tesla", "Microsoft", "Apple", "Facebook", "Intel", "NVIDIA", "IBM"}, 
         {"HR", "Finance", "Sales", "IT", "Engineering", "Operations", "R&D", "Customer Support", "Legal", "Marketing"}}, // Grace
        {{"Intel", "Apple", "Microsoft", "Tesla", "Amazon", "Google", "Facebook", "Netflix", "NVIDIA", "IBM"}, 
         {"R&D", "Operations", "Sales", "Marketing", "IT", "Engineering", "Finance", "HR", "Legal", "Customer Support"}}, // Hannah
        {{"IBM", "Google", "Apple", "Microsoft", "Tesla", "Amazon", "Netflix", "Intel", "NVIDIA", "Facebook"}, 
         {"Engineering", "Customer Support", "R&D", "Legal", "HR", "Finance", "IT", "Operations", "Marketing", "Sales"}}, // Ivan
        {{"NVIDIA", "Microsoft", "Google", "Amazon", "Tesla", "Apple", "Facebook", "Intel", "IBM", "Netflix"}, 
         {"Legal", "R&D", "IT", "Engineering", "Finance", "HR", "Marketing", "Customer Support", "Operations", "Sales"}} // Julia
    };

    // Preferences of companies for employees and departments (3D array)
    private static final String[][][] PREFERENCES_COMPANY = {
        {{"Alice", "Charlie", "Bob", "David", "Eva", "Frank", "Grace", "Hannah", "Ivan", "Julia"}, 
         {"Engineering", "HR", "Finance", "Sales", "Marketing", "Operations", "IT", "Legal", "R&D", "Customer Support"}}, // Microsoft
        {{"Bob", "David", "Alice", "Eva", "Charlie", "Grace", "Ivan", "Frank", "Julia", "Hannah"}, 
         {"Marketing", "Sales", "IT", "HR", "Engineering", "Operations", "Legal", "R&D", "Customer Support", "Finance"}}, // Amazon
        {{"Charlie", "Eva", "David", "Alice", "Bob", "Hannah", "Ivan", "Frank", "Grace", "Julia"}, 
         {"IT", "Marketing", "Sales", "Finance", "HR", "Operations", "R&D", "Customer Support", "Legal", "Engineering"}}, // Google
        {{"David", "Alice", "Bob", "Charlie", "Eva", "Frank", "Julia", "Hannah", "Grace", "Ivan"}, 
         {"HR", "Engineering", "Marketing", "Sales", "Finance", "R&D", "Operations", "Legal", "IT", "Customer Support"}}, // Tesla
        {{"Eva", "Bob", "Alice", "Charlie", "David", "Hannah", "Grace", "Julia", "Ivan", "Frank"}, 
         {"Sales", "Engineering", "Finance", "IT", "HR", "Operations", "R&D", "Customer Support", "Legal", "Marketing"}}, // Apple
        {{"Frank", "Grace", "Eva", "Alice", "Charlie", "Bob", "David", "Julia", "Ivan", "Hannah"}, 
         {"Marketing", "Finance", "Operations", "HR", "Sales", "Engineering", "Legal", "IT", "R&D", "Customer Support"}}, // Facebook
        {{"Grace", "Bob", "Eva", "Charlie", "David", "Frank", "Hannah", "Alice", "Ivan", "Julia"}, 
         {"IT", "Sales", "R&D", "Operations", "Customer Support", "Finance", "Legal", "HR", "Engineering", "Marketing"}}, // Netflix
        {{"Hannah", "Eva", "Bob", "David", "Charlie", "Frank", "Grace", "Julia", "Ivan", "Alice"}, 
         {"R&D", "Engineering", "IT", "Customer Support", "HR", "Operations", "Sales", "Finance", "Legal", "Marketing"}}, // Intel
        {{"Ivan", "Grace", "Frank", "Bob", "Charlie", "David", "Eva", "Alice", "Hannah", "Julia"}, 
         {"Customer Support", "IT", "Finance", "Sales", "Engineering", "HR", "R&D", "Operations", "Legal", "Marketing"}}, // NVIDIA
        {{"Julia", "Eva", "Alice", "Bob", "Charlie", "Grace", "Hannah", "David", "Ivan", "Frank"}, 
         {"Legal", "Finance", "IT", "HR", "Sales", "Engineering", "R&D", "Customer Support", "Operations", "Marketing"}} // IBM
    };

    // Preferences of departments for employees and companies (3D array)
    private static final String[][][] PREFERENCES_DEPARTMENT = {
        {{"Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Hannah", "Ivan", "Julia"}, 
         {"Microsoft", "Google", "Tesla", "Apple", "Amazon", "Facebook", "Netflix", "Intel", "NVIDIA", "IBM"}}, // Engineering
        {{"Charlie", "Eva", "David", "Alice", "Bob", "Grace", "Frank", "Hannah", "Ivan", "Julia"}, 
         {"Amazon", "Tesla", "Apple", "Google", "Microsoft", "Facebook", "Netflix", "Intel", "NVIDIA", "IBM"}},    // Sales
        {{"David", "Alice", "Bob", "Charlie", "Eva", "Frank", "Ivan", "Grace", "Julia", "Hannah"}, 
         {"Google", "Apple", "Microsoft", "Amazon", "Tesla", "Facebook", "Netflix", "Intel", "IBM", "NVIDIA"}}, // HR
        {{"Bob", "Eva", "Charlie", "Alice", "David", "Grace", "Hannah", "Frank", "Julia", "Ivan"}, 
         {"Tesla", "Amazon", "Microsoft", "Apple", "Google", "Facebook", "Netflix", "NVIDIA", "Intel", "IBM"}}, // Finance
        {{"Eva", "Charlie", "Alice", "David", "Bob", "Grace", "Frank", "Ivan", "Julia", "Hannah"}, 
         {"Apple", "Tesla", "Amazon", "Google", "Microsoft", "Facebook", "Netflix", "NVIDIA", "Intel", "IBM"}}, // Marketing
        {{"Frank", "David", "Alice", "Bob", "Charlie", "Eva", "Grace", "Hannah", "Ivan", "Julia"}, 
         {"Facebook", "Google", "Microsoft", "Tesla", "Apple", "Amazon", "Netflix", "Intel", "IBM", "NVIDIA"}}, // Operations
        {{"Grace", "Frank", "David", "Eva", "Alice", "Bob", "Charlie", "Hannah", "Ivan", "Julia"}, 
         {"Netflix", "Google", "Tesla", "Amazon", "Apple", "Microsoft", "Facebook", "Intel", "IBM", "NVIDIA"}}, // IT
        {{"Hannah", "Alice", "David", "Charlie", "Eva", "Bob", "Frank", "Julia", "Grace", "Ivan"}, 
         {"Intel", "Tesla", "Amazon", "Apple", "Google", "Microsoft", "Facebook", "Netflix", "IBM", "NVIDIA"}}, // Legal
        {{"Ivan", "Eva", "Charlie", "David", "Bob", "Frank", "Grace", "Alice", "Hannah", "Julia"}, 
         {"NVIDIA", "Tesla", "Google", "Amazon", "Apple", "Microsoft", "Facebook", "Intel", "IBM", "Netflix"}}, // R&D
        {{"Julia", "Frank", "Eva", "David", "Alice", "Bob", "Charlie", "Hannah", "Grace", "Ivan"}, 
         {"IBM", "Amazon", "Apple", "Google", "Microsoft", "Tesla", "Facebook", "Netflix", "Intel", "NVIDIA"}} // Customer Support
    };

    public TripletStableMatchingMOEA2() {
        super(2, 1);  // Hai biến quyết định (cho công ty và ban ngành) và một mục tiêu
    }

    @Override
    public void evaluate(Solution solution) {
        Permutation permForCompanies = (Permutation) solution.getVariable(0);  // Hoán vị cho công ty
        Permutation permForDepartments = (Permutation) solution.getVariable(1);  // Hoán vị cho ban ngành
    
        int[] matchedB = new int[NUM_PERSONS];  // Để lưu trữ ghép cặp với công ty
        Arrays.fill(matchedB, -1);
    
        int[] matchedC = new int[NUM_PERSONS];  // Để lưu trữ ghép cặp với ban ngành
        Arrays.fill(matchedC, -1);
    
        List<Integer> freeA = new ArrayList<>();  // Danh sách nhân viên tự do
        for (int i = 0; i < NUM_PERSONS; i++) {
            freeA.add(i);
        }
    
        int stableMatches = 0;
    
        // Vòng lặp qua hoán vị ngẫu nhiên của các cá thể
        while (!freeA.isEmpty()) {
            int a = freeA.remove(0);  // Chỉ số nhân viên A
    
            // Giai đoạn 1: Ghép nhân viên A với công ty B trước
            for (int bIndex = 0; bIndex < COMPANIES.length; bIndex++) {
                int b = permForCompanies.get(bIndex); // Công ty mà A đang xem xét
    
                // Kiểm tra xem công ty B có chấp nhận nhân viên A hay không (theo danh sách ưu tiên của công ty)
                String[] preferredEmployeesForB = PREFERENCES_COMPANY[b][0];
    
                // Kiểm tra xem công ty B có nằm trong danh sách ưa thích của nhân viên A hay không
                if (Arrays.asList(preferredEmployeesForB).contains(EMPLOYEES[a]) &&
                    Arrays.asList(PREFERENCES_EMPLOYEE[a][0]).contains(COMPANIES[b])) {
    
                    matchedB[b] = a;  // Ghép nhân viên A với công ty B
    
                    // Giai đoạn 2: Ghép với ban ngành
                    for (int cIndex = 0; cIndex < permForDepartments.size(); cIndex++) {
                        int departmentIndex = permForDepartments.get(cIndex);  // Ban ngành mà A đang xem xét
    
                        // Kiểm tra xem ban ngành này có chấp nhận nhân viên A và công ty B hay không
                        String[] preferredEmployeesForC = PREFERENCES_DEPARTMENT[departmentIndex][0];
                        String[] preferredCompaniesForC = PREFERENCES_DEPARTMENT[departmentIndex][1];
    
                        // Kiểm tra xem ban ngành có nằm trong danh sách ưa thích của nhân viên A hay không
                        if (Arrays.asList(preferredEmployeesForC).contains(EMPLOYEES[a]) &&
                            Arrays.asList(preferredCompaniesForC).contains(COMPANIES[b]) &&
                            Arrays.asList(PREFERENCES_EMPLOYEE[a][1]).contains(DEPARTMENTS[departmentIndex])) {
    
                            // Ghép A với ban ngành C
                            matchedC[departmentIndex] = b;
                            stableMatches++;  // Tăng số ghép cặp ổn định
                            break;  // Dừng sau khi ghép với ban ngành phù hợp
                        }
                    }
    
                    break;  // Dừng sau khi đã ghép công ty và ban ngành phù hợp cho nhân viên A
                }
            }
        }
    
        // Tối đa hóa số lượng ghép cặp ổn định
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
                    .withProblemClass(TripletStableMatchingMOEA2.class)
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
