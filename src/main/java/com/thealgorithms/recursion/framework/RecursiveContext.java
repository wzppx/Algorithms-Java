package com.thealgorithms.recursion.framework;

public final class RecursiveContext {
    private static final ThreadLocal<Integer> CURRENT_DEPTH = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> MAX_REACHED_DEPTH = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Boolean> SWITCHED_TO_ITERATIVE = ThreadLocal.withInitial(() -> false);

    private RecursiveContext() {
    }

    public static int getCurrentDepth() {
        return CURRENT_DEPTH.get();
    }

    public static void incrementDepth() {
        CURRENT_DEPTH.set(CURRENT_DEPTH.get() + 1);
        int current = CURRENT_DEPTH.get();
        if (current > MAX_REACHED_DEPTH.get()) {
            MAX_REACHED_DEPTH.set(current);
        }
    }

    public static void decrementDepth() {
        CURRENT_DEPTH.set(CURRENT_DEPTH.get() - 1);
    }

    public static int getMaxReachedDepth() {
        return MAX_REACHED_DEPTH.get();
    }

    public static boolean hasSwitchedToIterative() {
        return SWITCHED_TO_ITERATIVE.get();
    }

    public static void markSwitchedToIterative() {
        SWITCHED_TO_ITERATIVE.set(true);
    }

    public static void reset() {
        CURRENT_DEPTH.set(0);
        MAX_REACHED_DEPTH.set(0);
        SWITCHED_TO_ITERATIVE.set(false);
    }

    public static void resetDepth() {
        CURRENT_DEPTH.set(0);
    }
}
