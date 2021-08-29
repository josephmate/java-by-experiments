import java.util.*;

public final class Main {

  public static void main(String [] args) throws Exception {
    Thread currentTimeMillisRepeatThread = new Thread(Main::doesCurrentTimeMillisRepeat);
    Thread nanoTimeRepeatThread = new Thread(Main::doesNanoTimeRepeat);

    currentTimeMillisRepeatThread.start();
    nanoTimeRepeatThread.start();
    currentTimeMillisRepeatThread.join();
    nanoTimeRepeatThread.join();
    System.out.println("All threads complete");
  }
  
  private static void doesCurrentTimeMillisRepeat() {
    long prev = System.currentTimeMillis();
    while (true) {
      final long curr = System.currentTimeMillis();
      if (prev == curr) {
        System.out.println("System.currentTimeMillis() repeated prev=" + prev + ", curr=" + curr);
        break;
      } 
      prev = curr;
    }
  }

  private static void doesCurrentTimeMillisDecrease() {
    long prev = System.currentTimeMillis();
    while (true) {
      final long curr = System.currentTimeMillis();
      if (prev > curr) {
        System.out.println("System.currentTimeMillis() decreased prev=" + prev + ", curr=" + curr);
        break;
      } 
      prev = curr;
    }
  }

  private static void doesNanoTimeRepeat() {
    long prev = System.nanoTime();
    while (true) {
      final long curr = System.nanoTime();
      if (prev == curr) {
        System.out.println("System.nanoTime() repeated prev=" + prev + ", curr=" + curr);
        break;
      } 
      prev = curr;
    }
  }

  private static void doesNanoTimeDecrease() {
    long prev = System.nanoTime();
    while (true) {
      final long curr = System.nanoTime();
      if (prev > curr) {
        System.out.println("System.nanoTime() decreased prev=" + prev + ", curr=" + curr);
        break;
      } 
      prev = curr;
    }
  }
}
