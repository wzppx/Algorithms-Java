package com.thealgorithms.recursion;

import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * Factorial 任务实现，支持递归和迭代两种计算方式。
 * 使用 @RecursiveConfig 注解配置深度阈值和降级策略。
 */
@RecursiveConfig(
    depthThreshold = 500,
    enableFallback = true,
    enableMonitoring = true,
    taskName = "Factorial",
    maxDepth = 10000
)
public class FactorialTask implements RecursiveTask<Integer, BigInteger> {

    private final RecursiveEngine engine;
    private BiFunction<Integer, RecursiveContext, BigInteger> recursiveWrapper;

    public FactorialTask() {
        this.engine = RecursiveEngine.getInstance();
    }

    @Override
    public BigInteger execute(Integer input) {
        if (input < 0) {
            throw new IllegalArgumentException("number is negative");
        }
        return engine.register(this).apply(input);
    }

    @Override
    public BigInteger recursive(Integer n, RecursiveContext context) {
        // 基础情况
        if (n == 0 || n == 1) {
            return BigInteger.ONE;
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
            BigInteger prev = recursiveWrapper.apply(n - 1, context);
            return prev.multiply(BigInteger.valueOf(n));
        } finally {
            context.exit();
        }
    }

    @Override
    public BigInteger iterative(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("number is negative");
        }

        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    /**
     * 计算 n 的阶乘（静态便捷方法）。
     *
     * @param n 输入数字
     * @return n 的阶乘
     */
    public static BigInteger calculate(int n) {
        return new FactorialTask().execute(n);
    }
}
