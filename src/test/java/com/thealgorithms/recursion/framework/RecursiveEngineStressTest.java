package com.thealgorithms.recursion.framework;

import com.thealgorithms.maths.FindMaxRecursive;
import com.thealgorithms.recursion.FactorialRecursive;
import com.thealgorithms.recursion.FibonacciRecursive;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("RecursiveEngine Stress Tests")
public class RecursiveEngineStressTest {

    @BeforeEach
    void setUp() {
        RecursiveEngine.clearStatistics();
    }

    @Test
    @DisplayName("Fibonacci: Performance comparison - Recursive vs Iterative")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFibonacciPerformanceComparison() {
        int testValue = 30;
        
        long startRecursive = System.nanoTime();
        long recursiveResult = computeFibonacciRecursive(testValue);
        long recursiveTime = System.nanoTime() - startRecursive;
        
        long startIterative = System.nanoTime();
        long iterativeResult = computeFibonacciIterative(testValue);
        long iterativeTime = System.nanoTime() - startIterative;
        
        assertEquals(recursiveResult, iterativeResult, "Results should match");
        
        System.out.println("=== Fibonacci Performance Comparison ===");
        System.out.println("Input: " + testValue);
        System.out.println("Recursive time: " + (recursiveTime / 1_000_000.0) + " ms");
        System.out.println("Iterative time: " + (iterativeTime / 1_000_000.0) + " ms");
        System.out.println("Result: " + recursiveResult);
    }

    private long computeFibonacciRecursive(int n) {
        if (n <= 1) return n;
        return computeFibonacciRecursive(n - 1) + computeFibonacciRecursive(n - 2);
    }

