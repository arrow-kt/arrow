package arrow.core;

public class IorUsage {

    public void testUsage() {
        Ior<NonEmptyList<Integer>, Integer> bothNel = Ior.bothNel(1, 2);
        Ior<Integer, String> fromNullables = Ior.fromNullables(1, null);
    }
}
