package com.marcuseisele.example.twolayercache.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TwoLayerRedisCacheable {

    long firstLayerTtl() default 10L;
    long secondLayerTtl() default 60L;
    String key();
}