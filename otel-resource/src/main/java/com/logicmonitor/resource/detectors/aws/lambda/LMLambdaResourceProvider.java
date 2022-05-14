package com.logicmonitor.resource.detectors.aws.lambda;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.extension.aws.resource.LambdaResource;
import io.opentelemetry.sdk.resources.Resource;

public class LMLambdaResourceProvider implements ResourceProvider {

  @Override
  public Resource createResource(ConfigProperties config) {
    Resource resource = LambdaResource.get();

    if (resource == null || resource == Resource.empty()) {
      return Resource.empty();
    }
    AWSSecurityTokenService client = AWSSecurityTokenServiceClientBuilder.standard().build();
    return LMLambdaResource.get(resource, client);
  }
}
