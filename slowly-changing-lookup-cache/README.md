# slowly-changing-lookup-cache
An implementation of "Pattern: Slowly-changing lookup cache" pattern of [Guide to common Cloud Dataflow use-case patterns](https://cloud.google.com/blog/big-data/2017/06/guide-to-common-cloud-dataflow-use-case-patterns-part-1).

## What is this

* Reading a streaming data from Cloud PubSub as `mainInput` for pipeline. 
* Enriching `mainInput` data with `sideInput`.
    - In this example, both inputs are `string`, we merging these two and showing as an log.
* `sideInput` can be any external resources. For the convenience, reading a text file from Cloud Storage.
* `sideInput` is a slow changing data. We are updating this every `N` seconds. 

<img src="console.png" width="600">

## How to Run

0. Please substitute appropriate values
    - `${BUCKET}`: Cloud Storage Bucket name.
    - `${RUNNER}`: Beam Runner name. ex: DirectRunner, DataflowRunner
    - `${GOOGLE_PROJECT}`: Google Project name.
    - `${PUBSUB_TOPIC_NAME}`: Cloud Pubsub Topic name.
    
1. Prepare a text file, upload to Cloud Storage.
```bash
echo "world" > data_side_input.txt

gsutil cp data_side_input.txt gs://${BUCKET}/
```

2. Run the Beam Pipeline
```bash
mvn compile exec:java \
  -Dexec.mainClass=com.byam.beam.examples.App\
  -Dexec.args="\
  --runner=${RUNNER} \
  --project=${GOOGLE_PROJECT} \
  --topic=projects/${GOOGLE_PROJECT}/topics/${PUBSUB_TOPIC_NAME} \
  --intervalSeconds=60 \
  --sideInputFilePath=gs://${BUCKET}/data_side_input.txt \
  "
```

* Note that you should create the PubSub topic beforehand.

3. Send a dummy message to Cloud Pubsub. 

Open the new terminal, send dummy messages to Cloud Pubsub. 
```bash
# send a first dummy message
gcloud --project ${GOOGLE_PROJECT} pubsub publish ${PUBSUB_TOPIC_NAME} --message 'Hello'

# update the slow changing data
echo "Apache Beam" > data_side_input.txt
gsutil cp data_side_input.txt gs://${BUCKET}/

# send the second message after 60 seconds
sleep 60
gcloud --project ${GOOGLE_PROJECT} pubsub topics publish ${PUBSUB_TOPIC_NAME} --message 'Hello'
```

Check the pipeline terminal (log), should see same logs as following.
```bash
Hello world             # mainInput + sideInput

Hello Apache Beam       # mainInput + sideInput (updated after 60 seconds) 
```

## Reference
* [Guide to common Cloud Dataflow use-case patterns](https://cloud.google.com/blog/big-data/2017/06/guide-to-common-cloud-dataflow-use-case-patterns-part-1)
