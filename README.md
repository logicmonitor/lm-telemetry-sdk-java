# **LM Java OpenTelemetry SDK**
[![Java CI with Gradle][ci-image]][ci-url]

LM Java OpenTelemetry SDK is a wrapper around OpenTelemetry SDK. If you are using LM OpenTelemetry SDK in the application then no need to add OpenTelemetry SDK dependency as it is fetched transitively.

## Build

`./gradlew clean build publishToMavenLocal`

Above command will build the sdk and install in your local maven repository.

## Use

### Gradle

```groovy
dependencies {
    implementation ('com.logicmonitor:lm-opentelemetry-sdk:0.1.0-alpha')
}
```

### Maven
```xml
<dependency>
        <groupId>com.logicmonitor</groupId>
        <artifactId>lm-opentelemetry-sdk</artifactId>
        <version>0.1.0-alpha</version>
</dependency>
```

##### Using LMResourceDetector to detect resource
```java
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
```

You can also use environment variable or system properties
listed on [OpenTelemetry Document](https://github.com/open-telemetry/opentelemetry-java/blob/main/sdk-extensions/autoconfigure/README.md#opentelemetry-resource)
to provide resource information.

| System property          | Environment variable     | Description                                                                        |
|--------------------------|--------------------------|------------------------------------------------------------------------------------|
| otel.resource.attributes | OTEL_RESOURCE_ATTRIBUTES | Specify resource attributes in the following format: key1=val1,key2=val2,key3=val3 |
| otel.service.name        | OTEL_SERVICE_NAME        | Specify logical service name. Takes precedence over `service.name` defined with `otel.resource.attributes` |



## OpenTelemetry Component Dependency

List of OpenTelemetry Component Dependencies

| Component                                                                                                                            | Version |
| ---------------------------                                                                                                          | ------- |
| [Trace API](https://github.com/open-telemetry/opentelemetry-java/tree/v1.6.0/api)                                                    | v<!--VERSION_STABLE-->1.6.0<!--/VERSION_STABLE-->  |
| [Trace SDK](https://github.com/open-telemetry/opentelemetry-java/tree/v1.6.0/sdk)                                                    | v<!--VERSION_STABLE-->1.6.0<!--/VERSION_STABLE-->  |
| [OpenTelemetry SDK Autoconfigure](https://github.com/open-telemetry/opentelemetry-java/tree/v1.6.0/sdk-extensions/autoconfigure)     | v<!--VERSION_ALPHA-->1.6.0-alpha<!--/VERSION_ALPHA-->  |
| [OpenTelemetry AWS Utils](https://github.com/open-telemetry/opentelemetry-java/tree/v1.6.0/sdk-extensions/aws)                       | v<!--VERSION_STABLE-->1.6.0<!--/VERSION_STABLE-->  |
| [OpenTelemetry Semantic Conventions](https://github.com/open-telemetry/opentelemetry-java/tree/v1.6.0/semconv)                       | v<!--VERSION_STABLE-->1.6.0<!--/VERSION_STABLE-->  |
| [OpenTelemetry Resource Providers](https://github.com/open-telemetry/opentelemetry-java/tree/v1.6.0/sdk-extensions/resources)        | v<!--VERSION_STABLE-->1.6.0<!--/VERSION_STABLE-->  |



[ci-image]: https://github.com/logicmonitor/lm-telemetry-sdk-java/actions/workflows/gradle.yml/badge.svg?branch=main
[ci-url]: https://github.com/logicmonitor/lm-telemetry-sdk-java/actions/workflows/gradle.yml


