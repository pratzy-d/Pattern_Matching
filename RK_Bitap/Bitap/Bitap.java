package project_bitap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Bitap {
  /**
   * Matches a Pattern P in a Text T and returns the position where it is found using Bitap algorithm
   * @author Prateekshya Mohanty
   * @param T Text in which the pattern to be searched
   * @param P Pattern to search for
   * @return position where the Pattern is found
   */
  public int match(String T, String P) {

    // Convert the String T(Text) and P (Pattern) to Character Array for easy indexing
    char[] text = T.toCharArray();
    char[] pattern = P.toCharArray();

    int m = pattern.length;
    int n = text.length;

    // Array of pattern_mask of all character values in it
    long pattern_mask[] = new long[Character.MAX_VALUE + 1];

    // Initialize the Bit Array complemented with 1;
    long R = ~1;

    // Return -1 if the pattern length is zero or greater than the length of character array
    if (m == 0 || m > 63)
      return -1;

    // Pattern mask array is complemented with zero
    for (int i = 0; i <= Character.MAX_VALUE; ++i)
      pattern_mask[i] = ~0;

    // 1L means the long integer is shifted to left by i times
    for (int i = 0; i < m; ++i)
      pattern_mask[pattern[i]] &= ~(1L << i);

    // The R array is used as an OR function with pattern_mask at index of Text of i
    for (int i = 0; i < n; ++i) {
      R |= pattern_mask[text[i]];

      // we shift it to left side once
      R <<= 1;

      // If the 1L long integer if shifted left of the m AND R array
      // If that result is equal to 0 then we return the index value as i - m + 1
      if ((R & (1L << m)) == 0)
        return i - m + 1;
    }

    // if no match then we return it as -1
    return -1;
  }
}
