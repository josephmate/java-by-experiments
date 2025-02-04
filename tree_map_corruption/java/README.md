

# SimpleRepro

```
# may need to run multiple times and adjust number of threads and number of updates
# to get the problem to reproduce
mvn install
java \
  -cp $(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/classes \
  SimpleRepro 5 1000

# in another terminal/process:
kill -QUIT $(ps aux | rg '[S]impleRepro' | awk '{print $2}')

# in original terminal/process:
"Thread-0" #22 [14700] prio=5 os_prio=0 cpu=10359.38ms elapsed=11.49s tid=0x000001cdc35aaf60 nid=14700 runnable  [0x00000047cfffe000]
   java.lang.Thread.State: RUNNABLE
    at java.util.TreeMap.put(java.base@19.0.1/TreeMap.java:826)
    at java.util.TreeMap.put(java.base@19.0.1/TreeMap.java:534)
    at SimpleRepro.lambda$main$0(SimpleRepro.java:29)
    at SimpleRepro$$Lambda$14/0x00000008010031f0.run(Unknown Source)
    at java.lang.Thread.run(java.base@19.0.1/Thread.java:1589)
```


# ExecutorUncaughtRepro

Realistic looking reproduction of the problem using a ExecutorService.

```
# may need to run multiple times and adjust number of threads and number of updates
# to get the problem to reproduce
mvn install
java \
  -cp $(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/classes \
  ExecutorUncaughtRepro 5 1000

# in another terminal/process:
kill -QUIT $(ps aux | rg '[E]xecutorUncaughtRepro' | awk '{print $2}')

# in original terminal/process:
"pool-1-thread-1" #22 [15356] prio=5 os_prio=0 cpu=17734.38ms elapsed=21.39s tid=0x0000023c45dd3e90 nid=15356 runnable  [0x000000780b4fe000]
   java.lang.Thread.State: RUNNABLE
    at java.util.TreeMap.put(java.base@19.0.1/TreeMap.java:826)
    at java.util.TreeMap.put(java.base@19.0.1/TreeMap.java:534)
    at ExecutorUncaughtRepro.lambda$main$0(ExecutorUncaughtRepro.java:33)
    at ExecutorUncaughtRepro$$Lambda$14/0x00000008010031f0.run(Unknown Source)
    at java.util.concurrent.Executors$RunnableAdapter.call(java.base@19.0.1/Executors.java:577)
    at java.util.concurrent.FutureTask.run(java.base@19.0.1/FutureTask.java:317)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(java.base@19.0.1/ThreadPoolExecutor.java:1144)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(java.base@19.0.1/ThreadPoolExecutor.java:642)
    at java.lang.Thread.run(java.base@19.0.1/Thread.java:1589)

```

# GrpcRepo

Realistic looking reproduction of the problem using a gRPC service.

```
# may need to run multiple times and adjust number of threads and number of updates,
# hardcoded within the GrpcRepro code to get the problem to reproduce
mvn install
java \
  -cp $(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/classes \
  GrpcRepro

# in another terminal/process:
kill -QUIT $(ps aux | rg '[G]rpcRepro' | awk '{print $2}')

# in original terminal/process:
"grpc-default-executor-23" #54 [8796] daemon prio=5 os_prio=0 cpu=18671.88ms elapsed=175.50s tid=0x00000168b6c707c0 nid=8796 runnable  [0x000000059fbfe000]
   java.lang.Thread.State: RUNNABLE
    at java.util.TreeMap.put(java.base@19.0.1/TreeMap.java:826)
    at java.util.TreeMap.put(java.base@19.0.1/TreeMap.java:534)
    at ReceiptProcessorServiceImpl.addReceipt(GrpcRepro.java:59)
    at ReceiptProcessorServiceGrpc$MethodHandlers.invoke(ReceiptProcessorServiceGrpc.java:185)
    at io.grpc.stub.ServerCalls$UnaryServerCallHandler$UnaryServerCallListener.onHalfClose(ServerCalls.java:182)
    at io.grpc.internal.ServerCallImpl$ServerStreamListenerImpl.halfClosed(ServerCallImpl.java:346)
    at io.grpc.internal.ServerImpl$JumpToApplicationThreadServerStreamListener$1HalfClosed.runInContext(ServerImpl.java:860)
    at io.grpc.internal.ContextRunnable.run(ContextRunnable.java:37)
    at io.grpc.internal.SerializingExecutor.run(SerializingExecutor.java:133)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(java.base@19.0.1/ThreadPoolExecutor.java:1144)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(java.base@19.0.1/ThreadPoolExecutor.java:642)
    at java.lang.Thread.run(java.base@19.0.1/Thread.java:1589)
```

# ProtectedSimpleRepro


```
# Notice that no matter what parameters or use, or how many times you run it,
# it never gets stuck in a loop.
mvn install
java \
  -cp $(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/classes \
  ProtectedSimpleRepro 5 1000
Exception in thread "Thread-3" java.util.ConcurrentModificationException: TreeMap corrupted. Loop detected
        at ProtectedTreeMap.put(ProtectedTreeMap.java:840)
        at ProtectedTreeMap.put(ProtectedTreeMap.java:539)
        at ProtectedSimpleRepro.lambda$main$0(ProtectedSimpleRepro.java:29)
        at java.base/java.lang.Thread.run(Thread.java:833)

# Much better than having an infinite loop!
```
