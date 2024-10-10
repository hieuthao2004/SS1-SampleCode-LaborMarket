package org.example.double_stable_matching;

public class Dataset {
    public static final int NUM_PERSONS = 5;
    public static final String[] COMPANIES = {"Microsoft", "Amazon", "Google", "Tesla", "Apple"};
    public static final String[] EMPLOYEES = {"Alice", "Bob", "Charlie", "David", "Eva"};

    // Ưu tiên của nhân viên đối với công ty
    public static final int[][] PREFERENCES_EMPLOYEE = {
        {2, 1, 4, 0, 3},  // Alice: Google, Amazon, Apple, Microsoft, Tesla
        {0, 2, 3, 1, 4},  // Bob: Microsoft, Google, Tesla, Amazon, Apple
        {3, 4, 0, 1, 2},  // Charlie: Tesla, Apple, Microsoft, Amazon, Google
        {1, 0, 2, 3, 4},  // David: Amazon, Microsoft, Google, Tesla, Apple
        {4, 3, 2, 0, 1}   // Eva: Apple, Tesla, Google, Microsoft, Amazon
    };

    // Ưu tiên của công ty đối với nhân viên
    public static final int[][] PREFERENCES_COMPANY = {
        {1, 2, 0, 3, 4},  // Microsoft: Bob, Charlie, Alice, David, Eva
        {4, 0, 3, 2, 1},  // Amazon: Eva, Alice, David, Charlie, Bob
        {2, 1, 4, 3, 0},  // Google: Charlie, Bob, Eva, David, Alice
        {0, 3, 4, 2, 1},  // Tesla: Alice, David, Eva, Charlie, Bob
        {3, 4, 1, 0, 2}   // Apple: David, Eva, Bob, Alice, Charlie
    };
}
