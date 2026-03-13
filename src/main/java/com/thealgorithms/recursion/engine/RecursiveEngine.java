package com.thealgorithms.recursion.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class RecursiveEngine {
    private static final ThreadLocal<Map<Object, RecursionContext>> CONTEXTS = ThreadLocal.withInitial(HashMap::new);
    private static final Map<Class<?>, RecursiveConfig> CONFIG_CACHE = new HashMap<>();

    private RecursiveEngine() {
    }

    public static <I, O> O execute(RecursiveTask<I, O> task, I input) {
        return execute(task, input, RecursiveTask::execute);
    }

    public static <I, O> O execute(RecursiveTask<I, O> task, I input, BiFunction<RecursiveTask<I, O>, I, O> method) {
        Map<Object, RecursionContext> ctxMap = CONTEXTS.get();
        boolean isRoot = !ctxMap.containsKey(task);
        RecursionContext ctx = ctxMap.computeIfAbsent(task, k -> new RecursionContext(getConfig(task)));

        if (isRoot && ctx.config.enableIterationFallback()) {
            int expectedDepth = task.calculateDepth(input);
            if (expectedDepth > ctx.config.maxDepth()) {
                return task.executeIterative(input);
            }
        }

        try {
            ctx.enter();
            if (ctx.shouldFallback()) {
                return task.executeIterative(input);
            }
            try {
                return method.apply(task, input);
            } catch (StackOverflowError e) {
                if (ctx.config.enableIterationFallback()) {
                    ctx.setFallbackMode();
                    return task.executeIterative(input);
                }
                throw e;
            }
        } finally {
            ctx.exit();
            if (isRoot && ctx.depth == 0) {
                ctxMap.remove(task);
            }
        }
    }

    private static RecursiveConfig getConfig(RecursiveTask<?, ?> task) {
        Class<?> clazz = task.getClass();
        return CONFIG_CACHE.computeIfAbsent(clazz, k -> {
            RecursiveConfig annotation = k.getAnnotation(RecursiveConfig.class);
            if (annotation == null) {
                annotation = DefaultConfig.class.getAnnotation(RecursiveConfig.class);
            }
            return annotation;
        });
    }

    @RecursiveConfig
    private static final class DefaultConfig {
    }

    private static final class RecursionContext {
        private final RecursiveConfig config;
        private int depth;
        private boolean fallbackMode;

        RecursionContext(RecursiveConfig config) {
            this.config = config;
            this.depth = 0;
            this.fallbackMode = false;
        }

        void enter() {
            depth++;
        }

        void exit() {
            depth--;
        }

        boolean shouldFallback() {
            return fallbackMode || (config.enableIterationFallback() && depth > config.maxDepth());
        }

        void setFallbackMode() {
            this.fallbackMode = true;
        }
    }
}
