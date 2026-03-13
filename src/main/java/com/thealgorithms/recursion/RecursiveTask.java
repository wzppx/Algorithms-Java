package com.thealgorithms.recursion;

/**
 * 递归任务接口，定义递归算法的基本结构。
 * 该接口支持输入类型 I 和输出类型 O，允许算法实现者定义递归和迭代两种实现方式。
 *
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
public interface RecursiveTask<I, O> {

    /**
     * 执行递归计算。实现类应该在此方法中调用递归逻辑。
     * 引擎会自动监控递归深度并在超过阈值时切换到迭代实现。
     *
     * @param input 输入参数
     * @return 计算结果
     */
    O execute(I input);

    /**
     * 递归实现。这是算法的核心递归逻辑。
     * 注意：实现类应该通过 RecursiveEngine 调用此方法，而不是直接调用，
     * 以确保递归深度监控和自动降级机制正常工作。
     *
     * @param input 输入参数
     * @param context 递归上下文，用于跟踪调用深度和状态
     * @return 计算结果
     */
    O recursive(I input, RecursiveContext context);

    /**
     * 迭代实现。当递归深度超过阈值时，引擎会自动切换到此实现。
     * 实现类应该提供一个等效的迭代版本，避免栈溢出。
     *
     * @param input 输入参数
     * @return 计算结果
     */
    O iterative(I input);

    /**
     * 获取此任务的配置注解。如果实现类没有使用 @RecursiveConfig 注解，
     * 则返回默认配置。
     *
     * @return 递归配置
     */
    default RecursiveConfig getConfig() {
        RecursiveConfig config = this.getClass().getAnnotation(RecursiveConfig.class);
        if (config == null) {
            return DefaultRecursiveConfig.INSTANCE;
        }
        return config;
    }
}
