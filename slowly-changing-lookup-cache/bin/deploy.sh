#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

RUNNER=$1
GOOGLE_PROJECT=$2
PUBSUB_TOPIC_NAME=$3
BIGQUERY_PROJECT=$4
BIGQUERY_DATASET=$5
BIGQUERY_TABLE=$6

# set project
gcloud config set project ${GOOGLE_PROJECT}

# Run the Beam Pipeline
mvn compile exec:java \
  -Dexec.mainClass=com.byam.beam.examples.Main\
  -Dexec.args="\
  --runner=${RUNNER} \
  --project=${GOOGLE_PROJECT} \
  --topic=projects/${GOOGLE_PROJECT}/topics/${PUBSUB_TOPIC_NAME} \
  --intervalSeconds=60 \
  --bigQueryProject=${BIGQUERY_PROJECT} \
  --bigQueryDataset=${BIGQUERY_DATASET} \
  --bigQueryTable=${BIGQUERY_TABLE} \
  "
