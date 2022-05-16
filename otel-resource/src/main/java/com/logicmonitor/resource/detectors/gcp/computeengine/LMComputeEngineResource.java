package com.logicmonitor.resource.detectors.gcp.computeEngine;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LMComputeEngineResource {
  private static final Logger logger = LoggerFactory.getLogger(LMComputeEngineResource.class);

  public static Resource get(String baseURL) {
    AttributesBuilder attrBuilders = Attributes.builder();
    if (System.getenv("K_SERVICE") == null) {
      attrBuilders.put(
          ResourceAttributes.CLOUD_PROVIDER, ResourceAttributes.CloudProviderValues.GCP);
      attrBuilders.put(
          ResourceAttributes.CLOUD_PLATFORM,
          ResourceAttributes.CloudPlatformValues.GCP_COMPUTE_ENGINE);
      String hostId = getGcpResource(baseURL + "/computeMetadata/v1/instance/id");
      if (hostId != null) {
        attrBuilders.put(ResourceAttributes.HOST_ID, hostId);
      }
      String projectId = getGcpResource(baseURL + "/computeMetadata/v1/project/project-id");
      if (projectId != null) {
        attrBuilders.put(ResourceAttributes.CLOUD_ACCOUNT_ID, projectId);
      }
      String zone = getGcpResource(baseURL + "/computeMetadata/v1/instance/zone");
      if (zone != null) {
        attrBuilders.put(ResourceAttributes.CLOUD_AVAILABILITY_ZONE, zone);
      }
      return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    }
    return Resource.empty();
  }

  private static String getGcpResource(String key) {
    String resource = null;
    try {
      URL url = new URL(key);
      HttpURLConnection conn = (HttpURLConnection) (url.openConnection());
      conn.setRequestProperty("Metadata-Flavor", "Google");
      resource = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      conn.disconnect();
    } catch (Exception e) {
      logger.error("Exception:" + e);
    }
    return resource;
  }
}
