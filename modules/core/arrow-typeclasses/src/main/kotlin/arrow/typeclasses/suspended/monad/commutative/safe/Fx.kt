package arrow.typeclasses.suspended.monad.commutative.safe

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadContinuation
import arrow.unsafe

interface Fx<F> {

  fun monad(): Monad<F>

  fun <A, B, C> fx(
    fa: suspend () -> A,
    fb: suspend () -> B,
    f: (Tuple2<A, B>) -> C
  ): Kind<F, C> =
    monad().fx {
      map(fa.liftM(), fb.liftM(), f).bind()
    }

  fun <A, B, C, D> fx(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    f: (Tuple3<A, B, C>) -> D
  ): Kind<F, D> =
    monad().fx {
      map(fa.liftM(), fb.liftM(), fc.liftM(), f).bind()
    }

  suspend fun <A> unsafe.fx(
    f: suspend MonadContinuation<F, *>.() -> A
  ): Kind<F, A> =
    monad().fx(f)

}

