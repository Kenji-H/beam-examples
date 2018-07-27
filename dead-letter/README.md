# dead-letter
A implementation of "Dealing with bad data" pattern of [Guide to common Cloud Dataflow use-case patterns](https://cloud.google.com/blog/big-data/2017/06/guide-to-common-cloud-dataflow-use-case-patterns-part-1).

## What is this

* Read json data from pseudo input
* Parse json data and write to output file
* If data cannot be parsed, write to dead-letter file

## How to run

### set up
Please set environment variables as follows:

- `OUTPUT`: Path of output file
- `DEAD_LETTER`: Path of dead-letter file 

### run the pipeline
Run the pipeline with the following command:

```bash
mvn compile exec:java \
  -Dexec.mainClass=com.kenjih.beam.examples.Main \
  -Dexec.args="\
  --output=${OUTPUT} \
  --deadLetter=${DEAD_LETTER} \
  " -Pdirect-runner
```

### check the result
You can see the output:

```bash
$ cat ${OUTPUT}*
Alice,20,F
Charlie,25,M
Bob,22,M
```

Then invalid data in deal letter:

```bash
$ cat ${DEAD_LETTER}*
Dave,30,M
```


## Reference
* [Guide to common Cloud Dataflow use-case patterns, Part 1](https://cloud.google.com/blog/big-data/2017/06/guide-to-common-cloud-dataflow-use-case-patterns-part-1)
* [Handling Invalid Inputs in Dataflow](https://cloud.google.com/blog/big-data/2016/01/handling-invalid-inputs-in-dataflow)