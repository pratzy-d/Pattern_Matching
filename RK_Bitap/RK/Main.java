package project_rk;

public class Main {
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Please use 3 arguments as input: T P alg");
      System.out.println("alg should be Rabin-Karp(rk)");
    }
    if (args[2].equals("rk")) {
      RK rk = new RK();
      System.out.println(rk.match(args[0], args[1]));
    } else {
      System.out.println("Please specify pattern matching algorithm.");
    }
  }
}
