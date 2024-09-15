package org.example;

import java.text.DecimalFormat;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class LMPMain {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void main(String[] args) {
        int numEmployee = 20;  // Cập nhật số nhân viên thành 20
        int numCompany = 5;    // Số công ty không thay đổi

        // Cập nhật dữ liệu lương cho 20 nhân viên và 5 công ty
        double[][] salary = {
            {5000, 6000, 5500, 4000, 4800},
            {4800, 5500, 6000, 5000, 5200},
            {6000, 5500, 4000, 6500, 5000},
            {5200, 6200, 4000, 5300, 5400},
            {4500, 5000, 5200, 4700, 5800},
            {5100, 5800, 5600, 4900, 5500},
            {4700, 5400, 5100, 4500, 5700},
            {5300, 5600, 5500, 4700, 6000},
            {4900, 5200, 5100, 4600, 5800},
            {4700, 5400, 5000, 4800, 5600},
            {5000, 5500, 5400, 4700, 5900},
            {4600, 5200, 5300, 4400, 5700},
            {4900, 5100, 5200, 4600, 5800},
            {5300, 5400, 5500, 4700, 6100},
            {4600, 5000, 5200, 4300, 5500},
            {5100, 5600, 5500, 4900, 5700},
            {4700, 5200, 5000, 4500, 5900},
            {5300, 5700, 5400, 4800, 6000},
            {4900, 5200, 5100, 4600, 5600},
            {4600, 5100, 5300, 4400, 5500}
        };

        // Khởi tạo Executor và cấu hình thuật toán
        NondominatedPopulation result = new Executor()
            .withProblemClass(LaborMarketProblem.class, numEmployee, numCompany, salary)
            .withAlgorithm("NSGAII")  // Thay thế PESA2 nếu cần
            .withMaxEvaluations(10000)
            .run();

        // Hiển thị kết quả
        for (Solution solution : result) {
            int[] employeeChoice = (int[]) solution.getAttribute("employeeChoice");

            double totalSalaryAcquired = -solution.getObjective(0);  // Lấy giá trị mục tiêu và làm cho nó dương

            System.out.println("=================================================");
            System.out.println("Summary of Results:");
            System.out.println("-------------------------------------------------");
            System.out.println("Total Salary Acquired: " + df.format(totalSalaryAcquired) + " USD");
            System.out.println("Constraint Violations:");
            System.out.println("  - Company constraints: " + df.format(-solution.getConstraint(0)));
            System.out.println("  - Employee constraints: " + df.format(-solution.getConstraint(1)));
            System.out.println("-------------------------------------------------");

            System.out.println("Employee Assignments:");
            System.out.printf("%-12s%-12s%-12s\n", "Employee", "Company", "Salary (USD)");
            System.out.println("-------------------------------------------------");

            for (int i = 0; i < employeeChoice.length; i++) {
                int company = employeeChoice[i];
                if (company >= 0) {
                    double employeeSalary = salary[i][company];
                    System.out.printf("%-12d%-12d%-12s\n", (i + 1), company, df.format(employeeSalary));
                } else {
                    System.out.printf("%-12d%-12s%-12s\n", (i + 1), "None", "N/A");
                }
            }
            System.out.println("=================================================");
        }
    }
}
