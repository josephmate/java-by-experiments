import java.util.*;

public final class Main {
  
  public static void main(String [] args) throws Exception {
    int numOfThreads = Integer.parseInt(args[0]);
    for (int i = 1; i <= numOfThreads; i++) { 
      Thread thread = new Thread(() -> {
        while (true) {
          try {
            Thread.sleep(60*1000);
          } catch (InterruptedException e) {
          }
        }
      });
      thread.start();
      System.out.println(i);
    }
  }
  
}
