package com.byam.beam.examples;

import com.google.cloud.bigquery.*;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class ReadSlowChangingTable extends PTransform<PCollection<Long>, PCollection<KV<String, String>>> {
    private static final Logger LOG = LoggerFactory.getLogger(ReadSlowChangingTable.class);
    private final String query;
    private final String key;
    private final String value;

    ReadSlowChangingTable(@Nullable String name, String query, String key, String value) {
        super(name);
        this.query = query;
        this.key   = key;
        this.value = value;
    }

    public PCollection<KV<String, String>> expand(PCollection<Long> input) {
        return input.apply("Read BigQuery Table.", ParDo.of(new DoFn<Long, KV<String, String>>() {
            @ProcessElement
            public void processElement(ProcessContext c) throws InterruptedException {

                Long e = c.element();
                LOG.info("LONG: " + e);

                BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

                QueryJobConfiguration queryConfig =
                        QueryJobConfiguration.newBuilder(
                                query)
                                .setUseLegacySql(false)
                                .build();

                // Create a job ID so that we can safely retry.
                JobId jobId = JobId.of(UUID.randomUUID().toString());
                Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

                // Wait for the query to complete.
                queryJob = queryJob.waitFor();

                // Check for errors
                if (queryJob == null) {
                    throw new RuntimeException("Job no longer exists");
                } else if (queryJob.getStatus().getError() != null) {
                    // You can also look at queryJob.getStatus().getExecutionErrors() for all
                    // errors, not just the latest one.
                    throw new RuntimeException(queryJob.getStatus().getError().toString());
                }

                // Get the results.
                QueryResponse response = bigquery.getQueryResults(jobId);
                TableResult result = queryJob.getQueryResults();

                // Print all pages of the results.
                for (FieldValueList row : result.iterateAll()) {

                    String keyInstance = row.get(key).getStringValue();
                    String valueInstance = row.get(value).getStringValue();

                    LOG.info("key: " + keyInstance + ", value: " + valueInstance);

                    c.output(KV.of(keyInstance, valueInstance));
                }
            }
        }
        ));
    }
}
