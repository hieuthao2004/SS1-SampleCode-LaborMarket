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
The objective is to find a stable matching between employees and companies that optimizes the following goal:

1. **Maximize the Total Salary:**
   - Increase the overall salary received by all employees.