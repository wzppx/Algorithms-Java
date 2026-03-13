package com.thealgorithms.recursion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * GenerateSubsets 任务实现，支持递归和迭代两种计算方式。
 * 使用 @RecursiveConfig 注解配置深度阈值和降级策略。
 */
@RecursiveConfig(
    depthThreshold = 100,
    enableFallback = true,
    enableMonitoring = true,
    taskName = "GenerateSubsets",
    maxDepth = 10000
)
public class GenerateSubsetsTask implements RecursiveTask<String, List<String>> {

    private final RecursiveEngine engine;
    private BiFunction<SubsetInput, RecursiveContext, List<String>> recursiveWrapper;

    public GenerateSubsetsTask() {
        this.engine = RecursiveEngine.getInstance();
    }

    @Override
    public List<String> execute(String input) {
        return engine.register(this).apply(input);
    }

    @Override
    public List<String> recursive(String str, RecursiveContext context) {
        // 使用包装对象传递多个参数
        return recursiveInternal(new SubsetInput("", str), context);
    }

    /**
     * 内部递归实现，使用 SubsetInput 包装当前前缀和剩余字符串。
     */
    private List<String> recursiveInternal(SubsetInput input, RecursiveContext context) {
        String current = input.current;
        String remaining = input.remaining;

        // 基础情况
        if (remaining.isEmpty()) {
            List<String> result = new ArrayList<>();
            result.add(current);
            return result;
        }

        // 延迟初始化包装器
        if (recursiveWrapper == null) {
            recursiveWrapper = engine.createRecursiveWrapper(
                new RecursiveTask<SubsetInput, List<String>>() {
                    @Override
                    public List<String> execute(SubsetInput inp) {
                        return null;
                    }

                    @Override
                    public List<String> recursive(SubsetInput inp, RecursiveContext ctx) {
                        return recursiveInternal(inp, ctx);
                    }

                    @Override
                    public List<String> iterative(SubsetInput inp) {
                        return iterativeInternal(inp.current, inp.remaining);
                    }
                },
                context
            );
        }

        // 检查是否需要降级
        if (context.isThresholdExceeded() && context.getConfig().enableFallback() && !context.isFallbackTriggered()) {
            context.markFallbackTriggered();
            return iterativeInternal(current, remaining);
        }

        // 递归计算
        context.enter();
        try {
            char ch = remaining.charAt(0);
            String next = remaining.substring(1);

            // 包含当前字符
            List<String> withChar = recursiveWrapper.apply(new SubsetInput(current + ch, next), context);

            // 不包含当前字符
            List<String> withoutChar = recursiveWrapper.apply(new SubsetInput(current, next), context);

            withChar.addAll(withoutChar);
            return withChar;
        } finally {
            context.exit();
        }
    }

    @Override
    public List<String> iterative(String str) {
        return iterativeInternal("", str);
    }

    /**
     * 迭代实现，使用栈模拟递归。
     */
    private List<String> iterativeInternal(String initialCurrent, String initialRemaining) {
        List<String> result = new ArrayList<>();

        // 使用栈来模拟递归
        java.util.ArrayDeque<SubsetInput> stack = new java.util.ArrayDeque<>();
        stack.push(new SubsetInput(initialCurrent, initialRemaining));

        while (!stack.isEmpty()) {
            SubsetInput input = stack.pop();
            String current = input.current;
            String remaining = input.remaining;

            if (remaining.isEmpty()) {
                result.add(current);
                continue;
            }

            char ch = remaining.charAt(0);
            String next = remaining.substring(1);

            // 先压入"不包含"分支，后压入"包含"分支（栈的后进先出特性）
            stack.push(new SubsetInput(current, next));
            stack.push(new SubsetInput(current + ch, next));
        }

        return result;
    }

    /**
     * 生成所有子集（静态便捷方法）。
     *
     * @param str 输入字符串
     * @return 所有子集列表
     */
    public static List<String> generate(String str) {
        return new GenerateSubsetsTask().execute(str);
    }

    /**
     * 内部输入包装类，用于传递多个参数。
     */
    private static class SubsetInput {
        final String current;
        final String remaining;

        SubsetInput(String current, String remaining) {
            this.current = current;
            this.remaining = remaining;
        }
    }
}
