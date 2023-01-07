package com.logicmonitor.resource.detectors.aws.ec2;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.contrib.aws.resource.Ec2Resource;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class LMEc2Resource {

  private static final String AWS_ARN_FORMAT =
      "arn:aws:ec2:%s:%s:instance/%s"; // arn:aws:ec2:<REGION>:<ACCOUNT_ID>:instance/<instance-id>

  private static final String ARN = "aws.arn";

  private static final Resource INSTANCE = buildResource(Ec2Resource.get());

  public static Resource get() {
    return INSTANCE;
  }

  static Resource buildResource(Resource resource) {
    // if empty means it is not ec2
    if (resource == null || resource == Resource.empty()) {
      return Resource.empty();
    }
    String instanceId = resource.getAttribute(ResourceAttributes.HOST_ID);
    String region = resource.getAttribute(ResourceAttributes.CLOUD_REGION);
    String accountId = resource.getAttribute(ResourceAttributes.CLOUD_ACCOUNT_ID);
    if (instanceId != null && region != null && accountId != null) {
      AttributesBuilder attrBuilders = Attributes.builder();
      attrBuilders.put(ARN, String.format(AWS_ARN_FORMAT, region, accountId, instanceId));

      return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    } else {
      return resource;
    }
  }

  private LMEc2Resource() {}
}
