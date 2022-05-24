package com.logicmonitor.resource.detectors.azure.vm;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudPlatformValues;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudProviderValues;
import java.net.URL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LMAzureVMResource {

  private static final Logger logger = LoggerFactory.getLogger(LMAzureVMResource.class);
  private static final String HOST_ID = "host.id";

  static Resource get(OkHttpClient client) {
    String azureVmId = null;

    try {
      URL url = new URL("http://169.254.169.254/metadata/instance?api-version=2021-02-01");
      Request request = new Request.Builder().url(url).get().header("Metadata", "true").build();
      Response response = client.newCall(request).execute();

      if (response.code() == 200) {
        JSONObject responseBody = new JSONObject(response.body().string());
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
