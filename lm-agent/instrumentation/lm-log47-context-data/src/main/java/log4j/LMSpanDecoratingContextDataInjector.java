package log4j;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import java.util.List;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

public class LMSpanDecoratingContextDataInjector implements ContextDataInjector {
    private final ContextDataInjector delegate;

    public static final String Span_Name="span_name";

    public LMSpanDecoratingContextDataInjector(ContextDataInjector delegate) {
        this.delegate = delegate;
    }

    @Override
    public StringMap injectContextData(List<Property> list, StringMap stringMap) {
        StringMap contextData = delegate.injectContextData(list, stringMap);

        if (contextData.containsKey(Span_Name)) {
            return contextData;
        }

        SpanContext currentContext = Java8BytecodeBridge.currentSpan().getSpanContext();
        if (!currentContext.isValid()) {
            return contextData;
        }

        String []s=Java8BytecodeBridge.currentSpan().toString().split("name=");
        StringMap newContextData = new SortedArrayStringMap(contextData);
        newContextData.putValue(Span_Name,s[1].substring(0,s[1].indexOf(",")));
        return newContextData;
    }

    @Override
    public ReadOnlyStringMap rawContextData() {
        return delegate.rawContextData();
    }
}