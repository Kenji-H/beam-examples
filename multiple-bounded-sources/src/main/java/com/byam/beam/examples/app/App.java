package com.byam.beam.examples.app;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {

        System.out.println("Creating Pipeline");

        Pipeline p = Pipeline.create();

        // examples data
        final List<KV<String, String>> emailsList =
                Arrays.asList(
                        KV.of("amy", "amy@example.com"),
                        KV.of("carl", "carl@example.com"),
                        KV.of("julia", "julia@example.com"),
                        KV.of("carl", "carl@email.com"));

        final List<KV<String, String>> phonesList =
                Arrays.asList(
                        KV.of("amy", "111-222-3333"),
                        KV.of("james", "222-333-4444"),
                        KV.of("amy", "333-444-5555"),
                        KV.of("carl", "444-555-6666"));

        // creating input sources
        PCollection<KV<String, String>> emails = p.apply("CreateEmails", Create.of(emailsList));
        PCollection<KV<String, String>> phones = p.apply("CreatePhones", Create.of(phonesList));

        final TupleTag<String> emailsTag = new TupleTag<>();
        final TupleTag<String> phonesTag = new TupleTag<>();

        PCollection<KV<String, CoGbkResult>> results =
                KeyedPCollectionTuple.of(emailsTag, emails)
                        .and(phonesTag, phones)
                        .apply(CoGroupByKey.create());

        PCollection<String> contactLines =
                results.apply(
                        ParDo.of(
                                new DoFn<KV<String, CoGbkResult>, String>() {
                                    @ProcessElement
                                    public void processElement(ProcessContext c) {
                                        KV<String, CoGbkResult> e = c.element();

                                        String name = e.getKey();
                                        Iterable<String> emailsIter = e.getValue().getAll(emailsTag);
                                        Iterable<String> phonesIter = e.getValue().getAll(phonesTag);

                                        String formattedResult =
                                                String.format("%s, %s, %s", name, emailsIter, phonesIter);

                                        c.output(formattedResult);
                                    }
                                }));

        contactLines.apply(TextIO.write().to("contactLines").withNumShards(1));

        p.run();
    }
}
