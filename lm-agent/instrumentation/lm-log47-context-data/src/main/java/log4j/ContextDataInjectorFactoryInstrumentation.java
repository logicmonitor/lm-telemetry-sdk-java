package log4j;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.logging.log4j.core.ContextDataInjector;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class ContextDataInjectorFactoryInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("org.apache.logging.log4j.core.impl.ContextDataInjectorFactory");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(isPublic())
                        .and(isStatic())
                        .and(named("createInjector"))
                        .and(returns(named("org.apache.logging.log4j.core.ContextDataInjector"))),
                this.getClass().getName() + "$CreateInjectorAdvice");
    }

    @SuppressWarnings("unused")
    public static class CreateInjectorAdvice {
        @Advice.OnMethodExit(suppress = Throwable.class)
        public static void onExit(
                @Advice.Return(typing = Assigner.Typing.DYNAMIC, readOnly = false)
                ContextDataInjector injector) {
            injector = new LMSpanDecoratingContextDataInjector(injector);
        }
    }
}