package com.logicmonitor.resource.detectors.azure.vm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudPlatformValues;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudProviderValues;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LMAzureVMResourceProviderTest {

  OkHttpClient httpClient;

  @BeforeEach
  public void setUp() {
    httpClient = Mockito.mock(OkHttpClient.class);
  }

  @Test
  public void whenProvidedValidResponseThenReturnHostId() throws Exception {
    AttributesBuilder attrBuilders = Attributes.builder();
    attrBuilders.put(ResourceAttributes.CLOUD_PROVIDER, CloudProviderValues.AZURE);
    attrBuilders.put(ResourceAttributes.CLOUD_PLATFORM, CloudPlatformValues.AZURE_VM);
    attrBuilders.put("host.id", "hostId");
    Call call = Mockito.mock(Call.class);
    Mockito.when(httpClient.newCall(Mockito.any(Request.class))).thenReturn(call);
    ResponseBody body =
        ResponseBody.create(
            null, "{\n" + "  \"compute\" : {\n" + "    \"vmId\" : \"hostId\"\n" + "  }\n" + "}");
    Request request = new Request.Builder().url("http://dummysupport").build();
    Protocol protocol = Protocol.get("http/1.1");
    Response response =
        new Response.Builder()
            .body(body)
            .request(request)
            .protocol(protocol)
            .code(200)
            .message("OK")
            .build();
    Mockito.when(call.execute()).thenReturn(response);

    Resource azureVMResource = Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    Resource lmAzureVMResource = LMAzureVMResource.get(httpClient);
    assertEquals(azureVMResource, lmAzureVMResource);
  }

  @Test
  public void whenProvidedInvalidResponse() {
    AttributesBuilder attrBuilders = Attributes.builder();
    Resource azureVMResource = Resource.create(attrBuilders.build(), null);
    Resource lmAzureVMResource = LMAzureVMResource.get(httpClient);
    assertEquals(azureVMResource, lmAzureVMResource);
  }
}
