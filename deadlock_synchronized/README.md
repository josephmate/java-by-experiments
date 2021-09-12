What happens when you get a deadlock with synchronized in java?

1. Think about guess the answer. You will learn more if you guess first then
   check compared to immediately checking the answer.
2. Try to write some experimental code
3. Compare with my experiments

<details>
<summary>Click to reveal the results of my experiment.</summary>

To create a deadlock, we need to create a cycle in the wait for resource graph.
The easier way to do that is have one thread grab synchronized lock A then B.
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

</details>

