package com.thealgorithms.recursion.engine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RecursiveConfig {
    int maxDepth() default 1000;

    boolean enableIterationFallback() default true;

    boolean enableMonitoring() default false;
}
