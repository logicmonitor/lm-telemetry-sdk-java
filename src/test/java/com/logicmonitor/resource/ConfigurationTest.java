package com.logicmonitor.resource;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigurationTest {

  Configuration configuration = new Configuration(System.getProperties(), System.getenv());

  @Test
  public void configuration() {
    Configuration conf = new Configuration(System.getProperties(), System.getenv());
    Assertions.assertTrue(true);
  }

  @Test
  public void configureResource() {
    Resource resource = configuration.configureResource();
    Assertions.assertEquals(ResourceAttributes.SCHEMA_URL, resource.getSchemaUrl());
  }

  @Test
  public void get() {
    ConfigProperties configProperties = configuration.get();
    Resource resource = configuration.configureResource(configProperties);
    Assertions.assertEquals(ResourceAttributes.SCHEMA_URL, resource.getSchemaUrl());
  }
}
