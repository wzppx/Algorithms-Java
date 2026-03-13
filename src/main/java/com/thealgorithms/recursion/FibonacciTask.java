package com.thealgorithms.recursion;

import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * Fibonacci 任务实现，支持递归和迭代两种计算方式。
 * 使用 @RecursiveConfig 注解配置深度阈值和降级策略。
 */
@RecursiveConfig(
    depthThreshold = 100,
    enableFallback = true,
    enableMonitoring = true,
    taskName = "Fibonacci",
    maxDepth = 10000
)
public class FibonacciTask implements RecursiveTask<Integer, BigInteger> {

    private final RecursiveEngine engine;
    private BiFunction<Integer, RecursiveContext, BigInteger> recursiveWrapper;

    public FibonacciTask() {
        this.engine = RecursiveEngine.getInstance();
    }

    @Override
    public BigInteger execute(Integer input) {
        if (input < 0) {
            throw new IllegalArgumentException("n must be a non-negative integer");
        }
        return engine.register(this).apply(input);
    }

    @Override
    public BigInteger recursive(Integer n, RecursiveContext context) {
        // 基础情况
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }

        // 延迟初始化包装器
        if (recursiveWrapper == null) {
            recursiveWrapper = engine.createRecursiveWrapper(this, context);
        }

        // 检查是否需要降级
        if (context.isThresholdExceeded() && context.getConfig().enableFallback() && !context.isFallbackTriggered()) {
            context.markFallbackTriggered();
            return iterative(n);
        }

        // 递归计算
        context.enter();
        try {
            BigInteger prev1 = recursiveWrapper.apply(n - 1, context);
            BigInteger prev2 = recursiveWrapper.apply(n - 2, context);
            return prev1.add(prev2);
        } finally {
            context.exit();
        }
    }

    @Override
    public BigInteger iterative(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative integer");
        }
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }

        BigInteger prev2 = BigInteger.ZERO;
        BigInteger prev1 = BigInteger.ONE;
        BigInteger result = BigInteger.ONE;

        for (int i = 2; i <= n; i++) {
            result = prev1.add(prev2);
            prev2 = prev1;
            prev1 = result;
        }

        return result;
    }

    /**
     * 计算第 n 个 Fibonacci 数（静态便捷方法）。
     *
     * @param n 位置
     * @return 第 n 个 Fibonacci 数
     */
    public static BigInteger calculate(int n) {
        return new FibonacciTask().execute(n);
    }
}
