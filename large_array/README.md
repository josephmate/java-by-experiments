Does ArrayList have a limit? LinkedList?

<details>
<summary>Take a guess without an experiment.</summary>
The List interface uses an integer for the size.
As a result, the limit must be 2^31-1.
But what happens if you tried to go beyond?
Would the size overflow and ArrayList tries to allocate an array with negative
size?
Would LinkedList let you keep adding beyond the limit since it's not backed by
an array?
</details>

<details>
<summary>Click to reveal the solution by experiment.</summary>


new ArrayList<>(Integer.MAX_VALUE-1):
```
./run.sh array
Exception in thread "main" java.lang.OutOfMemoryError: Requested array size
exceeds VM limit
        at java.base/java.util.ArrayList.<init>(ArrayList.java:156)
        at Main.main(Main.java:9)
Error
```
JVM doesn't even let us allocate an array of that size.

The exception probably comes from native code because this is the line that's
failing:
```java
 147     /**
 148      * Constructs an empty list with the specified initial capacity.
 149      *
 150      * @param  initialCapacity  the initial capacity of the list
 151      * @throws IllegalArgumentException if the specified initial capacity
 152      *         is negative
 153      */
 154     public ArrayList(int initialCapacity) {
 155         if (initialCapacity > 0) {

////////////////////////////// THIS LINE //////////////////////////////
 156             this.elementData = new Object[initialCapacity];
///////////////////////////////////////////////////////////////////////

 157         } else if (initialCapacity == 0) {
 158             this.elementData = EMPTY_ELEMENTDATA;
 159         } else {
 160             throw new IllegalArgumentException("Illegal Capacity: "+
 161                                                initialCapacity);
 162         }
 163     }
```

ArrayList<>() and -Xmx10g:
```
./run.sh array
Died at: 354836040
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.Arrays.copyOf(Arrays.java:3511)
        at java.base/java.util.Arrays.copyOf(Arrays.java:3480)
        at java.base/java.util.ArrayList.grow(ArrayList.java:237)
        at java.base/java.util.ArrayList.grow(ArrayList.java:244)
        at java.base/java.util.ArrayList.add(ArrayList.java:454)
        at java.base/java.util.ArrayList.add(ArrayList.java:467)
        at Main.main(Main.java:17)
Error
```
My laptop might not have enought memory for this experiment :(


ArrayList<>() and -Xmx16g:
```
./run.sh array
Died at: 532254060
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.Arrays.copyOf(Arrays.java:3511)
        at java.base/java.util.Arrays.copyOf(Arrays.java:3480)
        at java.base/java.util.ArrayList.grow(ArrayList.java:237)
        at java.base/java.util.ArrayList.grow(ArrayList.java:244)
        at java.base/java.util.ArrayList.add(ArrayList.java:454)
        at java.base/java.util.ArrayList.add(ArrayList.java:467)
        at Main.main(Main.java:17)
Error
```

LinkedList<>()
```
./run.sh link
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at Main.main(Main.java:17)
Error
```

</details>
