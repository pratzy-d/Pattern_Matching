package project_hb;

/**
 * Hirschberg’s algorithm - a way of computing the Longest Common Subsequences(LCS) in linear space.
 * Dan Hirschberg is a faculty member in the Department of Computer Science at University of California, Irvine.
 * I have followed the algorithm as discussed in the class and class notes
 *
 * @author Prateekshya Mohanty
 */
public class Hirschberg {
  /**
   * This method calls algC that internally calls algB
   * @param A
   * @param B
   * @return
   */
  public String find(String A, String B) {
    return algorithmC(A.length(), B.length(), A, B);
  }

  /**
   * Implementation of Idea#1: (Slide# 15 in the class notes)
   * When we are computing the L table, we only need to store the previous row and the current row.
   * If we just want to know the last row of the LCS (e.g., to compute the length of the LCS),
   * this is enough to achieve linear space.
   * @param m
   * @param n
   * @param A
   * @param B
   * @return The following algorithm gives the last line of the L table:
   */
  public int[] algorithmB(int m, int n, String A, String B) {
    // create 2 x n matrix
    int[][] L = new int[2][n + 1];

    // Initialization of L array
    for( int j=0; j<=n; j++) {
      L[1][j] = 0;
    }

    // fill the table in the bottom up way
    for (int i = 1; i <= m; i++) {

      for(int j=0; j<=n; j++) {
        L[0][j] = L[1][j];
      }

      for (int j = 1; j <= n; j++) {
        if (A.charAt(i-1) == B.charAt(j-1))
          L[1][j] = L[0][j - 1] + 1;
        else
          //find the maximum value from the cell of the 2nd row + previous column AND 1st row + current column
          L[1][j] = Math.max(L[1][j-1], L[0][j]);
      }
    }

    // Return last row
    return L[1];
  }

  /**
   * Idea#2 : Divide and Conquer Algorithm (Slide#19 as per the Notes)
   * For i=m/2, find the place, k, where the LCS path crosses the middle column using Algorithm B.
   * Then recursively find the LCS for A[1,i] and B[1,k] and the LCS for A[i+1,m] and B[k+1,n].
   * @param m
   * @param n
   * @param A
   * @param B
   * @return
   */
  public String algorithmC(int m, int n, String A, String B) {
    int i=0;
    int j=0;
    String C = "";

    if(n == 0) { // For trivial problems initialize to empty string if the length is zero.
      C = "";
    }
    else if(m == 1) {
      C = "";

      for(j = 0; j < n; j++) { // Iterate through n if m == 1
        if(A.charAt(0) == B.charAt(j)) {
          C = "" + A.charAt(0);
          break;
        }
      }
    }
    else { // Split problem
      // Take the middle column
      i= (int) Math.floor(((double)m)/2);

      // Evaluation of L1 and L2
      int[] L1 = algorithmB(i, n, A.substring(0,i), B);
      int[] L2 = algorithmB(m-i, n, reverseString(A.substring(i)), reverseString(B));

      // Find the (k)th row
      int k = findK(L1, L2, n);

      // Find the sequences match through recursive calls
      String C1 = algorithmC(i, k, A.substring(0, i), B.substring(0, k));
      String C2 = algorithmC(m-i, n-k, A.substring(i), B.substring(k));

      // Concatenation of two strings
      C = C1 + C2;
    }

    // Return the LCS
    return C;
  }

  /**
   * Reverses the Input String passed from Algorithm C
   * @param text
   * @return reverse text
   */
  public String reverseString(String text) {
    String reversedString = "";

    for(int i = text.length()-1; i >= 0; i--) {
      reversedString = reversedString + text.charAt(i);
    }

    return reversedString;
  }

  /**
   * Find (k)th row = minimum j such that L1(j) + L2 (n-j) = M
   * @param L1
   * @param L2
   * @param n
   * @return minimum j as k
   */
  public int findK(int[] L1, int[] L2, int n) {
    int M = 0;
    int k = 0;

    for(int j = 0; j <= n; j++) {
      if(M < (L1[j] + L2[n-j])) {
        M = L1[j] + L2[n-j];
        k = j;
      }
    }

    return k;
  }
}