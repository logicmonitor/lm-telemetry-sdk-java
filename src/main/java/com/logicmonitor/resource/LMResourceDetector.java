package com.logicmonitor.resource;

import io.opentelemetry.sdk.autoconfigure.OpenTelemetryResourceAutoConfiguration;
import io.opentelemetry.sdk.resources.Resource;

public class LMResourceDetector {
  public static Resource detect() {
    return OpenTelemetryResourceAutoConfiguration.configureResource();
  }
}
