package com.thealgorithms.recursion;

import com.thealgorithms.recursion.framework.RecursiveConfig;
import com.thealgorithms.recursion.framework.RecursiveEngine;
import com.thealgorithms.recursion.framework.RecursiveTask;
import java.math.BigInteger;

@RecursiveConfig(maxDepth = 500, autoSwitchToIterative = true, description = "Factorial with auto-switch to iterative")
public class FactorialRecursive implements RecursiveTask<Integer, BigInteger> {

    @Override
    public BigInteger compute(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("number is negative");
        }
        if (n == 0 || n == 1) {
            return BigInteger.ONE;
        }
        return BigInteger.valueOf(n).multiply(recurse(n - 1));
    }

    @Override
    public BigInteger computeIterative(Integer n) {
        if (n < 0) {
            throw new IllegalArgumentException("number is negative");
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    public static BigInteger factorial(int n) {
        FactorialRecursive task = new FactorialRecursive();
        return RecursiveEngine.execute(task, n);
    }
}
