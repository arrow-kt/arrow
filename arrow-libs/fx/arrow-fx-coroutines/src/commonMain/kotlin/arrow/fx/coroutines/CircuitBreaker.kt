package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import arrow.fx.coroutines.CircuitBreaker.State.Closed
import arrow.fx.coroutines.CircuitBreaker.State.HalfOpen
import arrow.fx.coroutines.CircuitBreaker.State.Open
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

public class CircuitBreaker
private constructor(
  private val state: AtomicRefW<State>,
  private val maxFailures: Int,
  private val resetTimeout: Double,
  private val exponentialBackoffFactor: Double,
  private val maxResetTimeout: Double,
  private val onRejected: suspend () -> Unit,
  private val onClosed: suspend () -> Unit,
  private val onHalfOpen: suspend () -> Unit,
  private val onOpen: suspend () -> Unit
) {

  /** Returns the current [CircuitBreaker.State], meant for debugging purposes.
   */
  public suspend fun state(): State = state.value

  /**
   * Awaits for this `CircuitBreaker` to be [CircuitBreaker.State.Closed].
   *
   * If this `CircuitBreaker` is already in a closed state, then
   * it returns immediately, otherwise it will wait (asynchronously) until
   * the `CircuitBreaker` switches to the [CircuitBreaker.Closed]
   * state again.
   */
  public suspend fun awaitClose(): Unit =
    when (val curr = state.value) {
      is Closed -> Unit
      is Open -> curr.awaitClose.await()
      is State.HalfOpen -> curr.awaitClose.await()
    }

  /**
   * Returns a new task that upon execution will execute the given
   * task, but with the protection of this circuit breaker.
   * If an exception in [fa] occurs, other than an [ExecutionRejected] exception, it will be rethrown.
   */
  public suspend fun <A> protectEither(fa: suspend () -> A): Either<ExecutionRejected, A> =
    try {
      Either.Right(protectOrThrow(fa))
    } catch (e: ExecutionRejected) {
      Either.Left(e)
    }

  /**
   * Returns a new task that upon execution will execute the given
   * task, but with the protection of this circuit breaker.
   * If an exception in [fa] occurs it will be rethrown
   */
  public tailrec suspend fun <A> protectOrThrow(fa: suspend () -> A): A =
    when (val curr = state.value) {
      is Closed -> {
        val attempt = try {
          Either.Right(fa.invoke())
        } catch (e: Throwable) {
          Either.Left(e)
        }
        markOrResetFailures(attempt)
      }
      is Open -> {
        val now = timeInMillis()
        if (now >= curr.expiresAt) {
          // The Open state has expired, so we are letting just one
          // task to execute, while transitioning into HalfOpen
          if (!state.compareAndSet(
              curr,
              State.HalfOpen(curr.resetTimeoutNanos, curr.awaitClose)
            )
          ) protectOrThrow(fa) // retry!
          else attemptReset(fa, curr.resetTimeoutNanos, curr.awaitClose, curr.startedAt)
        } else {
          // Open isn't expired, so we need to fail
          val expiresInMillis = curr.expiresAt - now
          onRejected.invoke()
          throw ExecutionRejected(
            "Rejected because the CircuitBreaker is in the Open state, attempting to close in $expiresInMillis millis",
            curr
          )
        }
      }
      is State.HalfOpen -> {
        // CircuitBreaker is in HalfOpen state, which means we still reject all
        // tasks, while waiting to see if our reset attempt succeeds or fails
        onRejected.invoke()
        throw ExecutionRejected("Rejected because the CircuitBreaker is in the HalfOpen state", curr)
      }
    }

  /** Function for counting failures in the `Closed` state,
   * triggering the `Open` state if necessary.
   */
  private tailrec suspend fun <A> markOrResetFailures(result: Either<Throwable, A>): A =
    when (val curr = state.value) {
      is Closed -> {
        when (result) {
          is Either.Right -> {
            if (curr.failures == 0) result.value
            else { // In case of success, must reset the failures counter!
              val update = Closed(0)
              if (!state.compareAndSet(curr, update)) markOrResetFailures(result) // retry?
              else result.value
            }
          }
          is Either.Left -> {
            // In case of failure, we either increment the failures counter,
            // or we transition in the `Open` state.
            if (curr.failures + 1 < maxFailures) {
              // It's fine, just increment the failures count
              val update = Closed(curr.failures + 1)
              if (!state.compareAndSet(curr, update)) markOrResetFailures(result) // retry?
              else throw result.value
            } else {
              // N.B. this could be canceled, however we don't care
              val now = timeInMillis()
              // We've gone over the permitted failures threshold,
              // so we need to open the circuit breaker
              val update = Open(now, resetTimeout, CompletableDeferred())
              if (!state.compareAndSet(curr, update)) markOrResetFailures(result) // retry
              else {
                onOpen.invoke()
                throw result.value
              }
            }
          }
        }
      }
      else -> result.fold({ throw it }, ::identity)
    }

  /** Internal function that is the handler for the reset attempt when
   * the circuit breaker is in `HalfOpen`. In this state we can
   * either transition to `Closed` in case the attempt was
   * successful, or to `Open` again, in case the attempt failed.
   *
   * @param task is the task to execute, along with the attempt
   *        handler attached
   * @param resetTimeout is the last timeout applied to the previous
   *        `Open` state, to be multiplied by the backoff factor in
   *        case the attempt fails and it needs to transition to
   *        `Open` again
   */
  private suspend fun <A> attemptReset(
    task: suspend () -> A,
    resetTimeout: Double,
    awaitClose: CompletableDeferred<Unit>,
    lastStartedAt: Long
  ): A =
    bracketCase(
      acquire = onHalfOpen,
      use = { task.invoke() },
      release = { _, exit ->
        when (exit) {
          is ExitCase.Cancelled -> {
            // We need to return to Open state
            // otherwise we get stuck in Half-Open (see https://github.com/monix/monix/issues/1080 )
            state.value = Open(lastStartedAt, resetTimeout, awaitClose)
            onOpen.invoke()
          }
          ExitCase.Completed -> {
            // While in HalfOpen only a reset attempt is allowed to update
            // the state, so setting this directly is safe
            state.value = Closed(0)
            awaitClose.complete(Unit)
            onClosed.invoke()
          }
          is ExitCase.Failure -> {
            // Failed reset, which means we go back in the Open state with new expiry val nextTimeout
            val value: Double = (resetTimeout * exponentialBackoffFactor)
            val nextTimeout: Double =
              if (maxResetTimeout.isFinite() && value > maxResetTimeout) maxResetTimeout
              else value
            val ts = timeInMillis()
            state.value = Open(ts, nextTimeout, awaitClose)
            onOpen.invoke()
          }
        }
      }
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that upon a task being rejected will execute the given
   * `callback`.
   *
   * This is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit
   * breaker that will call multiple callbacks, thus the callback
   * given is cumulative with other specified callbacks.
   *
   * @param callback will be executed when tasks get rejected.
   * @return a new circuit breaker wrapping the state of the source.
   */
  public fun doOnRejectedTask(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      maxFailures = maxFailures,
      resetTimeout = resetTimeout,
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeout = maxResetTimeout,
      onRejected = suspend { onRejected.invoke(); callback.invoke() },
      onClosed = onClosed,
      onHalfOpen = onHalfOpen,
      onOpen = onOpen
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that will fire the given callback upon the circuit breaker
   * transitioning to the [CircuitBreaker.Closed] state.
   *
   * It is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit
   * breaker that will call multiple callbacks, thus the callback
   * given is cumulative with other specified callbacks.
   *
   * @param callback will be executed when the state evolves into [CircuitBreaker.Closed].
   * @return a new circuit breaker wrapping the state of the source.
   */
  public fun doOnClosed(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      maxFailures = maxFailures,
      resetTimeout = resetTimeout,
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeout = maxResetTimeout,
      onRejected = onRejected,
      onClosed = suspend { onClosed.invoke(); callback.invoke(); },
      onHalfOpen = onHalfOpen,
      onOpen = onOpen
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that will fire the given callback upon the circuit breaker
   * transitioning to the [CircuitBreaker.HalfOpen] state.
   *
   * It is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit
   * breaker that will call multiple callbacks, thus the callback
   * given is cumulative with other specified callbacks.
   *
   * @param callback is to be executed when the state evolves into [CircuitBreaker.HalfOpen]
   * @return a new circuit breaker wrapping the state of the source
   */
  public fun doOnHalfOpen(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      maxFailures = maxFailures,
      resetTimeout = resetTimeout,
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeout = maxResetTimeout,
      onRejected = onRejected,
      onClosed = onClosed,
      onHalfOpen = suspend { onHalfOpen.invoke(); callback.invoke() },
      onOpen = onOpen
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that will fire the given callback upon the circuit breaker
   * transitioning to the [CircuitBreaker.Open] state.
   *
   * It is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit
   * breaker that will call multiple callbacks, thus the callback
   * given is cumulative with other specified callbacks.
   *
   * @param callback will be executed when the state evolves into [CircuitBreaker.Open]
   * @return a new circuit breaker wrapping the state of the source
   */
  public fun doOnOpen(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      maxFailures = maxFailures,
      resetTimeout = resetTimeout,
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeout = maxResetTimeout,
      onRejected = onRejected,
      onClosed = onClosed,
      onHalfOpen = onHalfOpen,
      onOpen = suspend { onOpen.invoke(); callback.invoke() }
    )

  /**
   * The initial state when initializing a [CircuitBreaker] is [Closed].
   *
   * The available states are:
   *  - [Closed] in case tasks are allowed to go through
   *  - [Open] in case the circuit breaker is active and rejects incoming tasks
   *  - [HalfOpen] in case a reset attempt was triggered and it is waiting for
   *    the result in order to evolve in [Closed], or back to [Open]
   */
  public sealed class State {

    /** The initial [State] of the [CircuitBreaker]. While in this
     * state, the circuit breaker allows tasks to be executed.
     *
     * Contract:
     *  - Exceptions increment the `failures` counter.
     *  - Successes reset the failure count to zero.
     *  - When the `failures` counter reaches the `maxFailures` count,
     *    the breaker is tripped into the [CircuitBreaker.Open] state.
     *
     * @param failures is the current failures count
     */
    public class Closed(public val failures: Int) : State() {
      override fun hashCode(): Int =
        failures.hashCode()

      override fun equals(other: Any?): Boolean =
        if (other is Closed) failures == other.failures
        else false

      override fun toString(): String =
        "Closed(failures=$failures)"
    }

    public class Open internal constructor(
      public val startedAt: Long,
      public val resetTimeoutNanos: Double,
      internal val awaitClose: CompletableDeferred<Unit>
    ) : State() {

      /** [State] of the [CircuitBreaker] in which the circuit
       * breaker rejects all tasks with an [ExecutionRejected].
       *
       * Contract:
       *  - All tasks fail fast with [ExecutionRejected].
       *  - After the configured `resetTimeout`, the circuit breaker
       *    enters a [HalfOpen] state, allowing one task to go through
       *    for testing the connection.
       *
       * @param startedAt is the timestamp in milliseconds since the
       *        epoch when the transition to `Open` happened.
       *
       * @param resetTimeoutNanos is the current `resetTimeout` that is
       *        applied to this `Open` state, to be multiplied by the
       *        exponential backoff factor for the next transition from
       *        `HalfOpen` to `Open`, in case the reset attempt fails
       */
      public constructor(startedAt: Long, resetTimeoutNanos: Double) : this(
        startedAt,
        resetTimeoutNanos,
        CompletableDeferred()
      )

      /** The timestamp in milliseconds since the epoch, specifying
       * when the `Open` state is to transition to [HalfOpen].
       *
       * It is calculated as:
       * `startedAt + resetTimeout`
       */
      public val expiresAt: Long = startedAt + (resetTimeoutNanos.toLong() / 1_000_000)

      override fun equals(other: Any?): Boolean =
        if (other is Open) this.startedAt == startedAt &&
          this.resetTimeoutNanos == resetTimeoutNanos &&
          this.expiresAt == expiresAt
        else false

      override fun toString(): String =
        "CircuitBreaker.State.Open(startedAt=$startedAt, resetTimeoutNanos=$resetTimeoutNanos, expiresAt=$expiresAt)"

      override fun hashCode(): Int {
        var result = startedAt.hashCode()
        result = 31 * result + resetTimeoutNanos.hashCode()
        result = 31 * result + expiresAt.hashCode()
        return result
      }
    }

    public class HalfOpen internal constructor(
      public val resetTimeoutNanos: Double,
      internal val awaitClose: CompletableDeferred<Unit>
    ) : State() {

      /** [State] of the [CircuitBreaker] in which the circuit
       * breaker has already allowed a task to go through, as a reset
       * attempt, in order to test the connection.
       *
       * Contract:
       *  - The first task when `Open` has expired is allowed through
       *    without failing fast, just before the circuit breaker is
       *    evolved into the `HalfOpen` state.
       *  - All tasks attempted in `HalfOpen` fail-fast with an exception
       *    just as in [Open] state.
       *  - If that task attempt succeeds, the breaker is reset back to
       *    the `Closed` state, with the `resetTimeout` and the
       *    `failures` count also reset to initial values.
       *  - If the first call fails, the breaker is tripped again into
       *    the `Open` state (the `resetTimeout` is multiplied by the
       *    exponential backoff factor).
       *
       * @param resetTimeoutNanos is the current `resetTimeout` that was
       *        applied to the previous `Open` state, to be multiplied by
       *        the exponential backoff factor for the next transition to
       *        `Open`, in case the reset attempt fails.
       */
      public constructor(resetTimeoutNanos: Double) : this(resetTimeoutNanos, CompletableDeferred())

      override fun hashCode(): Int =
        resetTimeoutNanos.hashCode()

      override fun equals(other: Any?): Boolean =
        if (other is HalfOpen) resetTimeoutNanos == other.resetTimeoutNanos
        else false

      override fun toString(): String =
        "HalfOpen(resetTimeoutNanos=$resetTimeoutNanos)"
    }
  }

  public class ExecutionRejected(public val reason: String, public val state: State) : Throwable()

  public companion object {
    /**
     * Attempts to create a [CircuitBreaker].
     *
     * @param maxFailures is the maximum count for failures before
     *        opening the circuit breaker.
     *
     * @param resetTimeoutNanos is the timeout to wait in the `Open` state
     *        before attempting a close of the circuit breaker (but without
     *        the backoff factor applied) in nanoseconds.
     *
     * @param exponentialBackoffFactor is a factor to use for resetting
     *        the `resetTimeout` when in the `HalfOpen` state, in case
     *        the attempt to `Close` fails.
     *
     * @param maxResetTimeout is the maximum timeout the circuit breaker
     *        is allowed to use when applying the `exponentialBackoffFactor`.
     *
     * @param onRejected is a callback for signaling rejected tasks, so
     *         every time a task execution is attempted and rejected in
     *         [CircuitBreaker.Open] or [CircuitBreaker.HalfOpen]
     *         states.
     *
     * @param onClosed is a callback for signaling transitions to the [CircuitBreaker.State.Closed] state.
     *
     * @param onHalfOpen is a callback for signaling transitions to [CircuitBreaker.State.HalfOpen].
     *
     * @param onOpen is a callback for signaling transitions to [CircuitBreaker.State.Open].
     *
     */
    public suspend fun of(
      maxFailures: Int,
      resetTimeoutNanos: Double,
      exponentialBackoffFactor: Double = 1.0,
      maxResetTimeout: Double = Double.POSITIVE_INFINITY,
      onRejected: suspend () -> Unit = suspend { Unit },
      onClosed: suspend () -> Unit = suspend { Unit },
      onHalfOpen: suspend () -> Unit = suspend { Unit },
      onOpen: suspend () -> Unit = suspend { Unit }
    ): CircuitBreaker? =
      if (maxFailures >= 0 && resetTimeoutNanos > 0 && exponentialBackoffFactor > 0 && maxResetTimeout > 0) {
        CircuitBreaker(
          state = AtomicRefW(Closed(0)),
          maxFailures = maxFailures,
          resetTimeout = resetTimeoutNanos,
          exponentialBackoffFactor = exponentialBackoffFactor,
          maxResetTimeout = maxResetTimeout,
          onRejected = onRejected,
          onClosed = onClosed,
          onHalfOpen = onHalfOpen,
          onOpen = onOpen
        )
      } else null

    /**
     * Attempts to create a [CircuitBreaker].
     *
     * @param maxFailures is the maximum count for failures before
     *        opening the circuit breaker.
     *
     * @param resetTimeout is the timeout to wait in the `Open` state
     *        before attempting a close of the circuit breaker (but without
     *        the backoff factor applied).
     *
     * @param exponentialBackoffFactor is a factor to use for resetting
     *        the `resetTimeout` when in the `HalfOpen` state, in case
     *        the attempt to `Close` fails.
     *
     * @param maxResetTimeout is the maximum timeout the circuit breaker
     *        is allowed to use when applying the `exponentialBackoffFactor`.
     *
     * @param onRejected is a callback for signaling rejected tasks, so
     *         every time a task execution is attempted and rejected in
     *         [CircuitBreaker.Open] or [CircuitBreaker.HalfOpen]
     *         states.
     *
     * @param onClosed is a callback for signaling transitions to the [CircuitBreaker.State.Closed] state.
     *
     * @param onHalfOpen is a callback for signaling transitions to [CircuitBreaker.State.HalfOpen].
     *
     * @param onOpen is a callback for signaling transitions to [CircuitBreaker.State.Open].
     *
     */
    @ExperimentalTime
    public suspend fun of(
      maxFailures: Int,
      resetTimeout: Duration,
      exponentialBackoffFactor: Double = 1.0,
      maxResetTimeout: Duration = Duration.INFINITE,
      onRejected: suspend () -> Unit = suspend { Unit },
      onClosed: suspend () -> Unit = suspend { Unit },
      onHalfOpen: suspend () -> Unit = suspend { Unit },
      onOpen: suspend () -> Unit = suspend { Unit }
    ): CircuitBreaker? =
      of(
        maxFailures,
        resetTimeout.inNanoseconds,
        exponentialBackoffFactor,
        maxResetTimeout.inNanoseconds,
        onRejected,
        onClosed,
        onHalfOpen,
        onOpen
      )
  }
}
