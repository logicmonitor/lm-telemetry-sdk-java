package com.logicmonitor.resource.detectors.aws.ec2;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.extension.aws.resource.Ec2Resource;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class LMEc2Resource {

  private static final String AWS_ARN_FORMAT =
      "arn:aws:ec2:%s:%s:instance/%s"; // arn:aws:ec2:<REGION>:<ACCOUNT_ID>:instance/<instance-id>

  private static final String ARN = "aws.arn";

  private static final String CLOUD_PLATFORM = "cloud.platform";

  private static final String AWS_EC2_PLATFORM = "aws_ec2";

  private static final Resource INSTANCE = buildResource(Ec2Resource.get());

  public static Resource get() {
    return INSTANCE;
  }

  static Resource buildResource(Resource resource) {
    String accountId = "";
    String region = "";
    String instanceId = "";
    // if empty means it is not ec2
    if (resource == null || resource == Resource.empty()) {
      return Resource.empty();
    }
    instanceId = resource.getAttribute(ResourceAttributes.HOST_ID);
    region = resource.getAttribute(ResourceAttributes.CLOUD_REGION);
    accountId = resource.getAttribute(ResourceAttributes.CLOUD_ACCOUNT_ID);
    if (instanceId != null && region != null && accountId != null) {
      AttributesBuilder attrBuilders = Attributes.builder();
      attrBuilders.put(ARN, String.format(AWS_ARN_FORMAT, region, accountId, instanceId));
      attrBuilders.put(CLOUD_PLATFORM, AWS_EC2_PLATFORM);

      return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    } else {
      return resource;
    }
  }

  private LMEc2Resource() {}
}
