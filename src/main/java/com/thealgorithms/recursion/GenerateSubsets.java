package com.thealgorithms.recursion;

import com.thealgorithms.recursion.engine.RecursiveConfig;
import com.thealgorithms.recursion.engine.RecursiveEngine;
import com.thealgorithms.recursion.engine.RecursiveTask;
import java.util.ArrayList;
import java.util.List;

@RecursiveConfig(maxDepth = 1000)
public final class GenerateSubsets implements RecursiveTask<String, List<String>> {
    public static final GenerateSubsets INSTANCE = new GenerateSubsets();

    private GenerateSubsets() {
    }

    public static List<String> subsetRecursion(String str) {
        return RecursiveEngine.execute(INSTANCE, str);
    }

    @Override
    public List<String> execute(String str) {
        return generateSubsets("", str);
    }

    private List<String> generateSubsets(String current, String remaining) {
        if (remaining.isEmpty()) {
            List<String> result = new ArrayList<>();
            result.add(current);
            return result;
        }

        char ch = remaining.charAt(0);
        String next = remaining.substring(1);

        List<String> withChar = RecursiveEngine.execute(
            (RecursiveTask<String, List<String>>) s -> generateSubsets(current + ch, next),
            ""
        );
        if (withChar.isEmpty()) {
            withChar = generateSubsetsDirect(current + ch, next);
        }

        List<String> withoutChar = RecursiveEngine.execute(
            (RecursiveTask<String, List<String>>) s -> generateSubsets(current, next),
            ""
        );
        if (withoutChar.isEmpty() && !next.isEmpty()) {
            withoutChar = generateSubsetsDirect(current, next);
        }

        withChar.addAll(withoutChar);
        return withChar;
    }

    private List<String> generateSubsetsDirect(String current, String remaining) {
        List<String> result = new ArrayList<>();
        int n = remaining.length();
        for (int mask = 0; mask < (1 << n); mask++) {
            StringBuilder sb = new StringBuilder(current);
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    sb.append(remaining.charAt(i));
                }
            }
            result.add(sb.toString());
        }
        return result;
    }

    @Override
    public List<String> executeIterative(String str) {
        List<String> result = new ArrayList<>();
        int n = str.length();
        for (int mask = 0; mask < (1 << n); mask++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    sb.append(str.charAt(i));
                }
            }
            result.add(sb.toString());
        }
        return result;
    }

    @Override
    public int calculateDepth(String input) {
        return input.length();
    }
}
