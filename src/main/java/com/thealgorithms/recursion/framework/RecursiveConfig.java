package com.thealgorithms.recursion.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RecursiveConfig {
    int maxDepth() default 1000;
    boolean autoSwitchToIterative() default true;
    boolean enableMonitoring() default true;
    String description() default "";
}
