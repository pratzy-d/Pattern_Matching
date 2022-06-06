package project_dp;

public class DynamicProgramming {
    public String find(String A, String B) {
        int m = A.length();
        int n = B.length();

        // create a matrix which act as a table for LCS
        int[][] lcsTable = new int[m + 1][n + 1];

        // fill the table in the bottom up way
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                // First Row and Column - values are ZERO
                if (i == 0 || j == 0)
                    lcsTable[i][j] = 0;
                // if both chars are same, take the diagonal cell and add 1
                else if (A.charAt(i - 1) == B.charAt(j - 1))
                    lcsTable[i][j] = lcsTable[i - 1][j - 1] + 1;
                //find the maximum value from the cell of the previous column+current column AND current row+previous column
                else
                    lcsTable[i][j] = Math.max(lcsTable[i - 1][j], lcsTable[i][j - 1]);
            }
        }

        int index = lcsTable[m][n];
        int temp = index;

        char[] longestCommonSubsequence = new char[index + 1];
        longestCommonSubsequence[index] = '\0';

        int i = m, j = n;
        String lcs = "";
        while (i > 0 && j > 0) {
            if (A.charAt(i - 1) == B.charAt(j - 1)) {

                longestCommonSubsequence[index - 1] = A.charAt(i - 1);
                i--;
                j--;
                index--;
            } else if (lcsTable[i - 1][j] > lcsTable[i][j - 1])
                i--;
            else
                j--;
        }

        for (int k = 0; k <= temp; k++)
            lcs = lcs + longestCommonSubsequence[k];

        return lcs;
    }
}