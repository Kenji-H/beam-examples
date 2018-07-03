# example-title
Write a concise explanation for your example here.

## What is this

* Read a data from xxxx.
* Convert data
    - Enrich data with xxxx.
    - Group data by xxxx.
* Write data to xxxx.

[you can put some images here if needed.]

## How to run

### set up
Please set environment variables as follows:

- `RUNNER`: Beam Runner name. ex: DirectRunner, DataflowRunner
- `GOOGLE_PROJECT`: Google Project name.
- `PUBSUB_TOPIC`: Pub/Sub topic name.
- `OUTPUT_FILE`: Output file path.

Create a Pub/Sub topic.

```bash
gcloud pubsub topics create --project ${GOOGLE_PROJECT} ${PUBSUB_TOPIC}
```

### run the pipeline
Run the pipeline with the following command:

```bash
mvn compile exec:java \
  -Dexec.mainClass=xxxx \
  -Dexec.args="\
  --runner=${RUNNER} \
  --project=${GOOGLE_PROJECT}
  ...
```

### generate data
Generate an input data for the pipeline as follows:

```bash
gcloud pubsub topics publish --project ${GOOGLE_PROJECT} ${PUBSUB_TOPIC} --message hogehoge
```

### check the result
The result is saved to an output file. You can check the result:

```bash
cat ${OUTPUT_FILE}
```

## Reference
* [link to a reference](https://beam.apache.org/)
* [link to another reference](https://cloud.google.com/dataflow/)
* ...