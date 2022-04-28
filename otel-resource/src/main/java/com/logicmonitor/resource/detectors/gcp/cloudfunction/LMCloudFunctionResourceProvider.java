package com.logicmonitor.resource.detectors.gcp.cloudfunction;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

public class LMCloudFunctionResourceProvider implements ResourceProvider {
  @Override
  public Resource createResource(ConfigProperties config) {
    return LMCloudFunctionResource.get();
  }
}
