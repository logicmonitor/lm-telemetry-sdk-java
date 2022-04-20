package com.logicmonitor.resource.detectors.azure.vm;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import java.net.http.HttpClient;

public class LMAzureVMResourceProvider implements ResourceProvider {

  @Override
  public Resource createResource(ConfigProperties config) {
    HttpClient client = HttpClient.newHttpClient();
    return LMAzureVMResource.get(client);
  }
}
