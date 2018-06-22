package com.kenjih.beam.examples;

import java.util.List;
import java.util.Arrays;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.CreateDisposition;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import com.kenjih.beam.examples.converter.UserBigQueryConverter;
import com.kenjih.beam.examples.entity.User;
import com.kenjih.beam.examples.utils.BigQueryUtils;

public class MultiStorage {

  static final List<User> USERS = Arrays.asList(
      new User("Alice", 10),
      new User("Bob", 20),
      new User("Charlie", 30),
      new User("Dan", 40),
      new User("Eve", 50)
  );

  public static void main(String[] args) {

    MultiStorageOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
        .as(MultiStorageOptions.class);
    Pipeline p = Pipeline.create(options);

    PCollection<User> input =
        p.apply("ReadUsers", Create.of(USERS));

    input.apply("ConvertToDataRow", ParDo.of(new UserBigQueryConverter()))
        .apply("WriteToBigQuery", BigQueryIO.writeTableRows()
            .to(BigQueryUtils.getTable(options))
            .withSchema(UserBigQueryConverter.SCHEMA)
            .withCreateDisposition(CreateDisposition.CREATE_IF_NEEDED)
            .withWriteDisposition(WriteDisposition.WRITE_APPEND));

    p.run().waitUntilFinish();

  }

}
