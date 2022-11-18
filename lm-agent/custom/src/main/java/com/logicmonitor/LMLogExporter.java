package com.logicmonitor;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.api.Logs;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.common.CompletableResultCode;

import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;

public class LMLogExporter implements LogRecordExporter {

    final Configuration conf;
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    MyResponse responseInterface = new MyResponse();
    final HashMap<String, String> resourceIds = new HashMap<>();
    final Logs lmLogs;
    //LMLogsApi apiInstance ;

    public LMLogExporter(){
        conf = new Configuration();
        lmLogs= new Logs(conf, 10, true, responseInterface);
    }
    @Override
    public CompletableResultCode export(Collection<LogRecordData> logs) {
        StringBuilder stringBuilder = new StringBuilder(60);

        for (LogRecordData log : logs) {
            stringBuilder.setLength(0);
            formatLog(stringBuilder, log);
            Map<String, String> metadata=new HashMap<>();
            Map<AttributeKey<?>, Object> resourceAttributes = log
                .getResource().getAttributes().asMap();
            resourceAttributes.forEach((k,v)->metadata.put(k.getKey(),v.toString()));
            if(log.getSpanContext().isValid()) {
                metadata.put("trace_id",log.getSpanContext().getTraceId());
                metadata.put("span_id",log.getSpanContext().getSpanId());
            }
            try {
                lmLogs.sendLogs(stringBuilder.toString(), resourceIds, metadata);
            } catch (Exception e) {
            }

        }
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }

    static void formatLog(StringBuilder stringBuilder, LogRecordData log) {
        stringBuilder
            .append(
                ISO_FORMAT.format(
                    Instant.ofEpochMilli(NANOSECONDS.toMillis(log.getEpochNanos()))
                        .atZone(ZoneOffset.UTC)))
            .append(" ")
            .append(log.getSeverity())
            .append(" '")
            .append(log.getBody().asString())
            .append(" ")
            .append(log.getAttributes());
    }


    static class MyResponse implements ApiCallback<ApiResponse> {


        @Override
        public void onFailure(ApiException e, int i, Map<String, List<String>> map) {

        }

        @Override
        public void onSuccess(ApiResponse apiResponse, int i, Map<String, List<String>> map) {

        }

        @Override
        public void onUploadProgress(long l, long l1, boolean b) {

        }

        @Override
        public void onDownloadProgress(long l, long l1, boolean b) {

        }
    }
}
