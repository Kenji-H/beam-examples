package com.kenjih.beam.examples.converter;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.beam.sdk.transforms.DoFn;

import com.kenjih.beam.examples.entity.User;

public class UserPubSubConverter extends DoFn<User, String> {

  @ProcessElement
  public void processElement(ProcessContext c) {
    User user = c.element();
    ObjectMapper mapper = new ObjectMapper();
    try {
      String json = mapper.writeValueAsString(user);
      c.output(json);
    } catch (IOException e) {
      // error handling
    }
  }

}
