package com.byam.beam.examples;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.AfterProcessingTime;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.Repeatedly;
import org.apache.beam.sdk.transforms.windowing.Window;
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

        String query = String.format("SELECT category_id, description FROM `%s.%s.%s`;",
                options.getBigQueryProject(), options.getBigQueryDataset().get(), options.getBigQueryTable().get());

        PCollection<String> mainStream =  p.apply("MainInput Read: Pubsub",
                PubsubIO.readStrings().fromTopic(options.getTopic().get()));

        PCollection<Long> countingSource = p.apply(String.format("Updating every %s seconds", options.getIntervalSeconds().get()),
                GenerateSequence.from(0).withRate(1, Duration.standardSeconds(options.getIntervalSeconds().get())));

        // SideInput of pipeline, which is update in every N seconds. Reading a table from BigQuery.
        final PCollectionView<Map<String, String>> sideInput = countingSource
                .apply("Assign to Global Window", Window
                        .<Long>into(new GlobalWindows())
                        .triggering(Repeatedly.forever(AfterProcessingTime.pastFirstElementInPane()))
                        .discardingFiredPanes())

                .apply(new ReadSlowChangingTable("Read BigQuery Table", query, "category_id", "description"))

                .apply("View As Map", View.<String, String>asMap());

        // MainInput of pipeline. Reading streaming data from PubSub. Enriching data with sideInput
        mainStream.apply("Enriching MainInput with SideInput", ParDo.of(new DoFn<String, String>() {

            @DoFn.ProcessElement
            public void processElement(ProcessContext c){

                String categoryId = c.element();

                Map<String, String> enrichingData = c.sideInput(sideInput);

                LOG.info("[Stream] category id: " + categoryId + " [Enriching Data] description: " + enrichingData.get(categoryId));

                c.output(enrichingData.get(categoryId));
            }

        }).withSideInputs(sideInput));

        p.run();
    }
}
