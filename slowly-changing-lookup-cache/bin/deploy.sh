#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

BUCKET=$1
RUNNER=$2
GOOGLE_PROJECT=$3
PUBSUB_TOPIC_NAME=$4

# set project
gcloud config set project ${GOOGLE_PROJECT}

# Prepare a text file, upload to Cloud Storage.
echo "world" > data_side_input.txt
gsutil cp data_side_input.txt gs://${BUCKET}/

# Run the Beam Pipeline
mvn compile exec:java \
  -Dexec.mainClass=com.byam.beam.examples.App\
  -Dexec.args="\
  --runner=${RUNNER} \
  --project=${GOOGLE_PROJECT} \
  --topic=projects/${GOOGLE_PROJECT}/topics/${PUBSUB_TOPIC_NAME} \
  --intervalSeconds=60 \
  --sideInputFilePath=gs://${BUCKET}/data_side_input.txt \
  "
