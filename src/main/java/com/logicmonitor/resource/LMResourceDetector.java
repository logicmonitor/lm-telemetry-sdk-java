package com.logicmonitor.resource;

import io.opentelemetry.sdk.resources.Resource;

public class LMResourceDetector {

  static Configuration conf = new Configuration(System.getProperties(), System.getenv());

  public static Resource detect() {

    return conf.configureResource();
  }

  private LMResourceDetector() {}
}
