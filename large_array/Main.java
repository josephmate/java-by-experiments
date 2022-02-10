import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    final List<Long> big;
    if ("array".equals(args[0])) {
      big = new ArrayList<>();
    } else {
      big = new LinkedList<>();
    }

    long bigSize = (long)Integer.MAX_VALUE+1;
    for (long i = 0; i < bigSize; i++) {
      try {
        big.add(i);
      } catch (Throwable t) {
        System.out.println("Died at: " + i);
        throw t;
      }
    }

    System.out.println(big.size());
  }
}