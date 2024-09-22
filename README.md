# SS1-SampleCode-LaborDistribution
**Author**: Nguyen Trong Hieu, Dam Thanh Thuy, Nguyen Kien, Ngo Minh Duc
### Contribution ###
- **Ngo Minh Duc**:       Add attributes
- **Dam Thanh Thuy**:     Add LaborMarketProblem class
- **Nguyen Trong Hieu**:  Add LMPMain class
- **Nguyen Kien**:        Presenter
### Problem ###

**Description:**
You are tasked with matching a group of employees with a group of companies. Each side has a list of preferences, and the goal is to find a stable matching that ensures no one would prefer to be paired with someone else over their current match.

**Data:**
- **Number of Employees:** 5
- **Number of Companies:** 5

**Attributes:**

1. **Salaries (`salary`):**
   - Represents the salary offered by each company to each employee (in USD).

2. **Distances (`distant`):**
   - Represents the distance from each employee’s home to each company (in kilometers).

3. **Skills (`skill`):**
   - Represents the skill match between each employee and each company. Values range from 0 to 1, where 1 indicates a perfect skill match.

**Objective:**

1. **Tối đa hóa độ hài lòng**: Điều này có thể bao gồm việc tối đa hóa tổng độ hài lòng của cả nhân viên và công ty. Độ hài lòng của nhân viên có thể được đo bằng cách so sánh mức lương mà họ nhận được với yêu cầu lương của công ty, trong khi độ hài lòng của công ty có thể được đánh giá dựa trên kỹ năng của nhân viên so với yêu cầu kỹ năng.

2. **Tuân thủ các ràng buộc**: Đảm bảo rằng số lượng nhân viên được tuyển dụng bởi mỗi công ty không vượt quá số lượng tối đa cho phép, và mỗi nhân viên không nộp đơn vào quá số công ty cho phép.

3. **Tạo ra nhiều cặp match khác nhau**: Trả về một tập hợp các cặp match giữa nhân viên và công ty, từ đó cho phép đa dạng hóa các kết quả.