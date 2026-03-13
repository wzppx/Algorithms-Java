package com.thealgorithms.recursion;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * RecursiveEngine 压力测试类。
 * 包含性能对比测试和 10,000 层深度稳定性测试。
 */
class RecursiveEngineStressTest {

    private RecursiveEngine engine;

    @BeforeEach
    void setUp() {
        engine = RecursiveEngine.getInstance();
        engine.clearAllStatistics();
    }

    // ==================== Fibonacci 性能对比测试 ====================

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFibonacciPerformanceComparison() {
        System.out.println("=== Fibonacci Performance Comparison ===");

        // 传统递归实现（小数值测试，避免栈溢出）
        long traditionalTime = measureTraditionalFibonacci(30);
        System.out.println("Traditional Fibonacci(30): " + traditionalTime + " ms");

        // 引擎管理的递归实现
        FibonacciTask fibonacciTask = new FibonacciTask();
        long engineTime = measureEngineFibonacci(fibonacciTask, 30);
        System.out.println("Engine Fibonacci(30): " + engineTime + " ms");

        // 纯迭代实现
        long iterativeTime = measureIterativeFibonacci(30);
        System.out.println("Pure Iterative Fibonacci(30): " + iterativeTime + " ms");

        // 验证结果正确性
        BigInteger result = fibonacciTask.execute(30);
        assertEquals(BigInteger.valueOf(832040), result);

        // 打印统计信息
        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Fibonacci");
        System.out.println("Statistics: " + stats);

        // 引擎实现应该比传统递归快（因为有监控和优化）
        // 或者至少性能相当
        System.out.println("Performance ratio (Engine/Traditional): " + (double) engineTime / traditionalTime);
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFibonacciLargeValue() {
        System.out.println("=== Fibonacci Large Value Test ===");

        FibonacciTask fibonacciTask = new FibonacciTask();

        // 测试较大值
        int n = 1000;
        long startTime = System.currentTimeMillis();
        BigInteger result = fibonacciTask.execute(n);
        long endTime = System.currentTimeMillis();

        System.out.println("Fibonacci(" + n + ") calculated in " + (endTime - startTime) + " ms");
        System.out.println("Result digits: " + result.toString().length());

        // 验证结果不为空且为正数
        assertNotNull(result);
        assertTrue(result.compareTo(BigInteger.ZERO) > 0);

        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Fibonacci");
        System.out.println("Statistics: " + stats);
    }

    // ==================== Factorial 性能对比测试 ====================

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFactorialPerformanceComparison() {
        System.out.println("=== Factorial Performance Comparison ===");

        // 传统递归实现
        long traditionalTime = measureTraditionalFactorial(1000);
        System.out.println("Traditional Factorial(1000): " + traditionalTime + " ms");

        // 引擎管理的递归实现
        FactorialTask factorialTask = new FactorialTask();
        long engineTime = measureEngineFactorial(factorialTask, 1000);
        System.out.println("Engine Factorial(1000): " + engineTime + " ms");

        // 纯迭代实现
        long iterativeTime = measureIterativeFactorial(1000);
        System.out.println("Pure Iterative Factorial(1000): " + iterativeTime + " ms");

        // 验证结果正确性
        BigInteger result = factorialTask.execute(1000);
        assertNotNull(result);
        assertTrue(result.compareTo(BigInteger.ZERO) > 0);

        // 打印统计信息
        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Factorial");
        System.out.println("Statistics: " + stats);

        System.out.println("Performance ratio (Engine/Traditional): " + (double) engineTime / traditionalTime);
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFactorialLargeValue() {
        System.out.println("=== Factorial Large Value Test ===");

        FactorialTask factorialTask = new FactorialTask();

        // 测试较大值
        int n = 5000;
        long startTime = System.currentTimeMillis();
        BigInteger result = factorialTask.execute(n);
        long endTime = System.currentTimeMillis();

        System.out.println("Factorial(" + n + ") calculated in " + (endTime - startTime) + " ms");
        System.out.println("Result digits: " + result.toString().length());

        assertNotNull(result);
        assertTrue(result.compareTo(BigInteger.ZERO) > 0);

        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Factorial");
        System.out.println("Statistics: " + stats);
    }

    // ==================== GenerateSubsets 性能对比测试 ====================

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testGenerateSubsetsPerformanceComparison() {
        System.out.println("=== GenerateSubsets Performance Comparison ===");

        String testString = "abcdefghij"; // 10 characters = 1024 subsets

        // 传统递归实现
        long traditionalTime = measureTraditionalSubsets(testString);
        System.out.println("Traditional Subsets(\"" + testString + "\"): " + traditionalTime + " ms");

        // 引擎管理的递归实现
        GenerateSubsetsTask subsetsTask = new GenerateSubsetsTask();
        long engineTime = measureEngineSubsets(subsetsTask, testString);
        System.out.println("Engine Subsets(\"" + testString + "\"): " + engineTime + " ms");

        // 验证结果正确性
        List<String> result = subsetsTask.execute(testString);
        assertEquals(1024, result.size()); // 2^10 = 1024

        // 打印统计信息
        RecursiveEngine.TaskStatistics stats = engine.getStatistics("GenerateSubsets");
        System.out.println("Statistics: " + stats);

        System.out.println("Performance ratio (Engine/Traditional): " + (double) engineTime / traditionalTime);
    }

    // ==================== 10,000 层深度稳定性测试 ====================

    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testFactorial10000DepthStability() {
        System.out.println("=== Factorial 10,000 Depth Stability Test ===");

        FactorialTask factorialTask = new FactorialTask();

        // 计算 Factorial(10000)，这将触发深度降级
        long startTime = System.currentTimeMillis();
        BigInteger result = factorialTask.execute(10000);
        long endTime = System.currentTimeMillis();

        System.out.println("Factorial(10000) calculated in " + (endTime - startTime) + " ms");
        System.out.println("Result digits: " + result.toString().length());

        // 验证结果
        assertNotNull(result);
        assertTrue(result.compareTo(BigInteger.ZERO) > 0);

        // 验证没有栈溢出
        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Factorial");
        System.out.println("Statistics: " + stats);

        // 验证降级被触发
        assertTrue(stats.getFallbackExecutions() > 0, "Fallback should have been triggered for deep recursion");

        System.out.println("Test passed: No StackOverflowError occurred at 10,000 depth");
    }

    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testFibonacciWithDeepRecursion() {
        System.out.println("=== Fibonacci Deep Recursion Test ===");

        FibonacciTask fibonacciTask = new FibonacciTask();

        // 测试一个会导致深度递归的值
        // 注意：Fibonacci 的递归树非常深，但每个分支不深
        // 这里我们测试一个中等值，确保引擎能处理
        int n = 50;

        long startTime = System.currentTimeMillis();
        BigInteger result = fibonacciTask.execute(n);
        long endTime = System.currentTimeMillis();

        System.out.println("Fibonacci(" + n + ") calculated in " + (endTime - startTime) + " ms");

        // 验证结果正确性
        BigInteger expected = new BigInteger("12586269025");
        assertEquals(expected, result);

        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Fibonacci");
        System.out.println("Statistics: " + stats);
    }

    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testMultipleTasksConcurrentExecution() {
        System.out.println("=== Multiple Tasks Concurrent Execution Test ===");

        // 同时执行多个任务，验证引擎的线程安全性
        Thread factorialThread = new Thread(() -> {
            FactorialTask task = new FactorialTask();
            for (int i = 100; i <= 200; i++) {
                BigInteger result = task.execute(i);
                assertNotNull(result);
            }
            System.out.println("Factorial thread completed");
        });

        Thread fibonacciThread = new Thread(() -> {
            FibonacciTask task = new FibonacciTask();
            for (int i = 10; i <= 50; i++) {
                BigInteger result = task.execute(i);
                assertNotNull(result);
            }
            System.out.println("Fibonacci thread completed");
        });

        Thread subsetsThread = new Thread(() -> {
            GenerateSubsetsTask task = new GenerateSubsetsTask();
            for (int i = 5; i <= 12; i++) {
                String input = "a".repeat(i);
                List<String> result = task.execute(input);
                assertEquals(1 << i, result.size());
            }
            System.out.println("Subsets thread completed");
        });

        long startTime = System.currentTimeMillis();

        factorialThread.start();
        fibonacciThread.start();
        subsetsThread.start();

        try {
            factorialThread.join();
            fibonacciThread.join();
            subsetsThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread interrupted");
        }

        long endTime = System.currentTimeMillis();
        System.out.println("All threads completed in " + (endTime - startTime) + " ms");

        // 打印所有统计信息
        engine.getAllStatistics().forEach((name, stats) -> {
            System.out.println(name + ": " + stats);
        });
    }

    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testIterativeFallbackCorrectness() {
        System.out.println("=== Iterative Fallback Correctness Test ===");

        // 测试降级后的迭代实现结果是否正确
        FactorialTask factorialTask = new FactorialTask();

        // 小值测试（递归）
        BigInteger smallResult = factorialTask.execute(10);
        BigInteger expectedSmall = BigInteger.valueOf(3628800);
        assertEquals(expectedSmall, smallResult);

        // 大值测试（会触发降级）
        BigInteger largeResult = factorialTask.execute(10000);
        assertNotNull(largeResult);

        // 验证迭代实现的结果与递归实现一致（对于小值）
        BigInteger iterativeResult = factorialTask.iterative(10);
        assertEquals(expectedSmall, iterativeResult);

        System.out.println("Fallback correctness verified");

        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Factorial");
        System.out.println("Statistics: " + stats);
    }

    // ==================== 辅助方法 ====================

    private long measureTraditionalFibonacci(int n) {
        long start = System.currentTimeMillis();
        BigInteger result = traditionalFibonacci(n);
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }

    private BigInteger traditionalFibonacci(int n) {
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }
        return traditionalFibonacci(n - 1).add(traditionalFibonacci(n - 2));
    }

    private long measureEngineFibonacci(FibonacciTask task, int n) {
        long start = System.currentTimeMillis();
        BigInteger result = task.execute(n);
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }

    private long measureIterativeFibonacci(int n) {
        long start = System.currentTimeMillis();
        BigInteger result = iterativeFibonacci(n);
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }

    private BigInteger iterativeFibonacci(int n) {
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

    private long measureTraditionalFactorial(int n) {
        long start = System.currentTimeMillis();
        BigInteger result = traditionalFactorial(n);
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }

    private BigInteger traditionalFactorial(int n) {
        if (n == 0 || n == 1) {
            return BigInteger.ONE;
        }
        return traditionalFactorial(n - 1).multiply(BigInteger.valueOf(n));
    }

    private long measureEngineFactorial(FactorialTask task, int n) {
        long start = System.currentTimeMillis();
        BigInteger result = task.execute(n);
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }

    private long measureIterativeFactorial(int n) {
        long start = System.currentTimeMillis();
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }

    private long measureTraditionalSubsets(String str) {
        long start = System.currentTimeMillis();
        List<String> result = GenerateSubsets.subsetRecursion(str);
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }

    private long measureEngineSubsets(GenerateSubsetsTask task, String str) {
        long start = System.currentTimeMillis();
        List<String> result = task.execute(str);
        long end = System.currentTimeMillis();
        assertNotNull(result);
        return end - start;
    }
}
