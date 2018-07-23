#!/usr/bin/env bash

set -o nounset
set -o errexit

RUNNER=$1

# run pipeline
mvn compile exec:java \
  -Dexec.mainClass=com.byam.beam.examples.app.App\
  -Dexec.args="\
  --runner=${RUNNER} \
  "
