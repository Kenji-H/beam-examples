package com.kenjih.beam.examples.utils;

import com.google.api.services.bigquery.model.TableReference;

import com.kenjih.beam.examples.MultiStorageOptions;

public class BigQueryUtils {

  public static TableReference getTable(MultiStorageOptions options) {
    TableReference table = new TableReference();
    table.setProjectId(options.getProject());
    table.setDatasetId(options.getBigQueryDataset());
    table.setTableId(options.getBigQueryTable());
    return table;
  }

}
