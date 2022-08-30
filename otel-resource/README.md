## LM Resource Detectors

LM Telemetry SDK uses OpenTelemetry SDK Autoconfiguration Extension which provides SPI for Resource Detection.


Using the OpenTelemetry SDK Autoconfiguration Extension LM SDK will load all the classes found in classpath which implements ResourceProvider interface and call createResource method and will merge all the resources detected by all the different providers.

LM SDK will transitively fetch aws-sdk-extension which contains aws resource providers so AWS specific resource attributes will be detected automatically same will follow for GCP resources.

For the attributes which are not detected by this cloud provider extensions we will have resource module in SDK which will have custom resource providers for each cloud service and those providers will detect missing attributes

This SDK currently detects Resources on the platforms like <br />
- AWS Elastic Compute Cloud<br />
- AWS Lambda<br />
- Azure VM<br />
- Google Cloud Compute Engine (GCE)<br />
