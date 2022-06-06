package project_bitap;

public class Main {
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Please use 3 arguments as input: T P alg");
      System.out.println("alg should be bitap");
    }
    if (args[2].equals("bitap")) {
      Bitap bitap = new Bitap();
      System.out.println(bitap.match(args[0], args[1]));
    } else {
      System.out.println("Please specify pattern matching algorithm.");
    }
  }
}
