package com.thealgorithms.recursion.framework;

public interface RecursiveTask<I, O> {
    O compute(I input);
    O computeIterative(I input);
    default String getTaskName() {
        return this.getClass().getSimpleName();
    }
    default O recurse(I input) {
        return RecursiveEngine.executeStep(this, input);
    }
}
