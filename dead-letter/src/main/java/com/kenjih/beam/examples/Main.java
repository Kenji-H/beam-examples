package com.kenjih.beam.examples;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);
  private static final TupleTag<String> SUCCESS = new TupleTag<String>() {};
  private static final TupleTag<String> INVALID = new TupleTag<String>() {};

  public static void main(String[] args) {

    Options options =
        PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
    Pipeline p = Pipeline.create(options);

    PCollection<String> input = p.apply(
        Create.of(
            "{\"name\":\"Alice\", \"age\":20, \"sex\":\"F\"}",
            "{\"name\":\"Bob\", \"age\":22, \"sex\":\"M\"}",
            "{\"name\":\"Charlie\", \"age\":25, \"sex\":\"M\"}",
            "Dave,30,M"
        )
    );

    PCollectionTuple outputTuple =
        input.apply("transform", ParDo.of(new DoFn<String, String>() {
          @ProcessElement
          public void processElement(ProcessContext c) {
            String s = c.element();
            ObjectMapper mapper = new ObjectMapper();
            try {
              User user = mapper.readValue(s, User.class);
              c.output(SUCCESS, user.toString());
            } catch (Exception e) {
              LOG.error("json parse error", e);
              c.output(INVALID, c.element());
            }
          }
        }).withOutputTags(SUCCESS, TupleTagList.of(INVALID)));

    outputTuple.get(SUCCESS).apply(TextIO.write().to(options.getOutput()));
    outputTuple.get(INVALID).apply(TextIO.write().to(options.getDeadLetter()));

    p.run();
  }

}
