#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

GOOGLE_PROJECT=$1
PUBSUB_TOPIC_NAME=$2
BUCKET=$3

# send dummy messages to Cloud Pubsub.
gcloud --project ${GOOGLE_PROJECT} pubsub topics publish ${PUBSUB_TOPIC_NAME} --message 'Hello'

# update the slow changing data
echo "Apache Beam" > data_side_input.txt
gsutil cp data_side_input.txt gs://${BUCKET}/

# send the second message after 60 seconds
sleep 60
gcloud --project ${GOOGLE_PROJECT} pubsub topics publish ${PUBSUB_TOPIC_NAME} --message 'Hello'
