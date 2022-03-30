# **LM Javagent Layer**

Layers for running Java applications on AWS Lambda with LM-OpenTelemetry specific for resource
detection.

### LM-javaagent

The LogicMonitor Java Agent is bundled into the base of the layer and can be loaded into a Lambda
function by specifying the AWS_LAMBDA_EXEC_WRAPPER=/opt/otel-handler in your Lambda configuration.
The agent will be automatically loaded and instrument your application for all supported libraries.


