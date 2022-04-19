package com.logicmonitor.resource.detectors.common;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class LMCommonResource {
  private static final Resource INSTANCE = buildResource();

  public static Resource get() {
    return INSTANCE;
  }

  static Resource buildResource() {
    AttributesBuilder attrBuilders = Attributes.builder();
    attrBuilders.put(ResourceAttributes.TELEMETRY_SDK_NAME, "lm-opentelemetry");

    return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
  }
}
