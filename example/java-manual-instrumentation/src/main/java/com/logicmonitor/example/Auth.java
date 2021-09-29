package com.logicmonitor.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class Auth {

    Tracer tracer = GlobalOpenTelemetry.getTracer("auth-Service-instrumentation");

    //Tracer tracer= GlobalOpenTelemetry.getTracer("auth-Service-instrumentation","1.0.0");
    public void doLogin(String username, String password) {
        Span parentSpan = tracer.spanBuilder("doLogin").startSpan();
        parentSpan.setAttribute("priority", "business.priority");
        parentSpan.setAttribute("prodEnv", true);

        try (Scope scope = parentSpan.makeCurrent()) {
            Thread.sleep(200);
            boolean isValid = isValidAuth(username, password);
            //Do login

        } catch (Throwable t) {
            parentSpan.setStatus(StatusCode.ERROR, "Change it to your error message");
        } finally {
            parentSpan
                .end(); // closing the scope does not end the span, this has to be done manually
        }

    }

    private boolean isValidAuth(String username, String password) {

        Span childSpan = tracer.spanBuilder("isValidAuth").startSpan();
        // NOTE: setParent(...) is not required;
        // `Span.current()` is automatically added as the parent

        //Auth code goes here

        try {
            Thread.sleep(200);
            childSpan.setStatus(StatusCode.OK);
            Attributes eventAttributes = Attributes.builder().put("Username", username)
                .put("id", 101).build();
            childSpan.addEvent("User Logged In", eventAttributes);
        } catch (InterruptedException e) {
            childSpan.setStatus(StatusCode.ERROR, "Change it to your error message");
        } finally {
            childSpan.end();
        }
        return true;
    }
}
