package arrow.core;

import kotlin.jvm.functions.Function1;

public class IorUsage {

    public void testUsage() {
        Ior<NonEmptyList<Integer>, Integer> bothNel = Ior.bothNel(1, 2);
        Ior<Integer, String> fromNullables = Ior.fromNullables(1, null);
        Function1<Ior<? extends Integer, ? extends String>, Ior<Integer, String>> lift = Ior.lift((b) -> b.toUpperCase());
        Function1<Ior<? extends Integer, ? extends String>, Ior<Integer, String>> lift1 = Ior.lift((a) -> a + 1, (b) -> b.toUpperCase());
    }
}
