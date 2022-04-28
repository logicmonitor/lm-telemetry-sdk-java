package com.logicmonitor.resource.detectors.gcp.cloudfunction;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// import static com.logicmonitor.resource.detectors.extensions.GcpExtension.getGcpResource;

public class LMCloudFunctionResource {
  private static final Resource INSTANCE = buildResource();

  public static Resource get() {
    return INSTANCE;
  }

  static Resource buildResource() {
    AttributesBuilder attrBuilders = Attributes.builder();
    try {
      attrBuilders.put(
          "projectId", getGcpResource("/computeMetadata/v1/project/numeric-project-id"));
      attrBuilders.put("Region", getGcpResource("/computeMetadata/v1/instance/zone"));
    } catch (Throwable t) {
    }
    return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
  }

  public static String getGcpResource(String key) {
    String resource = null;
    HttpURLConnection conn = null;
    try {
      URL url = new URL("http://metadata.google.internal" + key);
      conn = (HttpURLConnection) (url.openConnection());
      conn.setRequestProperty("Metadata-Flavor", "Google");
      resource = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      conn.disconnect();
    } catch (Throwable t) {
    }
    return resource;
  }
}
