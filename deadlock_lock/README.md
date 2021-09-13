What happens when you get a deadlock with a lock in java?

1. Think about guess the answer. You will learn more if you guess first then
   check compared to immediately checking the answer.
2. Try to write some experimental code
3. Compare with my experiments

<details>
<summary>Click to reveal the results of my experiment.</summary>

To create a deadlock, we need to create a cycle in the wait for resource graph.
The easiest way to do that is have one thread grab synchronized lock A then B.
The other thread need to grab B then A.

However, there's a catch!
That won't reliably reproduce the problem 100% of the time.
Sometimes, the thread will storm through and grab A then B before the other
thread even starts to grab B.
As a result, we introduce a barrier that will wait until thread 1 grab lock a and thread 2 grabs lock b.

```
Found one Java-level deadlock:
=============================
"Thread-0":
  waiting for ownable synchronizer 0x000000070fe1a608, (a java.util.concurrent.locks.ReentrantLock$NonfairSync),
  which is held by "Thread-1"

"Thread-1":
  waiting for ownable synchronizer 0x000000070fe1a5d8, (a java.util.concurrent.locks.ReentrantLock$NonfairSync),
  which is held by "Thread-0"

Java stack information for the threads listed above:
===================================================
"Thread-0":
        at jdk.internal.misc.Unsafe.park(java.base@16.0.1/Native Method)
        - parking to wait for  <0x000000070fe1a608> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
        at java.util.concurrent.locks.LockSupport.park(java.base@16.0.1/LockSupport.java:211)
        at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(java.base@16.0.1/AbstractQueuedSynchronizer.java:714)
        at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(java.base@16.0.1/AbstractQueuedSynchronizer.java:937)
        at java.util.concurrent.locks.ReentrantLock$Sync.lock(java.base@16.0.1/ReentrantLock.java:153)
        at java.util.concurrent.locks.ReentrantLock.lock(java.base@16.0.1/ReentrantLock.java:322)
        at Main.lambda$main$0(Main.java:18)
        at Main$$Lambda$1/0x0000000800c00a08.run(Unknown Source)
        at java.lang.Thread.run(java.base@16.0.1/Thread.java:831)
"Thread-1":
        at jdk.internal.misc.Unsafe.park(java.base@16.0.1/Native Method)
        - parking to wait for  <0x000000070fe1a5d8> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
        at java.util.concurrent.locks.LockSupport.park(java.base@16.0.1/LockSupport.java:211)
        at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(java.base@16.0.1/AbstractQueuedSynchronizer.java:714)
        at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(java.base@16.0.1/AbstractQueuedSynchronizer.java:937)
        at java.util.concurrent.locks.ReentrantLock$Sync.lock(java.base@16.0.1/ReentrantLock.java:153)
        at java.util.concurrent.locks.ReentrantLock.lock(java.base@16.0.1/ReentrantLock.java:322)
        at Main.lambda$main$1(Main.java:30)
        at Main$$Lambda$2/0x0000000800c00c30.run(Unknown Source)
        at java.lang.Thread.run(java.base@16.0.1/Thread.java:831)

Found 1 deadlock.
```

Just like with synchronized, sending `kill -s QUIT` will detect the deadlock and print it to standard out.
You can use the stack traces to help debug and figure out what is going on.

</details>

