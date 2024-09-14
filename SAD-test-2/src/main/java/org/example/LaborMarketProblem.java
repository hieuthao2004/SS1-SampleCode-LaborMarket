package org.example;

import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.core.variable.BinaryVariable;

public class LaborMarketProblem extends AbstractProblem {

    private final int numEmployee ;
    private final int numCompany ;
    private final double[][] salary ;
    private final double[][] distant ;
    private final double[][] skill ;

    private final int maxEmployeeRequired = 2;
    //mỗi công ty chỉ tuyển tối đa 2 nhân viên
    private final int maxApplyforEmployee = 1;
    //mỗi nhân viên chỉ có thể làm ở 1 công ty ;

    public LaborMarketProblem(int numEmployee, int numCompany, double[][] salary, double[][] distant, double[][] skill) {
        super(1, 3);
        this.numEmployee = numEmployee;
        this.numCompany = numCompany ;
        this.salary = salary;
        this.distant = distant;
        this.skill = skill;
    }

    @Override
    public void evaluate(Solution solution) {
        //Giải mã các biến nhị phân thành các phân công
        int[] employeeToCompany = decodeBinaryVariable(solution);

        //khởi tạo các giá trị mục tiêu
        double totalSalary = 0;
        double totalDistance = 0;
        double totalSkill = 0;

        //đếm số nhân viên được phân công cho mỗi công ty
        int[] companyEmployeeCount = new int[numCompany];
        boolean[] employeeAssigned = new boolean[numEmployee];

        // kiểm tra constraint và tính toán mục tiêu
        boolean isFeasible = true; // kiểm tra xem giải pháp có thỏa mãn kh?
        for (int employee = 0; employee < numEmployee; employee++) {
            int company = employeeToCompany[employee];

            // check nếu nhân viên này đã được phân công cho một công ty
            if (employeeAssigned[employee]) {
                isFeasible = false; // nếu quá hơn 1 nvien sẽ trả về false
                break;
            }

            // đánh dấu nhân viên này là đã được phân công
            employeeAssigned[employee] = true;
            //đếm số nhân viên được phân công cho công ty này
            companyEmployeeCount[company]++;

            //check nếu công ty vượt quá số lượng nhân viên tối đa được phép
            if (companyEmployeeCount[company] > maxEmployeeRequired) {
                isFeasible = false; // nếu cty vượt số nvien cho phép
                break;
            }

            // Nếu tất cả ràng buộc được thỏa mãn, tính toán các mục tiêu
            totalSalary += salary[employee][company];
            totalDistance += distant[employee][company];
            totalSkill += skill[employee][company];
        }

        // Nếu giải pháp không thỏa mãn constraint, đặt giá trị kém nhất cho mục tiêu
        if (!isFeasible) {
            totalSalary = -1; // Hoặc một giá trị âm nào đó
            totalDistance = Double.MAX_VALUE; // Giá trị lớn nhất để loại bỏ giải pháp này
            totalSkill = -1; // Hoặc một giá trị âm nào đó
        }

        //set các mục tiêu trong đối tượng solution
        solution.setObjective(0, -totalSalary); // tối đa hóa mức lương (tối đa hóa -> giảm thiểu giá trị âm)
        solution.setObjective(1, totalDistance); // Giảm thiểu khoảng cách
        solution.setObjective(2, -totalSkill); // tối đa hóa kỹ năng (tối đa hóa -> giảm thiểu giá trị âm)
    }
    private int[] decodeBinaryVariable(Solution solution) {
        //phân công nvien cho cty
        int[] employeeCompany = new int[numEmployee];
        for (int employee = 0; employee < numEmployee; employee++) {
            //lấy binary của employee
            BinaryVariable binaryVariable = (BinaryVariable) solution.getVariable(employee);
            //duyệt qua các cty và tìm cty đầu tiên mà nv đc phân công
            for (int company = 0; company < numCompany; company++) {
                if (binaryVariable.get(company)) { // nếu gtri nhị phân là 1
                    employeeCompany[employee] = company; // gán nvien cho cty này
                    break; // nếu tìm thấy cty thì out khỏi vòng lặp
                }
            }
        }
        return employeeCompany;
    }

    @Override  // sử dụng BinaryVariable để khởi tạo quần thể
    public Solution newSolution() {
        Solution solution = new Solution(numEmployee, 3); // 3 objectives: salary, distance, skill
        int[] companyEmployeeCount = new int[numCompany];

        for (int i = 0; i < numEmployee; i++) {
            BinaryVariable binaryVariable = new BinaryVariable(numCompany);

            // Tìm một công ty vẫn có thể tuyển dụng (ít hơn maxEmployeeRequired)
            int companyIndex = -1;
            do {
                //ngẫu nhiên một công ty
                companyIndex = (int)(Math.random() * numCompany);
            } while (companyEmployeeCount[companyIndex] >= maxEmployeeRequired);

            for(int j=0; j<maxApplyforEmployee;j++){
                binaryVariable.set(companyIndex,true);
            }

            //Phân công nhân viên này cho công ty được chọn, tuân theo constraint maxApplyforEmployee = 1
            //đảm bảo mỗi nhân viên chỉ được phân công cho 1 công ty duy nhất
            binaryVariable.set(companyIndex, true);
            companyEmployeeCount[companyIndex]++;

            solution.setVariable(i, binaryVariable);
        }
        return solution;
    }
}
