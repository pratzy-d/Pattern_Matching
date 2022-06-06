package project_sa_skew;

import java.util.ArrayList;

/**
 * SuffixArray class implemented using Skew Algorithm by Karkainnen & Sanders
 * @author Prateekshya Mohanty
 */
public class SuffixArray {
  public ArrayList<Integer> construct(String S) {
    ArrayList<Integer> suffixList = new ArrayList<>();

    // Break the string into a Character Array
    char[] chars = S.toCharArray();

    // Let's rearrange S in such a way that
    // the lowest character in the S is given index = 1 and
    // accordingly other characters are given index value relative to the lowest character value
    // For e.g. MISSSISSIPPI - 5, 1, 11, 11, 1, 11, 11, 1, 8, 8, 1
    // I = 1 and M is given 5 and S is given 11 relative to I
    int[] newS = new int[chars.length];
    int min = 256;
    for (int j = 0; j< chars.length; j++) {
      newS[j] = chars[j];
      if (newS[j] < min)
        min = newS[j];
    }

    int max = 0;
    for (int j = 0; j< chars.length; j++) {
      newS[j] = chars[j] - min + 1; // Add 1 as we want to start the Index from 1 (instead of 0)
      if (newS[j] > max)
        max = newS[j];
    }
    // End of rearrangement of S by Index value
    //-----------------------------------------------------

    // Now we have a Char/Int array, use that to construct the Suffix Array
    // as the Skew Algorithm as discussed in the class notes
    int[] suffixArray = new int[chars.length];
    suffixArray = constructSuffixArray(newS, max);

    //convert Int array to ArrayList
    for (int ii : suffixArray) {
      suffixList.add(ii);
    }
    return suffixList;
  }

  /**
   * Construct the Suffix Array recursively as per the Skew Algorithm
   * @param sequence
   * @param K
   * @return Final and merged Suffix Array
   */
  public static int[] constructSuffixArray(int[] sequence, int K) {
    int n = sequence.length;

    int n0 = (int) Math.ceil(n / 3.0);
    int n1 = (int) Math.ceil((n - 1) / 3.0);
    int n2 = (int) Math.ceil((n - 2) / 3.0);

    // Append special characters to make the sequence mod 3 = 0
    int[] paddedSequence = null;
    paddedSequence = padWithSpecialChars(sequence);

    int tripletCount = n0 + n2; // for groups 1 and 2 as index started from 1
    int[] index = new int[tripletCount];
    for (int i = 0, j = 0; i < paddedSequence.length - 2; i++)
      if (i % 3 != 0) index[j++] = i;

    // ==================================================
    // lexicographically sort triplets
    // Sort the positions so that we get
    // I$$, IPP, ISS, ISS, PPI, SSI, SSI
    // Starting Index positions - 10, 7, 1, 4, 8, 2, 5
    // Text - M I S S I S S I P P I
    int[] sortedTriplets = new int[index.length];
    sortedTriplets = radixSort(paddedSequence, index, tripletCount, K, 2);
    sortedTriplets = radixSort(paddedSequence, sortedTriplets, tripletCount, K, 1);
    sortedTriplets = radixSort(paddedSequence, sortedTriplets, tripletCount, K, 0);

    int[] lexName = new int[tripletCount];
    lexName[0] = 1;
    boolean isUnique = nameTriplets(tripletCount, paddedSequence, sortedTriplets, lexName);

    // ==================================================
    // Arrange the triplet names in the right order
    // s = M I S S I S S I P P I $ $
    // For ISS | ISS | IPP | I $ $ | S S I | S S I | P P I
    //      3  |  3  |  2  |   1   |   5   |   5   |   4
    int[] lexNameSorted = new int[tripletCount];
    lexNameSorted = arrangeTripletNames(n0, tripletCount, sortedTriplets, lexName);

    // -------------------------------------------------------------------
    // Recursively Compute the Suffix Array for tokenized string
    // -------------------------------------------------------------------
    int[] suffixArray = new int[tripletCount];

    if (!isUnique) {
      int maximum = 0;
      for (int i = 0; i < lexNameSorted.length; i++) {
        if (lexNameSorted[i] > maximum)
          maximum = lexNameSorted[i];
      }
      // Call constructSuffixArray recursively for tokenized string
      suffixArray = constructSuffixArray(lexNameSorted, maximum);
    }
    else {
      for (int i = 0; i < tripletCount; i++) {
        suffixArray[lexNameSorted[i] - 1] = i;
      }
    }

    // Merge Group 1 and Group 2
    int[] Group12 = new int[tripletCount];
    mergeGroup12(n0, tripletCount, suffixArray, Group12);

    // derive A0
    int[] Group0 = new int[n0];
    deriveGroup0(tripletCount, Group12, Group0);
    Group0 = radixSort(paddedSequence, Group0, n0, K, 0);

    // merge A12 and A0 into suffix array finalSuffixArray
    int[] finalSuffixArray = new int[n0 + tripletCount - (n0 - n1)];
    mergeAll(n, tripletCount, paddedSequence, Group12, Group0, finalSuffixArray);

    return finalSuffixArray;
  }

