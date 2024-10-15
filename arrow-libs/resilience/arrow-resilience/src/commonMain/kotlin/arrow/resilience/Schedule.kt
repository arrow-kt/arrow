@file:OptIn(ExperimentalTypeInference::class)

package arrow.resilience

import arrow.core.Either
import arrow.core.NonFatal
import arrow.core.None
import arrow.core.Option
import arrow.core.left
import arrow.core.merge
import arrow.core.nonFatalOrThrow
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.fold
import arrow.core.right
import arrow.core.some
import arrow.resilience.Schedule.Companion.identity
import arrow.resilience.Schedule.Decision.Continue
import arrow.resilience.Schedule.Decision.Done
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.retry
import kotlin.experimental.ExperimentalTypeInference
import kotlin.math.pow
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.nanoseconds

/**
 * A [Schedule] describes how a `suspend fun` should [retry] or [repeat].
 *
 * It's defined by a [step] function that takes an [Input] and returns a [Decision],
* the [Decision] determines if the `suspend fun` should be [Continue] to be retried or repeated
* (and if so, the `delay` until the next attempt),
 * or if the [Schedule] is [Done] retrying or repeating.
 */
public fun interface Schedule<in Input, out Output> {

  public suspend fun step(input: Input): Decision<Input, Output>

  /** Repeat the schedule, and uses [block] as [Input] for the [step] function. */
  public suspend fun repeat(block: suspend () -> Input): Output =
    repeatOrElse(block) { e, _ -> throw e }

  /**
   * Repeat the schedule, and uses [block] as [Input] for the [step] function.
   * If the [step] function throws an exception, it will be caught and passed to [orElse].
   */
  public suspend fun repeatOrElse(
    block: suspend () -> Input,
    orElse: suspend (error: Throwable, output: Output?) -> @UnsafeVariance Output
  ): Output = repeatOrElseEither(block, orElse).merge()

  /**
   * Repeat the schedule, and uses [block] as [Input] for the [step] function.
   * If the [step] function throws an exception, it will be caught and passed to [orElse].
   * The resulting [Either] indicates if the [step] function threw an exception or not.
   */
  public suspend fun <A> repeatOrElseEither(
    block: suspend () -> Input,
    orElse: suspend (error: Throwable, output: Output?) -> A
  ): Either<A, Output> {
    var step: Schedule<Input, Output> = this
    var state: Option<Output> = None

    while (true) {
      currentCoroutineContext().ensureActive()
      try {
        val a = block.invoke()
        when (val decision = step(a)) {
          is Continue -> {
            if (decision.delay != ZERO) delay(decision.delay)
            state = decision.output.some()
            step = decision.step
          }

          is Done -> return Either.Right(decision.output)
        }
      } catch (e: Throwable) {
        return Either.Left(orElse(e.nonFatalOrThrow(), state.getOrNull()))
      }
    }
  }

  /**
   * Modify [Continue.delay] by the given function [transform].
   */
  public fun delayed(transform: suspend (Output, Duration) -> Duration): Schedule<Input, Output> =
    Schedule { step(it).delayed(transform) }

  /**
   * Transform the [Schedule] by mapping the [Input]'s.
   */
  public fun <A> contramap(transform: suspend (A) -> Input): Schedule<A, Output> =
    Schedule { step(transform(it)).contramap(transform) }

  /** Transforms every [Output]'ed value of `this` schedule using [transform]. */
  public fun <A> map(transform: suspend (output: Output) -> A): Schedule<Input, A> =
    Schedule { step(it).map(transform) }

  public fun mapDecision(f: suspend (Decision<Input, Output>) -> Decision<@UnsafeVariance Input, @UnsafeVariance Output>): Schedule<Input, Output> =
    Schedule { step(it).recursiveMap(f) }

  /**
   * Runs `this` schedule until [Done], and then runs [other] until [Done].
   * Wrapping the output of `this` in [Either.Left], and the output of [other] in [Either.Right].
   */
  public infix fun <A> andThen(other: Schedule<@UnsafeVariance Input, A>): Schedule<Input, Either<Output, A>> =
    andThen(other, { it.left() }) { it.right() }

  /**
   * Runs `this` schedule, and transforms the output of this schedule using [ifLeft],
   * When `this` schedule is [Done], it runs [other] schedule, and transforms the output using [ifRight].
   */
  public fun <A, B> andThen(
    other: Schedule<@UnsafeVariance Input, A>,
    ifLeft: suspend (Output) -> B,
    ifRight: suspend (A) -> B
  ): Schedule<Input, B> =
    Schedule { step(it).andThen(other::step, ifLeft, ifRight) }

  /** Runs `this` [Schedule] _while_ the [predicate] of [Input] and [Output] returns `false`. */
  public fun doWhile(predicate: suspend (@UnsafeVariance Input, Output) -> Boolean): Schedule<Input, Output> {
    suspend fun loop(input: Input, self: Schedule<Input, Output>): Decision<Input, Output> =
      when (val decision = self.step(input)) {
        is Continue ->
          if (predicate(input, decision.output)) Continue(decision.output, decision.delay) { loop(it, decision.step) }
          else Done(decision.output)

        is Done -> decision
      }

    return Schedule { input -> loop(input, this) }
  }

  /**
   * Runs the [Schedule] _until_ the [predicate] of [Input] and [Output] returns true.
   * Inverse version of [doWhile].
   */
  public fun doUntil(predicate: suspend (input: @UnsafeVariance Input, output: Output) -> Boolean): Schedule<Input, Output> =
    doWhile { input, output -> !predicate(input, output) }

  /**
   * Adds a logging action to the [Schedule].
   */
  public fun log(action: suspend (input: @UnsafeVariance Input, output: Output) -> Unit): Schedule<Input, Output> =
    doWhile { input, output ->
      action(input, output)
      true
    }

  /** Adds a [Random] jitter to the delay of the [Schedule]. */
  public fun jittered(
    min: Double = 0.0,
    max: Double = 1.0,
    random: Random = Random.Default
  ): Schedule<Input, Output> =
    delayed { _, duration -> duration * random.nextDouble(min, max) }

  /**
   * Collects all the [Output] of the [Schedule] into a [List].
   * This is useful in combination with [identity] to collect all the inputs.
   */
  public fun collect(): Schedule<Input, List<Output>> =
    fold(emptyList()) { acc, out -> acc + out }

  /**
   * Folds all the [Output] of the [Schedule] into a [List].
   * This is useful in combination with [identity] to fold all the [Input] into a final value [B].
   * If one of the [Schedule]s is done, the other [Schedule] is not executed anymore.
   */
  public fun <B> fold(b: B, f: suspend (B, Output) -> B): Schedule<Input, B> {
    suspend fun loop(input: Input, b: B, self: Schedule<Input, Output>): Decision<Input, B> =
      when (val decision = self.step(input)) {
        is Continue -> f(b, decision.output).let { b2 ->
          Continue(b2, decision.delay) { loop(it, b2, decision.step) }
        }

        is Done -> Done(b)
      }

    return Schedule { loop(it, b, this) }
  }

  /**
   * Combines two [Schedule]s into one, ignoring the output of [other] [Schedule].
   * It chooses the longest delay between the two [Schedule]s.
   * If one of the [Schedule]s is done, the other [Schedule] is not executed anymore.
   */
  public infix fun <B> zipLeft(other: Schedule<@UnsafeVariance Input, B>): Schedule<Input, Output> =
    and(other) { input, _ -> input }

  /**
   * Combines two [Schedule]s into one, ignoring the output of `this` [Schedule].
   * It chooses the longest delay between the two [Schedule]s.
   * If one of the [Schedule]s is done, the other [Schedule] is not executed anymore.
   */
  public infix fun <B> zipRight(other: Schedule<@UnsafeVariance Input, B>): Schedule<Input, B> =
    and(other) { _, b -> b }

  /**
   * Combines two [Schedule]s into one by combining the output of both [Schedule]s into a [Pair].
   * It chooses the longest delay between the two [Schedule]s.
   * If one of the [Schedule]s is done, the other [Schedule] is not executed anymore.
   */
  public infix fun <B> and(other: Schedule<@UnsafeVariance Input, B>): Schedule<Input, Pair<Output, B>> =
    and(other, ::Pair)

  /**
   * Combines two [Schedule]s into one by transforming the output of both [Schedule]s using [transform].
   * It chooses the longest delay between the two [Schedule]s.
   * If one of the [Schedule]s is done, the other [Schedule] is not executed anymore.
   */
  public fun <B, C> and(
    other: Schedule<@UnsafeVariance Input, B>,
    transform: suspend (output: Output, b: B) -> C
  ): Schedule<Input, C> = and(other, transform) { a, b -> maxOf(a, b) }

  /**
   * Combines two [Schedule]s into one by transforming the output of both [Schedule]s using [transform].
   * It combines the delay of both [Schedule]s using [combineDuration].
   * If one of the [Schedule]s is done, the other [Schedule] is not executed anymore.
   */
  public fun <B, C> and(
    other: Schedule<@UnsafeVariance Input, B>,
    transform: suspend (output: Output, b: B) -> C,
    combineDuration: suspend (left: Duration, right: Duration) -> Duration
  ): Schedule<Input, C> =
    Schedule { this.step(it).and(other.step(it), transform, combineDuration) }

  /**
   * Combines two [Schedule]s into one by transforming the output of both [Schedule]s using [transform].
   * It combines the delay of both [Schedule]s using [combineDuration].
   * It continues to execute both [Schedule]s until both are done,
   * padding the output and duration with `null` if one of the [Schedule]s is done.
   */
  public fun <B, C> or(
    other: Schedule<@UnsafeVariance Input, B>,
    transform: suspend (output: Output?, b: B?) -> C,
    combineDuration: suspend (left: Duration?, right: Duration?) -> Duration
  ): Schedule<Input, C> =
    Schedule { this.step(it).or(other.step(it), transform, combineDuration) }

  public companion object {

    /** Create a [Schedule] that continues `while` [predicate] returns true. */
    public fun <Input> doWhile(predicate: suspend (input: Input, output: Input) -> Boolean): Schedule<Input, Input> =
      identity<Input>().doWhile(predicate)

    /** Creates a [Schedule] that continues `until` [predicate] returns true. */
    public fun <Input> doUntil(predicate: suspend (input: Input, output: Input) -> Boolean): Schedule<Input, Input> =
      identity<Input>().doUntil(predicate)

    /** Creates a [Schedule] that outputs the [Input] unmodified. */
    public fun <Input> identity(): Schedule<Input, Input> {
      fun loop(input: Input): Decision<Input, Input> =
        Continue(input, ZERO) { loop(it) }

      return Schedule { loop(it) }
    }

    /** Creates a [spaced] backing-off [Schedule] with the provided [duration]. */
    public fun <Input> spaced(duration: Duration): Schedule<Input, Long> {
      fun loop(input: Long): Decision<Input, Long> = Continue(input, duration) { loop(input + 1) }
      return Schedule { loop(0L) }
    }

    /** Creates a [fibonacci] backing-off [Schedule] with the provided [one]. */
    public fun <Input> fibonacci(one: Duration): Schedule<Input, Duration> {
      fun loop(prev: Duration, curr: Duration): Decision<Input, Duration> =
        (prev + curr).let { next ->
          Continue(curr, curr) { loop(curr, next) }
        }

      return Schedule { loop(0.nanoseconds, one) }
    }

    /** Creates a linear backing-off [Schedule] with the provided [base] value. */
    public fun <Input> linear(base: Duration): Schedule<Input, Duration> {
      fun loop(count: Int): Decision<Input, Duration> =
        (base * count).let { next ->
          Continue(next, next) { loop(count + 1) }
        }

      return Schedule { loop(1) }
    }

    /** Creates a [exponential] backing-off [Schedule] with the provided [base] duration and exponential [factor]. */
    public fun <Input> exponential(base: Duration, factor: Double = 2.0): Schedule<Input, Duration> {
      fun loop(count: Int): Decision<Input, Duration> =
        (base * factor.pow(count)).let { next ->
          Continue(next, next) { loop(count + 1) }
        }

      return Schedule { loop(0) }
    }

    /** Creates a [Schedule] which [collect]s all its [Input] in a [List]. */
    public fun <Input> collect(): Schedule<Input, List<Input>> =
      identity<Input>().collect()

    /** Creates a Schedule that recurs [n] times. */
    public fun <Input> recurs(n: Long): Schedule<Input, Long> {
      fun loop(input: Long): Decision<Input, Long> =
        if (input < n) Continue(input, ZERO) { loop(input + 1) } else Done(input)

      return Schedule { loop(0L) }
    }

    /** Creates a [Schedule] that runs [forever] */
    public fun <Input> forever(): Schedule<Input, Long> =
      unfold(0) { it + 1 }

    /**
     * Creates a [Schedule] that unfolds values of [Output] with an [initial] value, and the [next] function to compute the next value.
     */
    public fun <Input, Output> unfold(initial: Output, next: suspend (Output) -> Output): Schedule<Input, Output> {
      fun loop(input: Output): Decision<Input, Output> =
        Continue(input, ZERO) { loop(next(input)) }

      return Schedule { loop(initial) }
    }
  }

  public sealed interface Decision<in Input, out Output> {
    public val output: Output

    public data class Done<out Output>(override val output: Output) : Decision<Any?, Output>
    public data class Continue<in Input, out Output>(
      override val output: Output,
      val delay: Duration,
      val step: Schedule<Input, Output>
    ) : Decision<Input, Output>

    public suspend fun recursiveMap(
      transform: suspend (Decision<Input, Output>) -> Decision<@UnsafeVariance Input, @UnsafeVariance Output>
    ): Decision<Input, Output> = when (val next = transform(this)) {
      is Done -> next
      is Continue -> Continue(next.output, next.delay) { next.step.step(it).recursiveMap(transform) }
    }

    public suspend fun delayed(transform: suspend (Output, Duration) -> Duration): Decision<Input, Output> = when (this) {
      is Done -> Done(output)
      is Continue -> Continue(output, transform(output, delay), step)
    }

    public suspend fun <A> contramap(f: suspend (A) -> Input): Decision<A, Output> = when (this) {
      is Done -> Done(output)
      is Continue -> Continue(output, delay) { step.step(f(it)).contramap(f) }
    }

    public suspend fun <A> map(f: suspend (output: Output) -> A): Decision<Input, A> = when (this) {
      is Done -> Done(f(output))
      is Continue -> Continue(f(output), delay) { step.step(it).map(f) }
    }

    public suspend fun andThen(
      other: suspend (@UnsafeVariance Input) -> Decision<@UnsafeVariance Input, @UnsafeVariance Output>
    ): Decision<Input, Output> = when (this) {
      is Done -> Continue(output, ZERO, other)
      is Continue -> Continue(output, delay) { step.step(it).andThen(other) }
    }

    public suspend fun <A, B> andThen(
      other: suspend (@UnsafeVariance Input) -> Decision<@UnsafeVariance Input, A>,
      ifLeft: suspend (Output) -> B,
      ifRight: suspend (A) -> B
    ): Decision<Input, B> =
      this.map(ifLeft).andThen { other(it).map(ifRight) }

    public suspend fun <B, C> and(
      other: Decision<@UnsafeVariance Input, B>,
      transform: suspend (output: Output, b: B) -> C,
      combineDuration: suspend (left: Duration, right: Duration) -> Duration
    ): Decision<Input, C> = when {
      this is Continue && other is Continue ->
        Continue(
          transform(this.output, other.output),
          combineDuration(this.delay, other.delay)
        ) { this.step.step(it).and(other.step.step(it), transform, combineDuration) }
      else -> Done(transform(this.output, other.output))
    }

    public suspend fun <B, C> or(
      other: Decision<@UnsafeVariance Input, B>,
      transform: suspend (output: Output?, b: B?) -> C,
      combineDuration: suspend (left: Duration?, right: Duration?) -> Duration
    ): Decision<Input, C> = when {
      this is Done && other is Done -> Done(transform(this.output, other.output))
      this is Done && other is Continue -> other.map { x -> transform(null, x) }
      this is Continue && other is Done -> this.map { x -> transform(x, null) }
      this is Continue && other is Continue ->
        Continue(
          transform(this.output, other.output),
          combineDuration(this.delay, other.delay)
        ) { this.step.step(it).or(other.step.step(it), transform, combineDuration) }
      else -> throw IllegalStateException()
    }
  }
}

