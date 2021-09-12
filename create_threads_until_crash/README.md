How many threads until your JVM crashes?
How much does each thread impact heap?
How can you increase the number of threads?
What does the JVM report?
What does the OS report?

1. Think about guess the answer. You will learn more if you guess first then
   check compared to immediately checking the answer.
2. Try to write some experimental code
3. Compare with my experiments

<details>
<summary>Click to reveal the results of my experiment.</summary>

I put together a program that creates threads and starts them, printing out the number of threads so far to standard out forever.
Eventually it will hit some bottleneck and fail.
I wrapped a bash script around it to let me modify the stack and heap size.

**WARNING:** if you run this script, sometimes your OS will crash because it uses too much memory.

```
./run.sh 1M 10M 10000
...
4072
4073
4074
[0.533s][warning][os,thread] Failed to start thread - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.
Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
        at java.base/java.lang.Thread.start0(Native Method)
        at java.base/java.lang.Thread.start(Thread.java:800)
        at Main.main(Main.java:16)
```

Looks like I can create 4,000 threads before my JVM crashes!
It looks like heap is unrelated.
I set the heap to 10MB but gave the stack 1MB of memory.
If the resources consumed threads were allocated on the heap, then I expect to only be able to create about 100 threads.
As a result, we know that Java creates the threads off heap and the heap has no effect.

How can you increase the number of threads?
Each thread comes with its own stack to keep track of the call stack.
Lets check if increase the stack size reduces the number of threads we can create.
```
./run.sh 2M 10M 10000
...
4072
4073
4074
[0.566s][warning][os,thread] Failed to start thread - pthread_create failed (EAGAIN) for attributes: stacksize: 2048k, guardsize: 4k, detached.
Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
        at java.base/java.lang.Thread.start0(Native Method)
        at java.base/java.lang.Thread.start(Thread.java:800)
        at Main.main(Main.java:16)
```
Again 4000?
I did not expect this.

```
./run.sh 256M 10M 10000
4072
4073
4074
[0.566s][warning][os,thread] Failed to start thread - pthread_create failed (EAGAIN) for attributes: stacksize: 2048k, guardsize: 4k, detached.
Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
        at java.base/java.lang.Thread.start0(Native Method)
        at java.base/java.lang.Thread.start(Thread.java:800)
        at Main.main(Main.java:16)
```
Still 4000?
How am I allocating 1 TB of memory?
My laptop only has 16GB.

Lets allocate less and see what the JVM reports and the OS reports to help
figure it out.
```
./run.sh 256M 10M 1000
jcmd $java_process_pid VM.native_memory
...
-                    Thread (reserved=264777398KB, committed=264777398KB)
                            (thread #1020)
                            (stack: reserved=264774656KB, committed=264774656KB)
                            (malloc=1549KB #6116)
                            (arena=1192KB #2036)
...
-    Native Memory Tracking (reserved=581KB, committed=581KB)
                            (malloc=98KB #1058)
                            (tracking overhead=483KB)

jcmd $java_process_pid GC.heap_info
1035:
 garbage-first heap   total 10240K, used 2107K [0x00000007ff600000, 0x0000000800000000)
  region size 1024K, 2 young (2048K), 0 survivors (0K)
 Metaspace       used 265K, committed 448K, reserved 1056768K
  class space    used 5K, committed 128K, reserved 1048576K

ps -l -p $java_process_pid
  UID   PID  PPID        F CPU PRI NI       SZ    RSS WCHAN     S             ADDR TTY           TIME CMD
  501  1035  1033     4006   0  31  0 270756616  95428 -      S+                  0 ttys001    0:02.26 /usr/bin/java -XX:NativeMemoryTracking=summary -Xss256M -Xmx10M Main 1000

```

The `jcmd` command tell us that the thread stacks have asked the OS for \~250GB of memory!
The `ps` command The OS has granted \~250GB virtual memory.
However only 93MB is actually being used (reported as RSS).
This makes sense since each of the stacks would only be two call stacks deep:
```java.lang.Exception
        at Main.lambda$main$0(Main.java:10)
        at java.base/java.lang.Thread.run(Thread.java:831)
```

In summary you can create about ~4000 threads before the JVM or your OS crashes.
This is variable depending on your OS and resources available.
Increasing or decreasing the stack size doesn't seem to have an impact since it 99% virtual memory.
We confirmed this by comparing virtual memory to resident set memory.
It made sense since the entire stack is allocated,
but only two call stacks were consumed by our program.

</details>

