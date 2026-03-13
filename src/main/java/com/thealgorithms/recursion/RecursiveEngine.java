package com.thealgorithms.recursion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 递归引擎，负责管理递归任务的执行、深度监控和自动降级。
 * <p>
 * 该引擎使用策略模式而非动态代理，避免自递归拦截失效的问题。
 * 通过显式的上下文传递和包装器模式，确保递归调用链完整可监控。
 * </p>
 */
public final class RecursiveEngine {

    private static final RecursiveEngine INSTANCE = new RecursiveEngine();

    // 性能统计
    private final Map<String, TaskStatistics> statisticsMap = new ConcurrentHashMap<>();

    private RecursiveEngine() {
    }

    /**
     * 获取引擎单例实例。
     *
     * @return 递归引擎实例
     */
    public static RecursiveEngine getInstance() {
        return INSTANCE;
    }

    /**
     * 注册并包装递归任务，返回可直接调用的包装器。
     * 这是主要的任务注册方法。
     *
     * @param task 递归任务实现
     * @param <I> 输入类型
     * @param <O> 输出类型
     * @return 包装后的任务函数
     */
    public <I, O> Function<I, O> register(RecursiveTask<I, O> task) {
        RecursiveConfig config = task.getConfig();
        String taskName = config.taskName().isEmpty() ? task.getClass().getSimpleName() : config.taskName();

        // 初始化统计信息
        statisticsMap.computeIfAbsent(taskName, k -> new TaskStatistics(taskName));

        // 返回包装后的函数
        return input -> execute(task, input, config, taskName);
    }

    /**
     * 创建支持递归调用的包装函数。
     * 这个函数可以在递归实现中使用，它会自动处理深度监控。
     *
     * @param task 递归任务
     * @param context 递归上下文
     * @param <I> 输入类型
     * @param <O> 输出类型
     * @return 支持监控的递归函数
     */
    public <I, O> BiFunction<I, RecursiveContext, O> createRecursiveWrapper(
            RecursiveTask<I, O> task, RecursiveContext context) {

        return (input, ctx) -> {
            // 检查是否超过最大深度
            if (ctx.isMaxDepthExceeded()) {
                throw new RecursiveDepthException(
                    "Max recursion depth exceeded: " + ctx.getCurrentDepth() +
                    " (max: " + ctx.getConfig().maxDepth() + ")");
            }

            // 检查是否需要降级
            if (ctx.isThresholdExceeded() && ctx.getConfig().enableFallback() && !ctx.isFallbackTriggered()) {
                ctx.markFallbackTriggered();
                recordFallback(ctx.getTaskName());
                return task.iterative(input);
            }

            // 正常递归调用
            ctx.enter();
            try {
                return task.recursive(input, ctx);
            } finally {
                ctx.exit();
            }
        };
    }

    /**
     * 执行递归任务。
     */
    private <I, O> O execute(RecursiveTask<I, O> task, I input, RecursiveConfig config, String taskName) {
        RecursiveContext context = new RecursiveContext(config, taskName);
        TaskStatistics stats = statisticsMap.get(taskName);

        long startTime = System.nanoTime();
        boolean success = false;

        try {
            O result;

            // 如果禁用递归或输入较小，直接使用迭代版本
            if (!shouldUseRecursion(task, input, config)) {
                result = task.iterative(input);
            } else {
                // 创建包装后的递归函数
                BiFunction<I, RecursiveContext, O> recursiveWrapper = createRecursiveWrapper(task, context);

                // 开始递归
                context.enter();
                try {
                    result = task.recursive(input, context);
                } finally {
                    context.exit();
                }
            }

            success = true;
            return result;

        } catch (StackOverflowError e) {
            // 如果发生栈溢出，尝试使用迭代版本
            if (config.enableFallback()) {
                recordFallback(taskName);
                return task.iterative(input);
            }
            throw new RecursiveDepthException("Stack overflow occurred", e);
        } finally {
            // 记录统计信息
            long executionTime = System.nanoTime() - startTime;
            stats.recordExecution(executionTime, success, context.isFallbackTriggered(), context.getMaxDepthReached());
        }
    }

