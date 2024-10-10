package org.example.triplet_stable_matching_test;

import java.util.Arrays;
import java.util.Random;

public class GaleShapleyTwoStage {
    private final String[] employees;
    private final String[] companies;
    private final String[] departments;
    private final int[][][] employeePreferences;
    private final int[][][] companyPreferences;  // Ưu tiên của công ty
    private final int[][][] departmentPreferences;  // Ưu tiên của ban ngành
    private int[] employeeMatches;  // Lưu trữ match của nhân viên với ban ngành
    private int[] departmentMatches;  // Lưu trữ match của ban ngành với công ty
    private int[] companyMatches;  // Lưu trữ match của công ty với ban ngành
    private Random random = new Random();

    public GaleShapleyTwoStage(String[] employees, String[] companies, String[] departments, 
                               int[][][] employeePreferences, int[][][] companyPreferences, int[][][] departmentPreferences) {
        this.employees = employees;
        this.companies = companies;
        this.departments = departments;
        this.employeePreferences = employeePreferences;
        this.companyPreferences = companyPreferences;
        this.departmentPreferences = departmentPreferences;
        employeeMatches = new int[employees.length];
        departmentMatches = new int[departments.length];
        companyMatches = new int[companies.length];
        Arrays.fill(employeeMatches, -1);   // -1 nghĩa là chưa match
        Arrays.fill(departmentMatches, -1);  // -1 nghĩa là chưa match
        Arrays.fill(companyMatches, -1);  // -1 nghĩa là chưa match
    }

    public void match() {
        // Step 1: Shuffle and match Employees to Departments
        for (int empIndex = 0; empIndex < employees.length; empIndex++) {
            int[] shuffledDepartmentPreferences = employeePreferences[empIndex][0].clone();
            shuffleArray(shuffledDepartmentPreferences);

            for (int preferredDeptIndex : shuffledDepartmentPreferences) {
                if (employeeMatches[empIndex] == -1) {  // Nếu nhân viên chưa ghép với ban ngành
                    if (departmentMatches[preferredDeptIndex] == -1) {
                        // Ghép nhân viên với ban ngành nếu ban ngành chưa có ghép cặp
                        employeeMatches[empIndex] = preferredDeptIndex;
                        departmentMatches[preferredDeptIndex] = empIndex;
                        break;  // Thoát vòng lặp
                    }
                }
            }
        }

        // Step 2: Match Departments to Companies (and check Company preferences)
        for (int deptIndex = 0; deptIndex < departments.length; deptIndex++) {
            int[] shuffledCompanyPreferences = departmentPreferences[deptIndex][1].clone();  // Lấy danh sách ưu tiên công ty của ban ngành
            shuffleArray(shuffledCompanyPreferences);

            for (int preferredCompanyIndex : shuffledCompanyPreferences) {
                if (departmentMatches[deptIndex] != -1) {  // Nếu ban ngành đã ghép với nhân viên
                    if (companyMatches[preferredCompanyIndex] == -1) {
                        // Công ty chưa ghép với ban ngành nào, ghép cặp luôn
                        departmentMatches[deptIndex] = preferredCompanyIndex;
                        companyMatches[preferredCompanyIndex] = deptIndex;
                        System.out.printf("Department %s matches with Employee %s and Company %s\n", 
                            departments[deptIndex], 
                            employees[departmentMatches[deptIndex]], 
                            companies[preferredCompanyIndex]);
                        break;
                    } else {
                        // Công ty đã ghép với ban ngành khác, kiểm tra xem có nên thay thế không
                        int currentDeptIndex = companyMatches[preferredCompanyIndex];
                        if (Arrays.asList(companyPreferences[preferredCompanyIndex][1]).indexOf(deptIndex) <
                                Arrays.asList(companyPreferences[preferredCompanyIndex][1]).indexOf(currentDeptIndex)) {
                            // Công ty ưu tiên ban ngành mới hơn, thay thế ghép cặp
                            companyMatches[preferredCompanyIndex] = deptIndex;
                            departmentMatches[deptIndex] = preferredCompanyIndex;
                            System.out.printf("Department %s replaces Department %s and matches with Company %s\n", 
                                departments[deptIndex], 
                                departments[currentDeptIndex], 
                                companies[preferredCompanyIndex]);
                            break;
                        }
                    }
                }
            }
        }
    }

    // Hàm để ngẫu nhiên hoán vị mảng
    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public void printMatches() {
        System.out.println("Matched Triples (Employee - Department - Company):");
        for (int i = 0; i < employees.length; i++) {
            if (employeeMatches[i] != -1) {
                int deptIndex = employeeMatches[i];
                if (departmentMatches[deptIndex] != -1) {
                    System.out.printf("%s matches with %s in Department %s and Company %s\n", 
                            employees[i], 
                            departments[deptIndex], 
                            departments[deptIndex], 
                            companies[departmentMatches[deptIndex]]);
                } else {
                    System.out.printf("%s - No Match\n", employees[i]);
                }
            }
        }
    }

    public int[] getEmployeeMatches() {
        return employeeMatches;  // Trả về kết quả ghép cặp của nhân viên với ban ngành
    }

    public int[] getDepartmentMatches() {
        return departmentMatches;  // Trả về kết quả ghép cặp của ban ngành với công ty
    }

    public int[] getCompanyMatches() {
        return companyMatches;  // Trả về kết quả ghép cặp của nhân viên với công ty (kết hợp từ 2 giai đoạn)
    }
}
