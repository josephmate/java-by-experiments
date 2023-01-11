import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.TreeMap;

public class ExploringTreeMap {

    /**
     * In order to run you need to add these JVM args:
     * <pre>
     * --add-opens java.base/java.util=ALL-UNNAMED
     * </pre>
     * @param args
     */
    public static void main(String[] args) throws Exception {

        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            treeMap.put(i, i);
        }

        TreeMapExplorer treeMapExplorer = new TreeMapExplorer(treeMap);
        treeMapExplorer.print();
    }


}

class TreeMapExplorer {
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

    public TreeMapExplorer(TreeMap<Integer,Integer> treeMap) {
        this.treeMap = treeMap;
    }

    public void print() throws Exception {
        print(treeMapRootField.get(treeMap), "");
    }

    private void print(Object treeMapEntry, String tabs) throws Exception {
        if (treeMapEntry != null) {
            // in order traversal
            print(treeMapEntryLeft.get(treeMapEntry), tabs + "  ");
            System.out.println(tabs + treeMapEntryKey.get(treeMapEntry));
            print(treeMapEntryRight.get(treeMapEntry), tabs + "  ");
        }
    }
}
