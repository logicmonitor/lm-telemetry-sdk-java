package com.logicmonitor.resource.detectors.azure.vm;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudPlatformValues;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudProviderValues;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LMAzureVMResource {

  private static final Logger logger = LoggerFactory.getLogger(LMAzureVMResource.class);
  private static final String HOST_ID = "host.id";

  static Resource get(HttpClient client) {
    String azureVmId = null;

    try {
      URI url = new URI("http://169.254.169.254/metadata/instance?api-version=2021-02-01");
      HttpRequest request =
          HttpRequest.newBuilder().uri(url).GET().header("Metadata", "true").build();
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        JSONObject responseBody = new JSONObject(response.body());
        JSONObject json = responseBody.getJSONObject("compute");
        azureVmId = json.get("vmId").toString();
      }
    } catch (Exception e) {
      logger.error("Exception:" + e);
    }

    AttributesBuilder attrBuilders = Attributes.builder();
    if (azureVmId != null) {
      attrBuilders.put(HOST_ID, azureVmId);
      attrBuilders.put(ResourceAttributes.CLOUD_PROVIDER, CloudProviderValues.AZURE);
      attrBuilders.put(ResourceAttributes.CLOUD_PLATFORM, CloudPlatformValues.AZURE_VM);
      return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    }
    return Resource.empty();
  }
}
