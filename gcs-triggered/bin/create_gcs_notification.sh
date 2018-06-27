#!/usr/bin/env bash

set -o nounset
set -o errexit

GOOGLE_PROJECT= $1
GCS_BUCKET=$2
PUBSUB_TOPIC=$3

# set project
gcloud config set project ${GOOGLE_PROJECT}

# create object notification for bucket
gsutil notification create -t ${PUBSUB_TOPIC} -f json -e OBJECT_FINALIZE gs://${GCS_BUCKET}
