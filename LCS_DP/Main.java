package project_dp;

public class Main {
  public static void main(String[] args) {
    // Dynamic Programming Method ///////////////////////////////////
    System.out.println("Dynamic Programming Method");
    System.out.println("===========================");
    DynamicProgramming bf = new DynamicProgramming();
    String res1 = bf.find(args[0], args[1]);

    System.out.println(res1);
  }
}

