package com.logicmonitor.resource.detectors.azure.vm;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import okhttp3.OkHttpClient;

public class LMAzureVMResourceProvider implements ResourceProvider {

  @Override
  public Resource createResource(ConfigProperties config) {
    OkHttpClient client = new OkHttpClient();
    return LMAzureVMResource.get(client);
  }
}
