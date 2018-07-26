# beam-examples
Apache Beam is a great library that enables you to build batch/streaming pipelines in the unified way.
Then again, there are some pitfalls and technical challenges when writing practical applications.

This project aims to collect know-hows and best practices about Apache Beam.

## Example summary
|  example  |  description  |
| ---- | ---- |
|  [word-count-beam](word-count-beam)  |  This is most likely your first Apache Beam project.   It includes fundamental features and best practices of Beam.  |
|  [multi-storage](multi-storage)  |  This shows how to push data to multiple storage locations.  |
|  [gcs-triggered](gcs-triggered)  |  This shows how to start processing data in an event-driven way with GCS notification.  |
|  [slowly-changing-lookup-cache](slowly-changing-lookup-cache)  |  Implementation of [Beam SideInput](https://beam.apache.org/documentation/programming-guide/#side-inputs) which updates periodically   |


## How to contribute
You can add an example project by sending PRs.  
Please follow the below procedure:

0. Please write a `README.md` using the [template](README-template.md).
0. Update "Example summary" section of this file.

## Contributors
- @Kenji-H
- @byam
