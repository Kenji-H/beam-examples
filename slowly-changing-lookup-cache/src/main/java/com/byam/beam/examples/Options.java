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

    @Description("Google BigQuery Project ID.")
    @Required
    ValueProvider<String> getBigQueryProject();
    void setBigQueryProject(ValueProvider<String> value);

    @Description("Google BigQuery Dataset ID.")
    @Required
    ValueProvider<String> getBigQueryDataset();
    void setBigQueryDataset(ValueProvider<String> value);

    @Description("Google BigQuery Table ID.")
    @Required
    ValueProvider<String> getBigQueryTable();
    void setBigQueryTable(ValueProvider<String> value);
}