  /**
   * If the String is not a multiple of 3, pad with a special character to make it a multiple of 3
   * @param s - sequence
   * @return Padded Sequence
   */
  public static int[] padWithSpecialChars(int[] s) {
    int[] s2 = null;
    int n = s.length;

    // Extend the array and add zeros at the end
    if (n % 3 == 0 || n % 3 == 2) {
      s2 = new int[n + 2];
      s2[n] = s2[n + 1] = 0;
    }
    if (n % 3 == 1) {
      s2 = new int[n + 3];
      s2[n] = s2[n + 1] = s2[n + 2] = 0;
    }

    // Copy original content to the new array
    for (int i = 0; i < n; i++)
      s2[i] = s[i];

    return s2;
  }

  /**
   * Radix Sort - O(n) time sort for n items when items can be divided into constant number of digits
   * Put into buckets based on least-significant digit, flatten, repeat with next-most significant
   * digit
   * @param s2
   * @param index
   * @param tripletCount
   * @param K
   * @param offset
   * @return Sorted Triplets
   */
  public static int[] radixSort(int[] s2, int[] index, int tripletCount, int K, int offset) {
    int[] c = new int[K+1];
    int[] result = new int[tripletCount];

    for (int i = 0; i < tripletCount; i++)
      c[s2[index[i] + offset]]++;

    for (int i = 1; i <= K; i++)
      c[i] += c[i - 1];

    for (int i = tripletCount - 1; i >= 0; i--)
      result[--c[s2[index[i] + offset]]] = index[i];

    return result;
  }

  /**
   * Assign Lex to the sorted Triplets. We can assign A B C D ... or 1 2 3 4 ...
   * 1 2 3 4 .... are assigned for better handling in the code
   * @param tripletCount
   * @param paddedText
   * @param sortedTriplets
   * @param lexName
   * @return Returns TRUE if there are unique triplets else FALSE
   */
  public static boolean nameTriplets(int tripletCount, int[] paddedText, int[] sortedTriplets, int[] lexName) {
    // For naming, I am using 1 2 3 4 .... instead of A B C
    // For ISS | ISS | IPP | I $ $ | S S I | S S I | P P I
    //      3  |  3  |  2  |   1   |   5   |   5   |   4
    // As the input is a Sorted Triplets, hence I am expecting the following as an input
    // I $ $ | I P P | I S S | I S S | P P I | S S I | S S I
    int name = 1;
    boolean isUnique = true;

    for (int i = 1; i < tripletCount; i++) {
      if ((paddedText[sortedTriplets[i]] == paddedText[sortedTriplets[i-1]]) &&
              (paddedText[sortedTriplets[i]+1] == paddedText[sortedTriplets[i-1]+1]) &&
              (paddedText[sortedTriplets[i]+2] == paddedText[sortedTriplets[i-1]+2]))
        isUnique = false;
      else
        name++;

      // Assign Lex
      lexName[i] = name;
    }
    return isUnique;
  }

