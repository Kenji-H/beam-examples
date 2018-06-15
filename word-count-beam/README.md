# word-count-beam
This is most likely your first Apache Beam project.   It includes fundamental features and best practices of Beam.

## What is this

* Count words in an input file

## How to Run
Run the program:

```
$ mvn compile exec:java -Dexec.mainClass=org.apache.beam.examples.WordCount \
     -Dexec.args="--inputFile=pom.xml --output=counts" -Pdirect-runner
```

Then check the results:

```
$ cat counts-*
```

## Reference
* [Apache Beam Java SDK Quickstart] (https://beam.apache.org/get-started/quickstart-java/)
