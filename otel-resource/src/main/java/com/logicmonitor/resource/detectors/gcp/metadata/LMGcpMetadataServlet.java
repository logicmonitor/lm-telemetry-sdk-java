package com.logicmonitor.resource.detectors.gcp.metadata;

import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServlet;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LMGcpMetadataServlet extends HttpServlet {
    private final static String[] metaPath = {
            "/computeMetadata/v1/project/numeric-project-id", //  (pending)
            "/computeMetadata/v1/project/project-id",
            "/computeMetadata/v1/instance/zone",
            "/computeMetadata/v1/instance/service-accounts/default/aliases",
            "/computeMetadata/v1/instance/service-accounts/default/email",
            "/computeMetadata/v1/instance/service-accounts/default/",
            "/computeMetadata/v1/instance/service-accounts/default/scopes"
    };
    private final static String metadata = "http://metadata.google.internal";
    // Use OkHttp from Square as it's quite easy to use for simple fetches.
    private final static OkHttpClient ok = new OkHttpClient.Builder()
            .readTimeout(500, TimeUnit.MILLISECONDS)  // Don't dawdle
            .writeTimeout(500, TimeUnit.MILLISECONDS)
            .build();
    // Fetch Metadata
    static String fetchMetadata(String key) throws IOException {
        Request request = new Request.Builder()
                .url(metadata + key)
                .addHeader("Metadata-Flavor", "Google")
                .get()
                .build();

        Response response = ok.newCall(request).execute();
        return response.body().toString();
    }

    private static final Resource INSTANCE = buildResource();
    public static Resource get() {
        return INSTANCE;
    }
    static Resource buildResource(){
           TreeMap<String, String> m = new TreeMap<>();
           for (String key : metaPath) {
               try{
                   String metaData = fetchMetadata(key);
                   m.put(key,metaData);
               }
               catch (Exception e)
               {
                   m.put(key,e.toString());
               }
            }
        AttributesBuilder attrBuilders = Attributes.builder();
        attrBuilders.put("Metadata", m.descendingMap().toString());
        return Resource.create(attrBuilders.build(), ResourceAttributes.SCHEMA_URL);
        }
}