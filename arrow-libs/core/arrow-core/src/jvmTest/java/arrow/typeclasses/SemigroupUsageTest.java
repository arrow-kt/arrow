package arrow.typeclasses;

import arrow.core.*;
import kotlin.Pair;
import kotlin.sequences.Sequence;

import java.util.List;
import java.util.Map;

public class SemigroupUsageTest {

    public void testUsage() {
        Semigroup<Boolean> bool = Semigroup.Boolean();
        Semigroup<Integer> integer = Semigroup.Integer();
        Semigroup<List<Integer>> list = Semigroup.list();
        Semigroup<String> string = Semigroup.string();
        Semigroup<Sequence<Integer>> sequence = Semigroup.sequence();
        Semigroup<Either<Boolean, Integer>> either = Semigroup.either(Semigroup.Boolean(), Semigroup.Integer());
        Semigroup<Ior<Boolean, Integer>> ior = Semigroup.ior(Semigroup.Boolean(), Semigroup.Integer());
        Semigroup<Endo<Integer>> endo = Semigroup.endo();
        Semigroup<Map<String, Integer>> map = Semigroup.map(Semigroup.Integer());
        Semigroup<Option<Integer>> option = Semigroup.option(Semigroup.Integer());
        Semigroup<NonEmptyList<Integer>> nonEmptyList = Semigroup.nonEmptyList();
        Semigroup<Pair<Boolean, Integer>> pair = Semigroup.pair(Semigroup.Boolean(), Semigroup.Integer());
        Semigroup<Const<Integer, Object>> constant = Semigroup.constant(Semigroup.Integer());
        Semigroup<Byte> aByte = Semigroup.Byte();
        Semigroup<Short> aShort = Semigroup.Short();
    }
}
