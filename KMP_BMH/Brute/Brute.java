package project_brute;

public class Brute {

  public int match(String T, String P) {
    int i,j;
    int n = T.length();
    int m = P.length();

    // Minimum Length Error Check
    if(m == 0 || n == 0 || n < m) return -1;

    // Loop through entire Text
    for (i = 0; i <= n - m; i++){
      // Reset j after each loop
      j = 0;
      // Move the j pointer if there is character match
      while ((j < m) && (T.charAt(i+j) == P.charAt(j))) {
        j++;
      }
      // Return the i position if there is a full match
      if (j == m)
        return i;
    }
    return -1;
  }
}
