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

        PCollection<String> mainStream =  p.apply("Main Stream PubSubIO",
                PubsubIO.readStrings().fromTopic(options.getTopic()));

        PCollection<Long> countingSource = p.apply(String.format("Emitting Element Every %s Seconds", options.getInterval()),
                GenerateSequence.from(0).withRate(1, Duration.standardSeconds(options.getInterval())));

        final PCollectionView<String> sideInputGcs = countingSource
                .apply("Place counting source into Global Window", Window
                        .<Long>into(new GlobalWindows())
                        .triggering(Repeatedly.forever(AfterProcessingTime.pastFirstElementInPane()))
                        .discardingFiredPanes())

                .apply("Set Side Input File Path", ParDo.of(new DoFn<Long, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){
                        LOG.info("Updating Side Input from GCS. Emitted element: " + c.element());
                        c.output(options.getSideInputFilePath());
                    }
                }))

                .apply("Reading GCS File", TextIO.readAll())

                .apply("To View ???", View.<String>asSingleton());

        mainStream.apply(ParDo.of(new DoFn<String, String>() {

            @ProcessElement
            public void processElement(ProcessContext c){

                String e = c.element();

                LOG.info("main input element: " + e);

                LOG.info("side input element: " + c.sideInput(sideInputGcs));

                c.output(e);
            }

        }).withSideInputs(sideInputGcs));

        p.run();
    }
}
