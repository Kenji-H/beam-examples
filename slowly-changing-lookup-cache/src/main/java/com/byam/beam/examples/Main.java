package com.byam.beam.examples;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.*;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Slowly-changing lookup cache
 * Main input: PubSubIO
 * Side input: BigQuery Table (using BigQuery Java Client)
 */
public class Main
{
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main( String[] args )
    {

        PipelineOptionsFactory.register(Options.class);
        final Options options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(Options.class);

        Pipeline p = Pipeline.create(options);

        // Query for external Slow Changing Database: BigQuery Table.
        String query = String.format("SELECT category_id, description FROM `%s.%s.%s`;",
                options.getBigQueryProject(), options.getBigQueryDataset().get(), options.getBigQueryTable().get());

        // Main Input: Reading streaming data from PubSub Topic
        PCollection<String> mainStream =  p.apply("MainInput Read: Pubsub",
                PubsubIO.readStrings().fromTopic(options.getTopic().get()));

        // Side Input: Reading batch data from BigQuery Table every N seconds.
        final PCollectionView<Map<String, String>> sideInput = p

                // This is trick for emitting a single long element in every N seconds.
                .apply(String.format("Updating every %s seconds", options.getIntervalSeconds().get()),
                        GenerateSequence.from(0).withRate(1, Duration.standardSeconds(options.getIntervalSeconds().get())))

                // Applying it to Fixed Window
                .apply("Assign to Fixed Window", Window
                        .<Long>into(FixedWindows.of(Duration.standardSeconds(options.getIntervalSeconds().get())))
                )

                // Emitted long data trigger this batch read BigQuery client job.
                .apply(new ReadSlowChangingTable("Read BigQuery Table", query, "category_id", "description"))

                // Caching results as Map.
                .apply("View As Map", View.<String, String>asMap());

        // Enriching mainInput with sideInput.
        mainStream
                .apply("Assign to Fixed Window", Window.<String>into(FixedWindows.of(Duration.standardSeconds(1))))

                .apply("Enriching MainInput with SideInput", ParDo.of(new DoFn<String, String>() {

                    @ProcessElement
                    public void processElement(ProcessContext c){

                        String categoryId = c.element();

                        Map<String, String> enrichingData = c.sideInput(sideInput);

                        LOG.info("[Map size]: " + enrichingData.size());
                        LOG.info("[Stream] category id: " + categoryId + " [Enriching Data] description: " + enrichingData.get(categoryId));

                        String output = categoryId + ", " + enrichingData.get(categoryId);

                        c.output(output);
                    }

                }).withSideInputs(sideInput));

        p.run();
    }
}
