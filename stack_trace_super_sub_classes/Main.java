import java.util.*;

public final class Main {
  
  private static class Sub extends Super{
    @Override
    public void callsSuper(boolean throwIt) {
      super.callsSuper(throwIt);
      throw new RuntimeException();
    }
  }

  private static class Super {
    public void callsSuper(boolean throwIt) {
      if (throwIt) {
        throw new RuntimeException();
      }
    }
  }

  public static void main(String [] args) throws Exception {
    try {
      new Sub().callsSuper(true);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    try {
      new Sub().callsSuper(false);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }
  
}
