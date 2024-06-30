import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorUncaughtRepro {

    public static void main(String[] args) throws Exception {
        final int numThreads;
        if (args.length >= 1) {
            numThreads = Integer.parseInt(args[0]);
        } else {
            numThreads = 5;
        }

        final int numUpdatesPerThread;
        if (args.length >= 2) {
            numUpdatesPerThread =  Integer.parseInt(args[1]);
        } else {
            numUpdatesPerThread = 100000;
        }

        final ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        final TreeMap<Integer,Integer> treeMap = new TreeMap<>();

        Random random = new Random();
        for (int i = 0; i < numThreads*numUpdatesPerThread; i++) {
            pool.submit( () -> {
                treeMap.put(random.nextInt(10000), random.nextInt(10000));
            });
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
    }

}
