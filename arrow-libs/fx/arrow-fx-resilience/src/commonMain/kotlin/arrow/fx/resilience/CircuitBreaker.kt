package arrow.fx.resilience

import arrow.core.Either
import arrow.core.continuations.AtomicRef
import arrow.core.identity
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.bracketCase
import arrow.fx.resilience.CircuitBreaker.State.Closed
import arrow.fx.resilience.CircuitBreaker.State.HalfOpen
import arrow.fx.resilience.CircuitBreaker.State.Open
import kotlin.jvm.JvmOverloads
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * A [CircuitBreaker] is used to `protect` resources or services from being overloaded
 * When a service is being overloaded, interacting with it more will only worsen its overloaded state.
 * Especially when combined with retry mechanisms such as [Schedule],
 * in some cases simply using a back-off retry policy might not be sufficient during peak traffics.
 *
 * To allow such overloaded resources from overloading, [CircuitBreaker] can help you `protect` the service by failing-fast.
 * Thus [CircuitBreaker] helps us to achieve stability and prevent cascading failures in distributed systems.
 *
 * [CircuitBreaker] has three [CircuitBreaker.State]:
 *  1. [Closed]: This is its normal state, where requests are being made. The state in which [CircuitBreaker] starts.
 *    - When an exception occurs it increments the failure counter
 *    - A successful request will reset the failure counter to zero
 *    - When the failure counter reaches the [maxFailures] threshold, the breaker is tripped into the [Open] state
 *
 *  2. [Open]: The [CircuitBreaker] will short-circuit/fail-fast all requests
 *    - All requests short-circuit/fail-fast with `ExecutionRejected`
 *    - If a request is made after the configured [resetTimeout] passes, the [CircuitBreaker] is tripped into the a [HalfOpen] state, allowing one request to go through as a test.
 *
 *  3. [HalfOpen]: The [CircuitBreaker] is in this state while it's allowing a request to go through, as a `test request`
 *    - All other requests made while `test request` is still running will short-circuit/fail-fast.
 *    - If the `test request` succeeds then the [CircuitBreaker] is tripped back into [Closed], with the resetTimeout and the failures count also reset to initial values.
 *    - If the `test request` fails, then the [CircuitBreaker] is tripped back into [Open], the [resetTimeout] is multiplied by the [exponentialBackoffFactor], up to the configured [maxResetTimeout].
 *
 * Let's say we'd want to create a [CircuitBreaker] that only allows us to call a remote service twice,
 * and then whenever more than two requests fail with an exception, the circuit breaker starts short-circuiting failing-fast.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.fx.resilience.CircuitBreaker
 * import kotlin.time.Duration.Companion.seconds
 * import kotlin.time.ExperimentalTime
 * import kotlinx.coroutines.delay
 *
 * @ExperimentalTime
 * suspend fun main(): Unit {
 * //sampleStart
 *   val circuitBreaker = CircuitBreaker.of(
 *     maxFailures = 2,
 *     resetTimeout = 2.seconds,
 *     exponentialBackoffFactor = 1.2,
 *     maxResetTimeout = 60.seconds,
 *   )
 *   circuitBreaker.protectOrThrow { "I am in Closed: ${circuitBreaker.state()}" }.also(::println)
 *
 *   println("Service getting overloaded . . .")
 *
 *   Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)
 *   Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)
 *   circuitBreaker.protectEither { }.also { println("I am Open and short-circuit with ${it}. ${circuitBreaker.state()}") }
 *
 *   println("Service recovering . . .").also { delay(2000) }
 *
 *   circuitBreaker.protectOrThrow { "I am running test-request in HalfOpen: ${circuitBreaker.state()}" }.also(::println)
 *   println("I am back to normal state closed ${circuitBreaker.state()}")
 * //sampleEnd
 * }
 * ```
 * <!--- KNIT example-circuitbreaker-01.kt -->
 *
 * A common pattern to make fault-tolerant/resilient systems is to compose a [CircuitBreaker] with a backing-off policy retry Schedule to guarantee not overloading the resource and the client interacting with it.
 * but also not the client that is interacting with the resource.
 * Below you can see how the simple `retry` function will result in `Either.Left<CircuitBreaker.RejectedExecution>`,
 * but when we combine it with another schedule, it will always call the `CircuitBreaker` on times that it could've entered the [HalfOpen] state.
 * The reason why [Schedule] is not sufficient to make your system resilient is because you also have to take into account parallel calls to your functions,
 *; In contrast, a [CircuitBreaker] can track failures of every function call or even different functions to the same resource or service.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.fx.resilience.CircuitBreaker
 * import arrow.fx.resilience.Schedule
 * import arrow.fx.resilience.retry
 * import kotlin.time.Duration.Companion.seconds
 * import kotlin.time.ExperimentalTime
 * import kotlinx.coroutines.delay
 *
 * @ExperimentalTime
 * suspend fun main(): Unit {
 *   suspend fun apiCall(): Unit {
 *     println("apiCall . . .")
 *     throw RuntimeException("Overloaded service")
 *   }
 *
 *   //sampleStart
 *   val circuitBreaker = CircuitBreaker.of(
 *     maxFailures = 2,
 *     resetTimeout = 2.seconds,
 *     exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
 *     maxResetTimeout = 60.seconds, // limit exponential back-off time
 *   )
 *
 *   suspend fun <A> resilient(schedule: Schedule<Throwable, *>, f: suspend () -> A): A =
 *     schedule.retry { circuitBreaker.protectOrThrow(f) }
 *
 *   Either.catch {
 *     resilient(Schedule.recurs(5), ::apiCall)
 *   }.let { println("recurs(5) apiCall twice and 4x short-circuit result from CircuitBreaker: $it") }
 *
 *   delay(2000)
 *   println("CircuitBreaker ready to half-open")
 *
 *   // Retry once and when the CircuitBreaker opens after 2 failures then retry with exponential back-off with same time as CircuitBreaker's resetTimeout
 *   val fiveTimesWithBackOff = Schedule.recurs<Throwable>(1) andThen
 *     Schedule.exponential(2.seconds) and Schedule.recurs(5)
 *
 *   Either.catch {
 *     resilient(fiveTimesWithBackOff, ::apiCall)
 *   }.let { println("exponential(2.seconds) and recurs(5) always retries with actual apiCall: $it") }
 *   //sampleEnd
 * }
 * ```
 * <!--- KNIT example-circuitbreaker-02.kt -->
 */
