package com.logicmonitor.resource.detectors.aws.ec2;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

public class LMEc2ResourceProvider implements ResourceProvider {

  @Override
  public Resource createResource(ConfigProperties config) {
    return LMEc2Resource.get();
  }
}
