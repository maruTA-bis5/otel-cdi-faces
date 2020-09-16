package net.bis5.opentracing.faces.interceptor;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * @see org.eclipse.microprofile.opentracing.Traced
 */
@Documented
@InterceptorBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface TracedSerializable {

    @Nonbinding
    boolean value() default true;

    @Nonbinding
    String operationName() default "";

}
