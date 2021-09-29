package com.logicmonitor.example;

import com.logicmonitor.resource.LMResourceDetector;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

public class TestApplication {

    private static final String SERVICE_NAME = "Authentication-Service";

    static {

        Resource serviceResource = LMResourceDetector.detect();

        //Create Span Exporter
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:55680")
            .build();

        //Create SdkTracerProvider
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                .setScheduleDelay(100, TimeUnit.MILLISECONDS).build())
            .setResource(serviceResource)
            .build();

        //This Instance can be used to get tracer if it is not configured as global
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider)
            .buildAndRegisterGlobal();

    }


    public static void main(String[] args) throws InterruptedException {
        Auth auth = new Auth();
        auth.doLogin("testUserName", "testPassword");
        Thread.sleep(1000);
    }

}
