import java.util.*;
import java.util.concurrent.CyclicBarrier;

public final class Main {
  
  public static void main(String [] args) throws Exception {
    final Object lockA = new Object();
    final Object lockB = new Object();
    final CyclicBarrier barrier = new CyclicBarrier(2);

    Thread thread1 = new Thread(() -> {
      synchronized(lockA) {
        try {
          barrier.await();
        } catch (Exception e) { // don't do this in production code :)
        }
        synchronized(lockB) {
          System.out.println("Got A then B");
        }
      }
    });
    thread1.start();
    Thread thread2 = new Thread(() -> {
      synchronized(lockB) {
        try {
          barrier.await();
        } catch (Exception e) { // don't do this in production code :)
        }
        synchronized(lockA) {
          System.out.println("Got B then A");
        }
      }
    });
    thread2.start();
    
    thread1.join();
    thread2.join();
  }
  
}
