import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {

  public static void main(String [] args) throws IOException {
    // add shutdown hook to clean up file on exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        Files.delete(Paths.get("ready.tmp"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));

    // Allocate 1GB of native memory (method accepts bytes)
    //                                                            KB   MB   GB
    ByteBuffer nativeMemoryBuffer = ByteBuffer.allocateDirect(1024*1024*1024);
    while(true) {
      Files.write(Paths.get("ready.tmp"), "I'm ready!".getBytes());
      // sleep until we're interrupted
      try {
        Thread.sleep(1000); // sleep for one second
      } catch (InterruptedException e) {
        break;
      }
    }
  }
  
}
