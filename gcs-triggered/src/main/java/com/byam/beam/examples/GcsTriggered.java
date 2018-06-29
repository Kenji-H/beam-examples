package com.byam.beam.examples;


import com.google.gson.Gson;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Event-driven example.
 * Upload file to Cloud Storage, it triggers Dataflow to read that file.
 */
public class GcsTriggered
{

    private static final Logger LOG = LoggerFactory.getLogger(GcsTriggered.class);


    public static void main( String[] args )
    {

        PipelineOptionsFactory.register(Options.class);
        Options options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(Options.class);

        Pipeline p = Pipeline.create(options);
        p
                .apply("Read PubSub Topic", PubsubIO.readStrings().fromTopic(options.getTopic()))

                .apply("Parse FilePath", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){

                        Gson gson = new Gson();

                        StorageNotification notification = gson.fromJson(c.element(), StorageNotification.class);

                        String filePath = String.format("gs://%s/%s", notification.bucket, notification.name);

                        LOG.info("[Created File]: " + filePath);

                        c.output(filePath);

                    }
                }))

                .apply("Read File from Cloud Storage", TextIO.readAll())

                .apply("Show content", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){

                        LOG.info("[File Content]: " + c.element());
                        c.output(c.element());
                    }
                }))
        ;

        p.run();

    }
}
