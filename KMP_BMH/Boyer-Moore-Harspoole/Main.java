package project_bmh;

public class Main {
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Please use 3 arguments as input: T P alg");
      System.out.println("alg should be bmh");
    }
    if (args[2].equals("bmh")) {
      BMH bmh = new BMH();
      System.out.println(bmh.match(args[0], args[1]));
    }
    else {
      System.out.println("Please specify pattern matching algorithm.");
    }
  }
}

