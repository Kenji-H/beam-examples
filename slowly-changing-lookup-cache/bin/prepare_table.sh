#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

GOOGLE_PROJECT=$1
DATASET_ID=$2
TABLE_ID=$3

# prepare sample data
cat <<EOF > store_data.csv
ABC100,sports and outdoors
ABC101,furniture
ABC102,handmade
ABC103,grocery
ABC104,fashion
EOF

# load data to BQ
bq --project_id ${GOOGLE_PROJECT} --dataset_id ${DATASET_ID} \
    load --source_format=CSV --replace ${TABLE_ID} store_data.csv category_id:STRING,description:STRING

# clean sample data
rm store_data.csv
