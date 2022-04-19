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

  public static Resource get(Resource resource, AWSSecurityTokenService client) {
    String accountId = null;

    try {
      GetCallerIdentityRequest request = new GetCallerIdentityRequest();
      GetCallerIdentityResult getCallerIdentityResponse = client.getCallerIdentity(request);
      accountId = getCallerIdentityResponse.getAccount();
    } catch (Exception e) {
      logger.error("Exception to getCallerIdentityResponse", e);
    }

    if (accountId != null) {
      AttributesBuilder attrBuilders = Attributes.builder();
      attrBuilders.put(ResourceAttributes.CLOUD_PLATFORM, CloudPlatformValues.AWS_LAMBDA);
      attrBuilders.put(ResourceAttributes.CLOUD_ACCOUNT_ID, accountId);

      return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
    } else {
      return resource;
    }
  }
}
