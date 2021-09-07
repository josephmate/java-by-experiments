#!/usr/bin/env bash
 
javac Main.java
retVal=$?
if [ $retVal -ne 0 ]; then
    echo "compile error"
    exit $retVal
fi


java -XX:NativeMemoryTracking=summary Main &
process_pid=$!
echo process_pid=$process_pid
# wait for temp file
while [ ! -f "ready.tmp" ]
do
  sleep 1
done

line=$(cat ready.tmp)
while [ "I'm ready!" != "$line" ]
do
  sleep 1
done

jcmd $process_pid VM.native_memory
jcmd $process_pid GC.heap_info

kill $process_pid

