What happens when you get a deadlock with synchronized in java?

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
./run.sh
<process never exists>
```
As expected, the process never ends because thread 1 and 2 are deadlocked, wait for the other to give up their resource.
Java does not detect this deadlock for you unless you ask it to.
We can send `kill -s QUIT` to tell the JVM to send a status report to standard out.

```
./run.sh > out.txt &
kill -s QUIT $java_pid
less out.txt
...
Found one Java-level deadlock:
=============================
"Thread-0":
  waiting to lock monitor 0x00007fc8b1f1cb00 (object 0x000000070fe1a5b8, a java.lang.Object),
  which is held by "Thread-1"

"Thread-1":
  waiting to lock monitor 0x00007fc8b5306eb0 (object 0x000000070fe1a5a8, a java.lang.Object),
  which is held by "Thread-0"

Java stack information for the threads listed above:
===================================================
"Thread-0":
        at Main.lambda$main$0(Main.java:18)
        - waiting to lock <0x000000070fe1a5b8> (a java.lang.Object)
        - locked <0x000000070fe1a5a8> (a java.lang.Object)
        at Main$$Lambda$1/0x0000000800c00a08.run(Unknown Source)
        at java.lang.Thread.run(java.base@16.0.1/Thread.java:831)
"Thread-1":
        at Main.lambda$main$1(Main.java:30)
        - waiting to lock <0x000000070fe1a5a8> (a java.lang.Object)
        - locked <0x000000070fe1a5b8> (a java.lang.Object)
        at Main$$Lambda$2/0x0000000800c00c30.run(Unknown Source)
        at java.lang.Thread.run(java.base@16.0.1/Thread.java:831)

Found 1 deadlock.
...
```

It found the deadlock but it does not do anything about it.
It even says the threads, locks, and stacktraces involved in the deadlock!
That's very useful for debugging.
At this point there is nothing we can do to recover, unless your application exposes a way to kill or interrupt threads.

</details>

