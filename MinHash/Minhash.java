package project_mh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.CRC32;

/**
 * Find the similarity between two documents based on MinHash algorithm
 * @author Prateekshya Mohanty
 */
public class Minhash {

  private static final int LARGE_PRIME = 2147483647; // = 2^31 - 1 !
  private int NO_OF_PERMUTATIONS = 10; // actual value is calculated in generateCoEffs() based on Error %
  private int[][] hashCoeffs;

  /**
   * Calculate probable Jaccard similarity based on MinHash comparison
   * @param fA File A
   * @param fB File B
   * @return
   */
  public double jaccard(String fA, String fB) {

    //----------------------------------------------------------------------------------------//
    //  Generate Shingles of 1-tuple and corresponding 32-bit Hash Set for File-A and File-B  //
    //----------------------------------------------------------------------------------------//
    System.out.println("Generating Shingles for Set A");
    Set<Integer> shingle32BitHashSetA = generateShingle32BitHashSet(fA);
    if (shingle32BitHashSetA == null) {
      System.out.println("***Error*** Generating Shingles for Set A");
      return -1;
    }
    else
      System.out.println("Shingles for Set A::" +  shingle32BitHashSetA.size());

    System.out.println("Generating Shingles for Set B");
    Set<Integer> shingle32BitHashSetB = generateShingle32BitHashSet(fB);
    if (shingle32BitHashSetB == null) {
      System.out.println("***Error*** Generating Shingles for Set B");
      return -1;
    }
    else
      System.out.println("Shingles for Set B::" +  shingle32BitHashSetB.size());

    //----------------------------------------------------------------------------------------//
    //              Find Jaccard Estimated Similarity based on MinHash Signatures             //
    //----------------------------------------------------------------------------------------//
    System.out.println("Generating Coeffcients"); // for debugging purpose
    generateCoEffs(0.05);

    int[] sig1 = generateMinHashSignature(shingle32BitHashSetA);
    int[] sig2 = generateMinHashSignature(shingle32BitHashSetB);

    System.out.println("Finding Signature Similarity"); // for debugging purpose
    double match = minHashSignatureSimilarity(sig1, sig2);

    //----------------------------------------------------------------------------------------//
    //                    JUST FOR COMPARISON - Jaccard Exact Similarity                      //
    //----------------------------------------------------------------------------------------//
    // To find union
    Set<Integer> union = new HashSet<Integer>(shingle32BitHashSetA);
    union.addAll(shingle32BitHashSetB);

    // To find intersection
    Set<Integer> intersection = new HashSet<Integer>(shingle32BitHashSetA);
    intersection.retainAll(shingle32BitHashSetB);

    double match_2 = (double) intersection.size() / union.size();
    System.out.println("Exact Jaccard Similarity:: " + match_2);

    return match;
  }

  /**
   * 1) Convert the document to a Set of Shingles
   *    NOTE: I am using 1-tuple of adjacent words as Shingle (because I am using MinHash Algorithm)
   * 2) Convert the Shingles to 32-bit hashes for easy sorting and comparison
   * @param fileName
   */
  private Set<Integer> generateShingle32BitHashSet(String fileName) {

    Path p = Paths.get(fileName);
    List<String> words = new ArrayList<>();

    // ------------------------------------------------------------ //
    //  Read all words from the file
    // ------------------------------------------------------------ //
    try {
      words = Files.readAllLines(p);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return null;
    }

    String shingle;
    Set<Integer> shinglesSet = new TreeSet<>();

    // For each word in the document, generate 32-bit hash
    for (int i=0; i < words.size(); i++) {
      // shingle = words.get(i) + " " + words.get(i+1) + " " + words.get(i+2);
      shingle = words.get(i);  // as focus is on MinHash algorithm, taking one word as shingle

      // Hash the Shingle to a 32-bit hash
      CRC32 crc = new CRC32();
      crc.update(shingle.getBytes());
      Integer shingleCRC = (int) crc.getValue() & 0xffffffff;
      shinglesSet.add(shingleCRC);
    }

    return shinglesSet;
  }

  /**
   * Generates MinHash Signature for all shingles
   * @return
   */
  public final int[] generateMinHashSignature(Set<Integer> shingleSet) {
    int[] sig = new int[NO_OF_PERMUTATIONS];

    // Initialize to Integer.MAX_VALUE
    for (int i = 0; i < NO_OF_PERMUTATIONS; i++) {
      sig[i] = Integer.MAX_VALUE;
    }

    // Assign min of Hash Codes for all shingles
    for (final int shingle : shingleSet) {
      for (int i = 0; i < NO_OF_PERMUTATIONS; i++) {
        // Take the minimum hash value
        sig[i] = Math.min(sig[i], hashFunction(i, shingle));
      }
    }

    return sig;
  }

  /**
   * Compute hash function coefficients
   * @param error percentage of error ( I am using 0.1 as error )
   */
  public void generateCoEffs(final double error) {
    Random r = new Random();
    // Calculate number of permutations based on error %
    this.NO_OF_PERMUTATIONS = (int) (1 / (error * error));

    // Hash Function h = (a * x) + b
    // a and b should be randomly generated in [1,PRIME-1]
    hashCoeffs = new int[NO_OF_PERMUTATIONS][2];
    for (int i = 0; i < NO_OF_PERMUTATIONS; i++) {
      hashCoeffs[i][0] = r.nextInt(LARGE_PRIME - 1) + 1; // a
      hashCoeffs[i][1] = r.nextInt(LARGE_PRIME - 1) + 1; // b
    }
  }

  /**
   * Computes hash function as (a * x + b) % LARGE_PRIME
   * hashCoeffs[i][0] = a
   * hashCoeffs[i][1] = b
   * @param i
   * @param x
   * @return Returns the hashed value of rawValue
   */
  private int hashFunction(final int i, final int x) {
    int hashCode = ((hashCoeffs[i][0] * x + hashCoeffs[i][1])  % LARGE_PRIME);
    return hashCode;
  }

  /**
   * Computes an estimation of Jaccard similarity (the number of elements in
   * common) between two sets, using the MinHash signatures of these two sets.
   *
   * @param sig1 MinHash signature of set1
   * @param sig2 MinHash signature of set2
   * @return the estimated similarity
   */
  public final double minHashSignatureSimilarity(final int[] sig1, final int[] sig2) {
    if (sig1.length != sig2.length) {
      throw new IllegalArgumentException("Size of signatures should be the same.");
    }

    double similarSigs = 0;
    for (int i = 0; i < sig1.length; i++) {
      if (sig1[i] == sig2[i]) {
        similarSigs += 1;
      }
    }

    // Return % of matches
    return similarSigs / sig1.length;
  }
}