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
import kotlinx.coroutines.flow.retry
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.nanoseconds
import kotlinx.coroutines.currentCoroutineContext

/**
 * # Retrying and repeating effects
 *
 * A common demand when working with effects is to retry or repeat them when certain circumstances happen. Usually, the retrial or repetition does not happen right away; rather, it is done based on a policy. For instance, when fetching content from a network request, we may want to retry it when it fails, using an exponential backoff algorithm, for a maximum of 15 seconds or 5 attempts, whatever happens first.
 *
 * [Schedule] allows you to define and compose powerful yet simple policies, which can be used to either repeat or retry computation.
 *
 * The two core methods of running a schedule are:
 * - __retry__: The effect is executed once, and if it fails, it will be reattempted based on the scheduling policy passed as an argument. It will stop if the effect ever succeeds, or the policy determines it should not be reattempted again.
 * - __repeat__: The effect is executed once, and if it succeeds, it will be executed again based on the scheduling policy passed as an argument. It will stop if the effect ever fails, or the policy determines it should not be executed again. It will return the last internal state of the scheduling policy, or the error that happened running the effect.
 *
 * ## Constructing a policy:
 *
 * Constructing a simple schedule which recurs 10 times until it succeeds:
 * ```kotlin
 * import arrow.fx.resilience.*
 *
 * fun <A> recurTenTimes() = Schedule.recurs<A>(10)
 * ```
 * <!--- KNIT example-schedule-01.kt -->
 *
 * A more complex schedule
 *
 * ```kotlin
 * import kotlin.time.Duration.Companion.milliseconds
 * import kotlin.time.Duration.Companion.seconds
 * import kotlin.time.ExperimentalTime
 * import arrow.fx.resilience.*
 *
 * @ExperimentalTime
 * fun <A> complexPolicy(): Schedule<A, List<A>> =
 *   Schedule.exponential<A>(10.milliseconds).whileOutput { it < 60.seconds }
 *     .andThen(Schedule.spaced<A>(60.seconds) and Schedule.recurs(100)).jittered()
 *     .zipRight(Schedule.identity<A>().collect())
 * ```
 * <!--- KNIT example-schedule-02.kt -->
 *
 * This policy will recur with exponential backoff as long as the delay is less than 60 seconds and then continue with a spaced delay of 60 seconds.
 * The delay is also randomized slightly to avoid coordinated backoff from multiple services.
 * Finally we also collect every input to the schedule and return it. When used with [retry] this will return a list of exceptions that occured on failed attempts.
 *
 * ## Common use cases
 *
 * Common use cases
 * Once we have building blocks and ways to combine them, let’s see how we can use them to solve some use cases.
 *
 * ### Repeating an effect and dealing with its result
 *
 * When we repeat an effect, we do it as long as it keeps providing successful results and the scheduling policy tells us to keep recursing. But then, there is a question on what to do with the results provided by each iteration of the repetition.
 *
 * There are at least 3 possible things we would like to do:
 *
 * - Discard all results; i.e., return `Unit`.
 * - Discard all intermediate results and just keep the last produced result.
 * - Keep all intermediate results.
 *
 * Assuming we have a suspend effect in, and we want to repeat it 3 times after its first successful execution, we can do:
 *
 * ```kotlin
 * import arrow.fx.resilience.*
 *
 * suspend fun main(): Unit {
 *   var counter = 0
 *   //sampleStart
 *   val res = Schedule.recurs<Unit>(3).repeat {
 *     println("Run: ${counter++}")
 *   }
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 * <!--- KNIT example-schedule-03.kt -->
 *
 * However, when running this new effect, its output will be the number of iterations it has performed, as stated in the documentation of the function. Also notice that we did not handle the error case, there are overloads [repeatOrElse] and [repeatOrElseEither] which offer that capability, [repeat] will just rethrow any error encountered.
 *
 * If we want to discard the values provided by the repetition of the effect, we can combine our policy with [Schedule.unit], using the [zipLeft] or [zipRight] combinators, which will keep just the output of one of the policies:
 *
 * ```kotlin
 * import arrow.fx.resilience.*
 *
 * suspend fun main(): Unit {
 *   var counter = 0
 *   //sampleStart
 *   val res = (Schedule.unit<Unit>() zipLeft Schedule.recurs(3)).repeat {
 *     println("Run: ${counter++}")
 *   }
 *   // equal to
 *   val res2 = (Schedule.recurs<Unit>(3) zipRight Schedule.unit()).repeat {
 *     println("Run: ${counter++}")
 *   }
 *   //sampleEnd
 *   println(res)
 *   println(res2)
 * }
 * ```
 * <!--- KNIT example-schedule-04.kt -->
 *
 * Following the same strategy, we can zip it with the [Schedule.identity] policy to keep only the last provided result by the effect.
 *
 * ```kotlin
 * import arrow.fx.resilience.*
 *
 * suspend fun main(): Unit {
 *   var counter = 0
 *   //sampleStart
 *   val res = (Schedule.identity<Int>() zipLeft Schedule.recurs(3)).repeat {
 *     println("Run: ${counter++}"); counter
 *   }
 *   // equal to
 *   val res2 = (Schedule.recurs<Int>(3) zipRight Schedule.identity<Int>()).repeat {
 *     println("Run: ${counter++}"); counter
 *   }
 *   //sampleEnd
 *   println(res)
 *   println(res2)
 * }
 * ```
 * <!--- KNIT example-schedule-05.kt -->
 *
 * Finally, if we want to keep all intermediate results, we can zip the policy with [Schedule.collect]:
 *
 * ```kotlin
 * import arrow.fx.resilience.*
 *
 * suspend fun main(): Unit {
 *   var counter = 0
 *   //sampleStart
 *   val res = (Schedule.collect<Int>() zipLeft Schedule.recurs(3)).repeat {
 *     println("Run: ${counter++}")
 *     counter
 *   }
 *   // equal to
 *   val res2 = (Schedule.recurs<Int>(3) zipRight Schedule.collect<Int>()).repeat {
 *     println("Run: ${counter++}")
 *     counter
 *   }
 *   //sampleEnd
 *   println(res)
 *   println(res2)
 * }
 * ```
 * <!--- KNIT example-schedule-06.kt -->
 *
 * ## Repeating an effect until/while it produces a certain value
 *
 * We can make use of the policies doWhile or doUntil to repeat an effect while or until its produced result matches a given predicate.
 *
 * ```kotlin
 * import arrow.fx.resilience.*
 *
 * suspend fun main(): Unit {
 *   var counter = 0
 *   //sampleStart
 *   val res = Schedule.doWhile<Int>{ it <= 3 }.repeat {
 *     println("Run: ${counter++}"); counter
 *   }
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 * <!--- KNIT example-schedule-07.kt -->
 *
 * ## Exponential backoff retries
 *
 * A common algorithm to retry effectful operations, as network requests, is the exponential backoff algorithm. There is a scheduling policy that implements this algorithm and can be used as:
 *
 * ```kotlin
 * import kotlin.time.Duration.Companion.milliseconds
 * import kotlin.time.ExperimentalTime
 * import arrow.fx.resilience.*
 *
 * @ExperimentalTime
 * val exponential = Schedule.exponential<Unit>(250.milliseconds)
 * ```
 * <!--- KNIT example-schedule-08.kt -->
 */

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

  public fun <B> andThen(other: Schedule<Input, B>): Schedule<Input, Either<Output, B>> =
    andThen(other) { it }

  public fun <B, C> andThen(
    other: Schedule<Input, B>,
    transform: suspend (Either<Output, B>) -> C
  ): Schedule<Input, C> {
    suspend fun loop(input: Input, self: Next<Input, B>): Decision<Input, C> =
      when (val decision = self(input)) {
        is Continue -> Continue(transform(decision.output.right()), decision.delay) {
          loop(input, decision.next)
        }

        is Done -> Done(transform(decision.output.right()))
      }

    suspend fun loop(input: Input, self: Next<Input, Output>): Decision<Input, C> =
      when (val decision = self(input)) {
        is Continue -> Continue(transform(decision.output.left()), decision.delay) { loop(it, decision.next) }
        is Done -> Continue(transform(decision.output.left()), ZERO) {
          loop(input, other.step)
        }
      }

    return Schedule { input -> loop(input, step) }
  }

  public infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B> {
    suspend fun loop(input: Input, self: Next<Input, Output>, other: Next<Output, B>): Decision<Input, B> =
      when (val decision = self(input)) {
        is Continue -> when (val decision2 = other(decision.output)) {
          is Continue -> Continue(decision2.output, decision.delay + decision2.delay) {
            loop(it, decision.next, decision2.next)
          }

          is Done -> Done(decision2.output)
        }

        is Done -> Done(other(decision.output).output)
      }

    return Schedule { input -> loop(input, step, other.step) }
  }

  /**
   * Returns a new schedule that passes each input and output of this schedule to the specified
   * function, and then determines whether to continue based on the return value of the
   * function.
   */
  public fun <A : Input> check(test: suspend (A, Output) -> Boolean): Schedule<A, Output> {
    suspend fun loop(input: A, self: Next<A, Output>): Decision<A, Output> =
      when (val decision = self(input)) {
        is Continue ->
          if (test(input, decision.output)) Continue(decision.output, decision.delay) { loop(it, decision.next) }
          else Done(decision.output)

        is Done -> decision
      }

    return Schedule { input -> loop(input, step) }
  }

  /**
   * `untilOutput(f) = whileOutput { !f(it) }`
   */
  public fun untilOutput(f: suspend (Output) -> Boolean): Schedule<Input, Output> =
    whileOutput { !f(it) }

  /**
   * `untilInput(f) = whileInput { !f(it) }`
   */
  public fun <A : Input> untilInput(f: suspend (A) -> Boolean): Schedule<A, Output> =
    whileInput { !f(it) }

  public fun <A : Input> whileInput(f: suspend (A) -> Boolean): Schedule<A, Output> =
    check { input, _ -> f(input) }

  public fun whileOutput(f: suspend (Output) -> Boolean): Schedule<Input, Output> =
    check { _, output -> f(output) }

  public fun log(f: suspend (Input, Output) -> Unit): Schedule<Input, Output> =
    check { input, output ->
      f(input, output)
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
    public fun <A> doWhile(f: suspend (A) -> Boolean): Schedule<A, A> =
      identity<A>().whileInput(f)

    /**
     * Creates a Schedule that continues until [f] returns true.
     */
    public fun <A> doUntil(f: suspend (A) -> Boolean): Schedule<A, A> =
      identity<A>().untilInput(f)

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
