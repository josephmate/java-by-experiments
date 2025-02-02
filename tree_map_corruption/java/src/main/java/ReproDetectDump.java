import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * In order to run on jdk11+ you need to add these JVM args:
 * <pre>
 * --add-opens java.base/java.util=ALL-UNNAMED
 * </pre>
 *
 * Need to run many experiments in order to reproduce the issue with so few entries.
 */
public class ReproDetectDump {


    public static void main(String[] args) throws Exception {

        final int numThreads;
        if (args.length >= 1) {
            numThreads = Integer.parseInt(args[0]);
        } else {
            numThreads = 3;
        }

        final int numUpdates;
        if (args.length >= 2) {
            numUpdates =  Integer.parseInt(args[1]);
        } else {
            numUpdates = 4;
        }

        final int maxVal;
        if (args.length >= 3) {
            maxVal =  Integer.parseInt(args[2]);
        } else {
            maxVal = 30;
        }

        final int numExperiments;
        if (args.length >= 4) {
            numExperiments =  Integer.parseInt(args[3]);
        } else {
            numExperiments = 1000;
        }

        for (int experiment = 1; experiment <= numExperiments; experiment++) {
            final TreeMap<Integer, Integer> treeMap = new TreeMap<>();


            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < numThreads; i++) {
                threads.add(new Thread(() -> {
                    Random random = new Random();
                    for (int j = 0; j < numUpdates; j++) {
                        try {
                            treeMap.put(random.nextInt(maxVal), random.nextInt(1000));
                        } catch (NullPointerException e) {
                            // let it keep going so we can reproduce the issue.
                        }
                    }
                }));
            }

            DetectorThread detectorThread = new DetectorThread(treeMap, threads);

            for (Thread thread : threads) {
                thread.start();
            }
            detectorThread.start();

            for (Thread thread : threads) {
                thread.join();
            }

            detectorThread.interrupt();
        }
    }

}

class DetectorThread extends Thread {
    private final TreeMap<Integer,Integer> treeMap;
    private final List<Thread> threadsToStop;
    private static final Field treeMapRootField;
    private static final Field treeMapEntryLeft;
    private static final Field treeMapEntryRight;
    private static final Field treeMapEntryKey;

    static {
        try {
            treeMapRootField = TreeMap.class.getDeclaredField("root");
            treeMapRootField.setAccessible(true);

            Class treeMapEntryClass = Arrays.stream(TreeMap.class.getDeclaredClasses())
                .filter(clazz -> "java.util.TreeMap$Entry".equals(clazz.getName()))
                .findAny()
                .get();

            treeMapEntryLeft = treeMapEntryClass.getDeclaredField("left");
            treeMapEntryLeft.setAccessible(true);
            treeMapEntryRight = treeMapEntryClass.getDeclaredField("right");
            treeMapEntryRight.setAccessible(true);
            treeMapEntryKey = treeMapEntryClass.getDeclaredField("key");
            treeMapEntryKey.setAccessible(true);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public DetectorThread(
        TreeMap<Integer,Integer> treeMap,
        List<Thread> threadsToStop
    ) {
        this.treeMap = treeMap;
        this.threadsToStop = threadsToStop;
    }

    private boolean isLoopDetected() throws Exception {
        return isLoopDetected(treeMapRootField.get(treeMap), new IdentityHashMap<>());
    }

    /**
     * DFS to detect loop
     */
    private boolean isLoopDetected(Object treeMapEntry, IdentityHashMap<Object, Object> visited) throws Exception {
        if (treeMapEntry == null) {
            return false;
        }

        if (visited.containsKey(treeMapEntry)) {
            return true;
        }

        visited.put(treeMapEntry, treeMapEntry);


        Object left = treeMapEntryLeft.get(treeMapEntry);
        Object right = treeMapEntryRight.get(treeMapEntry);
        return isLoopDetected(left, visited)
            || isLoopDetected(right, visited);
    }

    @Override
    public void run() {
        try {
            while (!isLoopDetected()) {
                if (this.isInterrupted()) {
                    return;
                }
            }
            System.out.println("Loop detected");
            new TreeMapExplorer(treeMap).print();
            for (Thread thread : threadsToStop) {
                thread.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}