import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class ReproDetectDump {

    /**
     * In order to run you need to add these JVM args:
     * <pre>
     * --add-opens java.base/java.util=ALL-UNNAMED
     * </pre>
     * @param args
     */
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

        DetectorThread detectorThread = new DetectorThread(treeMap);

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
        detectorThread.start();

        for (Thread thread : threads) {
            thread.join();
        }

        detectorThread.interrupt();
    }

}

class DetectorThread extends Thread {
    private final TreeMap<Integer,Integer> treeMap;
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

    public DetectorThread(TreeMap<Integer,Integer> treeMap) {
        this.treeMap = treeMap;
    }

    private boolean isLoopDetected() throws Exception {
        return isLoopDetected(treeMapRootField.get(treeMap), new IdentityHashMap<>());
    }

    private boolean isLoopDetected(Object treeMapEntry, IdentityHashMap<Object, Object> visited) throws Exception {
        if (treeMapEntry == null) {
            return false;
        }

        Object left = treeMapEntryLeft.get(treeMapEntry);
        Object right = treeMapEntryRight.get(treeMapEntry);
        if (visited.containsKey(left)) {
            return true;
        }
        if (visited.containsKey(right)) {
            return true;
        }
        visited.put(left, left);
        visited.put(right, right);

        return isLoopDetected(left, visited) || isLoopDetected(right, visited);
    }

    @Override
    public void run() {
        try {
            while (isLoopDetected()) {
                if (this.isInterrupted()) {
                    return;
                }
            }
            System.out.println("Loop detected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}