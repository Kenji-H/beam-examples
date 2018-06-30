package com.byam.beam.examples;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.TextIO;
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

/**
 * Slowly-changing lookup cache
 * Main input: PubSubIO
 * Side input: TextIO (Google Cloud Storage File)
 */
public class App
{
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {

        PipelineOptionsFactory.register(Options.class);
        final Options options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(Options.class);

        Pipeline p = Pipeline.create(options);
        final String sideInputFilePath = options.getSideInputFilePath().get();

        PCollection<String> mainStream =  p.apply("MainInput Read: Pubsub",
                PubsubIO.readStrings().fromTopic(options.getTopic().get()));

        PCollection<Long> countingSource = p.apply(String.format("Updating every %s seconds", options.getIntervalSeconds().get()),
                GenerateSequence.from(0).withRate(1, Duration.standardSeconds(options.getIntervalSeconds().get())));

        final PCollectionView<String> sideInputGcs = countingSource
                .apply("Assign to Global Window", Window
                        .<Long>into(new GlobalWindows())
                        .triggering(Repeatedly.forever(AfterProcessingTime.pastFirstElementInPane()))
                        .discardingFiredPanes())

                .apply("Get GCS FilePath", ParDo.of(new DoFn<Long, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){
                        LOG.info("Updating Side Input from GCS. Emitted element: " + c.element());
                        c.output(sideInputFilePath);
                    }
                }))

                .apply("SideInput Read: GCS", TextIO.readAll())

                .apply("ViewAsSingleton", View.<String>asSingleton());

        mainStream.apply("Enriching MainInput with SideInput", ParDo.of(new DoFn<String, String>() {

            @ProcessElement
            public void processElement(ProcessContext c){

                String e = c.element();

                LOG.info("[Merged mainInput and sideInput]: " + e + " " + c.sideInput(sideInputGcs));

                c.output(e);
            }

        }).withSideInputs(sideInputGcs));

        p.run();
    }
}