    /**
     * 判断是否应使用递归实现。
     */
    private <I, O> boolean shouldUseRecursion(RecursiveTask<I, O> task, I input, RecursiveConfig config) {
        // 可以根据输入大小或其他条件决定
        return true;
    }

    /**
     * 记录降级事件。
     */
    private void recordFallback(String taskName) {
        TaskStatistics stats = statisticsMap.get(taskName);
        if (stats != null) {
            stats.recordFallback();
        }
    }

    /**
     * 获取任务的统计信息。
     *
     * @param taskName 任务名称
     * @return 统计信息
     */
    public TaskStatistics getStatistics(String taskName) {
        return statisticsMap.get(taskName);
    }

    /**
     * 获取所有任务的统计信息。
     *
     * @return 统计信息映射
     */
    public Map<String, TaskStatistics> getAllStatistics() {
        return new ConcurrentHashMap<>(statisticsMap);
    }

    /**
     * 清除指定任务的统计信息。
     *
     * @param taskName 任务名称
     */
    public void clearStatistics(String taskName) {
        TaskStatistics stats = statisticsMap.get(taskName);
        if (stats != null) {
            stats.reset();
        }
    }

    /**
     * 清除所有统计信息。
     */
    public void clearAllStatistics() {
        statisticsMap.values().forEach(TaskStatistics::reset);
    }

    /**
     * 任务统计信息类。
     */
    public static class TaskStatistics {
        private final String taskName;
        private final AtomicLong totalExecutions = new AtomicLong(0);
        private final AtomicLong successfulExecutions = new AtomicLong(0);
        private final AtomicLong failedExecutions = new AtomicLong(0);
        private final AtomicLong fallbackExecutions = new AtomicLong(0);
        private final AtomicLong totalExecutionTimeNanos = new AtomicLong(0);
        private final AtomicLong maxDepthReached = new AtomicLong(0);

        TaskStatistics(String taskName) {
            this.taskName = taskName;
        }

        void recordExecution(long executionTimeNanos, boolean success, boolean fallback, int maxDepth) {
            totalExecutions.incrementAndGet();
            totalExecutionTimeNanos.addAndGet(executionTimeNanos);

            if (success) {
                successfulExecutions.incrementAndGet();
            } else {
                failedExecutions.incrementAndGet();
            }

            if (fallback) {
                fallbackExecutions.incrementAndGet();
            }

            // 更新最大深度
            long currentMax;
            do {
                currentMax = maxDepthReached.get();
                if (maxDepth <= currentMax) break;
            } while (!maxDepthReached.compareAndSet(currentMax, maxDepth));
        }

        void recordFallback() {
            fallbackExecutions.incrementAndGet();
        }

        void reset() {
            totalExecutions.set(0);
            successfulExecutions.set(0);
            failedExecutions.set(0);
            fallbackExecutions.set(0);
            totalExecutionTimeNanos.set(0);
            maxDepthReached.set(0);
        }

        public String getTaskName() {
            return taskName;
        }

        public long getTotalExecutions() {
            return totalExecutions.get();
        }

        public long getSuccessfulExecutions() {
            return successfulExecutions.get();
        }

        public long getFailedExecutions() {
            return failedExecutions.get();
        }

        public long getFallbackExecutions() {
            return fallbackExecutions.get();
        }

        public long getTotalExecutionTimeNanos() {
            return totalExecutionTimeNanos.get();
        }

        public double getAverageExecutionTimeNanos() {
            long total = totalExecutions.get();
            return total > 0 ? (double) totalExecutionTimeNanos.get() / total : 0;
        }

        public long getMaxDepthReached() {
            return maxDepthReached.get();
        }

        @Override
        public String toString() {
            return String.format(
                "TaskStatistics{taskName='%s', total=%d, success=%d, failed=%d, fallback=%d, avgTime=%.2f ns, maxDepth=%d}",
                taskName, totalExecutions.get(), successfulExecutions.get(),
                failedExecutions.get(), fallbackExecutions.get(),
                getAverageExecutionTimeNanos(), maxDepthReached.get()
            );
        }
    }

    /**
     * 递归深度异常。
     */
    public static class RecursiveDepthException extends RuntimeException {
        public RecursiveDepthException(String message) {
            super(message);
        }

        public RecursiveDepthException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
