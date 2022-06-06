package project_kmp;

public class KMP {
  
  public int match(String T, String P) {
    int m = P.length();
    int n = T.length();

    // Minimum Length Error Check
    if(m == 0 || n == 0 || n < m) return -1;

    int lps[] = new int[m];
    int i = 0; // for traversing through text T
    int j = 0; // for traversing through pattern P

    // Preprocess the pattern to find matches of prefixes of the pattern
    // with the pattern itself
    calculateLPSArray(P, m, lps);

    while (i < n) {
      // If the character in the Text matches with the character in the Pattern
      if (T.charAt(i) == P.charAt(j)) {
        // j = m-1 means the pattern is found in the text
        // So, return the position where it is found i.e. i - j
        if (j == m - 1) {
          return (i - j);
        }
        else { // increment both the pointers
          i++;
          j++;
        }
      }
      else { // if there is mismatch between Text and Pattern
        if (j != 0)
          // j-1 = last successful match
          // new j = corresponding index in the LPS table for the successful match
          j = lps[j - 1];
        else
          i++;
      }
    }
    return -1;
  }

  // LPS - Longest Prefix that is also a Suffix
  // The calculateLPSArray lps(len) is defined as the length
  // of the longest prefix of P[0..len] that is also a suffix of P[1..len]
  void calculateLPSArray(String P, int m, int lps[])
  {
    int len = 0; // length of the previous longest prefix suffix
    int i   = 1; // start from 2nd index
    lps[0]  = 0; // lps[0] is always 0

    while (i < m){
      if (P.charAt(i) == P.charAt(len)) { // if there is a match
        // increment the len pointer and store in the lps at i th position
        lps[i] = len + 1;
        // increment both the pointers as there is a match
        i++;
        len++;
      }
      else { // if there is mismatch
        // if length is not equal to ZERO
        if (len != 0) {
          // new len = the previous length
          len = lps[len - 1];
        }
        else {
          lps[i] = 0;
          i++; // just increment i
        }
      }
    }
  }
}