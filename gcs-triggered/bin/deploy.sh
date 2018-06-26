#!/usr/bin/env bash

set -o nounset
set -o errexit

RUNNER=$1
GOOGLE_PROJECT=$2
TOPIC_NAME=$3

# set project
gcloud config set project ${GOOGLE_PROJECT}

# run pipeline
mvn compile exec:java \
  -Dexec.mainClass=com.byam.beam.examples.GcsTriggered\
  -Dexec.args="\
  --runner=${RUNNER} \
  --project=${GOOGLE_PROJECT} \
  --topic=projects/${GOOGLE_PROJECT}/topics/${TOPIC_NAME} \
  " \
  -Dexec.cleanupDaemonThreads=false
