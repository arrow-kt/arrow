package arrow.integrations.android

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import arrow.core.Either
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.fix

/**
 * Unsafely run an [IO] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [IO.unsafeRunAsyncCancellable] but with its cancellation token wired to [LifecycleOwner]'s lifecycle.
 * Note that the operation will not be started if the owner state is already destroyed.
 *
 * @see [IO.unsafeRunAsyncCancellable] for a version that returns the cancellation token instead.
 */
fun <A> LifecycleOwner.unsafeRunIO(io: IOOf<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
  io.unsafeRunScoped(this, cb)

/**
 * Unsafely run an [IO] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [IO.unsafeRunAsyncCancellable] but with its cancellation token wired to [LifecycleOwner]'s lifecycle.
 * Note that the operation will not be started if the owner state is already destroyed.
 *
 * @see [IO.unsafeRunAsyncCancellable] for a version that returns the cancellation token instead.
 */
fun <A> IOOf<A>.unsafeRunScoped(
  owner: LifecycleOwner,
  cb: (Either<Throwable, A>) -> Unit
) {
  if (owner.lifecycle.currentState.isAtLeast(State.CREATED)) {
    val disposable = fix().unsafeRunAsyncCancellable(cb = cb)

    owner.lifecycle.addObserver(object : LifecycleEventObserver {
      override fun onStateChanged(source: LifecycleOwner, event: Event) {
        if (event == Event.ON_DESTROY) {
          source.lifecycle.removeObserver(this)
          disposable()
        }
      }
    })
  }
}
