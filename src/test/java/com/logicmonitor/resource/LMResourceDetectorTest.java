package com.logicmonitor.resource;

import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LMResourceDetectorTest {

  @Test
  public void emptyResourceTest() {
    Resource resource = LMResourceDetector.detect();
    Assertions.assertEquals(ResourceAttributes.SCHEMA_URL, resource.getSchemaUrl());
  }
}
