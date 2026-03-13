package com.thealgorithms.recursion.engine;

@FunctionalInterface
public interface RecursiveTask<I, O> {
    O execute(I input);

    default O executeIterative(I input) {
        throw new UnsupportedOperationException("Iterative execution not implemented");
    }

    default int calculateDepth(I input) {
        if (input instanceof Integer intInput) {
            return intInput;
        }
        return 1;
    }
}
