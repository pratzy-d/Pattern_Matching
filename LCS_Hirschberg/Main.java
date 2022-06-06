package project_hb;

public class Main {
  public static void main(String[] args) {
    Hirschberg lcs = new Hirschberg();
    String res = lcs.find(args[0], args[1]);

    System.out.println(res);
  }
}

