package com.byam.beam.examples;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation.Required;

public interface Options extends PipelineOptions {

    @Description("Cloud Pub Sub Topic Name.")
    @Required
    String getTopic();
    void setTopic(String value);
}
