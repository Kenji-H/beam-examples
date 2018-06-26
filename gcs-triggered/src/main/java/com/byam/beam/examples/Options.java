package com.byam.beam.examples;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.options.ValueProvider;

public interface Options extends PipelineOptions {

    @Description("Cloud Pub Sub Topic Name.")
    @Required
    ValueProvider<String> getTopic();
    void setTopic(ValueProvider<String> value);
}