/**
 * Retries [action] using any [E] that occurred as the input to the [Schedule].
 * It will throw the last exception if the [Schedule] is exhausted, and ignores the output of the [Schedule].
 */
public suspend inline fun <reified E: Throwable, A> Schedule<E, *>.retry(
  noinline action: suspend () -> A
): A = retry(E::class, action)

/**
 * Retries [action] using any [E] that occurred as the input to the [Schedule].
 * It will throw the last exception if the [Schedule] is exhausted, and ignores the output of the [Schedule].
 */
public suspend fun <E: Throwable, A> Schedule<E, *>.retry(
  exceptionClass: KClass<E>,
  action: suspend () -> A
): A = retryOrElse(exceptionClass, action) { e, _ -> throw e }

/**
 * Retries [action] using any [E] that occurred as the input to the [Schedule].
 * If the [Schedule] is exhausted,
 * it will invoke [orElse] with the last exception and the output of the [Schedule] to produce a fallback [Input] value.
 */
public suspend inline fun <reified E: Throwable, Input, Output> Schedule<E, Output>.retryOrElse(
  noinline action: suspend () -> Input,
  noinline orElse: suspend (Throwable, Output) -> Input
): Input = retryOrElse(E::class, action, orElse)

/**
 * Retries [action] using any [E] that occurred as the input to the [Schedule].
 * If the [Schedule] is exhausted,
 * it will invoke [orElse] with the last exception and the output of the [Schedule] to produce a fallback [Input] value.
 */
