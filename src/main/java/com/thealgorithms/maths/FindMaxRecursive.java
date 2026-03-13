package com.thealgorithms.maths;

import com.thealgorithms.recursion.framework.RecursiveConfig;
import com.thealgorithms.recursion.framework.RecursiveEngine;
import com.thealgorithms.recursion.framework.RecursiveTask;

@RecursiveConfig(maxDepth = 100, autoSwitchToIterative = true, description = "FindMax with auto-switch to iterative")
public class FindMaxRecursive implements RecursiveTask<FindMaxRecursive.Input, Integer> {

    public static final class Input {
        final int[] array;
        final int low;
        final int high;

        public Input(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        public Input(int[] array) {
            this(array, 0, array.length - 1);
        }
    }

    @Override
    public Integer compute(Input input) {
        int[] array = input.array;
        int low = input.low;
        int high = input.high;

        if (array.length == 0) {
            throw new IllegalArgumentException("Array must be non-empty.");
        }
        if (low == high) {
            return array[low];
        }

        int mid = (low + high) >>> 1;

        int leftMax = recurse(new Input(array, low, mid));
        int rightMax = recurse(new Input(array, mid + 1, high));

        return Math.max(leftMax, rightMax);
    }

    @Override
    public Integer computeIterative(Input input) {
        int[] array = input.array;
        if (array.length == 0) {
            throw new IllegalArgumentException("Array must be non-empty.");
        }
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static int max(int[] array) {
        return max(array, 0, array.length - 1);
    }

    public static int max(int[] array, int low, int high) {
        FindMaxRecursive task = new FindMaxRecursive();
        return RecursiveEngine.execute(task, new Input(array, low, high));
    }
}
