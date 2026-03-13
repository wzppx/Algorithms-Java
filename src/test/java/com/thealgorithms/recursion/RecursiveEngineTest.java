package com.thealgorithms.recursion;

import java.math.BigInteger;
import java.util.List;

/**
 * RecursiveEngine 简单测试类。
 * 用于验证递归框架的功能和性能。
 */
public class RecursiveEngineTest {

    public static void main(String[] args) {
        System.out.println("=== RecursiveEngine Test Suite ===\n");

        testFibonacciBasic();
        testFibonacciMedium();
        testFactorialBasic();
        testFactorial10000Depth();
        testGenerateSubsets();
        testPerformanceComparison();
        testConcurrentExecution();

        System.out.println("\n=== All Tests Completed ===");
    }

    private static void testFibonacciBasic() {
        System.out.println("--- Fibonacci Basic Test ---");
        FibonacciTask task = new FibonacciTask();

        // 测试小值
        BigInteger result = task.execute(10);
        BigInteger expected = BigInteger.valueOf(55);
        assertResult("Fibonacci(10)", expected, result);

        // 测试边界值
        assertResult("Fibonacci(0)", BigInteger.ZERO, task.execute(0));
        assertResult("Fibonacci(1)", BigInteger.ONE, task.execute(1));

        System.out.println("Fibonacci basic test passed!\n");
    }

    private static void testFibonacciMedium() {
        System.out.println("--- Fibonacci Medium Value Test ---");
        FibonacciTask task = new FibonacciTask();

        // 使用迭代版本测试中等值（避免递归太慢）
        long start = System.currentTimeMillis();
        BigInteger result = task.iterative(100);
        long end = System.currentTimeMillis();

        System.out.println("Fibonacci(100) calculated in " + (end - start) + " ms");
        System.out.println("Result has " + result.toString().length() + " digits");

        // 验证结果不为空且为正数
        if (result == null || result.compareTo(BigInteger.ZERO) <= 0) {
            throw new AssertionError("Fibonacci medium value test failed!");
        }

        System.out.println("Fibonacci medium value test passed!\n");
    }

    private static void testFactorialBasic() {
        System.out.println("--- Factorial Basic Test ---");
        FactorialTask task = new FactorialTask();

        assertResult("Factorial(0)", BigInteger.ONE, task.execute(0));
        assertResult("Factorial(1)", BigInteger.ONE, task.execute(1));
        assertResult("Factorial(5)", BigInteger.valueOf(120), task.execute(5));
        assertResult("Factorial(10)", BigInteger.valueOf(3628800), task.execute(10));

        System.out.println("Factorial basic test passed!\n");
    }

    private static void testFactorial10000Depth() {
        System.out.println("--- Factorial 10,000 Depth Stability Test ---");
        FactorialTask task = new FactorialTask();
        RecursiveEngine engine = RecursiveEngine.getInstance();
        engine.clearAllStatistics();

        long start = System.currentTimeMillis();
        BigInteger result = task.execute(10000);
        long end = System.currentTimeMillis();

        System.out.println("Factorial(10000) calculated in " + (end - start) + " ms");
        System.out.println("Result has " + result.toString().length() + " digits");

        RecursiveEngine.TaskStatistics stats = engine.getStatistics("Factorial");
        System.out.println("Statistics: " + stats);

        if (stats.getFallbackExecutions() == 0) {
            System.out.println("WARNING: Fallback was not triggered!");
        } else {
            System.out.println("SUCCESS: Fallback was triggered " + stats.getFallbackExecutions() + " times");
        }

        if (result == null || result.compareTo(BigInteger.ZERO) <= 0) {
            throw new AssertionError("Factorial 10000 depth test failed!");
        }

        System.out.println("Factorial 10,000 depth test passed! No StackOverflowError!\n");
    }

    private static void testGenerateSubsets() {
        System.out.println("--- Generate Subsets Test ---");
        GenerateSubsetsTask task = new GenerateSubsetsTask();

        List<String> result = task.execute("abc");
        System.out.println("Subsets of 'abc': " + result);

        if (result.size() != 8) { // 2^3 = 8
            throw new AssertionError("Expected 8 subsets, got " + result.size());
        }

        // 测试较大输入
        String largeInput = "abcdefghij"; // 10 characters
        long start = System.currentTimeMillis();
        List<String> largeResult = task.execute(largeInput);
        long end = System.currentTimeMillis();

        System.out.println("Subsets of 10-char string calculated in " + (end - start) + " ms");
        System.out.println("Number of subsets: " + largeResult.size() + " (expected 1024)");

        if (largeResult.size() != 1024) {
            throw new AssertionError("Expected 1024 subsets, got " + largeResult.size());
        }

        System.out.println("Generate subsets test passed!\n");
    }

