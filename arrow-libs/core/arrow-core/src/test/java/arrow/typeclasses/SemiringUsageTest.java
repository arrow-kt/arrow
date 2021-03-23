package arrow.typeclasses;

public class SemiringUsageTest {

    public void testUsage() {
        Semiring<Integer> integer = Semiring.Integer();
        Semiring<Byte> aByte = Semiring.Byte();
        Semiring<Double> aDouble = Semiring.Double();
        Semiring<Float> aFloat = Semiring.Float();
        Semiring<Short> aShort = Semiring.Short();
    }
}
