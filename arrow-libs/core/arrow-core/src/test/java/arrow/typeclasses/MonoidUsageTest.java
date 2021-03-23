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
        Monoid<Boolean> bool = Monoid.bool();
        Monoid<Integer> integer = Monoid.integer();
        Monoid<List<Integer>> list = Monoid.list();
        Monoid<String> string = Monoid.string();
        Monoid<Sequence<Integer>> sequence = Monoid.sequence();
        Monoid<Either<Boolean, Integer>> either = Monoid.either(Monoid.bool(), Monoid.integer());
        Monoid<Endo<Integer>> endo = Monoid.endo();
        Monoid<Map<String, Integer>> map = Monoid.map(Semigroup.integer());
        Monoid<Option<Integer>> option = Monoid.option(Semigroup.integer());
        Monoid<Validated<Integer, Boolean>> validated = Monoid.validated(Semigroup.integer(), Monoid.bool());

        // TODO fix
//        Monoid.const(Monoid.integer());
//        Monoid.byte();
//        Monoid.double();
//        Monoid.float();
//        Monoid.short();
    }
}
