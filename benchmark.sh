#!/usr/bin/env bash

PROGRAM_NAME=$0
TASK=$1
PROFILE=$2

if [ ! -d "${TASK}" ]; then
    usage
fi

usage() {
    echo "Usage: ${PROGRAM_NAME} directory-name {profiling|benchmark}"
    echo "run in benchmark mode by default if mode is not specified."
    exit 2
}

build() {
    mvn clean package
}

options() {
    JAVA_OPTIONS="-server -Xmx1g -Xms1g -XX:MaxDirectMemorySize=1g -XX:+UseG1GC"
    if [ "x${PROFILE}" = "xprofiling" ]; then
        JAVA_OPTIONS="${JAVA_OPTIONS} \
-XX:+UnlockCommercialFeatures \
-XX:+FlightRecorder \
-XX:StartFlightRecording=duration=30s,filename=${TASK}.jfr \
-XX:FlightRecorderOptions=stackdepth=256"
    fi
}

run() {
    if [ -d "${TASK}/target" ]; then
        JAR=`find ${TASK}/target/*.jar | head -n 1`
        echo
        echo "RUN ${TASK} IN ${PROFILE:-benchmark} MODE"
        CMD="java ${JAVA_OPTIONS} -jar ${JAR}"
        echo "command is: ${CMD}"
        echo
        ${CMD}
    fi
}

build
options
run






