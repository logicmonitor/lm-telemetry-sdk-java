# **LM Telemetry SDK Java**
[![Java CI with Gradle][ci-image]][ci-url]
[![codecov][codecov-image]][codecov-url]

LM Telemetry SDK java is a wrapper around OpenTelemetry SDK. If you are using LM Telemetry SDK in the application then no need to add OpenTelemetry SDK dependency as it is fetched transitively.
This SDK currently detects Resources on the platforms like <br />
- AWS Elastic Compute Cloud<br />
- AWS Lambda<br />
- Azure VM<br />
- Google Cloud Compute Engine (GCE)<br />

## Getting Started
### Auto Instrumentation
Check [latest agent version](https://github.com/logicmonitor/lm-telemetry-sdk-java/releases/latest)

For java Auto instrumentation Logicmonitor provides lm-agent which have all Opentelemetry provided instrumentation support.<br />


### Auto Instrument your app
```java
java -javaagent:/path/lm-telemetry-agent-<version>-alpha.jar \
-Dotel.exporter=otlp \
-Dotel.resource.attributes=service.namespace=<service-namespace>,service.name=<service-name> \
-Dotel.exporter.otlp.endpoint=http://localhost:4317 \
-Dotel.exporter.otlp.insecure=true \
-jar <your-app-jar>
```

By default, the lm-telemetry Java agent uses OTLP exporter configured to send data to lm-otel collector at http://localhost:4317.
### Manual Instrumentation
Check [Github Package](https://github.com/logicmonitor/lm-telemetry-sdk-java/packages/1615817) 
### Gradle

```groovy
dependencies {
    implementation ('com.logicmonitor:lm-telemetry-sdk:0.0.1-alpha')
}
```

### Maven
```xml
<dependency>
        <groupId>com.logicmonitor</groupId>
        <artifactId>lm-telemetry-sdk</artifactId>
        <version>0.0.1-alpha</version>
</dependency>
```

##### Using LMResourceDetector to detect resource
```java
Resource serviceResource = LMResourceDetector.detect();

//Create Span Exporter
OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
.setEndpoint("http://localhost:4317")
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

For Manual Instrumentation refer [Example](https://github.com/logicmonitor/lm-telemetry-sdk-java/tree/main/example/java-manual-instrumentation)

## OpenTelemetry Component Dependency

List of OpenTelemetry Component Dependencies

| Component                                                                                                                         | Version                                                |
|-----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------|
| [Trace API](https://github.com/open-telemetry/opentelemetry-java/tree/v1.20.1/api)                                                | v<!--VERSION_STABLE-->1.20.1<!--/VERSION_STABLE-->     |
| [Trace SDK](https://github.com/open-telemetry/opentelemetry-java/tree/v1.20.1/sdk)                                                | v<!--VERSION_STABLE-->1.20.1<!--/VERSION_STABLE-->     |
| [OpenTelemetry SDK Autoconfigure](https://github.com/open-telemetry/opentelemetry-java/tree/v1.20.1/sdk-extensions/autoconfigure) | v<!--VERSION_ALPHA-->1.20.1-alpha<!--/VERSION_ALPHA--> |
| [OpenTelemetry AWS Utils](https://github.com/open-telemetry/opentelemetry-java-contrib/tree/v1.20.1/aws-resources)                   | v<!--VERSION_STABLE-->1.20.1<!--/VERSION_STABLE-->      |
| [OpenTelemetry Semantic Conventions](https://github.com/open-telemetry/opentelemetry-java/tree/v1.20.1/semconv)                   | v<!--VERSION_STABLE-->1.20.1<!--/VERSION_STABLE-->     |
| [OpenTelemetry Resource Providers](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/v1.20.1/instrumentation/resources/library)    | v<!--VERSION_STABLE-->1.20.1<!--/VERSION_STABLE-->     |



[ci-image]: https://github.com/logicmonitor/lm-telemetry-sdk-java/actions/workflows/gradle.yml/badge.svg?branch=main
[ci-url]: https://github.com/logicmonitor/lm-telemetry-sdk-java/actions/workflows/gradle.yml
[codecov-image]: https://codecov.io/gh/logicmonitor/lm-telemetry-sdk-java/branch/main/graph/badge.svg?token=ONPPMTKE7F
[codecov-url]: https://codecov.io/gh/logicmonitor/lm-telemetry-sdk-java

