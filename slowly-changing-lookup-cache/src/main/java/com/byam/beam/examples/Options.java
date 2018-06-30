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

    @Description("Side Input Update Interval.")
    @Required
    ValueProvider<Integer> getIntervalSeconds();
    void setIntervalSeconds(ValueProvider<Integer> value);

    @Description("Cloud Storage File Path.")
    @Required
    ValueProvider<String> getSideInputFilePath();
    void setSideInputFilePath(ValueProvider<String> value);
}
