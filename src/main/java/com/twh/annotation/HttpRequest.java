package com.twh.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * http接口path
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface HttpRequest {
    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";

    RequestMethod method() default RequestMethod.POST;
}
