package arrow.effects.typeclasses.suspended.concurrent

import arrow.Kind
import arrow.core.Tuple2
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ConcurrentCancellableContinuation
import arrow.effects.typeclasses.Disposable

interface Fx<F> {
  fun concurrent(): Concurrent<F>
  fun <A> fx(f: suspend ConcurrentCancellableContinuation<F, *>.() -> A): Kind<F, A> =
    concurrent().bindingConcurrent(f).a
  fun <A> fxCancellable(f: suspend ConcurrentCancellableContinuation<F, *>.() -> A): Tuple2<Kind<F, A>, Disposable> =
    concurrent().bindingConcurrent(f)
}

