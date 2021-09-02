import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Main {

  public static void main(String [] args) throws Exception {
    if (args.length != 1) {
        throw new IllegalArgumentException("Program expects one argument,"
            + " the sequence or R's and W's"
            + " instructing the order in which to acquire the locks");
    }

    System.out.println("Program starting");
    ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    for (char c : args[0].toCharArray()) {
      if (c == 'r' || c == 'R') {
        System.out.println("Acquiring the read lock");
        readWriteLock.readLock().lock();
        System.out.println("Got the read lock");
      } else if (c == 'w' || c == 'W') {
        System.out.println("Acquiring the write lock");
        readWriteLock.writeLock().lock();
        System.out.println("Got the write lock");
      } else {
        throw new IllegalArgumentException("Character " + c + " is not a recognized action.");
      }
    }
  }

}
