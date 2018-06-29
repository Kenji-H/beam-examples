package com.byam.beam.examples;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation.Required;

public interface Options extends PipelineOptions {

    @Description("Cloud Pub Sub Topic Name.")
    @Required
    String getTopic();
    void setTopic(String value);

    @Description("Side Input Update Interval.")
    @Required
    Integer getInterval();
    void setInterval(Integer value);

    @Description("Cloud Storage File Path.")
    @Required
    String getSideInputFilePath();
    void setSideInputFilePath(String value);
}
