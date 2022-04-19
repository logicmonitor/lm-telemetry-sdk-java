package com.logicmonitor.resource.detectors.common;

// import com.logicmonitor.resource.detectors.aws.ec2.LMEc2Resource;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

public class LMCommonResourceProvider implements ResourceProvider {
  @Override
  public Resource createResource(ConfigProperties config) {
    return LMCommonResource.get();
  }
}
