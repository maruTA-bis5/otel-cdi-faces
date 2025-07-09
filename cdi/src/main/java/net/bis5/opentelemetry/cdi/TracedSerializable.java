package net.bis5.opentelemetry.cdi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.interceptor.InterceptorBinding;

/**
 * Annotation to trace public methods in a class
 */
@Documented
@InterceptorBinding
@Retention(RUNTIME)
@Target(TYPE)
public @interface TracedSerializable {

}
