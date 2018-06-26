package com.kenjih.beam.examples;

import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation.Required;

public interface MultiStorageOptions extends GcpOptions {
  @Description("BigQuery dataset name")
  @Required
  String getBigQueryDataset();
  void setBigQueryDataset(String dataset);

  @Description("BigQuery table name")
  @Required
  String getBigQueryTable();
  void setBigQueryTable(String table);

  @Description("PubSub topic name")
  @Required
  String getPubSubTopic();
  void setPubSubTopic(String table);

}
