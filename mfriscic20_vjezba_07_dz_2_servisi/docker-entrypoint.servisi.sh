#!/bin/bash
set -e

start_server() {
    echo "Starting $1"
    shift
    exec java "$@"
}

start_server "Servisi" -jar target/mfriscic20_vjezba_07_dz_2_servisi-1.0.0-jar-with-dependencies.jar


tail -f /dev/null
