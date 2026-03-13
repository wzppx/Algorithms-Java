package com.thealgorithms.recursion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 递归配置注解，用于标记递归任务的配置参数。
 * 可以指定递归深度阈值、是否启用迭代降级等选项。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecursiveConfig {

    /**
     * 递归深度阈值。当调用深度超过此值时，自动切换到迭代实现。
     * 默认值为 1000。
     *
     * @return 深度阈值
     */
    int depthThreshold() default 1000;

    /**
     * 是否启用迭代降级。当设置为 true 时，超过深度阈值会自动切换到迭代实现。
     * 默认启用。
     *
     * @return 是否启用降级
     */
    boolean enableFallback() default true;

    /**
     * 是否启用性能监控。启用后会收集调用次数、执行时间等统计信息。
     * 默认启用。
     *
     * @return 是否启用监控
     */
    boolean enableMonitoring() default true;

    /**
     * 任务名称，用于监控和日志识别。
     * 默认为类名。
     *
     * @return 任务名称
     */
    String taskName() default "";

    /**
     * 最大允许递归深度。超过此值即使启用降级也会抛出异常，防止无限递归。
     * 默认值为 10000。
     *
     * @return 最大递归深度
     */
    int maxDepth() default 10000;
}

/**
 * 默认递归配置实现，当类没有使用 @RecursiveConfig 注解时使用。
 */
enum DefaultRecursiveConfig implements RecursiveConfig {
    INSTANCE;

    @Override
    public int depthThreshold() {
        return 1000;
    }

    @Override
    public boolean enableFallback() {
        return true;
    }

    @Override
    public boolean enableMonitoring() {
        return true;
    }

    @Override
    public String taskName() {
        return "default";
    }

    @Override
    public int maxDepth() {
        return 10000;
    }

    @Override
    public Class<? extends java.lang.annotation.Annotation> annotationType() {
        return RecursiveConfig.class;
    }
}
