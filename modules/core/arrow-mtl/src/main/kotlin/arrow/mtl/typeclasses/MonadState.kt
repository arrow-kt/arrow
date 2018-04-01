package arrow.mtl.typeclasses

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Monad

inline operator fun <F, S, A> MonadState<F, S>.invoke(ff: MonadState<F, S>.() -> A) =
  run(ff)

interface MonadState<F, S> : Monad<F> {

  fun <A> state(f: (S) -> Tuple2<S, A>): Kind<F, A> = get().flatMap({ s -> f(s).let { (a, b) -> set(a).map({ b }) } })

  fun get(): Kind<F, S>

  fun set(s: S): Kind<F, Unit>

  fun modify(f: (S) -> S): Kind<F, Unit> = get().flatMap({ s -> set(f(s)) })

  fun <A> inspect(f: (S) -> A): Kind<F, A> = get().map(f)
}