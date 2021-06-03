package arrow.core;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EitherUsage {

    public void testUsage() {
        Either<Throwable, Integer> throwableIntegerEither = Either.tryCatch(() -> 1);
        Either<Throwable, Integer> throwableIntegerEither1 = Either.<Integer>tryCatchAndFlatten(() -> new Either.Right(1));
        Function1<Either<? extends String, ? extends Integer>, Either<String, Integer>> lift = Either.lift((b) -> b + 1);
        Function1<Either<? extends String, ? extends Integer>, Either<String, Integer>> lift1 = Either.lift((a) -> a.toUpperCase(), (b) -> b + 1);
        Either<Integer, String> conditionally = Either.conditionally(true, () -> 1, () -> "1");
        Either<Unit, Integer> unitIntegerEither = Either.fromNullable(1);
        Integer resolve = Either.<String, Integer, Integer>resolve(
                () -> new Either.Right(1),
                (a) -> new Either.Right(a + 1),
                (b) -> new Either.Right(0),
                (throwable) -> new Either.Left(throwable),
                (throwable) -> new Either.Left(throwable)
        );
    }
}
