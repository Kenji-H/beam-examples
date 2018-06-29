#!/usr/bin/env bash

set -o nounset
set -o errexit

RUNNER=$1
GOOGLE_PROJECT=$2
TOPIC_NAME=$3
INTERVAL=$4
SIDE_INPUT_FILE_PATH=$5

# set project
gcloud config set project ${GOOGLE_PROJECT}

# run pipeline
mvn compile exec:java \
  -Dexec.mainClass=com.byam.beam.examples.App\
  -Dexec.args="\
  --runner=${RUNNER} \
  --project=${GOOGLE_PROJECT} \
  --topic=projects/${GOOGLE_PROJECT}/topics/${TOPIC_NAME} \
  --interval=${INTERVAL} \
  --sideInputFilePath=${SIDE_INPUT_FILE_PATH} \
  "
