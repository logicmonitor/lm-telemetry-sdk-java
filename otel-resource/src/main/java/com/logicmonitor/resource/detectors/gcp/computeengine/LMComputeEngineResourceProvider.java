package com.logicmonitor.resource.detectors.gcp.computeengine;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

public class LMComputeEngineResourceProvider implements ResourceProvider {

  @Override
  public Resource createResource(ConfigProperties config) {
    return LMComputeEngineResource.get();
  }
}
