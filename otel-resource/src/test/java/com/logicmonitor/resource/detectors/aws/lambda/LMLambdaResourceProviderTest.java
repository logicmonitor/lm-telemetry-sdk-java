package com.logicmonitor.resource.detectors.aws.lambda;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LMLambdaResourceProviderTest {

  AWSSecurityTokenService client;

  @BeforeEach
  public void setUp() {
    client = Mockito.mock(AWSSecurityTokenService.class);
  }

  @Test
  public void whenProvidedEmptyLambdaResourceThenReturnEmptyResource() {
    Resource resource = LMLambdaResource.get(Resource.empty(), client);
    assertEquals(Resource.empty(), resource);
  }

  @Test
  public void whenProvidedValidLambdaResourceThenReturnResourceWithAccountId() {
    AttributesBuilder attrBuilders = Attributes.builder();
    attrBuilders.put(ResourceAttributes.CLOUD_REGION, "us-west-2");
    attrBuilders.put(ResourceAttributes.FAAS_NAME, "java-instrumentation");
    attrBuilders.put(ResourceAttributes.FAAS_VERSION, "$LATEST");

    GetCallerIdentityResult getCallerIdentityResponse = new GetCallerIdentityResult();
    getCallerIdentityResponse.setAccount("accountId");
    Mockito.when(client.getCallerIdentity(Mockito.any(GetCallerIdentityRequest.class)))
        .thenReturn(getCallerIdentityResponse);

    Resource lambdaResource = Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    Resource lmLambdaResource = LMLambdaResource.get(lambdaResource, client);
    String accountId = lmLambdaResource.getAttribute(AttributeKey.stringKey("cloud.account.id"));
    assertEquals(accountId, "accountId");
  }

  @Test
  public void whenProvidedNullAccountId() {
    AttributesBuilder attrBuilders = Attributes.builder();
    attrBuilders.put(ResourceAttributes.CLOUD_REGION, "us-west-2");
    attrBuilders.put(ResourceAttributes.FAAS_NAME, "java-instrumentation");
    attrBuilders.put(ResourceAttributes.FAAS_VERSION, "$LATEST");

    GetCallerIdentityResult getCallerIdentityResponse = new GetCallerIdentityResult();
    getCallerIdentityResponse.setAccount(null);
    Mockito.when(client.getCallerIdentity(Mockito.any(GetCallerIdentityRequest.class)))
        .thenReturn(getCallerIdentityResponse);

    Resource lambdaResource = Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    Resource lmLambdaResource = LMLambdaResource.get(lambdaResource, client);
    String accountId = lmLambdaResource.getAttribute(AttributeKey.stringKey("cloud.account.id"));
    assertEquals(accountId, null);
  }
}
