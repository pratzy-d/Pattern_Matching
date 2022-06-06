package project_rk;

import java.math.BigInteger;
import java.util.Random;

public class RK {

  // R is the number of characters in the input alphabet
  public final static int R = 256;

  /**
   * Matches a Pattern P in a Text T and returns the position where it is found using Rabin-Karp algorithm
   * @author Prateekshya Mohanty
   * @param T Text in which the pattern to be searched
   * @param P Pattern to search for
   * @return position where the Pattern is found
   */
  public int match(String T, String P) {
    int M = P.length();
    int N = T.length();

    if (N < M)
      return -1;

    ////////////////////////////////////////////////////////////////////////////////
    //Get a random prime number
    ////////////////////////////////////////////////////////////////////////////////
    long Q = generateRandomPrimeNumber();

    int i, j;
    long patternHash = 0; // hash value for pattern
    long textHash = 0; // hash value for txt

    // R^(M-1) % Q
    long RM = 1;

    // precompute R^(M-1) % Q for use in removing leading digit
    for (i = 0; i < M - 1; i++)
      RM = (RM * R) % Q;

    ////////////////////////////////////////////////////////////////////////////////
    // Calculate the hash value of pattern P and text T
    ////////////////////////////////////////////////////////////////////////////////
    for (i = 0; i < M; i++) {
      patternHash = (R * patternHash + P.charAt(i)) % Q;
      textHash = (R * textHash + T.charAt(i)) % Q;
    }

    // Slide the pattern over text one by one
    for (i = 0; i <= N - M; i++) {

      // Check the hash values of current window of Text T and Pattern P
      // If the hash values match then only check for characters one by one
      if (patternHash == textHash) {
        // Check characters one by one
        for (j = 0; j < M; j++) {
          if (T.charAt(i + j) != P.charAt(j))
            break;
        }

        // if hash values match AND P[0...M-1] = T[0+i, 1+i, ...i+M-1] match then return the i th position
        if (j == M)
          return i;
      }

      // Calculate hash value for next window of text
      // Remove leading digit, add trailing digit
      if (i < N - M) {

        textHash = (R * (textHash - T.charAt(i) * RM) + T.charAt(M + i)) % Q;

        // if textHash is negative then covert to positive value
        if (textHash < 0)
          textHash = (textHash + Q);
      }
    }

    return -1;
  }

  /**
   * Returns a random prime number
   * @return prime number
   */
  private static long generateRandomPrimeNumber()
  {
    BigInteger prime = BigInteger.probablePrime(31, new Random());
    return prime.longValue();
  }
}