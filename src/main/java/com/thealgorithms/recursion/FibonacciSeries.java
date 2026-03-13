package com.thealgorithms.recursion;

import com.thealgorithms.recursion.engine.RecursiveConfig;
import com.thealgorithms.recursion.engine.RecursiveEngine;
import com.thealgorithms.recursion.engine.RecursiveTask;
import java.math.BigInteger;

@RecursiveConfig(maxDepth = 1000)
public final class FibonacciSeries implements RecursiveTask<Integer, BigInteger> {
    public static final FibonacciSeries INSTANCE = new FibonacciSeries();

    private FibonacciSeries() {
    }

    public static int fibonacci(int n) {
        return RecursiveEngine.execute(INSTANCE, n).intValueExact();
    }

    @Override
    public BigInteger execute(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative integer");
        }
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }
        return RecursiveEngine.execute(this, n - 1).add(RecursiveEngine.execute(this, n - 2));
    }

    @Override
    public BigInteger executeIterative(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative integer");
        }
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }
        BigInteger prev = BigInteger.ZERO;
        BigInteger current = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            BigInteger next = prev.add(current);
            prev = current;
            current = next;
        }
        return current;
    }

    @Override
    public int calculateDepth(Integer input) {
        return input;
    }
}
