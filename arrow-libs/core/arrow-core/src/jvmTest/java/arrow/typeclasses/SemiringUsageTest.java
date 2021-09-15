package arrow.typeclasses;

public class SemiringUsageTest {

    public void testUsage() {
        Semiring<Integer> integer = Semiring.Integer();
        Semiring<Byte> aByte = Semiring.Byte();
        Semiring<Short> aShort = Semiring.Short();
    }
}
