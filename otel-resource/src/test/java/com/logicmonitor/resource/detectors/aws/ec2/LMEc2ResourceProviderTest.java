package com.logicmonitor.resource.detectors.aws.ec2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.jupiter.api.Test;

public class LMEc2ResourceProviderTest {

  @Test
  public void whenProvidedEmptyEc2ResourceThenReturnEmptyResource() {
    Resource resource = LMEc2Resource.buildResource(Resource.empty());
    assertEquals(Resource.empty(), resource);
  }

  @Test
  public void whenProvidedValidEc2ResourceThenReturnResourceWithARN() {
    AttributesBuilder attrBuilders = Attributes.builder();
    attrBuilders.put(ResourceAttributes.HOST_ID, "i-07e70363eb8aa6474");
    attrBuilders.put(ResourceAttributes.CLOUD_ACCOUNT_ID, "148849749053");
    attrBuilders.put(ResourceAttributes.CLOUD_REGION, "us-west-2");

    Resource ec2Resource = Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    Resource lmEc2Resource = LMEc2Resource.buildResource(ec2Resource);
    String arn = lmEc2Resource.getAttribute(AttributeKey.stringKey("aws.arn"));
    assertEquals(arn, "arn:aws:ec2:us-west-2:148849749053:instance/i-07e70363eb8aa6474");
  }

  @Test
  public void whenProvidedNullEc2ResourceThenReturnEmptyResource() {
    Resource lmEc2Resource = LMEc2Resource.buildResource(null);
    assertEquals(Resource.empty(), lmEc2Resource);
  }
}
