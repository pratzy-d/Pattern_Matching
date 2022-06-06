package project_sa_skew;

import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    SuffixArray SA = new SuffixArray();
    long startTime, endTime;
    startTime = System.nanoTime();
    ArrayList<Integer> res = SA.construct(args[0]);
    endTime = System.nanoTime();
    System.out.println("Time: " + (endTime-startTime));
    for (int i = 0; i < res.size(); i++) {
      System.out.println(res.get(i));
    }

    System.out.println("***************************************************");
    System.out.println("BruteForce Method");
    System.out.println("***************************************************");
    startTime = System.nanoTime();
    BruteSuffixArray bsa = new BruteSuffixArray(args[0]);
    bsa.createSuffixArray();
    endTime = System.nanoTime();
    System.out.println("Time: " + (endTime-startTime));
  }
}

