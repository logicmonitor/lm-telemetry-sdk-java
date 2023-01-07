package com.logicmonitor;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.logs.SdkLoggerProviderBuilder;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;

public class LMAutoConfigurationCustomizerProvider implements
    AutoConfigurationCustomizerProvider {

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        autoConfiguration.addLoggerProviderCustomizer(this::addLoggerProviderCustomizer);
    }

    private SdkLoggerProviderBuilder addLoggerProviderCustomizer(
        SdkLoggerProviderBuilder sdkLoggerProviderBuilder, ConfigProperties configProperties) {
        if(configProperties.getBoolean("lm.logs.export.enabled",false)) {
            return sdkLoggerProviderBuilder
                .addLogRecordProcessor(SimpleLogRecordProcessor.create(new LMLogExporter()));
        }
        return sdkLoggerProviderBuilder;
    }


}
