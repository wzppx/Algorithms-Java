package com.thealgorithms.recursion.framework;

import java.util.function.Function;

@RecursiveConfig(maxDepth = 1000, autoSwitchToIterative = true)
public final class RecursiveTaskWrapper<I, O> implements RecursiveTask<I, O> {
    private final Function<I, O> recursiveFunction;
    private final Function<I, O> iterativeFunction;
    private final String taskName;

    private RecursiveTaskWrapper(Function<I, O> recursiveFunction, Function<I, O> iterativeFunction, String taskName) {
        this.recursiveFunction = recursiveFunction;
        this.iterativeFunction = iterativeFunction;
        this.taskName = taskName;
    }

    public static <I, O> RecursiveTaskWrapper<I, O> of(Function<I, O> recursive, Function<I, O> iterative, String name) {
        return new RecursiveTaskWrapper<>(recursive, iterative, name);
    }

    @Override
    public O compute(I input) {
        return recursiveFunction.apply(input);
    }

    @Override
    public O computeIterative(I input) {
        return iterativeFunction.apply(input);
    }

    @Override
    public String getTaskName() {
        return taskName;
    }
}
