package com.twh.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 包扫描接口上有HttpServer注解的接口
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpServerImportSelector.class)
public @interface EnableHttpServer {
    @AliasFor("value")
    String[] basePackages() default {};

    @AliasFor("basePackages")
    String[] value() default {};
}