    private static void testPerformanceComparison() {
        System.out.println("--- Performance Comparison Test ---");

        // 传统递归 Fibonacci (小值)
        long traditionalTime = measureTraditionalFibonacci(20);
        System.out.println("Traditional Fibonacci(20): " + traditionalTime + " ms");

        // 引擎 Fibonacci
        FibonacciTask fibTask = new FibonacciTask();
        long engineStart = System.currentTimeMillis();
        BigInteger engineResult = fibTask.execute(20);
        long engineTime = System.currentTimeMillis() - engineStart;
        System.out.println("Engine Fibonacci(20): " + engineTime + " ms");

        // 验证结果正确
        BigInteger expected = BigInteger.valueOf(6765);
        if (!expected.equals(engineResult)) {
            throw new AssertionError("Engine result incorrect! Expected " + expected + ", got " + engineResult);
        }

        // 迭代 Fibonacci
        long iterativeStart = System.currentTimeMillis();
        BigInteger iterativeResult = iterativeFibonacci(20);
        long iterativeTime = System.currentTimeMillis() - iterativeStart;
        System.out.println("Iterative Fibonacci(20): " + iterativeTime + " ms");

        System.out.println("Performance ratio (Engine/Traditional): " + (double) engineTime / Math.max(traditionalTime, 1));
        System.out.println("Performance comparison test passed!\n");
    }

    private static void testConcurrentExecution() {
        System.out.println("--- Concurrent Execution Test ---");

        Thread factorialThread = new Thread(() -> {
            FactorialTask task = new FactorialTask();
            for (int i = 100; i <= 200; i++) {
                BigInteger result = task.execute(i);
                if (result == null) {
                    throw new AssertionError("Factorial(" + i + ") returned null");
                }
            }
            System.out.println("Factorial thread completed");
        });

        Thread fibonacciThread = new Thread(() -> {
            FibonacciTask task = new FibonacciTask();
            for (int i = 5; i <= 20; i++) {
                BigInteger result = task.execute(i);
                if (result == null) {
                    throw new AssertionError("Fibonacci(" + i + ") returned null");
                }
            }
            System.out.println("Fibonacci thread completed");
        });

        Thread subsetsThread = new Thread(() -> {
            GenerateSubsetsTask task = new GenerateSubsetsTask();
            for (int i = 5; i <= 10; i++) {
                String input = "a".repeat(i);
                List<String> result = task.execute(input);
                if (result.size() != (1 << i)) {
                    throw new AssertionError("Subsets size mismatch for length " + i);
                }
            }
            System.out.println("Subsets thread completed");
        });

        long start = System.currentTimeMillis();

        factorialThread.start();
        fibonacciThread.start();
        subsetsThread.start();

        try {
            factorialThread.join();
            fibonacciThread.join();
            subsetsThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted", e);
        }

        long end = System.currentTimeMillis();
        System.out.println("All threads completed in " + (end - start) + " ms");

        // 打印统计信息
        RecursiveEngine.getInstance().getAllStatistics().forEach((name, stats) -> {
            System.out.println(name + ": " + stats);
        });

        System.out.println("Concurrent execution test passed!\n");
    }

    // ==================== Helper Methods ====================

    private static void assertResult(String testName, BigInteger expected, BigInteger actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(testName + " failed! Expected " + expected + ", got " + actual);
        }
        System.out.println(testName + " = " + actual + " (correct)");
    }

    private static long measureTraditionalFibonacci(int n) {
        long start = System.currentTimeMillis();
        BigInteger result = traditionalFibonacci(n);
        long end = System.currentTimeMillis();
        if (result == null) {
            throw new AssertionError("Traditional Fibonacci returned null");
        }
        return end - start;
    }

    private static BigInteger traditionalFibonacci(int n) {
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }
        return traditionalFibonacci(n - 1).add(traditionalFibonacci(n - 2));
    }

    private static BigInteger iterativeFibonacci(int n) {
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
}
