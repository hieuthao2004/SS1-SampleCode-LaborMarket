package org.example;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.problem.AbstractProblem;

import java.util.Arrays;

public class LaborMarketProblemModified extends AbstractProblem {

    private final int numEmployee;
    private final int numCompany;
    private final double[][] salary;
    private final double[][] skill;      // Ma trận kỹ năng của nhân viên
    private final int[][] priority;      // Thứ tự ưu tiên công ty của nhân viên
    private final int[] companySalaryRequirement;  // Yêu cầu lương của từng công ty
    private final double[] companySkillRequirement;  // Yêu cầu kỹ năng của từng công ty

    private final int maxEmployeePerCompany = 2;
    private final int maxApplicationsPerEmployee;

    public LaborMarketProblemModified(int numEmployee, int numCompany, double[][] salary, double[][] skill, int[][] priority, 
                                      int[] companySalaryRequirement, double[] companySkillRequirement, int maxApplicationsPerEmployee) {
        super(1, 1, 2);
        this.numEmployee = numEmployee;
        this.numCompany = numCompany;
        this.salary = salary;
        this.skill = skill;
        this.priority = priority;
        this.companySalaryRequirement = companySalaryRequirement;
        this.companySkillRequirement = companySkillRequirement;
        this.maxApplicationsPerEmployee = maxApplicationsPerEmployee;
    }

    @Override
    public void evaluate(Solution solution) {
        BinaryVariable employeeChosen = (BinaryVariable) solution.getVariable(0);
        double totalSatisfaction = 0;

        int[] validConstraintCompany = new int[numCompany];
        int[] validConstraintEmployee = new int[numEmployee];
        int[] employeeChoice = new int[numEmployee];

        Arrays.fill(validConstraintCompany, 0);
        Arrays.fill(validConstraintEmployee, 0);
        Arrays.fill(employeeChoice, -1);

        // Tính toán độ hài lòng của nhân viên và kiểm tra yêu cầu lương & kỹ năng của công ty
        for (int i = 0; i < numEmployee; i++) {
            int applicationCount = 0;
            for (int j = 0; j < numCompany && applicationCount < maxApplicationsPerEmployee; j++) {
                int preferredCompany = priority[i][j];  // Công ty mà nhân viên i ưu tiên
                int position = i * numCompany + preferredCompany;
                
                if (employeeChosen.get(position) && validConstraintEmployee[i] == 0 && validConstraintCompany[preferredCompany] < maxEmployeePerCompany) {
                    // Nhân viên chỉ được chọn nếu yêu cầu lương và kỹ năng thỏa mãn
                    if (salary[i][preferredCompany] >= companySalaryRequirement[preferredCompany] &&
                        skill[i][preferredCompany] >= companySkillRequirement[preferredCompany]) {
                        validConstraintCompany[preferredCompany]++;
                        validConstraintEmployee[i]++;
                        employeeChoice[i] = preferredCompany;

                        // Độ hài lòng của nhân viên được tính dựa trên thứ tự ưu tiên và sự phù hợp với công ty
                        double salarySatisfaction = salary[i][preferredCompany] / 1000.0;
                        double skillSatisfaction = skill[i][preferredCompany] * 10.0;
                        totalSatisfaction += salarySatisfaction + skillSatisfaction;
                        applicationCount++;
                    }
                }
            }
        }

        // Kiểm tra ràng buộc
        int notSatisfiedForCompany = 0;
        int notSatisfiedForEmployee = 0;

        for (int i : validConstraintCompany) {
            if (i > maxEmployeePerCompany) notSatisfiedForCompany++;
        }
        for (int i : validConstraintEmployee) {
            if (i > 1) notSatisfiedForEmployee++;
        }

        // Đặt ràng buộc
        solution.setConstraint(0, -notSatisfiedForCompany);
        solution.setConstraint(1, -notSatisfiedForEmployee);

        // Đặt mục tiêu tối ưu hóa
        solution.setObjective(0, -totalSatisfaction);

        // Lưu lựa chọn công ty của nhân viên
        solution.setAttribute("employeeChoice", employeeChoice);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(this.numberOfVariables, this.numberOfObjectives, this.numberOfConstraints);
        solution.setVariable(0, new BinaryVariable(numEmployee * numCompany));
        return solution;
    }
}
