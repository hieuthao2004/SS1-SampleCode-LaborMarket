package org.example.triplet_stable_matching_test;

public class Dataset {
    public static final int NUM_PERSONS = 5;
    public static final String[] COMPANIES = {"Microsoft", "Amazon", "Google", "Tesla", "Apple"};
    public static final String[] EMPLOYEES = {"Alice", "Bob", "Charlie", "David", "Eva"};
    public static final String[] DEPARTMENTS = {"Engineering", "Marketing", "Finance", "HR", "IT"};

    // Ưu tiên của nhân viên cho công ty và ban ngành
    public static final int[][][] PREFERENCES_EMPLOYEE = {
        { {2, 1, 4, 0, 3}, {1, 3, 0, 2, 4} },  // Alice: Google, Amazon, Apple, Microsoft, Tesla | Engineering, HR, Marketing, Finance, IT
        { {0, 2, 3, 1, 4}, {0, 1, 4, 2, 3} },  // Bob: Microsoft, Google, Tesla, Amazon, Apple | Engineering, Marketing, IT, Finance, HR
        { {3, 4, 0, 1, 2}, {3, 0, 1, 4, 2} },  // Charlie: Tesla, Apple, Microsoft, Amazon, Google | HR, Engineering, Marketing, IT, Finance
        { {1, 0, 2, 3, 4}, {4, 3, 2, 1, 0} },  // David: Amazon, Microsoft, Google, Tesla, Apple | IT, HR, Finance, Marketing, Engineering
        { {4, 3, 2, 0, 1}, {2, 1, 0, 4, 3} }   // Eva: Apple, Tesla, Google, Microsoft, Amazon | Finance, Marketing, Engineering, IT, HR
    };

    // Ưu tiên của công ty cho nhân viên và ban ngành
    public static final int[][][] PREFERENCES_COMPANY = {
        { {1, 2, 0, 3, 4}, {0, 1, 2, 3, 4} },  // Microsoft: Bob, Charlie, Alice, David, Eva | Engineering, Marketing, Finance, HR, IT
        { {4, 0, 3, 2, 1}, {4, 0, 1, 2, 3} },  // Amazon: Eva, Alice, David, Charlie, Bob | IT, Engineering, Marketing, Finance, HR
        { {2, 1, 4, 3, 0}, {3, 4, 1, 2, 0} },  // Google: Charlie, Bob, Eva, David, Alice | HR, IT, Marketing, Finance, Engineering
        { {0, 3, 4, 2, 1}, {2, 3, 4, 0, 1} },  // Tesla: Alice, David, Eva, Charlie, Bob | Finance, HR, IT, Engineering, Marketing
        { {3, 4, 1, 0, 2}, {1, 0, 3, 4, 2} }   // Apple: David, Eva, Bob, Alice, Charlie | Marketing, Engineering, HR, IT, Finance
    };

    // Ưu tiên của ban ngành cho nhân viên và công ty
    public static final int[][][] PREFERENCES_DEPARTMENT = {
        { {0, 2, 3, 1, 4}, {4, 3, 0, 1, 2} },  // Engineering: Alice, Charlie, David, Bob, Eva | Apple, Tesla, Microsoft, Amazon, Google
        { {4, 1, 0, 3, 2}, {2, 0, 4, 1, 3} },  // Marketing: Eva, Bob, Alice, David, Charlie | Google, Microsoft, Apple, Amazon, Tesla
        { {3, 2, 1, 0, 4}, {1, 4, 3, 0, 2} },  // Finance: David, Charlie, Bob, Alice, Eva | Amazon, Apple, Tesla, Microsoft, Google
        { {2, 1, 4, 3, 0}, {3, 0, 4, 1, 2} },  // HR: Charlie, Bob, Eva, David, Alice | Tesla, Microsoft, Apple, Amazon, Google
        { {1, 0, 2, 4, 3}, {0, 1, 2, 3, 4} }   // IT: Bob, Alice, Charlie, Eva, David | Microsoft, Amazon, Google, Apple, Tesla
    };
}
