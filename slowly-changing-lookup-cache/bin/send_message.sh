#!/usr/bin/env bash

set -o nounset
set -o errexit

GOOGLE_PROJECT= $1
PUBSUB_TOPIC=$2
MESSAGE=$3

gcloud --project ${GOOGLE_PROJECT} pubsub topics publish ${PUBSUB_TOPIC} --message ${MESSAGE}
