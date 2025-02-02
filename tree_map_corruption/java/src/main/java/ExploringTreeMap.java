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

