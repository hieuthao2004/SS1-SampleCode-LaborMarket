package org.example;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.problem.AbstractProblem;

import java.util.Arrays;

public class LaborMarketProblem extends AbstractProblem {

    private final int numEmployee;   // số nhân viên
    private final int numCompany;   // số công ty
    private final double[][] salary;   // mảng chứa tiền lương cho mỗi nhân viên ứng với mỗi công ty

    private final int maxEmployeeRequired = 2;  // mỗi công ty chỉ tuyển 2 nhân viên
    private final int maxApplyforEmployee = 1;  // mỗi nhân viên chỉ có thể apply vào 1 công ty

    public LaborMarketProblem(int numEmployee, int numCompany, double[][] salary) {
        super(1, 1, 2);
        this.numEmployee = numEmployee;
        this.numCompany = numCompany;
        this.salary = salary;
    }

    @Override
    public void evaluate(Solution solution) {

        BinaryVariable employeeChosen = (BinaryVariable) solution.getVariable(0); // khỏi tạo population initialization
        double totalSalaryAcquired = 0;

        int[] validConstraintCompany = new int[numCompany];  // ràng buộc của công ty (mỗi công ty chỉ tuyển 2 nhân viên)
        int[] validConstraintEmployee = new int[numEmployee];   // ràng buộc của nhân viên ( mỗi nhân viên chỉ có thể vào 1 công ty)
        int[] employeeChoice = new int[numEmployee];  // mảng chứa lựa chọn công ty mà nhân viên ứng tuyển


        Arrays.fill(validConstraintCompany, 0);
        Arrays.fill(validConstraintEmployee, 0);
        Arrays.fill(employeeChoice, -1);
        // Check employee choices and assign them
        for (int i = 0; i < numEmployee; i++) {
            for (int j = 0; j < numCompany; j++) {
                int position = i * numCompany + j;
                if (employeeChosen.get(position) && validConstraintEmployee[i] < maxApplyforEmployee && validConstraintCompany[j] < maxEmployeeRequired) {
                    validConstraintCompany[j]++;
                    validConstraintEmployee[i]++;
                    totalSalaryAcquired += salary[i][j];
                    employeeChoice[i] = j ;
                }
            }
        }

        // Constraints
        int notSatisfiedForCompany = 0;  // kiểm tra có thỏa mãn ràng buộc của công ty
        int notSatisfiedForEmployee = 0;  // kiểm tra có thỏa mãn ràng buộc của nhân viên

        for (int i : validConstraintCompany) {
            if (i > maxEmployeeRequired) notSatisfiedForCompany++;
        }
        for (int i : validConstraintEmployee) {
            if (i > maxApplyforEmployee) notSatisfiedForEmployee++;
        }

        solution.setConstraint(0, -notSatisfiedForCompany);
        solution.setConstraint(1, -notSatisfiedForEmployee);

        solution.setObjective(0, -totalSalaryAcquired); // tối ưu tổng tiền lương đạt được
        solution.setAttribute("employeeChoice", employeeChoice);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(this.numberOfVariables, this.numberOfObjectives, this.numberOfConstraints);
        solution.setVariable(0, new BinaryVariable(numEmployee * numCompany)); // population initialization, tạo ra mảng binary với chiều dài 10 phần tử
        return solution;                                                                         // quyết định có hay không vào công ty nào trong số 10 công ty

    }
}