    private long computeFibonacciIterative(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    @Test
    @DisplayName("Fibonacci: Framework auto-switch test")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFibonacciFrameworkAutoSwitch() {
        int testValue = 40;
        
        long start = System.nanoTime();
        long result = FibonacciRecursive.fibonacci(testValue);
        long duration = System.nanoTime() - start;
        
        RecursiveEngine.TaskStatistics stats = RecursiveEngine.getStatistics("FibonacciRecursive");
        
        System.out.println("=== Fibonacci Framework Test ===");
        System.out.println("Input: " + testValue);
        System.out.println("Result: " + result);
        System.out.println("Duration: " + (duration / 1_000_000.0) + " ms");
        if (stats != null) {
            System.out.println("Statistics: " + stats);
        }
        
        assertEquals(102334155L, result, "Fibonacci(40) should equal 102334155");
    }

    @Test
    @DisplayName("Factorial: 10,000 depth stability test")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFactorialTenThousandDepthStability() {
        int depth = 10000;
        
        System.out.println("=== Factorial 10,000 Depth Stability Test ===");
        System.out.println("Testing depth: " + depth);
        
        long start = System.nanoTime();
        BigInteger result = FactorialRecursive.factorial(depth);
        long duration = System.nanoTime() - start;
        
        RecursiveEngine.TaskStatistics stats = RecursiveEngine.getStatistics("FactorialRecursive");
        
        assertTrue(result.compareTo(BigInteger.ZERO) > 0, "Factorial should be positive");
        assertTrue(result.toString().length() > 30000, "Factorial(10000) should have > 30000 digits");
        
        System.out.println("Result digits: " + result.toString().length());
        System.out.println("Duration: " + (duration / 1_000_000.0) + " ms");
        if (stats != null) {
            System.out.println("Statistics: " + stats);
            System.out.println("Iterative switches: " + stats.getIterativeSwitches());
        }
    }

    @Test
    @DisplayName("Factorial: Performance comparison at various depths")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testFactorialPerformanceComparison() {
        int[] depths = {100, 1000, 5000, 10000};
        
        System.out.println("=== Factorial Performance Comparison ===");
        
        for (int depth : depths) {
            RecursiveEngine.clearStatistics();
            
            long startFramework = System.nanoTime();
            BigInteger frameworkResult = FactorialRecursive.factorial(depth);
            long frameworkTime = System.nanoTime() - startFramework;
            
            long startIterative = System.nanoTime();
            BigInteger iterativeResult = computeFactorialIterative(depth);
            long iterativeTime = System.nanoTime() - startIterative;
            
            assertEquals(iterativeResult, frameworkResult, "Results should match for depth " + depth);
            
            System.out.println("Depth: " + depth);
            System.out.println("  Framework: " + (frameworkTime / 1_000_000.0) + " ms");
            System.out.println("  Iterative: " + (iterativeTime / 1_000_000.0) + " ms");
            System.out.println("  Digits: " + frameworkResult.toString().length());
        }
    }

    private BigInteger computeFactorialIterative(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    @Test
    @DisplayName("FindMax: Large array stability test")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFindMaxLargeArrayStability() {
        int arraySize = 100000;
        int[] largeArray = generateRandomArray(arraySize);
        int expectedMax = findExpectedMax(largeArray);
        
        System.out.println("=== FindMax Large Array Test ===");
        System.out.println("Array size: " + arraySize);
        
        long start = System.nanoTime();
        int result = FindMaxRecursive.max(largeArray);
        long duration = System.nanoTime() - start;
        
        assertEquals(expectedMax, result, "Should find correct max");
        
        RecursiveEngine.TaskStatistics stats = RecursiveEngine.getStatistics("FindMaxRecursive");
        System.out.println("Duration: " + (duration / 1_000_000.0) + " ms");
        if (stats != null) {
            System.out.println("Statistics: " + stats);
        }
    }

    @Test
    @DisplayName("FindMax: 10,000 element array depth test")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFindMaxTenThousandElements() {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        
        int result = FindMaxRecursive.max(array);
        assertEquals(9999, result, "Max should be 9999");
        
        RecursiveEngine.TaskStatistics stats = RecursiveEngine.getStatistics("FindMaxRecursive");
        if (stats != null) {
            System.out.println("FindMaxRecursive statistics: " + stats);
        }
    }

    @RepeatedTest(5)
    @DisplayName("Fibonacci: Repeated stress test")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testFibonacciRepeatedStress() {
        int n = 35;
        long result = FibonacciRecursive.fibonacci(n);
        assertEquals(9227465L, result, "Fibonacci(35) should equal 9227465");
    }

    @Test
    @DisplayName("Factorial: Edge cases")
    void testFactorialEdgeCases() {
        assertEquals(BigInteger.ONE, FactorialRecursive.factorial(0), "0! = 1");
        assertEquals(BigInteger.ONE, FactorialRecursive.factorial(1), "1! = 1");
        assertEquals(BigInteger.valueOf(2), FactorialRecursive.factorial(2), "2! = 2");
        assertEquals(BigInteger.valueOf(6), FactorialRecursive.factorial(3), "3! = 6");
        assertEquals(BigInteger.valueOf(120), FactorialRecursive.factorial(5), "5! = 120");
    }

    @Test
    @DisplayName("Factorial: Negative input exception")
    void testFactorialNegativeInput() {
        assertThrows(IllegalArgumentException.class, () -> FactorialRecursive.factorial(-1));
    }

    @Test
    @DisplayName("Fibonacci: Negative input exception")
    void testFibonacciNegativeInput() {
        assertThrows(IllegalArgumentException.class, () -> FibonacciRecursive.fibonacci(-1));
    }

    @Test
    @DisplayName("FindMax: Empty array exception")
    void testFindMaxEmptyArray() {
        assertThrows(IllegalArgumentException.class, () -> FindMaxRecursive.max(new int[]{}));
    }

    @Test
    @DisplayName("FindMax: Single element")
    void testFindMaxSingleElement() {
        assertEquals(42, FindMaxRecursive.max(new int[]{42}));
    }

    @Test
    @DisplayName("Concurrent execution test")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testConcurrentExecution() throws InterruptedException {
        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];
        boolean[] results = new boolean[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    long fib = FibonacciRecursive.fibonacci(20);
                    results[index] = (fib == 6765);
                    
                    BigInteger fact = FactorialRecursive.factorial(100);
                    results[index] = results[index] && (fact.compareTo(BigInteger.ZERO) > 0);
                } catch (Exception e) {
                    results[index] = false;
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        for (boolean result : results) {
            assertTrue(result, "All concurrent executions should succeed");
        }
        
        System.out.println("Concurrent execution test passed with " + numThreads + " threads");
    }

    @Test
    @DisplayName("Memory stress test - multiple deep recursions")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testMemoryStress() {
        System.out.println("=== Memory Stress Test ===");
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            FactorialRecursive.factorial(5000);
            FibonacciRecursive.fibonacci(30);
        }
        
        RecursiveEngine.TaskStatistics factorialStats = RecursiveEngine.getStatistics("FactorialRecursive");
        RecursiveEngine.TaskStatistics fibonacciStats = RecursiveEngine.getStatistics("FibonacciRecursive");
        
        System.out.println("Iterations: " + iterations);
        if (factorialStats != null) {
            System.out.println("Factorial stats: " + factorialStats);
        }
        if (fibonacciStats != null) {
            System.out.println("Fibonacci stats: " + fibonacciStats);
        }
    }

    @Test
    @DisplayName("Statistics tracking test")
    void testStatisticsTracking() {
        RecursiveEngine.clearStatistics();
        
        FibonacciRecursive.fibonacci(10);
        FibonacciRecursive.fibonacci(15);
        FibonacciRecursive.fibonacci(20);
        
        RecursiveEngine.TaskStatistics stats = RecursiveEngine.getStatistics("FibonacciRecursive");
        
        assertNotNull(stats, "Statistics should be tracked");
        assertEquals(3, stats.getTotalExecutions(), "Should have 3 executions");
        assertTrue(stats.getAverageDurationMillis() >= 0, "Average duration should be non-negative");
        
        System.out.println("Statistics tracking test: " + stats);
    }

    private int[] generateRandomArray(int size) {
        Random random = new Random(42);
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000000);
        }
        return array;
    }

    private int findExpectedMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
