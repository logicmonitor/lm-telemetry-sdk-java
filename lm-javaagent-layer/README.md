# **LM Javaagent Layer**

Layer for running Java applications on AWS Lambda with LM-OpenTelemetry specific resource
detection.

### LM-javaagent

The LogicMonitor Java Agent is bundled into the base of the layer and can be loaded into a Lambda
function by specifying the AWS_LAMBDA_EXEC_WRAPPER=/opt/otel-handler in your Lambda configuration.
The agent will be automatically loaded and instrument your application for all supported libraries.
You will get extra resource attributes using lm-javaagent layer like :

| Attributes     |                                                                                                                        
| ---------------| 
|cloud.region    | 
|cloud.provider  | 
|cloud.platform  |
|cloud.account.id|
|faas.name       |
|faas.version    | 

### Build
`./gradlew clean build`

The layer zip will be present at ` lm-javaagent-layer/build/distributions/lm-opentelemetry-javaagent-layer.zip`.
Upload this zip while creating you lambda layer.

To Auto-Instrument your lambda function use below environment variables

| Environment variable     | Description             |
|--------------------------|-------------------------|
| AWS_LAMBDA_EXEC_WRAPPER  | Specify the file system path of an executable script i.e. /opt/otel-handler |
|OTEL_EXPORTER_OTLP_ENDPOINT | The OTLP traces endpoint to connect to. Example: http://localhost:4317  |
|OTEL_RESOURCE_ATTRIBUTES | Specify resource attributes in the following format: key1=val1,key2=val2,key3=val3|

You can also use environment variable or system properties
listed on [OpenTelemetry Document](https://github.com/open-telemetry/opentelemetry-java/blob/main/sdk-extensions/autoconfigure/README.md#opentelemetry-resource)
to provide resource information.






