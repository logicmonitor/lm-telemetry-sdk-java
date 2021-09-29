### Java Manual instrumentation example with LM-SDK

Import this project in your IDE.

Make sure you have build and published sdk repository in your local maven before using this project.

##### Build
```
./gradlew clean build
```
Export below environment variable to provide service name and custom resource attribute.

| Environment variable     | Description                                                                        |
|--------------------------|------------------------------------------------------------------------------------|
| OTEL_RESOURCE_ATTRIBUTES | Specify resource attributes in the following format: key1=val1,key2=val2,key3=val3 |
| OTEL_SERVICE_NAME        | Specify logical service name. Takes precedence over `service.name` defined with `otel.resource.attributes` |

###### Example

```
export OTEL_SERVICE_NAME=Authentication-Service
export OTEL_RESOURCE_ATTRIBUTES=service.namespace=QA
```

###### Run
```
java -jar java-manual-instrumentation-1.0-SNAPSHOT.jar
```
