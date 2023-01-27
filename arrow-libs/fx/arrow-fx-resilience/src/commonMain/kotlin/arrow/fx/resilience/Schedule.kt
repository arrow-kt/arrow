package arrow.fx.resilience

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
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
import kotlin.jvm.JvmName
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit.NANOSECONDS
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
@JvmInline
public value class Schedule<Input, Output>(
  public val step: ScheduleStep<Input, Output>
) {

  public suspend fun <C> repeatOrElseEither(
    block: suspend () -> Input,
    orElse: suspend (Throwable, Output?) -> C
  ): Either<C, Output> {
    var step: ScheduleStep<Input, Output> = step
    var lastOutput: Option<Output> = None // TODO: turn into EMPTY_VALUE

    while (true) {
      currentCoroutineContext().ensureActive()
      try {
        val a = block.invoke()
        when (val decision = step(a)) {
          is Continue -> {
            if (decision.delay != ZERO) delay(decision.delay)
            lastOutput = decision.output.some()
            step = decision.next
          }

          is Done -> return Right(decision.output)
        }
      } catch (e: Throwable) {
        return Left(orElse(e.nonFatalOrThrow(), lastOutput.orNull()))
      }
    }
  }

  /**
   * Runs this effect once and, if it succeeded, decide using the provided policy if the effect should be repeated and if so, with how much delay.
   * Returns the last output from the policy or raises an error if a repeat failed.
   */
  public suspend fun repeat(fa: suspend () -> Input): Output =
    repeatOrElse(fa) { e, _ -> throw e }

  /**
   * Runs this effect once and, if it succeeded, decide using the provided policy if the effect should be repeated and if so, with how much delay.
   * Also offers a function to handle errors if they are encountered during repetition.
   */
  public suspend fun repeatOrElse(fa: suspend () -> Input, orElse: suspend (Throwable, Output?) -> Output): Output =
    repeatOrElseEither(fa, orElse).fold(::identity, ::identity)

  public suspend fun <C> repeatOrElseEitherAsFlow(
    block: suspend () -> Input,
    orElse: suspend (Throwable, Output?) -> C
  ): Flow<Either<C, Output>> = flow {
    var loop = true
    var step: ScheduleStep<Input, Output> = step
    var lastOutput: Option<Output> = None

    try {
      while (loop) {
        currentCoroutineContext().ensureActive()
        val a = block.invoke()
        when (val decision = step(a)) {
          is Continue -> {
            if (decision.delay != ZERO) delay(decision.delay)
            lastOutput = decision.output.some()
            step = decision.next
            emit(Right(decision.output))
          }

          is Done -> {
            emit(Right(decision.output))
            loop = false
          }
        }
      }
    } catch (e: Throwable) {
      emit(Left(orElse(e.nonFatalOrThrow(), lastOutput.orNull())))
    }
  }

  /**
   * Runs this effect and emits the output, if it succeeded, decide using the provided policy if the effect should be repeated and emitted, if so, with how much delay.
   * This will raise an error if a repeat failed.
   */
  public suspend fun repeatAsFlow(fa: suspend () -> Input): Flow<Output> =
    repeatOrElseAsFlow(fa) { e, _ -> throw e }

  /**
   * Runs this effect and emits the output, if it succeeded, decide using the provided policy if the effect should be repeated and emitted, if so, with how much delay.
   * Also offers a function to handle errors if they are encountered during repetition.
   */
  public suspend fun repeatOrElseAsFlow(
    fa: suspend () -> Input,
    orElse: suspend (Throwable, Output?) -> Output
  ): Flow<Output> =
    repeatOrElseEitherAsFlow(fa, orElse).map { it.fold(::identity, ::identity) }

  /** Changes the output of a schedule. Does not alter the decision of the schedule. */
  public fun <B> map(transform: suspend (output: Output) -> B): Schedule<Input, B> {
    suspend fun loop(input: Input, self: ScheduleStep<Input, Output>): Decision<Input, B> =
      when (val decision = self(input)) {
        is Continue -> Continue(transform(decision.output), decision.delay) { loop(it, decision.next) }
        is Done -> Done(transform(decision.output))
      }

    return Schedule { input -> loop(input, step) }
  }

  /** Changes the input of the schedule. May alter a schedule's decision if it is based on input. */
  public fun <B> contramap(f: suspend (B) -> Input): Schedule<B, Output> {
    suspend fun loop(input: B, self: ScheduleStep<Input, Output>): Decision<B, Output> =
      when (val decision = self(f(input))) {
        is Continue -> Continue(decision.output, decision.delay) { loop(it, decision.next) }
        is Done -> Done(decision.output)
      }

    return Schedule { input -> loop(input, step) }
  }

  /** Conditionally checks on both the input and the output whether to continue. */
  public fun <A : Input> check(test: suspend (A, Output) -> Boolean): Schedule<A, Output> {
    suspend fun loop(input: A, self: ScheduleStep<A, Output>): Decision<A, Output> =
      when (val decision = self(input)) {
        is Continue ->
          if (test(input, decision.output)) Continue(decision.output, decision.delay) { loop(it, decision.next) }
          else Done(decision.output)

        is Done -> decision
      }

    return Schedule { input -> loop(input, step) }
  }

  /**
   * Inverts the decision of a schedule.
   */
  public operator fun not(): Schedule<Input, Output> {
    suspend fun loop(input: Input, self: ScheduleStep<Input, Output>): Decision<Input, Output> =
      when (val decision = self(input)) {
        is Continue -> Done(decision.output)
        is Done -> Continue(decision.output, ZERO) { loop(it, self) }
      }

    return Schedule { input -> loop(input, step) }
  }

  /**
   * Combines with another schedule by combining the result and the delay of the [Decision] with the [zipContinue], [zipDuration] and a [zip] functions
   */
  public fun <A : Input, B, C> combine(
    other: Schedule<A, B>,
    zipContinue: suspend (cont: Boolean, otherCont: Boolean) -> Boolean,
    zipDuration: suspend (duration: Duration, otherDuration: Duration) -> Duration,
    zip: suspend (Output, B) -> C
  ): Schedule<A, C> {
    suspend fun loop(
      input: A,
      self: ScheduleStep<Input, Output>,
      that: ScheduleStep<A, B>
    ): Decision<A, C> {
      val left = self(input)
      val right = that(input)
      return when (left) {
        is Continue -> when (right) {
          is Continue ->
            if (zipContinue(true, true)) Continue(
              zip(left.output, right.output),
              zipDuration(left.delay, right.delay)
            ) { loop(it, left.next, right.next) }
            else Done(zip(left.output, right.output))

          is Done -> if (zipContinue(true, false)) Continue(zip(left.output, right.output), left.delay) {
            loop(
              it,
              left.next,
              that
            )
          }
          else Done(zip(left.output, right.output))
        }

        is Done -> when (right) {
          is Continue -> if (zipContinue(false, true)) Continue(zip(left.output, right.output), right.delay) {
            loop(
              it,
              self,
              right.next
            )
          }
          else Done(zip(left.output, right.output))

          is Done -> if (zipContinue(false, false)) Continue(
            zip(left.output, right.output),
            zipDuration(ZERO, ZERO)
          ) { loop(it, self, that) }
          else Done(zip(left.output, right.output))
        }
      }
    }

    return Schedule { input -> loop(input, step, other.step) }
  }

  /**
   * Combines with another schedule by combining the result and the delay of the [Decision] with the functions [zipContinue], [zipDuration] and a [zip] function
   */
  @ExperimentalTime
  @Deprecated(
    NanosDeprecation,
    ReplaceWith(
      "combine(other, zipContinue, { a, b -> zipDuration(a.toDouble(DurationUnit.NANOSECONDS), b.toDouble(DurationUnit.NANOSECONDS)).nanoseconds }, zip)",
      "kotlin.time.DurationUnit", "kotlin.time.Duration.Companion.nanoseconds"
    )
  )
  public fun <A : Input, B, C> combineNanos(
    other: Schedule<A, B>,
    zipContinue: (cont: Boolean, otherCont: Boolean) -> Boolean,
    zipDuration: (duration: Double, otherDuration: Double) -> Double,
    zip: (Output, B) -> C
  ): Schedule<A, C> =
    combine(
      other,
      zipContinue,
      { a, b -> zipDuration(a.toDouble(NANOSECONDS), b.toDouble(NANOSECONDS)).nanoseconds },
      zip
    )

  /**
   * Always retries a schedule regardless of the decision made prior to invoking this method.
   */
  public fun forever(): Schedule<Input, Output> {
    suspend fun loop(input: Input, self: ScheduleStep<Input, Output>): Decision<Input, Output> =
      when (val decision = self(input)) {
        is Continue -> Continue(decision.output, decision.delay) { loop(it, decision.next) }
        is Done -> Continue(decision.output, ZERO) { loop(it, self) }
      }

    return Schedule { loop(it, step) }
  }

  /**
   * Executes one schedule after the other. When the first schedule ends, it continues with the second.
   */
  public infix fun <A : Input, B> andThen(other: Schedule<A, B>): Schedule<A, Either<Output, B>> {
    suspend fun loop(input: A, self: ScheduleStep<A, B>): Decision<A, Either<Output, B>> =
      when (val decision = self(input)) {
        is Continue -> Continue(decision.output.right(), decision.delay) {
          loop(input, decision.next)
        }

        is Done -> Done(decision.output.right())
      }

    suspend fun loop(input: Input, self: ScheduleStep<Input, Output>): Decision<A, Either<Output, B>> =
      when (val decision = self(input)) {
        is Continue -> Continue(decision.output.left(), decision.delay) { loop(it, decision.next) }
        is Done -> Continue(decision.output.left(), ZERO) { loop(it, other.step) }
      }

    return Schedule { input -> loop(input, step) }
  }

  /**
   * Changes the delay of a resulting [Decision] based on the [Output] and the produced delay.
   */
  public fun modify(f: suspend (Output, Duration) -> Duration): Schedule<Input, Output> {
    suspend fun loop(input: Input, self: ScheduleStep<Input, Output>): Decision<Input, Output> =
      when (val decision = self(input)) {
        is Continue -> Continue(decision.output, f(decision.output, decision.delay)) { loop(it, decision.next) }
        is Done -> Done(decision.output)
      }
    return Schedule { input -> loop(input, step) }
  }

  @ExperimentalTime
  @Deprecated(
    NanosDeprecation,
    ReplaceWith(
      "modify { output, d -> f(output, d.toDouble(DurationUnit.NANOSECONDS)).nanoseconds }",
      "kotlin.time.DurationUnit", "kotlin.time.Duration.Companion.nanoseconds"
    )
  )
  public fun modifyNanos(f: suspend (Output, Double) -> Double): Schedule<Input, Output> =
    modify { output, d -> f(output, d.toDouble(NANOSECONDS)).nanoseconds }

  public fun log(f: suspend (Input, Output) -> Unit): Schedule<Input, Output> =
    check { input, output ->
      f(input, output)
      true
    }

  /**
   * Runs an effectful handler on every input. Does not alter the decision.
   */
  public fun logInput(f: suspend (input: Input) -> Unit): Schedule<Input, Output> =
    log { input, _ -> f(input) }

  /**
   * Runs an effectful handler on every output. Does not alter the decision.
   */
  public fun logOutput(f: suspend (output: Output) -> Unit): Schedule<Input, Output> =
    log { _, output -> f(output) }

  /**
   * Accumulates the results of a schedule by folding over them effectfully.
   */
  public fun <C> foldLazy(
    initial: suspend () -> C,
    f: suspend (acc: C, output: Output) -> C
  ): Schedule<Input, C> {
    suspend fun loop(input: Input, c: C, self: ScheduleStep<Input, Output>): Decision<Input, C> =
      when (val decision = self(input)) {
        is Continue -> Continue(c, decision.delay) { loop(it, f(c, decision.output), decision.next) }
        is Done -> Done(c)
      }
    return Schedule { input -> loop(input, initial(), step) }
  }

  /**
   * Composes this schedule with the other schedule by piping the output of this schedule into the input of the other.
   */
  public infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B> {
    suspend fun loop(
      input: Input,
      self: ScheduleStep<Input, Output>,
      other: ScheduleStep<Output, B>
    ): Decision<Input, B> =
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

  public fun duration(): Schedule<Input, Duration> {
    suspend fun loop(input: Input, self: ScheduleStep<Input, Output>): Decision<Input, Duration> =
      when (val decision = self(input)) {
        is Continue -> Continue(decision.delay, decision.delay) { loop(it, decision.next) }
        is Done -> Done(ZERO)
      }
    return Schedule { input -> loop(input, step) }
  }

  public fun decision(): Schedule<Input, Decision<Input, Output>> {
    suspend fun loop(input: Input, self: ScheduleStep<Input, Output>): Decision<Input, Decision<Input, Output>> =
      when (val decision = self(input)) {
        is Continue -> Continue(decision, decision.delay) { loop(it, decision.next) }
        is Done -> Done(decision)
      }
    return Schedule { input -> loop(input, step) }
  }

  /**
   * Combines two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  public infix fun <A, B> zip(other: Schedule<A, B>): Schedule<Pair<Input, A>, Pair<Output, B>> =
    zip(other, ::Pair)

  /**
   * Combines two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  public fun <A, B, C> zip(other: Schedule<A, B>, f: (Output, B) -> C): Schedule<Pair<Input, A>, C> {
    suspend fun loop(
      input: Pair<Input, A>,
      self: ScheduleStep<Input, Output>,
      other: ScheduleStep<A, B>
    ): Decision<Pair<Input, A>, C> =
      when (val decision = self(input.first)) {
        is Continue -> when (val decision2 = other(input.second)) {
          is Continue -> Continue(f(decision.output, decision2.output), decision.delay max decision2.delay) {
            loop(it, decision.next, decision2.next)
          }

          is Done -> Done(f(decision.output, decision2.output))
        }

        is Done -> Done(f(decision.output, other(input.second).output))
      }

    return Schedule { input -> loop(input, step, other.step) }
  }

  /**
   * Combines two schedules with different input and output and conditionally choose between the two.
   * Continues when the chosen schedule continues and uses the chosen schedules delay.
   */
  public infix fun <A, B> choose(other: Schedule<A, B>): Schedule<Either<Input, A>, Either<Output, B>> {
    suspend fun loop(
      input: Either<Input, A>,
      step: ScheduleStep<Input, Output>,
      other: ScheduleStep<A, B>
    ): Decision<Either<Input, A>, Either<Output, B>> = when (input) {
      is Left -> when(val dec = step(input.value)) {
        is Continue -> Continue(Left(dec.output), dec.delay) { loop(it, dec.next, other) }
        is Done -> Done(dec.output.left())
      }
      is Right -> when(val dec = other(input.value)) {
        is Continue -> Continue(Right(dec.output), dec.delay) { loop(it, step, dec.next) }
        is Done -> Done(dec.output.right())
      }
    }

    return Schedule { input -> loop(input, step, other.step) }
  }

  public fun void(): Schedule<Input, Unit> =
    map { }

  /**
   * Changes the result of a [Schedule] to always be [b].
   */
  public fun <B> const(b: B): Schedule<Input, B> =
    map { b }

  /**
   * Continues or stops the schedule based on the output.
   */
  public fun whileOutput(f: suspend (Output) -> Boolean): Schedule<Input, Output> =
    check { _, output -> f(output) }

  /**
   * Continues or stops the schedule based on the input.
   */
  public fun <A : Input> whileInput(f: suspend (A) -> Boolean): Schedule<A, Output> =
    check { input, _ -> f(input) }

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

  public fun <B, C> dimap(f: suspend (B) -> Input, g: (Output) -> C): Schedule<B, C> =
    contramap(f).map(g)

  /**
   * Combines two schedules. Continues only when both continue and chooses the maximum delay.
   */
  public infix fun <A : Input, B> and(other: Schedule<A, B>): Schedule<A, Pair<Output, B>> =
    combine(other, { a, b -> a && b }, { a, b -> a max b }, ::Pair)

  /**
   * Combines two schedules. Continues if one continues and chooses the minimum delay.
   */
  public infix fun <A : Input, B> or(other: Schedule<A, B>): Schedule<A, Pair<Output, B>> =
    combine(other, { a, b -> a || b }, { a, b -> a min b }, ::Pair)

  /**
   * Combines two schedules with [and] but throws away the left schedule's result.
   */
  public infix fun <A : Input, B> zipRight(other: Schedule<A, B>): Schedule<A, B> =
    (this and other).map { it.second }

  /**
   * Combines two schedules with [and] but throws away the right schedule's result.
   */
  public infix fun <A : Input, B> zipLeft(other: Schedule<A, B>): Schedule<A, Output> =
    (this and other).map { it.first }

  public fun delay(f: suspend (duration: Duration) -> Duration): Schedule<Input, Output> =
    modify { _, duration -> f(duration) }

  @ExperimentalTime
  @Deprecated(
    NanosDeprecation,
    ReplaceWith(
      "delay { f(it.toDouble(DurationUnit.NANOSECONDS)).nanoseconds }",
      "kotlin.time.DurationUnit", "kotlin.time.Duration.Companion.nanoseconds"
    )
  )
  public fun delayedNanos(f: suspend (duration: Double) -> Double): Schedule<Input, Output> =
    delay { f(it.toDouble(NANOSECONDS)).nanoseconds }

//  @Deprecated(
//    NanosDeprecation,
//    ReplaceWith(
//      "jittered(suspend { genRand().nanoseconds })",
//      "kotlin.time.Duration.Companion.nanoseconds"
//    )
//  )
//  public fun jittered(genRand: suspend () -> Double): Schedule<Input, Output> =
//    jittered(suspend { genRand().nanoseconds })

  public fun jittered(genRand: suspend () -> Duration): Schedule<Input, Output> =
    modify { _, duration ->
      val n = genRand.invoke()
      duration.times(n.toDouble(NANOSECONDS))
    }

  /**
   * Add random jitter to a schedule.
   *
   * By requiring Kotlin's [Random] as a parameter, this function is deterministic and testable.
   * The result returned by [Random.nextDouble] between 0.0 and 1.0 is multiplied with the current duration.
   */
  @ExperimentalTime
  public fun jittered(random: Random = Random.Default): Schedule<Input, Output> =
    jittered(suspend { random.nextDouble(0.0, 1.0).nanoseconds })

  public fun <C> fold(initial: C, f: suspend (acc: C, output: Output) -> C): Schedule<Input, C> =
    foldLazy(suspend { initial }) { acc, o -> f(acc, o) }

  /**
   * Accumulates the results of every execution into a list.
   */
  public fun collect(): Schedule<Input, List<Output>> =
    fold(emptyList()) { acc, o -> acc + listOf(o) }

  /**
   * Infix variant of pipe with reversed order.
   */
  public infix fun <B> compose(other: Schedule<B, Input>): Schedule<B, Output> =
    (other pipe this)

  // Dependent type emulation
//  @Suppress("UNCHECKED_CAST")
//  internal class ScheduleImpl<State, Input, Output>(
//    val initialState: suspend () -> State,
//    val update: suspend (a: Input, s: State) -> Decision<State, Output>
//  ) : Schedule<Input, Output>() {


//    override fun <A : Input> check(pred: suspend (input: A, output: Output) -> Boolean): Schedule<A, Output> =
//      updated { f ->
//        { a: A, s: State ->
//          val dec = f(a, s)
//          if (dec.cont) pred(a, dec.finish.value()).let { dec.copy(cont = it) }
//          else dec
//        }
//      }

//    override fun <A : Input, B, C> combine(
//      other: Schedule<A, B>,
//      zipContinue: (cont: Boolean, otherCont: Boolean) -> Boolean,
//      zipDuration: (duration: Duration, otherDuration: Duration) -> Duration,
//      zip: (Output, B) -> C
//    ): Schedule<A, C> = (other as ScheduleImpl<Any?, A, B>).let { o ->
//      ScheduleImpl(suspend { Pair(initialState.invoke(), o.initialState.invoke()) }) { i, s: Pair<State, Any?> ->
//        update(i, s.first).combine(o.update(i, s.second), zipContinue, zipDuration, zip)
//      }
//    }

//    override fun modify(f: suspend (output: Output, duration: Duration) -> Duration): Schedule<Input, Output> =
//      updated { update ->
//        { a: Input, s: State ->
//          val step = update(a, s)
//          val d = f(step.finish.value(), step.duration)
//          step.copy(duration = d)
//        }
//      }

//    override fun <C> foldLazy(initial: suspend () -> C, f: suspend (acc: C, output: Output) -> C): Schedule<Input, C> =
//      ScheduleImpl(suspend { Pair(initialState.invoke(), initial.invoke()) }) { i, s ->
//        val dec = update(i, s.first)
//        val c = if (dec.cont) f(s.second, dec.finish.value()) else s.second
//        dec.bimap({ state -> Pair(state, c) }, { c })
//      }

//    @Suppress("NAME_SHADOWING")
//    override infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B> =
//      (other as ScheduleImpl<Any?, Output, B>).let { other ->
//        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
//          val dec1 = update(i, s.first)
//          val dec2 = other.update(dec1.finish.value(), s.second)
//          dec1.combine(dec2, { a, b -> a && b }, { a, b -> a + b }, { _, b -> b })
//        }
//      }

//    @Suppress("NAME_SHADOWING")
//    override fun <A, B, C> zip(other: Schedule<A, B>, f: (Output, B) -> C): Schedule<Pair<Input, A>, C> =
//      (other as ScheduleImpl<Any?, A, B>).let { other ->
//        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
//          val dec1 = update(i.first, s.first)
//          val dec2 = other.update(i.second, s.second)
//          dec1.combine(dec2, { a, b -> a && b }, { a, b -> a max b }, f)
//        }
//      }

//    @Suppress("NAME_SHADOWING")
//    override infix fun <A, B> choose(other: Schedule<A, B>): Schedule<Either<Input, A>, Either<Output, B>> =
//      (other as ScheduleImpl<Any?, A, B>).let { other ->
//        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
//          i.fold(
//            {
//              update(it, s.first).mapLeft { state -> Pair(state, s.second) }.map { output -> output.left() }
//            },
//            {
//              other.update(it, s.second).mapLeft { otherState -> Pair(s.first, otherState) }
//                .map { otherOutput -> otherOutput.right() }
//            }
//          )
//        }
//      }


  /** A decision:
   *  - To [Continue] with a delay, the next [Output] and the [ScheduleStep].
   *  - To stop with an [Output]
   */
  public sealed interface Decision<in Input, out Output> {
    public val output: Output

    public data class Done<Output>(override val output: Output) : Decision<Any?, Output>
    public data class Continue<in Input, out Output>(
      override val output: Output,
      val delay: Duration,
      val next: ScheduleStep<Input, Output>
    ) : Decision<Input, Output>
  }

  public companion object {

    /**
     * Invoke constructor to manually define a schedule. If you need this, please consider adding it to Arrow or suggest
     *  a change to avoid using this manual method.
     */
    public operator fun <S, A, B> invoke(
      initial: suspend () -> S,
      update: suspend (input: A, state: S) -> Decision<S, B>
    ): Schedule<A, B> = TODO()

    /**
     * Creates a Schedule that continues without delay and just returns its input.
     */
    public fun <A> identity(): Schedule<A, A> {
      fun loop(input: A): Decision<A, A> = Continue(input, ZERO) { loop(it) }
      return Schedule { loop(it) }
    }

    /**
     * Creates a Schedule that continues without delay and always returns Unit.
     */
    public fun <A> unit(): Schedule<A, Unit> = identity<A>().void()

    /**
     * Creates a schedule that unfolds effectfully using a seed value [c] and a unfold function [f].
     * This keeps the current state (the current seed) as state and runs the unfold function on every
     *  call to update. This schedule always continues without delay and returns the current state.
     */
    public fun <I, A> unfoldLazy(c: suspend () -> A, f: suspend (A) -> A): Schedule<I, A> {
      suspend fun loop(input: A): Decision<I, A> = Continue(input, ZERO) { loop(f(input)) }
      return Schedule { _ -> loop(c()) }
    }

    /**
     * Non-effectful variant of [unfoldLazy]
     */
    public fun <I, A> unfold(c: A, f: suspend (A) -> A): Schedule<I, A> =
      unfoldLazy(suspend { c }) { f(it) }

    /**
     * Creates a Schedule that continues forever and returns the number of iterations.
     */
    public fun <A> forever(): Schedule<A, Int> =
      unfold(0) { it + 1 }

    /**
     * Creates a Schedule that continues [n] times and returns the number of iterations.
     */
    public fun <A> recurs(n: Long): Schedule<A, Long> {
      fun loop(input: Long): Decision<A, Long> =
        if (input < n) Continue(input, ZERO) { loop(input + 1) } else Done(input)

      return Schedule { loop(0L) }
    }

    /**
     * Creates a Schedule that only retries once.
     */
    public fun <A> once(): Schedule<A, Unit> =
      recurs<A>(1).void()

    /**
     * Creates a schedule that never retries.
     *
     * Note that this will hang a program if used as a repeat/retry schedule unless cancelled.
     */
    public fun <A> never(): Schedule<A, Nothing> =
      Schedule { _ -> awaitCancellation() }

    /**
     * Creates a Schedule that uses another Schedule to generate the delay of this schedule.
     * Continues for as long as [delaySchedule] continues and adds the output of [delaySchedule] to
     *  the delay that [delaySchedule] produced. Also returns the full delay as output.
     *
     *  The Schedule [delaySchedule] is should specify the delay in nanoseconds.
     *
     * A common use case is to define a unfolding schedule and use the result to change the delay.
     *  For an example see the implementation of [spaced], [linear], [fibonacci] or [exponential]
     */
    @JvmName("delayedNanos")
    @ExperimentalTime
    @Deprecated(
      NanosDeprecation,
      ReplaceWith(
        "delayed(delaySchedule.map { it.nanoseconds }).map { it.toDouble(DurationUnit.NANOSECONDS) }",
        "kotlin.time.DurationUnit", "kotlin.time.Duration.Companion.nanoseconds"
      )
    )
    public fun <A> delayed(delaySchedule: Schedule<A, Double>): Schedule<A, Double> =
      delayed(delaySchedule.map { it.nanoseconds }).map { it.toDouble(NANOSECONDS) }

    /**
     * Creates a Schedule that uses another Schedule to generate the delay of this schedule.
     * Continues for as long as [delaySchedule] continues and adds the output of [delaySchedule] to
     *  the delay that [delaySchedule] produced. Also returns the full delay as output.
     *
     * A common use case is to define an unfolding schedule and use the result to change the delay.
     *  For an example see the implementation of [spaced], [linear], [fibonacci] or [exponential]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <A> delayed(delaySchedule: Schedule<A, Duration>): Schedule<A, Duration> {
      suspend fun loop(input: A, step: ScheduleStep<A, Duration>): Decision<A, Duration> =
        when (val decision = step(input)) {
          is Continue -> Continue(decision.delay, decision.delay) { loop(input, step) }
          is Done -> Done(ZERO)
        }

      return Schedule { loop(it, delaySchedule.step) }
    }

    /**
     * Creates a Schedule which collects all its inputs in a list.
     */
    public fun <A> collect(): Schedule<A, List<A>> =
      identity<A>().collect()

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

    /**
     * Creates a Schedule with an effectful handler on the input.
     */
    public fun <A> logInput(f: suspend (A) -> Unit): Schedule<A, A> =
      identity<A>().logInput(f)

    /**
     * Creates a Schedule with an effectful handler on the output.
     */
    public fun <A> logOutput(f: suspend (A) -> Unit): Schedule<A, A> =
      identity<A>().logOutput(f)

    @ExperimentalTime
    @Deprecated(
      NanosDeprecation,
      ReplaceWith(
        "duration<A>().map { it.toDouble(DurationUnit.NANOSECONDS) }",
        "kotlin.time.DurationUnit"
      )
    )
    public fun <A> delayInNanos(): Schedule<A, Double> =
      duration<A>().map { it.toDouble(NANOSECONDS) }

    @Suppress("UNCHECKED_CAST")
    public fun <A> duration(): Schedule<A, Duration> =
      identity<A>().duration()

    /**
     * Creates a Schedule that returns its decisions.
     */
    @Suppress("UNCHECKED_CAST")
    @ExperimentalTime
    public fun <A> decision(): Schedule<A, Boolean> =
      identity<A>().decision().map { it is Continue }

    /**
     * Creates a Schedule that continues with a fixed delay.
     *
     * @param interval fixed delay in nanoseconds
     */
    @ExperimentalTime
    @Deprecated(
      NanosDeprecation,
      ReplaceWith(
        "spaced(interval.nanoseconds)",
        "kotlin.time.Duration.Companion.nanoseconds"
      )
    )
    public fun <A> spaced(interval: Double): Schedule<A, Int> =
      spaced(interval.nanoseconds)

    /**
     * Creates a Schedule that continues with a fixed delay.
     *
     * @param interval fixed delay in [Duration]
     */
    public fun <A> spaced(interval: Duration): Schedule<A, Int> =
      forever<A>().delay { d -> d + interval }

    /**
     * Creates a Schedule that continues with increasing delay by adding the last two delays.
     *
     * @param one initial delay in nanoseconds
     */
    @ExperimentalTime
    @Deprecated(
      NanosDeprecation,
      ReplaceWith(
        "fibonacci(one.nanoseconds).map { it.toDouble(DurationUnit.NANOSECONDS) }",
        "kotlin.time.DurationUnit", "kotlin.time.Duration.Companion.nanoseconds"
      )
    )
    public fun <A> fibonacci(one: Double): Schedule<A, Double> =
      fibonacci<A>(one.nanoseconds).map { it.toDouble(NANOSECONDS) }

    /**
     * Creates a Schedule that continues with increasing delay by adding the last two delays.
     */
    public fun <A> fibonacci(one: Duration): Schedule<A, Duration> =
      delayed(
        unfold<A, Pair<Duration, Duration>>(Pair(ZERO, one)) { (del, acc) ->
          Pair(acc, del + acc)
        }.map { it.first }
      )

    /**
     * Creates a Schedule which increases its delay linearly, by n * base where n is the number of executions.
     *
     * @param base the base delay in nanoseconds
     */
    @ExperimentalTime
    @Deprecated(
      NanosDeprecation,
      ReplaceWith(
        "linear(base.nanoseconds).map { it.toDouble(DurationUnit.NANOSECONDS) }",
        "kotlin.time.DurationUnit", "kotlin.time.Duration.Companion.nanoseconds"
      )
    )
    public fun <A> linear(base: Double): Schedule<A, Double> =
      linear<A>(base.nanoseconds).map { it.toDouble(NANOSECONDS) }

    /**
     * Creates a Schedule which increases its delay linearly, by n * base where n is the number of executions.
     */
    public fun <A> linear(base: Duration): Schedule<A, Duration> =
      delayed(forever<A>().map { base * it })

    /**
     * Creates a Schedule that increases its delay exponentially with a given factor and base.
     * Delays can be calculated as [base] * factor ^ n where n is the number of executions.
     *
     * @param base the base delay in nanoseconds
     */
    @ExperimentalTime
    @Deprecated(
      NanosDeprecation,
      ReplaceWith(
        "exponential(base.nanoseconds).map { it.toDouble(DurationUnit.NANOSECONDS) }",
        "kotlin.time.DurationUnit", "kotlin.time.Duration.Companion.nanoseconds"
      )
    )
    public fun <A> exponential(base: Double, factor: Double = 2.0): Schedule<A, Double> =
      exponential<A>(base.nanoseconds).map { it.toDouble(NANOSECONDS) }

    /**
     * Creates a Schedule that increases its delay exponentially with a given factor and base.
     * Delays can be calculated as [base] * factor ^ n where n is the number of executions.
     */
    public fun <A> exponential(base: Duration, factor: Double = 2.0): Schedule<A, Duration> =
      delayed(forever<A>().map { base * factor.pow(it).roundToInt() })
  }
}

public typealias ScheduleStep<Input, Output> =
  suspend (Input) -> Schedule.Decision<Input, Output>

/**
 * Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay.
 * Returns the result of the effect if if it was successful or re-raises the last error encountered when the schedule ends.
 */
public suspend fun <A, B> Schedule<Throwable, B>.retry(fa: suspend () -> A): A =
  retryOrElse(fa) { e, _ -> throw e }

/**
 * Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
public suspend fun <A, B> Schedule<Throwable, B>.retryOrElse(
  fa: suspend () -> A,
  orElse: suspend (Throwable, B) -> A
): A =
  retryOrElseEither(fa, orElse).fold(::identity, ::identity)

/**
 * Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
@Suppress("UNCHECKED_CAST")

public suspend fun <A, B, C> Schedule<Throwable, B>.retryOrElseEither(
  fa: suspend () -> A,
  orElse: suspend (Throwable, B) -> C
): Either<C, A> {
  var step: ScheduleStep<Throwable, B> = step

  while (true) {
    currentCoroutineContext().ensureActive()
    try {
      return Right(fa.invoke())
    } catch (e: Throwable) {
      when (val decision = step(e)) {
        is Continue -> {
          if (decision.delay != ZERO) delay(decision.delay)
          step = decision.next
        }

        is Done -> return Left(orElse(e.nonFatalOrThrow(), decision.output))
      }
    }
  }
}

private infix fun Duration.max(other: Duration): Duration =
  if (this >= other) this else other

private infix fun Duration.min(other: Duration): Duration =
  if (this <= other) this else other

public const val NanosDeprecation: String =
  "Please prefer Duration-based APIs over those based on nanoseconds."
