import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Main {
  
  public static void main(String [] args) throws Exception {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0);
    executorService.scheduleWithFixedDelay(
        () -> System.out.println("Hello World!"),
        0, // Initial delay
        10, // delay
        TimeUnit.MINUTES
    );
    while (true) {
      Thread.sleep(10000);
    }
  }
  
}
