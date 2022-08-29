### Java Manual instrumentation example with lm-telemetry-sdk

Import this project in your IDE.

####Prerequisites
Generate a personal access token for your github account with repo and read:packages scope.
[Follow these steps.](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)

Before build set your GITHUB_USERNAME and GITHUB_TOKEN as environment variable.
##### Build
```
./gradlew clean build
```
Export below environment variable to provide service name and custom resource attribute.

For more details [refer](https://github.com/logicmonitor/lm-telemetry-sdk-java/packages/1615817)

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
