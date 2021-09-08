#!/usr/bin/env bash

if [ "$#" -ne 3 ]; then
    echo "./run.sh [stacksize] [heapsize] [numThreads]"
    exit -1
fi

javac Main.java
retVal=$?
if [ $retVal -ne 0 ]; then
    echo "compile error"
    exit $retVal
fi

java -Xss$1 -Xmx$2 Main $3

