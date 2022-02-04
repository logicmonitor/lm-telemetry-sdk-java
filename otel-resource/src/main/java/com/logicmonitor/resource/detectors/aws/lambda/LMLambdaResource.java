package com.logicmonitor.resource.detectors.aws.lambda;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes.CloudPlatformValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LMLambdaResource {

  private static final Logger logger = LoggerFactory.getLogger(LMLambdaResource.class);

  private static final String AWS_FAAS_ID_FORMAT =
      "arn:aws:lambda:%s:%s:function:%s"; // arn:aws:lambda:<REGION>:<ACCOUNT_ID>:function:<FUNCTION_NAME>

  private static final String AWS_FAAS_ID_FORMAT_WITH_VERSION =
      "arn:aws:lambda:%s:%s:function:%s:%s"; // arn:aws:lambda:<REGION>:<ACCOUNT_ID>:function:<FUNCTION_NAME>:<VERSION>

  public static Resource get(Resource resource, AWSSecurityTokenService client) {
    String accountId = null;

    try {
      GetCallerIdentityRequest request = new GetCallerIdentityRequest();
      GetCallerIdentityResult getCallerIdentityResponse = client.getCallerIdentity(request);
      accountId = getCallerIdentityResponse.getAccount();
    } catch (Exception e) {
      logger.error("Exception to getCallerIdentityResponse", e);
    }

    String region = resource.getAttribute(ResourceAttributes.CLOUD_REGION);
    String functionName = resource.getAttribute(ResourceAttributes.FAAS_NAME);
    String version = resource.getAttribute(ResourceAttributes.FAAS_VERSION);

    AttributesBuilder attrBuilders = Attributes.builder();
    attrBuilders.put(ResourceAttributes.CLOUD_PLATFORM, CloudPlatformValues.AWS_LAMBDA);
    attrBuilders.put(ResourceAttributes.CLOUD_ACCOUNT_ID, accountId);

    if (region != null && accountId != null && functionName != null) {
      if (version.contains("LATEST")) {
        attrBuilders.put(
            ResourceAttributes.FAAS_ID,
            String.format(AWS_FAAS_ID_FORMAT, region, accountId, functionName));
      } else {
        attrBuilders.put(
            ResourceAttributes.FAAS_ID,
            String.format(
                AWS_FAAS_ID_FORMAT_WITH_VERSION, region, accountId, functionName, version));
      }
      return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    } else {
      return resource;
    }
  }
}
