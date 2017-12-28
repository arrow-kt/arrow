package arrow.syntax.monaderror

import arrow.*

inline fun <reified F, A, reified E> HK<F, A>.ensure(
        FT: MonadError<F, E> = monadError(),
        noinline error: () -> E,
        noinline predicate: (A) -> Boolean): HK<F, A> = FT.ensure(this, error, predicate)