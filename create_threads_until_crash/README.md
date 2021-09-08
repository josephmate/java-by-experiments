How many threads until your JVM crashes?
How much does each thread impact heap?
How can you increase the number of threads?
What does the JVM report?
What does the OS report?

1. Think about the answer.
2. Try to write some experimental code to determine the answer and compare with
   your stack traces.

<details>
<summary>Click to reveal the results of my experiment.</summary>

I put together a program that creates threads and starts them, printing out the number of threads so far to standard out forever.
Eventually it will hit some bottleneck and fail.
I wrapped a bash script around it to let me modify the stack and heap size.

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

What does the JVM report when we're creating this mess?
```
TODO
```

What does the OS report when we're creating this mess?
```
TODO
```

</details>

