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
                        {4500, 5000, 5200, 4700, 5800}
                };
                // đơn vị là đô la

                double[][] distant= {
                        {9, 8, 5, 6, 10},
                        {6, 7, 8.5, 6, 9},
                        {7, 9, 6, 5, 8},
                        {6, 9, 4, 6.5, 7},
                        {8, 6, 7, 7.5, 9}
                };
                // đơn vị là km

                double[][] skill= {
                        {0.8, 0.9, 0.85, 0.6, 0.7},
                        {0.7, 0.85, 0.9, 0.8, 0.8},
                        {0.9, 0.85, 0.6, 0.9, 0.8},
                        {0.8, 0.9, 0.6, 0.8, 0.8},
                        {0.65,0.8, 0.8, 0.7, 0.85}
                };
                // thước đo tối đa là 1

                NondominatedPopulation result = new Executor()
                        .withProblemClass(LaborMarketProblem.class,numEmployee, numCompany, salary, distant, skill)
                        .withAlgorithm("NSGAIII")
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
