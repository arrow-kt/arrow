package arrow.integrations.android

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.IOResult
import arrow.fx.fix

/**
 * Unsafely run an [IO] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [IO.unsafeRunAsyncCancellableEither] but with its cancellation token wired to [LifecycleOwner]'s lifecycle.
 * Note that the operation will not be started if the owner state is already destroyed.
 *
 * @see [IO.unsafeRunAsyncCancellableEither] for a version that returns the cancellation token instead.
 */
fun <E, A> LifecycleOwner.unsafeRunIO(io: IOOf<E, A>, cb: (IOResult<E, A>) -> Unit): Unit =
  io.unsafeRunScoped(this, cb)

/**
 * Unsafely run an [IO] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [IO.unsafeRunAsyncCancellableEither] but with its cancellation token wired to [LifecycleOwner]'s lifecycle.
 * Note that the operation will not be started if the owner state is already destroyed.
 *
 * @see [IO.unsafeRunAsyncCancellableEither] for a version that returns the cancellation token instead.
 */
fun <E, A> IOOf<E, A>.unsafeRunScoped(
  owner: LifecycleOwner,
  cb: (IOResult<E, A>) -> Unit
) {
  if (owner.lifecycle.currentState.isAtLeast(State.CREATED)) {
    val disposable = fix().unsafeRunAsyncCancellableEither(cb = cb)

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
