package com.logicmonitor.resource.detectors.gcp.computeEngine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

public class LMComputeEngineResourceProviderTest {

  @Test
  public void ifKServiceIsNull() throws IOException {
    Map<AttributeKey<?>, Object> expectedOutputSet =
        new HashMap<>() {
          {
            put(ResourceAttributes.CLOUD_PROVIDER, "gcp");
            put(ResourceAttributes.CLOUD_PLATFORM, "gcp_compute_engine");
            put(ResourceAttributes.HOST_ID, "i-1234567890abcdef0");
            put(ResourceAttributes.CLOUD_AVAILABILITY_ZONE, "us-west-2");
            put(ResourceAttributes.CLOUD_ACCOUNT_ID, "123456789012");
          }
        };
    MockWebServer server = new MockWebServer();

    server.enqueue(new MockResponse().setBody("i-1234567890abcdef0"));
    server.enqueue(new MockResponse().setBody("123456789012"));
    server.enqueue(new MockResponse().setBody("us-west-2"));

    server.start();
    HttpUrl baseUrl = server.url("/v1");
    Resource resource = LMComputeEngineResource.get(baseUrl.toString());
    Attributes attributes = resource.getAttributes();
    Map<AttributeKey<?>, Object> actualOutputSet = attributes.asMap();
    for (Map.Entry<AttributeKey<?>, Object> entry : actualOutputSet.entrySet()) {
      assertThat(expectedOutputSet, hasEntry(entry.getKey(), entry.getValue()));
    }
    server.shutdown();
  }

  @Test
  @SetEnvironmentVariable(key = "K_SERVICE", value = "cloudFunction")
  public void ifKServiceIsNotNull() {
    Resource computeEngineResource = LMComputeEngineResource.get("dummyURL");
    assertEquals(Resource.empty(), computeEngineResource);
  }
}
