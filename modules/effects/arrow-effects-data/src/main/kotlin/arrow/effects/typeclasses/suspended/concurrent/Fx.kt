package arrow.effects.typeclasses.suspended.concurrent

import arrow.Kind
import arrow.core.Tuple2
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ConcurrentCancellableContinuation
import arrow.effects.typeclasses.Disposable

/**
 * Fx allows you to run pure sequential code as if it was imperative.
 *
 * The [Concurrent] version of Fx is the only one that allows effects to be run by calling [arrow.effects.typeclasses.suspended.FxSyntax.effect].
 *
 * To run effects use any of the binding functions like you would with any other [Kind].
 *
 * ```
 * suspend fun sideEffect(n: Int) = println("Seen $n").let { n + 1 }
 *
 * fx {
 *   val one = effect { sideEffect() }.bind() // using bind
 *   val (two) = effect { sideEffect(one) } // using destructuring
 *   val three = !effect { sideEffect(two) } // easiest to grep for
 * }
 * ```
 *
 * @see [arrow.typeclasses.suspended.BindSyntax]
 * @see [arrow.typeclasses.suspended.monad.Fx]
 * @see [arrow.typeclasses.suspended.monaderror.Fx]
 * @see [arrow.effects.typeclasses.suspended.monaddefer.Fx]
 * @see [arrow.effects.typeclasses.suspended.concurrent.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.safe.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.unsafe.Fx]
 */
interface Fx<F> {
  fun concurrent(): Concurrent<F>
  fun <A> fx(f: suspend ConcurrentCancellableContinuation<F, *>.() -> A): Kind<F, A> =
    concurrent().bindingConcurrent(f).a
  fun <A> fxCancellable(f: suspend ConcurrentCancellableContinuation<F, *>.() -> A): Tuple2<Kind<F, A>, Disposable> =
    concurrent().bindingConcurrent(f)
}
