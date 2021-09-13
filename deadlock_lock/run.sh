#!/usr/bin/env bash

if [ "$#" -ne 0 ]; then
    echo "./run.sh"
    exit -1
fi

javac Main.java
retVal=$?
if [ $retVal -ne 0 ]; then
    echo "compile error"
    exit $retVal
fi

java -XX:NativeMemoryTracking=summary Main