  /**
   * Arrange Triplets in Lexicographical Order
   * @param n0
   * @param tripletCount
   * @param sortedTriplets
   * @param lexName
   * @return Arranged Triplets
   */
  public static int[] arrangeTripletNames(int n0, int tripletCount, int[] sortedTriplets, int[] lexName) {
    int[] lexNameSorted = new int[tripletCount];

    for (int i = 0; i < tripletCount; i++) {
      if (sortedTriplets[i] % 3 == 1) {
        lexNameSorted[(sortedTriplets[i]-1)/3] = lexName[i];
      }
      if (sortedTriplets[i] % 3 == 2) {
        lexNameSorted[(sortedTriplets[i]-2)/3 + n0] = lexName[i];
      }
    }
    return lexNameSorted;
  }

  /**
   * Merge Group 1 and 2 triplets
   * @param n0
   * @param tripletCount
   * @param SA
   * @param Group12
   */
  public static void mergeGroup12(int n0, int tripletCount, int[] SA, int[] Group12) {
    for (int i = 0; i < tripletCount; i++) {
      if (SA[i] < n0)
        Group12[i] = 1 + 3 * SA[i];
      else
        Group12[i] = 2 + 3 * (SA[i] - n0);
    }
  }

  /**
   * Derive Group0
   * @param tripletCount
   * @param Group12
   * @param A0
   */
  public static void deriveGroup0(int tripletCount, int[] Group12, int[] A0) {
    for (int i = 0, j = 0; i < tripletCount; i++)
      if (Group12[i] % 3 == 1) {
        A0[j++] = Group12[i] - 1;
      }
  }

  /**
   * Overloaded Method - Compare values
   */
  public static boolean compare(int i, int j, int k, int l) {
    return (i < j || i == j && k < l);
  }

  /**
   * Overloaded Method - Compare values
   */
  public static boolean compare(int i, int j, int k, int l, int m, int n) {
    return compare(i, j, k, l) || (i == j && k == l && m < n);
  }

  /**
   * Merge Group12 and Group0 Arrays
   * @param n
   * @param tripletCount
   * @param s2
   * @param A12
   * @param A0
   * @param mergedSA
   */
  public static void mergeAll(int n, int tripletCount, int[] s2, int[] A12, int[] A0, int[] mergedSA) {
    int[] R12 = new int[s2.length];
    int n0 = (int) Math.ceil(n / 3.0);

    int index0 = 0;
    int index12 = 0;
    int m = 0;

    // if mod of total characters is 1
    if (n % 3 == 1)
      index12 = 1;
    boolean end = false;

    for (int i = 0; i < tripletCount; i++)
      R12[A12[i]] = i + 1;

    // Loop through all characters
    for (int i = 0; i < tripletCount + n0; i++) {
      // if it is same as Group0 triples
      if (index0 == n0) {
        while (index12 < tripletCount) {
          mergedSA[m++] = A12[index12];
          index12++;
        }
        end = true;
      }
      if (end) break;

      // if it is same as total number of triplets
      if (index12 == tripletCount) {
        while (index0 < n0) {
          mergedSA[m++] = A0[index0];
          index0++;
        }
        end = true;
      }
      if (end) break;

      if (A12[index12] % 3 == 1) {
        if (compare(s2[A0[index0]], s2[A12[index12]], R12[A0[index0] + 1], R12[A12[index12] + 1])) {
          mergedSA[m++] = A0[index0];
          index0++;
        }
        else {
          mergedSA[m++] = A12[index12];
          index12++;
        }
      }
      else {
        if (compare(s2[A0[index0]], s2[A12[index12]], s2[A0[index0] + 1], s2[A12[index12] + 1], R12[A0[index0] + 2], R12[A12[index12] + 2])) {
          mergedSA[m++] = A0[index0];
          index0++;
        }
        else {
          mergedSA[m++] = A12[index12];
          index12++;
        }
      }
    }
  }
}
