package com.kenjih.beam.examples.converter;

import java.util.List;
import java.util.ArrayList;

import org.apache.beam.sdk.transforms.DoFn;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;

import com.kenjih.beam.examples.entity.User;

public class UserBigQueryConverter extends DoFn<User, TableRow> {

  public static final TableSchema SCHEMA;

  static {
    SCHEMA = new TableSchema();
    List<TableFieldSchema> fields = new ArrayList<>();
    fields.add(new TableFieldSchema().setName("name").setType("STRING"));
    fields.add(new TableFieldSchema().setName("age").setType("INTEGER"));
    SCHEMA.setFields(fields);
  }

  @ProcessElement
  public void processElement(ProcessContext c) {
    User user = c.element();

    TableRow row = new TableRow();
    row.set("name", user.getName());
    row.set("age", user.getAge());

    c.output(row);
  }

}
