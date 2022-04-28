package com.logicmonitor.resource.detectors.extensions;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GcpExtension {
  public static String getGcpResource(String key) {
    String resource = null;
    HttpURLConnection conn = null;
    try {
      URL url = new URL("http://metadata.google.internal" + key);
      conn = (HttpURLConnection) (url.openConnection());
      conn.setRequestProperty("Metadata-Flavor", "Google");
      resource = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      conn.disconnect();
    } catch (Throwable t) {
    }
    return resource;
  }
}