@OptIn(ExperimentalTime::class)
public class CircuitBreaker
private constructor(
  private val state: AtomicRef<State>,
  private val openingStrategy: OpeningStrategy,
  private val resetTimeoutNanos: Double,
  private val exponentialBackoffFactor: Double,
  private val maxResetTimeoutNanos: Double,
  private val timeSource: TimeSource,
  private val onRejected: suspend () -> Unit,
  private val onClosed: suspend () -> Unit,
  private val onHalfOpen: suspend () -> Unit,
  private val onOpen: suspend () -> Unit
) {

  private val resetTimeout: Duration = resetTimeoutNanos.nanoseconds
  private val maxResetTimeout: Duration = maxResetTimeoutNanos.nanoseconds

  /** Returns the current [State], meant for debugging purposes.*/
  public suspend fun state(): State = state.get()

  /**
   * Awaits for this `CircuitBreaker` to be [Closed].
   *
   * If this `CircuitBreaker` is already in a closed state, then it returns immediately,
   * otherwise it will wait (asynchronously) until the `CircuitBreaker` switches to the [Closed] state again.
   */
  public suspend fun awaitClose(): Unit =
    when (val curr = state.get()) {
      is Closed -> Unit
      is Open -> curr.awaitClose.await()
      is HalfOpen -> curr.awaitClose.await()
    }

  /**
   * Returns a new task that upon execution will execute the given task, but with the protection of this circuit breaker.
   * If an exception in [fa] occurs, other than an [ExecutionRejected] exception, it will be rethrown.
   */
  public suspend fun <A> protectEither(fa: suspend () -> A): Either<ExecutionRejected, A> =
    try {
      Either.Right(protectOrThrow(fa))
    } catch (e: ExecutionRejected) {
      Either.Left(e)
    }

  /**
   * Returns a new task that upon execution will execute the given task, but with the protection of this circuit breaker.
   * If an exception in [fa] occurs it will be rethrown
   */
  public tailrec suspend fun <A> protectOrThrow(fa: suspend () -> A): A =
    when (val curr = state.get()) {
      is Closed -> markOrResetFailures(Either.catch { fa() })
      is Open -> {
        val elapsedNow = curr.startedAt.elapsedNow()
        if (elapsedNow >= curr.resetTimeout) {
          // The Open state has expired, so we are transition to HalfOpen and attempt to close the CircuitBreaker
          if (!state.compareAndSet(curr, HalfOpen(curr.resetTimeout, curr.awaitClose))) protectOrThrow(fa)
          else attemptReset(fa, curr.resetTimeout, curr.awaitClose, curr.startedAt)
        } else {
          // Open isn't expired, so we reject execution
          val expiresInMillis = elapsedNow - curr.resetTimeout
          onRejected.invoke()
          throw ExecutionRejected(
            "Rejected because the CircuitBreaker is in the Open state, attempting to close in $expiresInMillis millis",
            curr
          )
        }
      }

      is HalfOpen -> {
        // CircuitBreaker is in HalfOpen state, which means we still reject all tasks, while waiting to see if our attempt to close the CircuitBreaker succeeds or fails
        onRejected.invoke()
        throw ExecutionRejected("Rejected because the CircuitBreaker is in the HalfOpen state", curr)
      }
    }

  /** Function for counting failures in the `Closed` state, triggering the `Open` state if necessary.*/
  private tailrec suspend fun <A> markOrResetFailures(result: Either<Throwable, A>): A =
    when (val curr = state.get()) {
      is Closed -> {
        when (result) {
          is Either.Right -> {
            if (curr.failures == 0) result.value
            else {
              // In case of success, must reset the failures counter!
              if (!state.compareAndSet(curr, Closed(0))) markOrResetFailures(result) else result.value
            }
          }

          is Either.Left -> {
            // In case of failure, we either increment the failures counter, or we transition in the `Open` state.
            if (curr.shouldOpen()) {
              // We've gone over the permitted failures threshold, so we need to open the circuit breaker
              val update = Open(TimeSource.Monotonic.markNow(), resetTimeout, CompletableDeferred())
              if (!state.compareAndSet(curr, update)) markOrResetFailures<A>(result)
              else {
                onOpen.invoke()
                throw result.value
              }
            } else {
              // It's fine, just increment the failures count
              if (!state.compareAndSet(curr, curr.withFailure(result))) markOrResetFailures<A>(result)
              else throw result.value
            }
          }
        }
      }

      else -> result.fold({ throw it }, ::identity)
    }

  private fun Closed.shouldOpen(): Boolean =
    when (openingStrategy) {
      is OpeningStrategy.Count -> failures + 1 > openingStrategy.maxFailures
    }

  private fun <A> Closed.withFailure(result: Either<Throwable, A>): Closed =
    when (openingStrategy) {
      is OpeningStrategy.Count -> Closed(failures + 1)
    }


  /** Internal function that is the handler for the reset attempt when the circuit breaker is in `HalfOpen`.
   * In this state we can either transition to `Closed` in case the attempt was successful, or to `Open` again, in case the attempt failed.
   *
   * @param task is the task to execute, along with the attempt handler attached
   * @param resetTimeout is the last timeout applied to the previous [Open] state,
   * to be multiplied by the backoff factor in case the attempt fails, and it needs to transition to [Open] again
   */
  private suspend fun <A> attemptReset(
    task: suspend () -> A,
    resetTimeout: Duration,
    awaitClose: CompletableDeferred<Unit>,
    lastStartedAt: TimeMark
  ): A =
    bracketCase(
      acquire = onHalfOpen,
      use = { task.invoke() },
      release = { _, exit ->
        when (exit) {
          is ExitCase.Cancelled -> {
            // We need to return to Open state, otherwise we get stuck in Half-Open (see https://github.com/monix/monix/issues/1080 )
            state.set(Open(lastStartedAt, resetTimeout, awaitClose))
            onOpen.invoke()
          }

          ExitCase.Completed -> {
            // While in HalfOpen only a reset attempt is allowed to update the state, so setting this directly is safe
            state.set(Closed(0))
            awaitClose.complete(Unit)
            onClosed.invoke()
          }

          is ExitCase.Failure -> {
            // Failed reset, which means we go back in the Open state with new expiry val nextTimeout
            val value = (resetTimeout * exponentialBackoffFactor)
            val nextTimeout = if (maxResetTimeout.isFinite() && value > maxResetTimeout) maxResetTimeout else value
            state.set(Open(TimeSource.Monotonic.markNow(), nextTimeout, awaitClose))
            onOpen.invoke()
          }
        }
      }
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that upon a task being rejected will execute the given [callback].
   *
   * This is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit breaker that will call multiple callbacks,
   * thus the callback given is cumulative with other specified callbacks.
   *
   * @param callback will be executed when tasks get rejected.
   * @return a new circuit breaker wrapping the state of the source.
   */
  public fun doOnRejectedTask(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      openingStrategy = openingStrategy,
      resetTimeoutNanos = resetTimeout.toDouble(DurationUnit.NANOSECONDS),
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeoutNanos = maxResetTimeout.toDouble(DurationUnit.NANOSECONDS),
      onRejected = suspend { onRejected.invoke(); callback.invoke() },
      timeSource = timeSource,
      onClosed = onClosed,
      onHalfOpen = onHalfOpen,
      onOpen = onOpen
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that will fire the given callback upon the circuit breaker
   * transitioning to the [CircuitBreaker.State.Closed] state.
   *
   * It is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit
   * breaker that will call multiple callbacks, thus the callback
   * given is cumulative with other specified callbacks.
   *
   * @param callback will be executed when the state evolves into [CircuitBreaker.State.Closed].
   * @return a new circuit breaker wrapping the state of the source.
   */
  public fun doOnClosed(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      openingStrategy = openingStrategy,
      resetTimeoutNanos = resetTimeout.toDouble(DurationUnit.NANOSECONDS),
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeoutNanos = maxResetTimeout.toDouble(DurationUnit.NANOSECONDS),
      onRejected = onRejected,
      timeSource = timeSource,
      onClosed = suspend { onClosed.invoke(); callback.invoke(); },
      onHalfOpen = onHalfOpen,
      onOpen = onOpen
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that will fire the given callback upon the circuit breaker
   * transitioning to the [CircuitBreaker.State.HalfOpen] state.
   *
   * It is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit
   * breaker that will call multiple callbacks, thus the callback
   * given is cumulative with other specified callbacks.
   *
   * @param callback is to be executed when the state evolves into [CircuitBreaker.State.HalfOpen]
   * @return a new circuit breaker wrapping the state of the source
   */
  public fun doOnHalfOpen(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      openingStrategy = openingStrategy,
      resetTimeoutNanos = resetTimeout.toDouble(DurationUnit.NANOSECONDS),
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeoutNanos = maxResetTimeout.toDouble(DurationUnit.NANOSECONDS),
      timeSource = timeSource,
      onRejected = onRejected,
      onClosed = onClosed,
      onHalfOpen = { onHalfOpen.invoke(); callback.invoke() },
      onOpen = onOpen
    )

  /** Returns a new circuit breaker that wraps the state of the source
   * and that will fire the given callback upon the circuit breaker
   * transitioning to the [CircuitBreaker.State.Open] state.
   *
   * It is useful for gathering stats.
   *
   * NOTE: calling this method multiple times will create a circuit
   * breaker that will call multiple callbacks, thus the callback
   * given is cumulative with other specified callbacks.
   *
   * @param callback will be executed when the state evolves into [CircuitBreaker.State.Open]
   * @return a new circuit breaker wrapping the state of the source
   */
  public fun doOnOpen(callback: suspend () -> Unit): CircuitBreaker =
    CircuitBreaker(
      state = state,
      openingStrategy = openingStrategy,
      resetTimeoutNanos = resetTimeout.toDouble(DurationUnit.NANOSECONDS),
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeoutNanos = maxResetTimeout.toDouble(DurationUnit.NANOSECONDS),
      timeSource = timeSource,
      onRejected = onRejected,
      onClosed = onClosed,
      onHalfOpen = onHalfOpen,
      onOpen = { onOpen.invoke(); callback.invoke() }
    )

  /**
   * The initial state when initializing a [CircuitBreaker] is [Closed].
   *
   * The available states are:
   *  - [Closed] in case tasks are allowed to go through
   *  - [Open] in case the circuit breaker is active and rejects incoming tasks
   *  - [HalfOpen] in case a reset attempt was triggered, and it is waiting for the result in order to evolve in [Closed], or back to [Open]
   */
  public sealed class State {

    /**
     * [Closed] is the normal state of the [CircuitBreaker], where requests are being made. The state in which [CircuitBreaker] starts.
     *    - When an exception occurs it increments the failure counter
     *    - A successful request will reset the failure counter to zero
     *    - When the failure counter reaches the [maxFailures] threshold, the breaker is tripped into the [Open] state
     *
     * @param failures is the current failures count
     */
    public data class Closed @JvmOverloads constructor(
      public val failures: Int,
      public val lastFailure: TimeMark? = null
    ) : State()

    /**
     *  When the [CircuitBreaker] is in the [Open] state it will short-circuit/fail-fast all requests
     *    - All requests short-circuit/fail-fast with [ExecutionRejected]
     *    - If a request is made after the configured [resetTimeout] passes, the [CircuitBreaker] is tripped into the a [HalfOpen] state, allowing one request to go through as a test.
     *
     * @param startedAt is the [TimeMark] when the transition to [Open] happened.
     *
     * @param resetTimeout is the current `resetTimeout` that is applied to this [Open] state, to be multiplied by the
     *        exponential backoff factor for the next transition from [HalfOpen] to [Open].
     */
    public class Open internal constructor(
      public val startedAt: TimeMark,
      public val resetTimeout: Duration,
      internal val awaitClose: CompletableDeferred<Unit>,
    ) : State() {

      @Deprecated(
        "Prefer to use resetTimeout with kotlin.time.Duration",
        ReplaceWith(
          "resetTimeout.toDouble(DurationUnit.NANOSECONDS)",
          "kotlin.time.DurationUnit"
        )
      )
      public val resetTimeoutNanos: Double
        get() = resetTimeout.toDouble(DurationUnit.NANOSECONDS)

      @Deprecated(
        "This constructor will be removed in Arrow 2.0",
        level = DeprecationLevel.WARNING
      )
      public constructor(startedAt: Long, resetTimeout: Duration) : this(
        TimeSource.Monotonic.markNow(),
        resetTimeout,
        CompletableDeferred()
      )

      @Deprecated(
        "This constructor will be removed in Arrow 2.0",
        level = DeprecationLevel.WARNING
      )
      internal constructor(
        startedAt: Long,
        resetTimeoutNanos: Double,
        awaitClose: CompletableDeferred<Unit>,
      ) : this(TimeSource.Monotonic.markNow(), resetTimeoutNanos.nanoseconds, awaitClose)

      @Deprecated(
        "This constructor will be removed in Arrow 2.0",
        level = DeprecationLevel.WARNING
      )
      public constructor(startedAt: Long, resetTimeoutNanos: Double) : this(
        TimeSource.Monotonic.markNow(),
        resetTimeoutNanos.nanoseconds,
        CompletableDeferred()
      )

      /** The timestamp in milliseconds since the epoch, specifying when the [Open] state is to transition to [HalfOpen].
       *
       * It is calculated as:
       * `startedAt + resetTimeout`
       */
      public val expiresAt: Long = timeInMillis() + (resetTimeout - startedAt.elapsedNow()).inWholeMilliseconds

      override fun equals(other: Any?): Boolean =
        if (other is Open) this.startedAt == startedAt && this.resetTimeout == resetTimeout
        else false

      override fun toString(): String =
        "CircuitBreaker.State.Open(startedAt=$startedAt, resetTimeoutNanos=$resetTimeout, expiresAt=$expiresAt)"

      override fun hashCode(): Int {
        var result = startedAt.hashCode()
        result = 31 * result + resetTimeout.hashCode()
        result = 31 * result + expiresAt.hashCode()
        return result
      }
    }

    /**
     * The [CircuitBreaker] is in [HalfOpen] state while it's allowing a test request to go through.
     *   - All other requests made while the test request is still running will short-circuit/fail-fast.
     *   - If the `test request` succeeds, then the [CircuitBreaker] is tripped back into [Closed], with the reset timeout, and the failures count also reset to their initial values.
     *   - If the `test request` fails, then the [CircuitBreaker] is tripped back into [Open], the [resetTimeout] is multiplied by the [exponentialBackoffFactor], up to the configured [maxResetTimeout].
     *
     * @param resetTimeout is the current `reset timeout` that the [CircuitBreaker] has to stay in [Open] state.
     * When the `reset timeout` lapsed, than the [CircuitBreaker] will allow a test request to go through in [HalfOpen].
     * If the test request failed, the [CircuitBreaker] will go back into [Open] and it'll multiply the [resetTimeout] with the exponential backoff factor.
     */
    public class HalfOpen internal constructor(
      public val resetTimeout: Duration,
      internal val awaitClose: CompletableDeferred<Unit>
    ) : State() {

      @Deprecated(
        "Prefer to use resetTimeout with kotlin.time.Duration",
        ReplaceWith(
          "resetTimeout.toDouble(DurationUnit.NANOSECONDS)",
          "kotlin.time.DurationUnit"
        )
      )
      public val resetTimeoutNanos: Double
        get() = resetTimeout.toDouble(DurationUnit.NANOSECONDS)

      public constructor(resetTimeout: Duration) : this(resetTimeout, CompletableDeferred())

      @Deprecated(
        "This constructor will be removed in Arrow 2.0",
        level = DeprecationLevel.WARNING
      )
      internal constructor(
        resetTimeoutNanos: Double,
        awaitClose: CompletableDeferred<Unit>,
      ) : this(resetTimeoutNanos.nanoseconds, awaitClose)

      public constructor(resetTimeoutNanos: Double) : this(resetTimeoutNanos.nanoseconds, CompletableDeferred())

      override fun hashCode(): Int =
        resetTimeout.hashCode()

      override fun equals(other: Any?): Boolean =
        if (other is HalfOpen) resetTimeout == other.resetTimeout
        else false

      override fun toString(): String =
        "HalfOpen(resetTimeoutNanos=$resetTimeout)"
    }
  }

  public class ExecutionRejected(public val reason: String, public val state: State) : Throwable()

  public companion object {
    /**
     * Attempts to create a [CircuitBreaker].
     *
     * @param maxFailures is the maximum count for failures before opening the circuit breaker.
     *
     * @param resetTimeoutNanos is the timeout to wait in the `Open` state before attempting a close of the circuit breaker
     * (but without the backoff factor applied) in nanoseconds.
     *
     * @param exponentialBackoffFactor is a factor to use for resetting the `resetTimeout` when in the `HalfOpen` state,
     * in case the attempt to `Close` fails.
     *
     * @param maxResetTimeoutNanos is the maximum timeout the circuit breaker is allowed to use when applying the `exponentialBackoffFactor`.
     *
     * @param onRejected is a callback for signaling rejected tasks,
     * so every time a task execution is attempted and rejected in [Open] or [HalfOpen] states.
     *
     * @param onClosed is a callback for signaling transitions to the [CircuitBreaker.State.Closed] state.
     *
     * @param onHalfOpen is a callback for signaling transitions to [CircuitBreaker.State.HalfOpen].
     *
     * @param onOpen is a callback for signaling transitions to [CircuitBreaker.State.Open].
     */
    @Deprecated(
      "Prefer the kotlin.time.Duration constructor instead",
      ReplaceWith(
        "CircuitBreaker(maxFailures, resetTimeoutNanos.nanoseconds, exponentialBackoffFactor, maxResetTimeout, onRejected, onClosed, onHalfOpen, onOpen)",
        "import kotlin.time.Duration.Companion.nanoseconds"
      )
    )
    public suspend fun of(
      maxFailures: Int,
      resetTimeoutNanos: Double,
      exponentialBackoffFactor: Double = 1.0,
      maxResetTimeoutNanos: Double = Double.POSITIVE_INFINITY,
      onRejected: suspend () -> Unit = { },
      onClosed: suspend () -> Unit = { },
      onHalfOpen: suspend () -> Unit = { },
      onOpen: suspend () -> Unit = { },
    ): CircuitBreaker =
      CircuitBreaker(
        state = AtomicRef(Closed(0)),
        openingStrategy = OpeningStrategy.Count(
          maxFailures
            .takeIf { it >= 0 }
            .let { requireNotNull(it) { "maxFailures expected to be greater than or equal to 0, but was $maxFailures" } }
        ),
        resetTimeoutNanos = resetTimeoutNanos
          .takeIf { it > 0 }
          .let { requireNotNull(it) { "resetTimeout expected to be greater than 0, but was $resetTimeoutNanos" } },
        exponentialBackoffFactor = exponentialBackoffFactor
          .takeIf { it > 0 }
          .let { requireNotNull(it) { "exponentialBackoffFactor expected to be greater than 0, but was $exponentialBackoffFactor" } },
        maxResetTimeoutNanos = maxResetTimeoutNanos
          .takeIf { it > 0 }
          .let { requireNotNull(it) { "maxResetTimeout expected to be greater than 0, but was $maxResetTimeoutNanos" } },
        timeSource = TimeSource.Monotonic,
        onRejected = onRejected,
        onClosed = onClosed,
        onHalfOpen = onHalfOpen,
        onOpen = onOpen
      )

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
    @Deprecated(
      "Use the non-suspend constructor instead.",
      ReplaceWith("CircuitBreaker(maxFailures, resetTimeout, exponentialBackoffFactor, maxResetTimeout, onRejected, onClosed, onHalfOpen, onOpen)")
    )
    public suspend fun of(
      maxFailures: Int,
      resetTimeout: Duration,
      exponentialBackoffFactor: Double = 1.0,
      maxResetTimeout: Duration = Duration.INFINITE,
      onRejected: suspend () -> Unit = suspend { },
      onClosed: suspend () -> Unit = suspend { },
      onHalfOpen: suspend () -> Unit = suspend { },
      onOpen: suspend () -> Unit = suspend { },
    ): CircuitBreaker =
      invoke(
        maxFailures,
        resetTimeout,
        exponentialBackoffFactor,
        maxResetTimeout,
        TimeSource.Monotonic,
        onRejected,
        onClosed,
        onHalfOpen,
        onOpen
      )

    public operator fun invoke(
      maxFailures: Int,
      resetTimeout: Duration,
      exponentialBackoffFactor: Double = 1.0,
      maxResetTimeout: Duration = Duration.INFINITE,
      timeSource: TimeSource = TimeSource.Monotonic,
      onRejected: suspend () -> Unit = suspend { },
      onClosed: suspend () -> Unit = suspend { },
      onHalfOpen: suspend () -> Unit = suspend { },
      onOpen: suspend () -> Unit = suspend { },
    ): CircuitBreaker =
      CircuitBreaker(
        state = AtomicRef(Closed(0)),
        openingStrategy = OpeningStrategy.Count(
          maxFailures
            .takeIf { it >= 0 }
            .let { requireNotNull(it) { "maxFailures expected to be greater than or equal to 0, but was $maxFailures" } }
        ),
        resetTimeoutNanos = resetTimeout
          .takeIf { it.isPositive() && it != Duration.ZERO }
          .let { requireNotNull(it) { "resetTimeout expected to be greater than ${Duration.ZERO}, but was $resetTimeout" } }
          .toDouble(DurationUnit.NANOSECONDS),
        exponentialBackoffFactor = exponentialBackoffFactor
          .takeIf { it > 0 }
          .let { requireNotNull(it) { "exponentialBackoffFactor expected to be greater than 0, but was $exponentialBackoffFactor" } },
        maxResetTimeoutNanos = maxResetTimeout
          .takeIf { it.isPositive() && it != Duration.ZERO }
          .let { requireNotNull(it) { "maxResetTimeout expected to be greater than ${Duration.ZERO}, but was $maxResetTimeout" } }
          .toDouble(DurationUnit.NANOSECONDS),
        timeSource = timeSource,
        onRejected = onRejected,
        onClosed = onClosed,
        onHalfOpen = onHalfOpen,
        onOpen = onOpen
      )
  }

  public sealed interface OpeningStrategy {
    public data class Count(val maxFailures: Int) : OpeningStrategy
  }
}
