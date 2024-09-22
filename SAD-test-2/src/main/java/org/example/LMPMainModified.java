package org.example;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Random;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class LMPMainModified {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final Random random = new Random();

    public static void main(String[] args) {
        int numEmployee = 10;
        int numCompany = 5;
        int maxApplicationsPerEmployee = 2;  // Mỗi nhân viên có thể nộp đơn tối đa vào 2 công ty

        String[] employeeNames = {
            "Nguyen Van A", "Tran Thi B", "Le Van C", "Pham Thi D", "Hoang Van E",
            "Vu Thi F", "Do Van G", "Nguyen Thi H", "Pham Van I", "Le Thi K"
        };
        
        String[] companyNames = {
            "Company 1", "Company 2", "Company 3", "Company 4", "Company 5"
        };
        

        double[][] salary = new double[numEmployee][numCompany];
        for (int i = 0; i < numEmployee; i++) {
            for (int j = 0; j < numCompany; j++) {
                salary[i][j] = 4000 + random.nextDouble() * 3000; // Lương ngẫu nhiên từ 4000 đến 7000
            }
        }

        // Bảng kỹ năng ngẫu nhiên của nhân viên với từng công ty
        double[][] skill = new double[numEmployee][numCompany];
        for (int i = 0; i < numEmployee; i++) {
            for (int j = 0; j < numCompany; j++) {
                skill[i][j] = random.nextDouble(); // Kỹ năng ngẫu nhiên từ 0.0 đến 1.0
            }
        }

        // Thứ tự ưu tiên ngẫu nhiên cho mỗi nhân viên
        int[][] priority = new int[numEmployee][numCompany];
        for (int i = 0; i < numEmployee; i++) {
            List<Integer> indices = IntStream.range(0, numCompany).boxed().collect(Collectors.toList());
            Collections.shuffle(indices);
            for (int j = 0; j < numCompany; j++) {
                priority[i][j] = indices.get(j);
            }
        }

        // Yêu cầu lương và kỹ năng của từng công ty
        int[] companySalaryRequirement = { 5000, 5500, 4000, 4500, 4800 };
        double[] companySkillRequirement = { 0.6, 0.7, 0.5, 0.6, 0.8 };

        // Tối ưu hóa với NSGA-II
        NondominatedPopulation result = new Executor()
            .withProblemClass(LaborMarketProblemModified.class, numEmployee, numCompany, salary, skill, priority, 
                              companySalaryRequirement, companySkillRequirement, maxApplicationsPerEmployee)
            .withAlgorithm("NSGAII")
            .withMaxEvaluations(10000)
            .run();

        // Hiển thị kết quả
        for (Solution solution : result) {
            int[] employeeChoice = (int[]) solution.getAttribute("employeeChoice");

            double totalSatisfaction = -solution.getObjective(0);
            double totalEmployeeSatisfaction = 0;
            double totalCompanySatisfaction = 0;

            System.out.println("========================================================================================================================================");
            System.out.println("Summary of Results:");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Total Satisfaction: " + df.format(totalSatisfaction));
            System.out.println("Constraint Violations:");
            System.out.println("  - Company constraints: " + df.format(-solution.getConstraint(0)));
            System.out.println("  - Employee constraints: " + df.format(-solution.getConstraint(1)));
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

            System.out.println("Employee Assignments:");
            System.out.printf("%-20s%-20s%-15s%-12s%-25s%-25s%-20s\n", "Employee", "Company", "Salary (USD)", "Skill", "Emp Satisfaction", "Comp Satisfaction", "Total Satisfaction");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < employeeChoice.length; i++) {
                int company = employeeChoice[i];
                if (company >= 0) {
                    double employeeSalary = salary[i][company];
                    double employeeSkill = skill[i][company];

                    // Độ hài lòng của nhân viên: dựa trên mức lương so với yêu cầu của công ty
                    double employeeSatisfaction = Math.max(0, employeeSalary - companySalaryRequirement[company]);

                    // Độ hài lòng của công ty: dựa trên kỹ năng của nhân viên so với yêu cầu kỹ năng của công ty
                    double companySatisfaction = Math.max(0, employeeSkill - companySkillRequirement[company]);

                    totalEmployeeSatisfaction += employeeSatisfaction;
                    totalCompanySatisfaction += companySatisfaction;

                    double totalPairSatisfaction = employeeSatisfaction + companySatisfaction;

                    System.out.printf("%-20s%-20s%-15s%-12s%-25s%-25s%-20s\n", 
                        employeeNames[i], 
                        companyNames[company], 
                        df.format(employeeSalary), 
                        df.format(employeeSkill),
                        df.format(employeeSatisfaction),
                        df.format(companySatisfaction),
                        df.format(totalPairSatisfaction)
                    );
                } else {
                    System.out.printf("%-20s%-20s%-15s%-12s%-25s%-25s%-20s\n", 
                        employeeNames[i], "None", "N/A", "N/A", "0", "0", "0");
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Total Employee Satisfaction: " + df.format(totalEmployeeSatisfaction));
            System.out.println("Total Company Satisfaction: " + df.format(totalCompanySatisfaction));
            System.out.println("Combined Total Satisfaction: " + df.format(totalEmployeeSatisfaction + totalCompanySatisfaction));
            System.out.println("=================================================");
        }
    }
}