public suspend fun <E: Throwable, Input, Output> Schedule<E, Output>.retryOrElse(
  exceptionClass: KClass<E>,
  action: suspend () -> Input,
  orElse: suspend (E, Output) -> Input
): Input = retryOrElseEither(exceptionClass, action, orElse).merge()

/**
 * Retries [action] using any [E] that occurred as the input to the [Schedule].
 * If the [Schedule] is exhausted,
 * it will invoke [orElse] with the last exception and the output of the [Schedule] to produce a fallback value of [A].
 * Returns [Either] with the fallback value if the [Schedule] is exhausted, or the successful result of [action].
 */
public suspend inline fun <reified E: Throwable, Input, Output, A> Schedule<E, Output>.retryOrElseEither(
  noinline action: suspend () -> Input,
  noinline orElse: suspend (E, Output) -> A
): Either<A, Input> = retryOrElseEither(E::class, action, orElse)

/**
 * Retries [action] using any [E] that occurred as the input to the [Schedule].
 * If the [Schedule] is exhausted,
 * it will invoke [orElse] with the last exception and the output of the [Schedule] to produce a fallback value of [A].
 * Returns [Either] with the fallback value if the [Schedule] is exhausted, or the successful result of [action].
 */
public suspend fun <E: Throwable, Input, Output, A> Schedule<E, Output>.retryOrElseEither(
  exceptionClass: KClass<E>,
  action: suspend () -> Input,
  orElse: suspend (E, Output) -> A
): Either<A, Input> {
  var step: Schedule<E, Output> = this

  while (true) {
    currentCoroutineContext().ensureActive()
    try {
      return Either.Right(action.invoke())
    } catch (e: Throwable) {
      @Suppress("NAME_SHADOWING") val e = when {
        exceptionClass.isInstance(e) -> exceptionClass.cast(e)
        else -> throw e
      }

      when (val decision = step(e)) {
        is Continue -> {
          if (decision.delay != ZERO) delay(decision.delay)
          step = decision.step
        }

        is Done ->
          if (NonFatal(e)) return Either.Left(orElse(e, decision.output))
          else throw e
      }
    }
  }
}

