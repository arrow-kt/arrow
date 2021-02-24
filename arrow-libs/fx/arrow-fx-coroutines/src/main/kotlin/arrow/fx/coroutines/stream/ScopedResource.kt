package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.SuspendConnection
import arrow.fx.coroutines.stream.concurrent.modify
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import java.util.concurrent.CancellationException
import kotlin.coroutines.coroutineContext

/**
 * Represents a resource acquired during stream interpretation.
 *
 * A resource is acquired by `Pull.Acquire` and then released by `Pull.CloseScope`.
 *
 * The acquisition of a resource has three steps:
 *
 * 1. A `Resource` instance is created and registered with the current scope (`Pull.Acquire`)
 * 2. Resource acquisition action is evaluated
 * 3. `acquired` is invoked to confirm acquisition of the resource
 *
 * The reason for this is that during asynchronous stream evaluation, one scope may close the other scope
 * (e.g., merged stream fails while another stream is still acquiring an asynchronous resource).
 * In such a case, a resource may be `released` before `acquired` was evaluated, resulting
 * in an immediate finalization after acquisition is confirmed.
 *
 * A resource may be released by any of the following methods:
 *
 * (1) The owning scope was closed by `Pull.CloseScope`. This essentially evaluates `release` of
 *     the `Resource` and acts like (1).
 * (2) `acquired` was evaluated after scope was `released` by either (1) or (2). In this case,
 *     finalizer will be invoked immediately if the resource is not leased.
 * (3) `cancel` is invoked on a `Lease` for the resource. This will invoke the finalizer
 *     if the resource was already acquired and released and there are no other outstanding leases.
 *
 * Resources may be leased to other scopes. Each scope must lease with `lease` and  when the other
 * scope is closed (or when the resource lease is no longer required) release the lease with `Lease#cancel`.
 *
 * Note that every method which may potentially call a resource finalizer returns `suspend () -> Either<Throwable, Unit>`
 * instead of `suspend () -> Unit` to make sure any errors that occur when releasing the resource are properly handled.
 */
internal class ScopedResource {

  private val state = atomic(State.initial)

  private val id: Token = Token()

  suspend fun release(ec: ExitCase): Either<Throwable, Unit> {
    val finalizer = state.modify { s ->
      if (s.leases != 0) {
        // do not allow to run finalizer if there are leases open
        Pair(s.copy(open = false), null)
      } else {
        // reset finalizer to None, will be run, it available, otherwise the acquire will take care of it
        Pair(s.copy(open = false, finalizer = null), s.finalizer)
      }
    }

    return finalizer?.invoke(ec) ?: Either.Right(Unit)
  }

  suspend fun acquired(finalizer: suspend (ExitCase) -> Unit): Either<Throwable, Boolean> {
    val conn = coroutineContext[SuspendConnection] ?: SuspendConnection.uncancellable
    return state.modify { s ->
      when {
        conn.isCancelled() -> {
          // state is closed and there are no leases, finalizer has to be invoked right away
          Pair(
            s,
            suspend {
              Either.catch {
                finalizer(ExitCase.Cancelled(CancellationException()))
                false
              }
            }
          )
        }
        s.isFinished() -> {
          // state is closed and there are no leases, finalizer has to be invoked right away
          Pair(
            s,
            suspend {
              Either.catch {
                finalizer(ExitCase.Completed)
                false
              }
            }
          )
        }
        else -> {
          val attemptFinalizer: suspend (ExitCase) -> Either<Throwable, Unit> =
            { ec -> Either.catch { finalizer(ec) } }
          // either state is open, or leases are present, either release or `Lease#cancel` will run the finalizer
          Pair(s.copy(finalizer = attemptFinalizer), suspend { Either.Right(true) })
        }
      }
    }.invoke()
  }

  suspend fun lease(): Scope.Lease? =
    state.modify { s ->
      if (s.open) Pair(s.copy(leases = s.leases + 1), TheLease())
      else Pair(s, null)
    }

  fun TheLease(): Scope.Lease =
    object : Scope.Lease() {
      override suspend fun cancel(): Either<Throwable, Unit> {
        val now = state.updateAndGet { s ->
          s.copy(leases = s.leases - 1)
        }

        return if (now.isFinished()) {
          state.modify { s ->
            // Scope is closed and this is last lease, assure finalizer is removed from the state and run
            // previous finalizer shall be always present at this point, this shall invoke it
            Pair(
              s.copy(finalizer = null),
              when (val ff = s.finalizer) {
                null -> suspend { Either.Right(Unit) }
                else -> suspend { ff(ExitCase.Completed) }
              }
            )
          }.invoke()
        } else Either.Right(Unit)
      }
    }

  /**
   * State of the resource
   *
   * @param open Resource is open. At this state resource is either awating its acquisition
   *                   by invoking the `acquired` or is used by Stream.
   * @param finalizer When resource is successfully acquired, this will contain finalizer that shall be
   *                   invoked when the resource is released.
   * @param leases References (leases) of this resource
   */
  private data class State(
    val open: Boolean,
    val finalizer: (suspend (ExitCase) -> Either<Throwable, Unit>)?,
    val leases: Int
  ) {
    /* The `isFinished` predicate indicates that the finalizer can be run at the present state:
    which happens IF it is closed, AND there are no acquired leases pending to be released. */
    fun isFinished(): Boolean = !open && leases == 0

    companion object {
      val initial = State(open = true, finalizer = null, leases = 0)
    }
  }
}
