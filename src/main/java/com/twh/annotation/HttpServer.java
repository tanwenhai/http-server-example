package com.twh.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 使用jdk代理实现接口访问HTTP服务
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HttpServer {
    @AliasFor("host")
    String value() default "";

    @AliasFor("value")
    String host() default "";

    String path() default "";
}
