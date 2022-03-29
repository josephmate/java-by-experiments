#!/usr/bin/env bash

NUM_CONNECTIONS=$1

javac src/Main.java

sar -o out.$NUM_CONNECTIONS.sar -A 1 3600 2>&1 > /dev/null  &
SAR_PID=$!

java -cp src/ Main $NUM_CONNECTIONS | tee out.$NUM_CONNECTIONS.txt

kill $SAR_PID
