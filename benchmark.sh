#!/usr/bin/env bash

usage() {
    echo "Usage: ${PROGRAM_NAME} command dirname"
    echo "command: [m|s|p|f]"
    echo "         -v Dubbo version, eg: 2.7.11, 3.0.0.preview"
    echo "         -m [profiling|benchmark], specify benchmark mode"
    echo "         -s hostname, host name"
    echo "         -p port, port number"
    echo "         -f output folder path"
    echo "         -n output file name"
    echo "         -a other args"
    echo "dirname: test module name"
}

build() {
    ./mvnw --projects benchmark-base,client-base,server-base,${PROJECT_DIR} clean package -Ddubbo.version=${DUBBO_VERSION}
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
        CMD="java ${JAVA_OPTIONS} -Dserver.host=${SERVER} -Dserver.port=${PORT} -Dbenchmark.output.dir=${OUTPUT} -Dbenchmark.output.name=${FILENAME} -jar ${JAR} ${OTHERARGS}"
        echo "command is: ${CMD}"
        echo
        ${CMD}
    fi
}

PROGRAM_NAME=$0
MODE="benchmark"
SERVER="localhost"
PORT="8080"
OUTPUT=""
OPTIND=1
OTHERARGS=""

while getopts "v:m:s:p:f:n:a:" opt; do
    case "$opt" in
        v)
            DUBBO_VERSION=${OPTARG}
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
        f)
            OUTPUT=${OPTARG}
            ;;
        n)
            FILENAME=${OPTARG}
            ;;
        a)
            OTHERARGS=${OPTARG}
            ;;
        ?)
            usage
            exit 0
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






