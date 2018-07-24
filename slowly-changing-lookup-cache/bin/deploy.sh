#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

RUNNER=$1
GOOGLE_PROJECT=$2
PUBSUB_TOPIC_NAME=$3
BQ_PROJECT_ID=$4
BQ_DATASET_ID=$5
BQ_TABLE_ID=$6
INTERVAL_SECONDS=$7

# set project
gcloud config set project ${GOOGLE_PROJECT}

# Run the Beam Pipeline
mvn compile exec:java \
  -Dexec.mainClass=com.byam.beam.examples.Main\
  -Dexec.args="\
  --runner=${RUNNER} \
  --project=${GOOGLE_PROJECT} \
  --topic=projects/${GOOGLE_PROJECT}/topics/${PUBSUB_TOPIC_NAME} \
  --intervalSeconds=${INTERVAL_SECONDS} \
  --bigQueryProject=${BQ_PROJECT_ID} \
  --bigQueryDataset=${BQ_DATASET_ID} \
  --bigQueryTable=${BQ_TABLE_ID} \
  "
