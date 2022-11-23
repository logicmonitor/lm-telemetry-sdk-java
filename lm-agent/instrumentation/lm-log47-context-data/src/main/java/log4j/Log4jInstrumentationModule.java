package log4j;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static java.util.Collections.singletonList;

@AutoService(InstrumentationModule.class)
public class Log4jInstrumentationModule extends InstrumentationModule {
    public Log4jInstrumentationModule() {
        super("log4j-context-data", "log4j-context-data-2.7");
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

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return hasClassesNamed("org.apache.logging.log4j.core.impl.ContextDataInjectorFactory");
    }
}