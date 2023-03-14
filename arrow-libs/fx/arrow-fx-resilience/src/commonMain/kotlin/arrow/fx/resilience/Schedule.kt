package arrow.fx.resilience

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.identity
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.core.some
import arrow.fx.resilience.Schedule.Decision.Continue
import arrow.fx.resilience.Schedule.Decision.Done
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.jvm.JvmInline
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.nanoseconds
import kotlinx.coroutines.currentCoroutineContext

public typealias Next<Input, Output> =
  suspend (Input) -> Schedule.Decision<Input, Output>

@JvmInline
public value class Schedule<Input, Output>(
  public val step: Next<Input, Output>
) {

  public suspend fun repeat(block: suspend () -> Input): Output =
    repeatOrElse(block) { e, _ -> throw e }

  public suspend fun repeatOrElse(
    block: suspend () -> Input,
    orElse: suspend (error: Throwable, output: Output?) -> Output
  ): Output =
    repeatOrElseEither(block, orElse).fold(::identity, ::identity)

  public suspend fun <A> repeatOrElseEither(
    block: suspend () -> Input,
    orElse: suspend (error: Throwable, output: Output?) -> A
  ): Either<A, Output> {
    var step: Next<Input, Output> = step
    var state: Option<Output> = None

    while (true) {
      currentCoroutineContext().ensureActive()
      try {
        val a = block.invoke()
        when (val decision = step(a)) {
          is Continue -> {
            if (decision.delay != ZERO) delay(decision.delay)
            state = decision.output.some()
            step = decision.next
          }

          is Done -> return Either.Right(decision.output)
        }
      } catch (e: Throwable) {
        return Either.Left(orElse(e.nonFatalOrThrow(), state.getOrNull()))
      }
    }
  }

  public fun <B> map(transform: suspend (output: Output) -> B): Schedule<Input, B> {
    suspend fun loop(input: Input, self: Next<Input, Output>): Decision<Input, B> =
      when (val decision = self(input)) {
        is Continue -> Continue(transform(decision.output), decision.delay) { loop(it, decision.next) }
        is Done -> Done(transform(decision.output))
      }

    return Schedule { input -> loop(input, step) }
  }

  /**
   * Runs `this` schedule until [Done], and then runs [other] until [Done].
   * Wrapping the output of `this` in [Either.Left], and the output of [other] in [Either.Right].
   */
  public infix fun <B> andThen(other: Schedule<Input, B>): Schedule<Input, Either<Output, B>> =
    andThen(other, { it.left() }) { it.right() }

  /**
   * Runs `this` schedule, and transforms the output of this schedule using [ifLeft],
   * When `this` schedule is [Done], it runs [other] schedule, and transforms the output using [ifRight].
   */
  public fun <B, C> andThen(
    other: Schedule<Input, B>,
    ifLeft: suspend (Output) -> C,
    ifRight: suspend (B) -> C
  ): Schedule<Input, C> {
    suspend fun loop(input: Input, self: Next<Input, B>): Decision<Input, C> =
      when (val decision = self(input)) {
        is Done -> Done(ifRight(decision.output))
        is Continue -> Continue(ifRight(decision.output), decision.delay) {
          loop(input, decision.next)
        }
      }

    suspend fun loop(input: Input, self: Next<Input, Output>): Decision<Input, C> =
      when (val decision = self(input)) {
        is Continue -> Continue(ifLeft(decision.output), decision.delay) { loop(it, decision.next) }
        is Done -> Continue(ifLeft(decision.output), ZERO) { loop(input, other.step) }
      }

    return Schedule { input -> loop(input, step) }
  }

  /**
   * Pipes the output of this schedule to the input of the [other].
   * Similar to |> in F# but for [Schedule].
   */
  public infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B> {
    suspend fun loop(input: Input, self: Next<Input, Output>, other: Next<Output, B>): Decision<Input, B> =
      when (val decision = self(input)) {
        is Done -> Done(other(decision.output).output)
        is Continue -> when (val decision2 = other(decision.output)) {
          is Done -> Done(decision2.output)
          is Continue -> Continue(decision2.output, decision.delay + decision2.delay) {
            loop(it, decision.next, decision2.next)
          }
        }
      }

    return Schedule { input -> loop(input, step, other.step) }
  }

  /**
   * Runs the [Schedule] until the predicate of [Input] and [Output] returns false.
   */
  public fun doWhile(test: suspend (Input, Output) -> Boolean): Schedule<Input, Output> {
    suspend fun loop(input: Input, self: Next<Input, Output>): Decision<Input, Output> =
      when (val decision = self(input)) {
        is Continue ->
          if (test(input, decision.output)) Continue(decision.output, decision.delay) { loop(it, decision.next) }
          else Done(decision.output)

        is Done -> decision
      }

    return Schedule { input -> loop(input, step) }
  }

  /**
   * Runs the [Schedule] until the predicate of [Input] and [Output] returns true.
   * Inverse version of [doWhile].
   */
  public fun doUntil(test: suspend (input: Input, output: Output) -> Boolean): Schedule<Input, Output> =
    doWhile { input, output -> !test(input, output) }

  /**
   * Adds a logging action to the [Schedule].
   */
  public fun log(action: suspend (input: Input, output: Output) -> Unit): Schedule<Input, Output> =
    doWhile { input, output ->
      action(input, output)
      true
    }

  public fun delay(f: suspend (Duration) -> Duration): Schedule<Input, Output> =
    delayed { _, duration -> f(duration) }

  public fun delayed(f: suspend (Output, Duration) -> Duration): Schedule<Input, Output> {
    suspend fun loop(input: Input, self: Next<Input, Output>): Decision<Input, Output> =
      when (val decision = self(input)) {
        is Continue -> Continue(decision.output, f(decision.output, decision.delay)) { loop(it, decision.next) }
        is Done -> decision
      }

    return Schedule { input -> loop(input, step) }
  }

  public fun jittered(
    min: Double = 0.0,
    max: Double = 1.0,
    random: Random = Random.Default
  ): Schedule<Input, Output> =
    delayed { _, duration -> duration * random.nextDouble(min, max) }

  public fun mapDecision(f: suspend (Decision<Input, Output>) -> Decision<Input, Output>): Schedule<Input, Output> {
    suspend fun loop(input: Input, self: Next<Input, Output>): Decision<Input, Output> =
      f(self(input))

    return Schedule { input -> loop(input, step) }
  }

  public fun collect(): Schedule<Input, List<Output>> =
    fold(emptyList()) { acc, out -> acc + out }

  public fun <B> fold(b: B, f: suspend (B, Output) -> B): Schedule<Input, B> {
    suspend fun loop(input: Input, b: B, self: Next<Input, Output>): Decision<Input, B> =
      when (val decision = self(input)) {
        is Continue -> f(b, decision.output).let { b2 ->
          Continue(b2, decision.delay) { loop(it, b2, decision.next) }
        }

        is Done -> Done(b)
      }

    return Schedule { loop(it, b, step) }
  }

  public infix fun <B> zipLeft(other: Schedule<Input, B>): Schedule<Input, Output> =
    and(other) { input, _ -> input }

  public infix fun <B> zipRight(other: Schedule<Input, B>): Schedule<Input, B> =
    and(other) { _, b -> b }

  public infix fun <B> and(other: Schedule<Input, B>): Schedule<Input, Pair<Output, B>> =
    and(other, ::Pair)

  public fun <B, C> and(
    other: Schedule<Input, B>,
    transform: suspend (output: Output, b: B) -> C
  ): Schedule<Input, C> = and(other, transform) { a, b -> maxOf(a, b) }

  public fun <B, C> and(
    other: Schedule<Input, B>,
    transform: suspend (output: Output, b: B) -> C,
    combineDuration: suspend (left: Duration, right: Duration) -> Duration
  ): Schedule<Input, C> {
    suspend fun loop(
      input: Input,
      self: Next<Input, Output>,
      that: Next<Input, B>
    ): Decision<Input, C> {
      val left = self(input)
      val right = that(input)
      return if (left is Continue && right is Continue) Continue(
        transform(left.output, right.output),
        combineDuration(left.delay, right.delay)
      ) {
        loop(it, left.next, right.next)
      } else Done(transform(left.output, right.output))
    }

    return Schedule { input ->
      loop(input, step, other.step)
    }
  }

  // LongMethodJail, cries in pattern matching
  public fun <B, C> or(
    other: Schedule<Input, B>,
    transform: suspend (output: Output?, b: B?) -> C,
    combineDuration: suspend (left: Duration?, right: Duration?) -> Duration
  ): Schedule<Input, C> {
    suspend fun loop(
      input: Input,
      self: Next<Input, Output>?,
      that: Next<Input, B>?
    ): Decision<Input, C> =
      when (val left = self?.invoke(input)) {
        is Continue -> when (val right = that?.invoke(input)) {
          is Continue -> Continue(
            transform(left.output, right.output),
            combineDuration(left.delay, right.delay)
          ) {
            loop(it, left.next, right.next)
          }

          is Done -> Continue(
            transform(left.output, right.output),
            combineDuration(left.delay, null)
          ) {
            loop(it, left.next, null)
          }

          null -> Continue(
            transform(left.output, null),
            combineDuration(left.delay, null)
          ) {
            loop(it, left.next, null)
          }
        }

        is Done -> when (val right = that?.invoke(input)) {
          is Continue -> Continue(
            transform(left.output, right.output),
            combineDuration(null, right.delay)
          ) {
            loop(it, null, right.next)
          }

          is Done -> Done(transform(left.output, right.output))
          null -> Done(transform(left.output, null))
        }

        null -> when (val right = that?.invoke(input)) {
          is Continue -> Continue(
            transform(null, right.output),
            combineDuration(null, right.delay)
          ) {
            loop(it, null, right.next)
          }

          is Done -> Done(transform(null, right.output))
          null -> Done(transform(null, null))
        }
      }

    return Schedule { input ->
      loop(input, step, other.step)
    }
  }

  public companion object {

    /**
     * Creates a Schedule that continues as long as [f] returns true.
     */
    public fun <A> doWhile(f: suspend (input: A, output: A) -> Boolean): Schedule<A, A> =
      identity<A>().doWhile(f)

    /**
     * Creates a Schedule that continues until [f] returns true.
     */
    public fun <A> doUntil(f: suspend (input: A, output: A) -> Boolean): Schedule<A, A> =
      identity<A>().doUntil(f)

    public fun <A> identity(): Schedule<A, A> {
      fun loop(input: A): Decision<A, A> =
        Continue(input, ZERO) { loop(it) }

      return Schedule { loop(it) }
    }

    public fun <A> spaced(duration: Duration): Schedule<A, Long> {
      fun loop(input: Long): Decision<A, Long> = Continue(input, duration) { loop(input + 1) }
      return Schedule { loop(0L) }
    }

    public fun <A> fibonacci(one: Duration): Schedule<A, Duration> {
      fun loop(prev: Duration, curr: Duration): Decision<A, Duration> =
        (prev + curr).let { next ->
          Continue(next, next) { loop(curr, next) }
        }

      return Schedule { loop(0.nanoseconds, one) }
    }

    public fun <A> linear(base: Duration): Schedule<A, Duration> {
      fun loop(count: Int): Decision<A, Duration> =
        (base * count).let { next ->
          Continue(next, next) { loop(count + 1) }
        }

      return Schedule { loop(1) }
    }

    public fun <A> exponential(base: Duration, factor: Double = 2.0): Schedule<A, Duration> {
      fun loop(count: Int): Decision<A, Duration> =
        (base * factor.pow(count)).let { next ->
          Continue(next, next) { loop(count + 1) }
        }

      return Schedule { loop(0) }
    }

    /** Creates a Schedule which collects all its inputs in a list. */
    public fun <A> collect(): Schedule<A, List<A>> =
      identity<A>().collect()

    public fun <A> never(): Schedule<A, Nothing> =
      Schedule { awaitCancellation() }

    /** Creates a Schedule that only retries once. */
    public fun <A> once(): Schedule<A, Unit> =
      recurs<A>(1).map { }

    public fun <A> recurs(n: Int): Schedule<A, Long> =
      recurs(n.toLong())

    public fun <A> recurs(n: Long): Schedule<A, Long> {
      fun loop(input: Long): Decision<A, Long> =
        if (input < n) Continue(input, ZERO) { loop(input + 1) } else Done(input)

      return Schedule { loop(0L) }
    }

    public fun <Input> forever(): Schedule<Input, Long> =
      unfold(0) { it + 1 }

    public fun <Input, Output> unfold(initial: Output, next: suspend (Output) -> Output): Schedule<Input, Output> {
      fun loop(input: Output): Decision<Input, Output> =
        Continue(input, ZERO) { loop(next(input)) }

      return Schedule { loop(initial) }
    }
  }

  public sealed interface Decision<in Input, out Output> {
    public val output: Output

    public data class Done<Output>(override val output: Output) : Decision<Any?, Output>
    public data class Continue<in Input, out Output>(
      override val output: Output,
      val delay: Duration,
      val next: Next<Input, Output>
    ) : Decision<Input, Output>
  }
}

public suspend fun <A, B> Schedule<Throwable, B>.retry(fa: suspend () -> A): A =
  retryOrElse(fa) { e, _ -> throw e }

public suspend fun <A, B> Schedule<Throwable, B>.retryOrElse(
  fa: suspend () -> A,
  orElse: suspend (Throwable, B) -> A
): A =
  retryOrElseEither(fa, orElse).fold(::identity, ::identity)

public suspend fun <A, B, C> Schedule<Throwable, B>.retryOrElseEither(
  fa: suspend () -> A,
  orElse: suspend (Throwable, B) -> C
): Either<C, A> {
  var step: Next<Throwable, B> = step

  while (true) {
    currentCoroutineContext().ensureActive()
    try {
      return Either.Right(fa.invoke())
    } catch (e: Throwable) {
      when (val decision = step(e)) {
        is Continue -> {
          if (decision.delay != ZERO) delay(decision.delay)
          step = decision.next
        }

        is Done -> return Either.Left(orElse(e.nonFatalOrThrow(), decision.output))
      }
    }
  }
}
