## Event-Driven 

1. Upload a file to Cloud Storage bucket
2. Object Notification is sent to Cloud Pub Sub topic
3. Cloud Dataflow
    - Read Cloud Pub Sub topic with PubSubIO
    - Parse object notification json to get file path
    - Read Cloud Storage file with TextIO
    - Showing file content as log

## Create Notification for Cloud Storage

```bash
bash bin/create_gcs_notification.sh GOOGLE_PROJECT GCS_BUCKET PUBSUB_TOPIC
``` 

## Deploy

```bash
bash bin/deploy.sh DirectRunner GOOGLE_PROJECT PUBSUB_TOPIC
```
