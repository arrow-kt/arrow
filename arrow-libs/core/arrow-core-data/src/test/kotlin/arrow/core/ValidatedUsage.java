package arrow.core;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ValidatedUsage {

    public void testUsage() {
        Validated<Throwable, Integer> throwableIntegerValidated = Validated.tryCatch(() -> 1);
        Validated<String, Integer> stringIntegerValidated = Validated.tryCatch((throwable) -> throwable.getMessage(), () -> 1);
        Function1<Validated<? extends String, ? extends Integer>, Validated<String, Integer>> lift = Validated.<String, Integer, Integer>lift((b) -> b + 1);
        Function1<Validated<? extends String, ? extends Integer>, Validated<String, Integer>> lift1 = Validated.<String, Integer, String, Integer>lift((a) -> a.toUpperCase(), (b) -> b + 1);
        Validated<Unit, Integer> unitIntegerValidated = Validated.fromNullable(1, () -> Unit.INSTANCE);
        Validated<Unit, Integer> unitIntegerValidated1 = Validated.fromOption(Option.invoke(1), () -> Unit.INSTANCE);
        Validated<Unit, Integer> unitIntegerValidated2 = Validated.fromEither(Either.fromNullable(1));
        Validated<NonEmptyList<String>, Integer> nonEmptyListIntegerValidated = Validated.validNel(1);
        Validated<NonEmptyList<String>, Integer> nonEmptyListObjectValidated = Validated.invalidNel("1");
    }
}
