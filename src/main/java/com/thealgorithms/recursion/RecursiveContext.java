package com.thealgorithms.recursion;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 递归上下文，用于跟踪递归调用的状态和深度。
 * 每个递归调用链都有独立的上下文实例。
 */
public final class RecursiveContext {

    private final AtomicInteger currentDepth;
    private final RecursiveConfig config;
    private final String taskName;
    private volatile boolean fallbackTriggered;
    private volatile long startTime;
    private volatile int maxDepthReached;

    /**
     * 创建新的递归上下文。
     *
     * @param config 递归配置
     * @param taskName 任务名称
     */
    public RecursiveContext(RecursiveConfig config, String taskName) {
        this.currentDepth = new AtomicInteger(0);
        this.config = config;
        this.taskName = taskName;
        this.fallbackTriggered = false;
        this.startTime = System.nanoTime();
        this.maxDepthReached = 0;
    }

    /**
     * 进入递归调用，增加深度计数。
     *
     * @return 进入前的深度
     */
    public int enter() {
        int depth = currentDepth.incrementAndGet();
        if (depth > maxDepthReached) {
            maxDepthReached = depth;
        }
        return depth;
    }

    /**
     * 退出递归调用，减少深度计数。
     *
     * @return 退出后的深度
     */
    public int exit() {
        return currentDepth.decrementAndGet();
    }

    /**
     * 获取当前递归深度。
     *
     * @return 当前深度
     */
    public int getCurrentDepth() {
        return currentDepth.get();
    }

    /**
     * 检查是否超过深度阈值。
     *
     * @return 如果超过阈值返回 true
     */
    public boolean isThresholdExceeded() {
        return currentDepth.get() >= config.depthThreshold();
    }

    /**
     * 检查是否超过最大允许深度。
     *
     * @return 如果超过最大深度返回 true
     */
    public boolean isMaxDepthExceeded() {
        return currentDepth.get() >= config.maxDepth();
    }

    /**
     * 标记已触发降级。
     */
    public void markFallbackTriggered() {
        this.fallbackTriggered = true;
    }

    /**
     * 检查是否已触发降级。
     *
     * @return 如果已触发降级返回 true
     */
    public boolean isFallbackTriggered() {
        return fallbackTriggered;
    }

    /**
     * 获取配置。
     *
     * @return 递归配置
     */
    public RecursiveConfig getConfig() {
        return config;
    }

    /**
     * 获取任务名称。
     *
     * @return 任务名称
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 获取最大达到的递归深度。
     *
     * @return 最大深度
     */
    public int getMaxDepthReached() {
        return maxDepthReached;
    }

    /**
     * 获取执行时间（纳秒）。
     *
     * @return 执行时间
     */
    public long getExecutionTimeNanos() {
        return System.nanoTime() - startTime;
    }

    /**
     * 重置上下文状态，用于新的调用链。
     */
    public void reset() {
        currentDepth.set(0);
        fallbackTriggered = false;
        startTime = System.nanoTime();
        maxDepthReached = 0;
    }
}
