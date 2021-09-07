How does native memory impact heap? OS memory?


1. Think about the answer.
2. Try to write some experimental code to determine the answer.

<details>
<summary>Click to reveal the result from my experiment.</summary>

To use native memory, I used the direct byte buffer.
I allocated 1 gigabyte.
I used `jcmd` `VM.native_memory` and `GC.heap_info` with `-XX:NativeMemoryTracking=summary` to get memory stats from the JVM.
Here are the results:

```
Native Memory Tracking:

Total: reserved=6787775KB, committed=1417475KB
-                 Java Heap (reserved=4194304KB, committed=266240KB)
                            (mmap: reserved=4194304KB, committed=266240KB) 

... 

-                     Other (reserved=1048578KB, committed=1048578KB)
                            (malloc=1048578KB #3) 
...

54416:
 garbage-first heap   total 266240K, used 3375K [0x0000000700000000, 0x0000000800000000)
  region size 2048K, 1 young (2048K), 0 survivors (0K)
 Metaspace       used 348K, committed 512K, reserved 1056768K
  class space    used 25K, committed 128K, reserved 1048576K
```

The heap has only committed 260MB while the Other category has committed just over 1024MB.
That's the direct byte buffer!
From this you can see that native is not allocated on the heap.
So make sure to always monitor both the OS memory usage and the heap usage.
If you notice heap is fine, but the OS memory is not, then you've got a native memory issue!

</details>

