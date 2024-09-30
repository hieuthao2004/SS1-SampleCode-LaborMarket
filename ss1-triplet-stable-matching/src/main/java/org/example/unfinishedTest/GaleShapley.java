package org.example.unfinishedTest;

import java.util.Arrays;

public class GaleShapley {
    private final String[] employeeNames;
    private final String[] companyNames;
    private final int[][] employeePreferences;
    private final int[][] companyPreferences;

    public GaleShapley(String[] employeeNames, String[] companyNames, String[][] employeePref, String[][] companyPref) {
        this.employeeNames = employeeNames;
        this.companyNames = companyNames;
        this.employeePreferences = convertPreferences(employeePref);
        this.companyPreferences = convertPreferences(companyPref);
    }

    public String[][] calcMatches() {
        int numEmployees = employeeNames.length;
        int numCompanies = companyNames.length;

        int[] companyMatch = new int[numCompanies];
        boolean[] companyOccupied = new boolean[numCompanies];
        Arrays.fill(companyMatch, -1);

        int[] employeeNextChoice = new int[numEmployees];
        boolean[] employeeMatched = new boolean[numEmployees];

        boolean freeEmployeeExists = true;
        while (freeEmployeeExists) {
            freeEmployeeExists = false;
            for (int i = 0; i < numEmployees; i++) {
                if (!employeeMatched[i]) {
                    freeEmployeeExists = true;

                    int companyIndex = employeePreferences[i][employeeNextChoice[i]];

                    if (!companyOccupied[companyIndex]) {
                        companyMatch[companyIndex] = i;
                        companyOccupied[companyIndex] = true;
                        employeeMatched[i] = true;
                    } else {
                        int currentEmployee = companyMatch[companyIndex];
                        if (prefers(companyIndex, i, currentEmployee)) {
                            companyMatch[companyIndex] = i;
                            employeeMatched[i] = true;
                            employeeMatched[currentEmployee] = false;
                        }
                    }
                    employeeNextChoice[i]++;
                }
            }
        }

        String[][] matches = new String[numCompanies][2];
        int matchIndex = 0;
        for (int companyIndex = 0; companyIndex < numCompanies; companyIndex++) {
            if (companyMatch[companyIndex] != -1) {
                int employeeIndex = companyMatch[companyIndex];
                matches[matchIndex][0] = employeeNames[employeeIndex];
                matches[matchIndex][1] = companyNames[companyIndex];
                matchIndex++;
            }
        }

        return Arrays.copyOf(matches, matchIndex);
    }

    private boolean prefers(int companyIndex, int newEmployeeIndex, int currentEmployeeIndex) {
        for (int preference : companyPreferences[companyIndex]) {
            if (preference == newEmployeeIndex) {
                return true;
            }
            if (preference == currentEmployeeIndex) {
                return false;
            }
        }
        return false;
    }

    private int[][] convertPreferences(String[][] preferences) {
        int[][] converted = new int[preferences.length][preferences[0].length];
        for (int i = 0; i < preferences.length; i++) {
            for (int j = 0; j < preferences[i].length; j++) {
                converted[i][j] = Arrays.asList(companyNames).indexOf(preferences[i][j]);
            }
        }
        return converted;
    }
}
