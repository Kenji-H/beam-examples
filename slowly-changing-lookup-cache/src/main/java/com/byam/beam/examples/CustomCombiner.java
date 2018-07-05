package com.byam.beam.examples;

import org.apache.beam.sdk.transforms.Combine;

public class CustomCombiner extends Combine.CombineFn<String, String, String> {

    public String createAccumulator() {
        return "";
    }

    public String addInput(String accumulator, String input) {
        return input;
    }

    public String mergeAccumulators(Iterable<String> accumulators) {
        String merged = createAccumulator();
        for (String accum: accumulators) {
            merged = accum;
        }
        return merged;
    }

    public String extractOutput(String accumulator) {
        return accumulator;
    }
}
