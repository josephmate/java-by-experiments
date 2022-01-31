#!/usr/bin/env bash

# install an old JDK from like u92
# https://www.oracle.com/ca-en/java/technologies/javase/javase8-archive-downloads.html
# /usr/libexec/java_home -V
# to list java versions


if [ "$#" -ne 0 ]; then
    echo "./run.sh"
    exit -1
fi

old_java_home=/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home
#old_java_home=/Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home

$old_java_home/bin/javac Main.java
retVal=$?
if [ $retVal -ne 0 ]; then
    echo "compile error"
    exit $retVal
fi

#$old_java_home/bin/java Main

docker build . --tag josephmate/scheduled-executor-service-100-cpu:1.0.0
docker run --name scheduled_executor_service josephmate/scheduled-executor-service-100-cpu:1.0.0

# in another terminal
# docker exec --tty --interactive scheduled_executor_service sh
# top
# top -H -p 1