/**
 * Retries [action] using any [Error] that occurred as the input to the [Schedule].
 * It will return the last [Error] if the [Schedule] is exhausted, and ignores the output of the [Schedule].
 */
public suspend inline fun <Error, Result, Output> Schedule<Error, Output>.retryRaise(
  @BuilderInference action: Raise<Error>.() -> Result,
): Either<Error, Result> = either {
  retry(this@retryRaise, action)
}

/**
 * Retries [action] using any [Error] that occurred as the input to the [Schedule].
 * It will return the last [Error] if the [Schedule] is exhausted, and ignores the output of the [Schedule].
 */
public suspend inline fun <Error, Result, Output> Schedule<Error, Output>.retryEither(
  @BuilderInference action: () -> Either<Error, Result>,
): Either<Error, Result> = retryRaise {
  action().bind()
}

/**
 * Retries [action] using any [Error] that occurred as the input to the [Schedule].
 * It will return the last [Error] if the [Schedule] is exhausted, and ignores the output of the [Schedule].
 */
public suspend inline fun <Error, Result, Output> Raise<Error>.retry(
  schedule: Schedule<Error, Output>,
  @BuilderInference action: Raise<Error>.() -> Result,
): Result {
  var step: Schedule<Error, Output> = schedule

  while (true) {
    currentCoroutineContext().ensureActive()
    fold(
      action,
      recover = { error ->
        when (val decision = step.step(error)) {
          is Continue -> {
            if (decision.delay != ZERO) delay(decision.delay)
            step = decision.step
          }

          is Done -> raise(error)
        }
      },
      transform = { result ->
        return result
      },
    )
  }
}
