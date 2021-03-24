package arrow.core;

public class NullableUsage {

    public void testUsage() {
        Nullable.<Integer, Integer, Integer, Integer>zip(1, null, 2, (a, b, c) -> a + b + c);
    }
}
