package arrow.mtl.typeclasses

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Monad

interface MonadState<F, S> : Monad<F> {

    fun <A> state(f: (S) -> Tuple2<S, A>): Kind<F, A> = flatMap(get(), { s -> f(s).let { (a, b) -> map(set(a), { b }) } })

    fun get(): Kind<F, S>

    fun set(s: S): Kind<F, Unit>

    fun modify(f: (S) -> S): Kind<F, Unit> = flatMap(get(), { s -> set(f(s)) })

    fun <A> inspect(f: (S) -> A): Kind<F, A> = map(get(), f)
}