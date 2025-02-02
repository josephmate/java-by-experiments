import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.TreeMap;

class TreeMapExplorer {
    private final TreeMap<Integer,Integer> treeMap;
    private static final Field treeMapRootField;
    private static final Field treeMapEntryLeft;
    private static final Field treeMapEntryRight;
    private static final Field treeMapEntryKey;
    private static final Field treeMapEntryColor;

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
            treeMapEntryColor = treeMapEntryClass.getDeclaredField("color");
            treeMapEntryColor.setAccessible(true);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public TreeMapExplorer(TreeMap<Integer,Integer> treeMap) {
        this.treeMap = treeMap;
    }

    public void print() throws Exception {
        print(treeMapRootField.get(treeMap), "", new IdentityHashMap<>());
    }

    private void print(
        Object treeMapEntry, String tabs, IdentityHashMap<Object, Object> visited
    ) throws Exception {
        if (treeMapEntry != null && !visited.containsKey(treeMapEntry)) {
            // in order traversal
            visited.put(treeMapEntry, treeMapEntry);
            print(treeMapEntryLeft.get(treeMapEntry), tabs + "  ", visited);
            System.out.println(tabs + treeMapEntryKey.get(treeMapEntry) + ":"
                + (treeMapEntryColor.getBoolean(treeMapEntry) ? "BLACK" : "RED"));
            print(treeMapEntryRight.get(treeMapEntry), tabs + "  ", visited);
        } else if (treeMapEntry != null && visited.containsKey(treeMapEntry)) {
            System.out.println(tabs + treeMapEntryKey.get(treeMapEntry)  + ":"
                + (treeMapEntryColor.getBoolean(treeMapEntry) ? "BLACK" : "RED")
                + " CYCLE"
            );
        }
    }
}
