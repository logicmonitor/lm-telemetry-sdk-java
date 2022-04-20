package com.logicmonitor.resource.detectors.azure.vm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudPlatformValues;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudProviderValues;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LMAzureVMResourceProviderTest {

  HttpClient httpClient;
  HttpResponse mockedResponse;

  @BeforeEach
  public void setUp() {
    httpClient = Mockito.mock(HttpClient.class);
    mockedResponse = Mockito.mock(HttpResponse.class);
  }

  @Test
  public void whenProvidedValidResponseThenReturnHostId() throws Exception {
    AttributesBuilder attrBuilders = Attributes.builder();
    attrBuilders.put(ResourceAttributes.CLOUD_PROVIDER, CloudProviderValues.AZURE);
    attrBuilders.put(ResourceAttributes.CLOUD_PLATFORM, CloudPlatformValues.AZURE_VM);
    attrBuilders.put("host.id", "hostId");
    Mockito.when(mockedResponse.statusCode()).thenReturn(200);
    Mockito.when(mockedResponse.body())
        .thenReturn("{\n" + "  \"compute\" : {\n" + "    \"vmId\" : \"hostId\"\n" + "  }\n" + "}");
    Mockito.when(
            httpClient.send(
                Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockedResponse);
    Resource azureVMResource = Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    Resource lmAzureVMResource = LMAzureVMResource.get(httpClient);
    assertEquals(azureVMResource, lmAzureVMResource);
  }

  @Test
  public void whenProvidedInvalidResponse() throws Exception {
    AttributesBuilder attrBuilders = Attributes.builder();
    Mockito.when(
            httpClient.send(
                Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockedResponse);
    Resource azureVMResource = Resource.create(attrBuilders.build(), null);
    Resource lmAzureVMResource = LMAzureVMResource.get(httpClient);
    assertEquals(azureVMResource, lmAzureVMResource);
  }
}
