package log4j;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.List;
import static java.util.Collections.singletonList;

@AutoService(InstrumentationModule.class)
public class Log4jInstrumentationModule extends InstrumentationModule {
    public Log4jInstrumentationModule() {
        super("log4j-context-data", "log4j-context-data-2.19");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new ContextDataInjectorFactoryInstrumentation());
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        return singletonList(
                "log4j.LMSpanDecoratingContextDataInjector");
    }
}