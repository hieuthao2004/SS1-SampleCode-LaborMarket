package org.example.double_stable_matching;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class GaleShapley {
    private final String[] employees;
    private final String[] companies;
    private final int[][] employeePreferences;
    private final int[][] companyPreferences;
    private int[] employeeMatches;
    private int[] companyMatches;
    private Random random = new Random();

    public GaleShapley(String[] employees, String[] companies, int[][] employeePreferences, int[][] companyPreferences) {
        this.employees = employees;
        this.companies = companies;
        this.employeePreferences = employeePreferences;
        this.companyPreferences = companyPreferences;
        employeeMatches = new int[employees.length];
        companyMatches = new int[companies.length];
        Arrays.fill(employeeMatches, -1);
        Arrays.fill(companyMatches, -1);
    }

    public void match() {
        Queue<Integer> freeEmployees = new LinkedList<>();
        for (int i = 0; i < employees.length; i++) {
            freeEmployees.add(i);
        }

        while (!freeEmployees.isEmpty()) {
            int empIndex = freeEmployees.poll();

            // Ngẫu nhiên chọn công ty trong danh sách ưu tiên của nhân viên
            int[] shuffledPreferences = employeePreferences[empIndex].clone();
            shuffleArray(shuffledPreferences);

            for (int preferredCompanyIndex : shuffledPreferences) {
                int companyIndex = preferredCompanyIndex;

                if (companyMatches[companyIndex] == -1) {
                    // Công ty tự do, ghép cặp
                    employeeMatches[empIndex] = companyIndex;
                    companyMatches[companyIndex] = empIndex;
                    break;
                } else {
                    // Công ty đã ghép, kiểm tra ưu tiên
                    int currentMatch = companyMatches[companyIndex];

                    // Kiểm tra xem công ty có ưu tiên nhân viên mới hơn không
                    if (Arrays.asList(companyPreferences[companyIndex]).indexOf(empIndex) <
                            Arrays.asList(companyPreferences[companyIndex]).indexOf(currentMatch)) {

                        // Ngắt ghép cặp hiện tại
                        freeEmployees.add(currentMatch);
                        employeeMatches[currentMatch] = -1;

                        // Ghép cặp nhân viên mới
                        employeeMatches[empIndex] = companyIndex;
                        companyMatches[companyIndex] = empIndex;
                        break;
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

    public int[] getEmployeeMatches() {
        return employeeMatches;
    }

    public void printMatches() {
        System.out.println("Matched Pairs:");
        for (int i = 0; i < employees.length; i++) {
            if (employeeMatches[i] != -1) {
                System.out.printf("%s - %s\n", employees[i], companies[employeeMatches[i]]);
            } else {
                System.out.printf("%s - No Match\n", employees[i]);
            }
        }
    }
}
