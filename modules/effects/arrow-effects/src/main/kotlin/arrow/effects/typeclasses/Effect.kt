package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either

inline operator fun <F, A> Effect<F>.invoke(ff: Effect<F>.() -> A) =
        run(ff)

interface Effect<F> : Async<F> {
    fun <A> Kind<F, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}
