import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public final class Main {
  
  public static void main(String [] args) throws Exception {
    ReentrantLock lockA = new ReentrantLock();
    ReentrantLock lockB = new ReentrantLock();
    final CyclicBarrier barrier = new CyclicBarrier(2);

    Thread thread1 = new Thread(() -> {
      lockA.lock();
      try {
        barrier.await();
      } catch (Exception e) { // don't do this in production code :)
      }
      lockB.lock();
      System.out.println("Got A then B");
      lockB.unlock();
      lockA.unlock();
    });
    thread1.start();
    Thread thread2 = new Thread(() -> {
      lockB.lock();
      try {
        barrier.await();
      } catch (Exception e) { // don't do this in production code :)
      }
      lockA.lock();
      System.out.println("Got B then A");
      lockA.unlock();
      lockB.unlock();
    });
    thread2.start();
    
    thread1.join();
    thread2.join();
  }
  
}
