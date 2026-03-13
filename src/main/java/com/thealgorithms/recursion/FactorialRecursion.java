package com.thealgorithms.recursion;

import com.thealgorithms.recursion.engine.RecursiveConfig;
import com.thealgorithms.recursion.engine.RecursiveEngine;
import com.thealgorithms.recursion.engine.RecursiveTask;
import java.math.BigInteger;

@RecursiveConfig(maxDepth = 1000)
public final class FactorialRecursion implements RecursiveTask<Integer, BigInteger> {
    public static final FactorialRecursion INSTANCE = new FactorialRecursion();

    private FactorialRecursion() {
    }

    public static long factorial(int n) {
        return RecursiveEngine.execute(INSTANCE, n).longValueExact();
    }

    @Override
    public BigInteger execute(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("number is negative");
        }
        if (n == 0 || n == 1) {
            return BigInteger.ONE;
        }
        return BigInteger.valueOf(n).multiply(RecursiveEngine.execute(this, n - 1));
    }

    @Override
    public BigInteger executeIterative(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("number is negative");
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    @Override
    public int calculateDepth(Integer input) {
        return input;
    }
}
