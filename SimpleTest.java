import com.thealgorithms.recursion.FibonacciSeries;
import com.thealgorithms.recursion.FactorialRecursion;
import java.math.BigInteger;

public class SimpleTest {
    public static void main(String[] args) {
        System.out.println("=== Basic Functionality ===");
        System.out.println("Fibonacci(10): " + FibonacciSeries.fibonacci(10));
        System.out.println("Factorial(5): " + FactorialRecursion.factorial(5));
        
        System.out.println("\n=== Testing Iterative Fallback (10000) ===");
        long start = System.currentTimeMillis();
        BigInteger result = FactorialRecursion.INSTANCE.execute(10000);
        long time = System.currentTimeMillis() - start;
        System.out.println("Factorial(10000) completed in " + time + "ms");
        System.out.println("Result has " + result.toString().length() + " digits");
        
        System.out.println("\n=== Testing Fibonacci(5000) ===");
        start = System.currentTimeMillis();
        result = FibonacciSeries.INSTANCE.execute(5000);
        time = System.currentTimeMillis() - start;
        System.out.println("Fibonacci(5000) completed in " + time + "ms");
        System.out.println("Result has " + result.toString().length() + " digits");
        
        System.out.println("\n=== Testing 10000 layer stability (Fibonacci) ===");
        start = System.currentTimeMillis();
        result = FibonacciSeries.INSTANCE.execute(10000);
        time = System.currentTimeMillis() - start;
        System.out.println("Fibonacci(10000) completed in " + time + "ms");
        System.out.println("Result has " + result.toString().length() + " digits");
        
        System.out.println("\nAll tests passed!");
    }
}
