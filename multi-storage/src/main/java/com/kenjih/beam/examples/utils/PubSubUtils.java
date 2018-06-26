package com.kenjih.beam.examples.utils;

import com.kenjih.beam.examples.MultiStorageOptions;

public class PubSubUtils {

  public static String getTopicPath(MultiStorageOptions options) {
    return "projects/" + options.getProject()
        + "/topics/" + options.getPubSubTopic();
  }

}
