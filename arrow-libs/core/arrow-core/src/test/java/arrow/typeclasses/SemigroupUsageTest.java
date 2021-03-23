package arrow.typeclasses;

import arrow.core.*;
import kotlin.sequences.Sequence;

import java.util.List;
import java.util.Map;

public class SemigroupUsageTest {

    public void testUsage() {
        Semigroup<Boolean> bool = Semigroup.bool();
        Semigroup<Integer> integer = Semigroup.integer();
        Semigroup<List<Integer>> list = Semigroup.list();
        Semigroup<String> string = Semigroup.string();
        Semigroup<Sequence<Integer>> sequence = Semigroup.sequence();
        Semigroup<Either<Boolean, Integer>> either = Semigroup.either(Semigroup.bool(), Semigroup.integer());
        Semigroup<Ior<Boolean, Integer>> ior = Semigroup.ior(Semigroup.bool(), Semigroup.integer());
        Semigroup<Endo<Integer>> endo = Semigroup.endo();
        Semigroup<Map<String, Integer>> map = Semigroup.map(Semigroup.integer());
        Semigroup<Option<Integer>> option = Semigroup.option(Semigroup.integer());
        Semigroup<Validated<Boolean, Integer>> validated = Semigroup.validated(Semigroup.bool(), Semigroup.integer());
        Semigroup<NonEmptyList<Integer>> nonEmptyList = Semigroup.nonEmptyList();

        // TODO fix
//        Semigroup.const(Semigroup.integer());
//        Semigroup.byte();
//        Semigroup.double();
//        Semigroup.float();
//        Semigroup.short();
    }
}
