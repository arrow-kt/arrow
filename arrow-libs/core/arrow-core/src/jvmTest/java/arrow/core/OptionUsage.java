package arrow.core;

public class OptionUsage {

    public void testUsage() {
        Option<Integer> fromNullable = Option.fromNullable(null);
        Option<Integer> invoke = Option.invoke(1);
    }
}
