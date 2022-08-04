#!/usr/bin/env bash

usage() {
    echo "Usage: ${PROGRAM_NAME} command dirname"
    echo "         -h help"
    echo "dirname: test module name"
}

run() {
    if [ -d "${PROJECT_DIR}" ]; then
        go mod tidy
        export DUBBO_GO_CONFIG_PATH=${PROJECT_DIR}/conf/dubbogo.yaml
        CMD="go run ${PROJECT_DIR}/main.go ${ARGS}"
        echo "command is: ${CMD}"
        ${CMD}
    fi
}

PROGRAM_NAME=$0
ARGS=""


PROJECT_DIR=$1
shift
if [ ! -d "${PROJECT_DIR}" ]; then
    usage
    exit 0
fi

ARGS=$@


run






