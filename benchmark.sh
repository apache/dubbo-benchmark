#!/usr/bin/env bash

usage() {
    echo "Usage: ${PROGRAM_NAME} -m {profiling|benchmark} -s hostname -p port dirname"
}

build() {
    mvn --projects benchmark-base,client-base,server-base,${PROJECT_DIR} clean package
}

java_options() {
    JAVA_OPTIONS="-server -Xmx1g -Xms1g -XX:MaxDirectMemorySize=1g -XX:+UseG1GC"
    if [ "x${MODE}" = "xprofiling" ]; then
        JAVA_OPTIONS="${JAVA_OPTIONS} \
-XX:+UnlockCommercialFeatures \
-XX:+FlightRecorder \
-XX:StartFlightRecording=duration=30s,filename=${PROJECT_DIR}.jfr \
-XX:FlightRecorderOptions=stackdepth=256"
    fi
}

run() {
    if [ -d "${PROJECT_DIR}/target" ]; then
        JAR=`find ${PROJECT_DIR}/target/*.jar | head -n 1`
        echo
        echo "RUN ${PROJECT_DIR} IN ${MODE:-benchmark} MODE"
        CMD="java ${JAVA_OPTIONS} -Dserver.host=${SERVER} -Dserver.port=${PORT} -jar ${JAR}"
        echo "command is: ${CMD}"
        echo
        ${CMD}
    fi
}

PROGRAM_NAME=$0
MODE="benchmark"
SERVER="localhost"
PORT="8080"
OPTIND=1

while getopts "h?m:s:p:" opt; do
    case "$opt" in
        h|\?)
            usage
            exit 0
            ;;
        m)
            MODE=${OPTARG}
            ;;
        s)
            SERVER=${OPTARG}
            ;;
        p)
            PORT=${OPTARG}
            ;;
    esac
done

shift $((OPTIND-1))
PROJECT_DIR=$1

if [ ! -d "${PROJECT_DIR}" ]; then
    usage
    exit 0
fi

build
java_options
run






