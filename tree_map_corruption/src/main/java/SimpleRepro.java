import java.util.*;

public class SimpleRepro {

    public static void main(String[] args) throws Exception {

        final int numThreads;
        if (args.length >= 1) {
            numThreads = Integer.parseInt(args[0]);
        } else {
            numThreads = 5;
        }

        final int numUpdates;
        if (args.length >= 2) {
            numUpdates =  Integer.parseInt(args[1]);
        } else {
            numUpdates = 1000;
        }

        final TreeMap<Integer,Integer> treeMap = new TreeMap<>();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Thread(() -> {
                Random random = new Random();
                for(int j = 0; j < numUpdates; j++) {
                    try {
                        treeMap.put(random.nextInt(1000), random.nextInt(1000));
                    } catch (NullPointerException e) {
                        // let it keep going so we can reproduce the issue.
                    }
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

    }

}
