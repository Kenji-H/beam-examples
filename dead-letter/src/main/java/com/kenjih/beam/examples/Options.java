package com.kenjih.beam.examples;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation.Required;

public interface Options extends PipelineOptions {
  @Description("output file path")
  @Required
  String getOutput();
  void setOutput(String output);

  @Description("dead Letter file path")
  @Required
  String getDeadLetter();
  void setDeadLetter(String deadLetter);
}
