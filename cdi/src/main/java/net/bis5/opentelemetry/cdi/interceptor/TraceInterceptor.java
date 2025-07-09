package net.bis5.opentelemetry.cdi.interceptor;

import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import net.bis5.opentelemetry.cdi.Traced;

/**
 * Trace all public methods and constructors.
 */
@Traced
@Interceptor
@Priority(value = Interceptor.Priority.LIBRARY_BEFORE + 1)
public class TraceInterceptor extends SerializableTraceInterceptor {

}