package arrow.typeclasses;

import arrow.core.Either;
import arrow.core.Endo;
import arrow.core.Option;
import arrow.core.Validated;
import kotlin.sequences.Sequence;

import java.util.List;
import java.util.Map;

public class MonoidUsageTest {

    public void testUsage() {
        Monoid<Byte> aByte = Monoid.Byte();
        Monoid<Double> aDouble = Monoid.Double();
        Monoid<Float> aFloat = Monoid.Float();
        Monoid<Short> aShort = Monoid.Short();
        Monoid<Boolean> bool = Monoid.Boolean();
        Monoid<Integer> integer = Monoid.Integer();

        Monoid<String> string = Monoid.string();
        Monoid<List<Integer>> list = Monoid.list();
        Monoid<Sequence<Integer>> sequence = Monoid.sequence();
        Monoid<Either<Boolean, Integer>> either = Monoid.either(Monoid.Boolean(), Monoid.Integer());
        Monoid<Endo<Integer>> endo = Monoid.endo();
        Monoid<Map<String, Integer>> map = Monoid.map(Semigroup.Integer());
        Monoid<Option<Integer>> option = Monoid.option(Semigroup.Integer());
        Monoid<Validated<Integer, Boolean>> validated = Monoid.validated(Semigroup.Integer(), Monoid.Boolean());

        Monoid.constant(Monoid.Integer());
    }
}
