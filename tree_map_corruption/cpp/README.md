# Results

Most of the time, the program succeds and runs like this with all threads
completing.
```
./SimpleRepro
Thread Thread Thread Thread 02 started.3Thread  started.Thread 1 started.4
started.Thread 4: 10% complete (10/100 updates)
Thread Thread Thread 4: 20% complete (20/100 updates)
 started.Thread 4: 30% complete (30/100 updates)
2: 10% complete (10/100 updates)
Thread 4: 40% complete (40/100 updates)
Thread 2: 20% complete (20/100 updates)
Thread 4: 50% complete (50/100 updates)
0: 10% complete (10/100 updates)
Thread 4: 60% complete (60/100 updates)
Thread 0: 20% complete (20/100 updates)
Thread 4: 70% complete (70/100 updates)
Thread 0: 30% complete (30/100 updates)
Thread 4: 80% complete (80/100 updates)
Thread 0: 40% complete (40/100 updates)
Thread 4: 90% complete (90/100 updates)
Thread 0: 50% complete (50/100 updates)
Thread 4: 100% complete (100/100 updates)
Thread 4 completed.Thread 0: 60% complete (60/100 updates)
Thread 0: 70% complete (70/100 updates)
Thread 0: 80% complete (80/100 updates)
Thread 2: 30% complete (Thread 30/100 updates)
3: 10% complete (10/100 updates)
Thread 1: 10% complete (10/100 updates)
Thread 3: 20Thread 2: 40% complete (40/100 updates)
Thread 2: 50% complete (50/100 updates)
Thread 2: 60% complete (60/100 updates)
Thread 2: 70% complete (70/100 updates)
Thread 2: 80% complete (80/100 updates)
Thread 2: 90% complete (90/100 updates)
Thread 1: 20% complete (Thread 2200: 90% complete (90/100 updates)
/100 updates)
Thread 0: 100% complete (100/100 updates)
Thread 0 completed.Thread 1: 30% complete (30/100 updates)
: % complete (20/100 updates)
Thread 3: 30% complete (30/100 updates)
100% complete (100/100 updates)
Thread 1: 40% complete (40/100 updates)
Thread 1: 50% complete (50/100 updates)
Thread 1: 60% complete (60/100 updates)
Thread 1: 70% complete (70/100 updates)
Thread 1: 80% complete (80/100 updates)
Thread 1: 90% complete (90/100 updates)
Thread 1: 100% complete (100/100 updates)
Thread 1 completed.Thread 2 completed.Thread 3: 40% complete (40/100 updates)
Thread 3: 50% complete (50/100 updates)
Thread 3: 60% complete (60/100 updates)
Thread 3: 70% complete (70/100 updates)
Thread 3: 80% complete (80/100 updates)
Thread 3: 90% complete (90/100 updates)
Thread 3: 100% complete (100/100 updates)
Thread 3 completed.
```

Occasionally, the program segfaults and crashes like before.
This is what I was expecting the worst failure to look like.
```
./SimpleRepro
zsh: segmentation fault  ./SimpleRepro
```

However, very rarely the output would look like indicating the threads got
stuck.
After waiting 10 minutes, the threads never completed.
I did not expect this to happen because I thought an exception would be required
for the problem to occur, that that is not happening here.
```
./SimpleRepro
Thread 0 started.Thread Thread 0: 10% complete (Thread 10/10012Thread  started.3
started.Thread 4 started.Thread 4: 10% complete (10/100 updates)
 updates)
Thread 0: 20% complete (20/100 updates)
Thread 0: 30% complete (30/100 updates)
Thread 0: 40% complete (40/100 updates)
Thread 1: 10% complete (10/100 updates)
 started.Thread 2: 10% complete (10/100 updates)
Thread 1: 20% complete (20/100 updates)
```

From top we can see that the problem reproduced because of the high cpu
utilization.
```
top
PID    COMMAND      %CPU  TIME     #TH   #WQ  #PORT MEM    PURG   CMPRS  PGRP
PPID  STATE    BOOSTS         %CPU_ME %CPU_OTHRS UID  FAULTS     COW    MSGSENT
MSGRECV 
59815  SimpleRepro  170.8 08:49.61 6/5   0    15    372K   0B     308K   59815
44017 running  *0[1]          0.00000 0.00000    501  376        85     69+
13
```

# Running

Make sure you have `g++` installed
```
make
./SimpleRepro
```
