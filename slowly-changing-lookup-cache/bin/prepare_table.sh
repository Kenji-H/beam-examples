#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

BQ_PROJECT_ID=$1
BQ_DATASET_ID=$2
BQ_TABLE_ID=$3

# prepare sample data
cat <<EOF > store_data.csv
ABC100,sports and outdoors
ABC101,furniture
ABC102,handmade
ABC103,grocery
ABC104,fashion
EOF

# load data to BQ
bq --project_id ${BQ_PROJECT_ID} --dataset_id ${BQ_DATASET_ID} \
    load --source_format=CSV --replace ${BQ_TABLE_ID} store_data.csv category_id:STRING,description:STRING

# clean sample data
rm store_data.csv
