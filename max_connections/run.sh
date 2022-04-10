#!/usr/bin/env bash

NUM_CONNECTIONS=$1

javac src/Main.java

sar -o out.$NUM_CONNECTIONS.sar -A 1 3600 2>&1 > /dev/null  &
SAR_PID=$!

echo running:
echo java \
	-agentpath:/home/joseph/lib/async-profiler-2.7-linux-x64/build/libasyncProfiler.so=start,collapsed,event=wall,file=collapsed.wall.$NUM_CONNECTIONS.txt \
	-cp src/ \
	Main $NUM_CONNECTIONS

java \
	-agentpath:/home/joseph/lib/async-profiler-2.7-linux-x64/build/libasyncProfiler.so=start,collapsed,event=wall,file=collapsed.wall.$NUM_CONNECTIONS.txt \
	-cp src/ \
	Main $NUM_CONNECTIONS \
	| tee out.$NUM_CONNECTIONS.txt

kill $SAR_PID
