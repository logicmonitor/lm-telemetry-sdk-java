package com.logicmonitor.resource;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurationException;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

class Configuration implements ConfigProperties {

  private final Map<String, String> config;

  static final String ATTRIBUTE_PROPERTY = "otel.resource.attributes";
  static final String SERVICE_NAME_PROPERTY = "otel.service.name";

  Configuration(Map<?, ?> systemProperties, Map<String, String> environmentVariables) {
    Map<String, String> config = new HashMap<>();
    environmentVariables.forEach(
        (name, value) -> config.put(name.toLowerCase(Locale.ROOT).replace('_', '.'), value));
    systemProperties.forEach(
        (key, value) ->
            config.put(((String) key).toLowerCase(Locale.ROOT).replace('-', '.'), (String) value));

    this.config = config;
  }

  static ConfigProperties get() {
    return new Configuration(System.getProperties(), System.getenv());
  }

  public static Resource configureResource() {
    return configureResource(Configuration.get());
  }

  public static Resource configureResource(ConfigProperties config) {
    Resource result = Resource.getDefault();

    // TODO(anuraaga): We use a hyphen only once in this artifact, for
    // otel.java.disabled.resource-providers. But fetching by the dot version is the simplest way
    // to implement it for now.
    Set<String> disabledProviders =
        new HashSet<>(config.getList("otel.java.disabled.resource.providers"));
    for (ResourceProvider resourceProvider : ServiceLoader.load(ResourceProvider.class)) {
      if (disabledProviders.contains(resourceProvider.getClass().getName())) {
        continue;
      }
      result = result.merge(resourceProvider.createResource(config));
    }

    result = result.merge(Resource.create(getAttributes(config), ResourceAttributes.SCHEMA_URL));

    return result;
  }

  static Attributes getAttributes(ConfigProperties configProperties) {

    AttributesBuilder resourceAttributes = Attributes.builder();
    configProperties.getMap(ATTRIBUTE_PROPERTY).forEach(resourceAttributes::put);
    String serviceName = configProperties.getString(SERVICE_NAME_PROPERTY);
    if (serviceName != null) {
      resourceAttributes.put(ResourceAttributes.SERVICE_NAME, serviceName);
    }
    return resourceAttributes.build();
  }

  @Override
  public String getString(String name) {
    return config.get(name);
  }

  @Override
  public Boolean getBoolean(String name) {
    String value = config.get(name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    return Boolean.parseBoolean(value);
  }

  @Override
  @SuppressWarnings("UnusedException")
  public Integer getInt(String name) {
    String value = config.get(name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      throw newInvalidPropertyException(name, value, "integer");
    }
  }

  @Override
  @SuppressWarnings("UnusedException")
  public Long getLong(String name) {
    String value = config.get(name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw newInvalidPropertyException(name, value, "long");
    }
  }

  @Override
  @SuppressWarnings("UnusedException")
  public Double getDouble(String name) {
    String value = config.get(name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException ex) {
      throw newInvalidPropertyException(name, value, "double");
    }
  }

  @Override
  @SuppressWarnings("UnusedException")
  public Duration getDuration(String name) {
    String value = config.get(name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    String unitString = getUnitString(value);
    // TODO: Environment variables have unknown encoding.  `trim()` may cut codepoints oddly
    // but likely we'll fail for malformed unit string either way.
    String numberString = value.substring(0, value.length() - unitString.length());
    try {
      long rawNumber = Long.parseLong(numberString.trim());
      TimeUnit unit = getDurationUnit(unitString.trim());
      return Duration.ofMillis(TimeUnit.MILLISECONDS.convert(rawNumber, unit));
    } catch (NumberFormatException ex) {
      throw new ConfigurationException(
          "Invalid duration property "
              + name
              + "="
              + value
              + ". Expected number, found: "
              + numberString,
          ex);
    } catch (ConfigurationException ex) {
      throw new ConfigurationException(
          "Invalid duration property " + name + "=" + value + ". " + ex.getMessage());
    }
  }

  @Override
  public List<String> getList(String name) {
    String value = config.get(name);
    if (value == null) {
      return Collections.emptyList();
    }
    return filterBlanksAndNulls(value.split(","));
  }

  /**
   * Returns {@link ConfigProperties#getList(String)} as a {@link Set} after validating there are no
   * duplicate entries.
   *
   * @throws ConfigurationException if {@code name} contains duplicate entries
   */
  static Set<String> getSet(ConfigProperties config, String name) {
    List<String> list = config.getList(name);
    Set<String> set = new HashSet<>(list);
    if (set.size() != list.size()) {
      String duplicates =
          list.stream()
              .collect(groupingBy(Function.identity(), Collectors.counting()))
              .entrySet()
              .stream()
              .filter(entry -> entry.getValue() > 1)
              .map(Map.Entry::getKey)
              .collect(joining(",", "[", "]"));
      throw new ConfigurationException(name + " contains duplicates: " + duplicates);
    }
    return set;
  }

  @Override
  public Map<String, String> getMap(String name) {
    return getList(name).stream()
        .map(keyValuePair -> filterBlanksAndNulls(keyValuePair.split("=", 2)))
        .map(
            splitKeyValuePairs -> {
              if (splitKeyValuePairs.size() != 2) {
                throw new ConfigurationException(
                    "Invalid map property: " + name + "=" + config.get(name));
              }
              return new AbstractMap.SimpleImmutableEntry<>(
                  splitKeyValuePairs.get(0), splitKeyValuePairs.get(1));
            })
        // If duplicate keys, prioritize later ones similar to duplicate system properties on a
        // Java command line.
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (first, next) -> next, LinkedHashMap::new));
  }

  private static ConfigurationException newInvalidPropertyException(
      String name, String value, String type) {
    throw new ConfigurationException(
        "Invalid value for property " + name + "=" + value + ". Must be a " + type + ".");
  }

  private static List<String> filterBlanksAndNulls(String[] values) {
    return Arrays.stream(values)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());
  }

  /** Returns the TimeUnit associated with a unit string. Defaults to milliseconds. */
  private static TimeUnit getDurationUnit(String unitString) {
    switch (unitString) {
      case "": // Fallthrough expected
      case "ms":
        return TimeUnit.MILLISECONDS;
      case "s":
        return TimeUnit.SECONDS;
      case "m":
        return TimeUnit.MINUTES;
      case "h":
        return TimeUnit.HOURS;
      case "d":
        return TimeUnit.DAYS;
      default:
        throw new ConfigurationException("Invalid duration string, found: " + unitString);
    }
  }

  private static String getUnitString(String rawValue) {
    int lastDigitIndex = rawValue.length() - 1;
    while (lastDigitIndex >= 0) {
      char c = rawValue.charAt(lastDigitIndex);
      if (Character.isDigit(c)) {
        break;
      }
      lastDigitIndex -= 1;
    }
    // Pull everything after the last digit.
    return rawValue.substring(lastDigitIndex + 1);
  }
}
