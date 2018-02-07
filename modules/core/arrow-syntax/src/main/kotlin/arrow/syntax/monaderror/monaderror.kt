package arrow.syntax.monaderror

import arrow.*
import arrow.typeclasses.MonadError
import arrow.typeclasses.monadError

inline fun <reified F, A, reified E> Kind<F, A>.ensure(
        FT: MonadError<F, E> = monadError(),
        noinline error: () -> E,
        noinline predicate: (A) -> Boolean): Kind<F, A> = FT.ensure(this, error, predicate)