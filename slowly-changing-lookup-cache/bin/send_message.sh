#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

GOOGLE_PROJECT=$1
PUBSUB_TOPIC_NAME=$2
MESSAGE=$3

# send dummy messages to Cloud Pubsub.
gcloud --project ${GOOGLE_PROJECT} pubsub topics publish ${PUBSUB_TOPIC_NAME} --message ${MESSAGE}
