package com.logicmonitor.resource.detectors.gcp.metadata;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

public class LMGcpResourceProvider implements ResourceProvider {

    @Override
    public Resource createResource(ConfigProperties config) {
        return LMGcpMetadataServlet.get();
    }
}
