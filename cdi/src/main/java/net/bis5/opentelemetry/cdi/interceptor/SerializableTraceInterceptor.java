package net.bis5.opentelemetry.cdi.interceptor;

import java.io.Serializable;
import java.lang.reflect.Parameter;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import net.bis5.opentelemetry.cdi.SerializableTracer;
import net.bis5.opentelemetry.cdi.TracedSerializable;

/**
 * Trace all public methods and constructors.
 */
@TracedSerializable
@Interceptor
@Priority(value = Interceptor.Priority.LIBRARY_BEFORE + 1)
public class SerializableTraceInterceptor implements Serializable {

    @Inject
    @SerializableTracer
    protected Tracer tracer;

    @AroundInvoke
    public Object wrap(InvocationContext ctx) throws Exception {
        if (ctx.getMethod() != null) {
            return wrapMethodCall(ctx);
        } else {
            return wrapConstructorCall(ctx);
        }
    }

    private Object wrapMethodCall(InvocationContext ctx) throws Exception {
        var method = ctx.getMethod();
        if (method.isAnnotationPresent(WithSpan.class)) {
            return invokeNoOp(ctx);
        }
        Attributes spanAttributes = extractSpanAttributes(method.getParameters(), ctx.getParameters());
        String operationName = method.getDeclaringClass().getName() + "#" + method.getName();
        return invoke(operationName, ctx, spanAttributes);
    }

    private Object wrapConstructorCall(InvocationContext ctx) throws Exception {
        var constructor = ctx.getConstructor();
        if (constructor.isAnnotationPresent(WithSpan.class)) {
            return invokeNoOp(ctx);
        }
        Attributes spanAttributes = extractSpanAttributes(constructor.getParameters(), ctx.getParameters());
        String operationName = constructor.getDeclaringClass().getName() + "#" + constructor.getName();
        return invoke(operationName, ctx, spanAttributes);
    }

    private Attributes extractSpanAttributes(Parameter[] declaredParameters, Object[] actualParameters) {
        var builder = Attributes.builder();
        
        for (int i = 0; i < declaredParameters.length; i++) {
            Parameter declaredParameter = declaredParameters[i];
            Object actualParameter = actualParameters[i];

            if (declaredParameter.isAnnotationPresent(SpanAttribute.class)) {
                extractSpanAttribute(builder, declaredParameter, actualParameter);
            }
        }

        return builder.build();
    }

    private void extractSpanAttribute(AttributesBuilder builder, Parameter declaredParameter, Object actualParameter) {
        var attribute = declaredParameter.getAnnotation(SpanAttribute.class);
        String name = attribute.value();
        if (actualParameter instanceof Number) {
            var num = (Number)actualParameter;
            if (num.equals(num.longValue())) {
                builder.put(name, num.longValue());
            } else {
                builder.put(name, num.doubleValue());
            }
        } else if (actualParameter instanceof Boolean) {
            builder.put(name, (boolean)actualParameter);
        } else {
            builder.put(name, String.valueOf(actualParameter));
        }
    }

    private Object invokeNoOp(InvocationContext ctx) throws Exception {
        return ctx.proceed();
    }

    private Object invoke(String operationName, InvocationContext ctx, Attributes spanAttributes) throws Exception {
        Span span = tracer.spanBuilder(operationName)
            .setParent(Context.current())
            .setAllAttributes(spanAttributes)
            .startSpan();
        try (Scope scope = span.makeCurrent()) {
            return ctx.proceed();
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}