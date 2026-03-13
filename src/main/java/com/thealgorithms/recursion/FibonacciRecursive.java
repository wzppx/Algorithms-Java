package com.thealgorithms.recursion;

import com.thealgorithms.recursion.framework.RecursiveConfig;
import com.thealgorithms.recursion.framework.RecursiveEngine;
import com.thealgorithms.recursion.framework.RecursiveTask;

@RecursiveConfig(maxDepth = 100, autoSwitchToIterative = true, description = "Fibonacci with auto-switch to iterative")
public class FibonacciRecursive implements RecursiveTask<Integer, Long> {

    @Override
    public Long compute(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative integer");
        }
        if (n <= 1) {
            return n.longValue();
        }
        return recurse(n - 1) + recurse(n - 2);
    }

    @Override
    public Long computeIterative(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative integer");
        }
        if (n == 0) {
            return 0L;
        }
        if (n == 1) {
            return 1L;
        }
        long prev = 0;
        long curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    public static long fibonacci(int n) {
        FibonacciRecursive task = new FibonacciRecursive();
        return RecursiveEngine.execute(task, n);
    }
}
