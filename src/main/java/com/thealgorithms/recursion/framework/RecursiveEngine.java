package com.thealgorithms.recursion.framework;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class RecursiveEngine {
    private static final ConcurrentHashMap<String, TaskStatistics> STATISTICS_MAP = new ConcurrentHashMap<>();
    private static final ThreadLocal<ExecutionContext<?>> CURRENT_CONTEXT = new ThreadLocal<>();

    private RecursiveEngine() {
    }

    @SuppressWarnings("unchecked")
    public static <I, O> O execute(RecursiveTask<I, O> task, I input) {
        RecursiveConfig config = task.getClass().getAnnotation(RecursiveConfig.class);
        int maxDepth = config != null ? config.maxDepth() : 1000;
        boolean autoSwitch = config == null || config.autoSwitchToIterative();

        RecursiveContext.reset();
        long startTime = System.nanoTime();

        O result;
        try {
            CURRENT_CONTEXT.set(new ExecutionContext<>(task, maxDepth, autoSwitch));
            result = executeWithDepthControl(task, input, maxDepth, autoSwitch);
        } finally {
            long endTime = System.nanoTime();
            recordStatistics(task.getTaskName(), endTime - startTime, RecursiveContext.getMaxReachedDepth(), RecursiveContext.hasSwitchedToIterative());
            RecursiveContext.reset();
            CURRENT_CONTEXT.remove();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <I, O> O executeStep(RecursiveTask<I, O> task, I input) {
        ExecutionContext<I> ctx = (ExecutionContext<I>) CURRENT_CONTEXT.get();
        if (ctx == null) {
            return task.computeIterative(input);
        }
        
        int currentDepth = RecursiveContext.getCurrentDepth();
        if (currentDepth >= ctx.maxDepth) {
            if (ctx.autoSwitch) {
                RecursiveContext.markSwitchedToIterative();
                return task.computeIterative(input);
            }
            throw new StackOverflowError("Recursion depth exceeded: " + currentDepth + " > " + ctx.maxDepth);
        }
        
        RecursiveContext.incrementDepth();
        try {
            return task.compute(input);
        } finally {
            RecursiveContext.decrementDepth();
        }
    }

    private static <I, O> O executeWithDepthControl(RecursiveTask<I, O> task, I input, int maxDepth, boolean autoSwitch) {
        int currentDepth = RecursiveContext.getCurrentDepth();

        if (currentDepth >= maxDepth) {
            if (autoSwitch) {
                RecursiveContext.markSwitchedToIterative();
                return task.computeIterative(input);
            }
            throw new StackOverflowError("Recursion depth exceeded: " + currentDepth + " > " + maxDepth);
        }

        RecursiveContext.incrementDepth();
        try {
            return task.compute(input);
        } finally {
            RecursiveContext.decrementDepth();
        }
    }

    private static void recordStatistics(String taskName, long durationNanos, int maxDepth, boolean switched) {
        TaskStatistics stats = STATISTICS_MAP.computeIfAbsent(taskName, k -> new TaskStatistics());
        stats.recordExecution(durationNanos, maxDepth, switched);
    }

    public static TaskStatistics getStatistics(String taskName) {
        return STATISTICS_MAP.get(taskName);
    }

    public static void clearStatistics() {
        STATISTICS_MAP.clear();
    }

    public static final class TaskStatistics {
        private final AtomicLong totalExecutions = new AtomicLong(0);
        private final AtomicLong totalDurationNanos = new AtomicLong(0);
        private final AtomicLong maxDepthReached = new AtomicLong(0);
        private final AtomicLong iterativeSwitches = new AtomicLong(0);

        public void recordExecution(long durationNanos, int depth, boolean switched) {
            totalExecutions.incrementAndGet();
            totalDurationNanos.addAndGet(durationNanos);
            if (depth > maxDepthReached.get()) {
                maxDepthReached.set(depth);
            }
            if (switched) {
                iterativeSwitches.incrementAndGet();
            }
        }

        public long getTotalExecutions() {
            return totalExecutions.get();
        }

        public double getAverageDurationMillis() {
            long executions = totalExecutions.get();
            return executions > 0 ? (totalDurationNanos.get() / 1_000_000.0) / executions : 0;
        }

        public long getMaxDepthReached() {
            return maxDepthReached.get();
        }

        public long getIterativeSwitches() {
            return iterativeSwitches.get();
        }

        @Override
        public String toString() {
            return String.format("TaskStatistics{executions=%d, avgDuration=%.3fms, maxDepth=%d, switches=%d}",
                totalExecutions.get(), getAverageDurationMillis(), maxDepthReached.get(), iterativeSwitches.get());
        }
    }

    private static final class ExecutionContext<I> {
        final RecursiveTask<I, ?> task;
        final int maxDepth;
        final boolean autoSwitch;

        ExecutionContext(RecursiveTask<I, ?> task, int maxDepth, boolean autoSwitch) {
            this.task = task;
            this.maxDepth = maxDepth;
            this.autoSwitch = autoSwitch;
        }
    }
}
