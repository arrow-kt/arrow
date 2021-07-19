package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Eval
import arrow.core.identity
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.fx.coroutines.Schedule.ScheduleImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds

/**
 * # Retrying and repeating effects
 *
 * A common demand when working with effects is to retry or repeat them when certain circumstances happen. Usually, the retrial or repetition does not happen right away; rather, it is done based on a policy. For instance, when fetching content from a network request, we may want to retry it when it fails, using an exponential backoff algorithm, for a maximum of 15 seconds or 5 attempts, whatever happens first.
 *
 * [Schedule] allows you to define and compose powerful yet simple policies, which can be used to either repeat or retry computation.
 *
 * > [Schedule] has been derived from Scala ZIO's [Schedule](https://zio.dev/docs/datatypes/datatypes_schedule) datatype and has been adapted to kotlin.
 *
 * The two core methods of running a schedule are:
 * - __retry__: The effect is executed once, and if it fails, it will be reattempted based on the scheduling policy passed as an argument. It will stop if the effect ever succeeds, or the policy determines it should not be reattempted again.
 * - __repeat__: The effect is executed once, and if it succeeds, it will be executed again based on the scheduling policy passed as an argument. It will stop if the effect ever fails, or the policy determines it should not be executed again. It will return the last internal state of the scheduling policy, or the error that happened running the effect.
 *
 * ## Constructing a policy:
 *
 * Constructing a simple schedule which recurs 10 times until it succeeds:
 * ```kotlin:ank
 * import arrow.fx.coroutines.*
 *
 * fun <A> recurTenTimes() = Schedule.recurs<A>(10)
 * ```
 *
 * A more complex schedule
 *
 * ```kotlin:ank
 * import kotlin.time.seconds
 * import kotlin.time.milliseconds
 * import kotlin.time.ExperimentalTime
 * import arrow.fx.coroutines.*
 *
 * @ExperimentalTime
 * fun <A> complexPolicy(): Schedule<A, List<A>> =
 *   Schedule.exponential<A>(10.milliseconds).whileOutput { it.inNanoseconds < 60.seconds.inNanoseconds }
 *     .andThen(Schedule.spaced<A>(60.seconds) and Schedule.recurs(100)).jittered()
 *     .zipRight(Schedule.identity<A>().collect())
 * ```
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
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
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
 *
 * However, when running this new effect, its output will be the number of iterations it has performed, as stated in the documentation of the function. Also notice that we did not handle the error case, there are overloads [repeatOrElse] and [repeatOrElseEither] which offer that capability, [repeat] will just rethrow any error encountered.
 *
 * If we want to discard the values provided by the repetition of the effect, we can combine our policy with [Schedule.unit], using the [zipLeft] or [zipRight] combinators, which will keep just the output of one of the policies:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
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
 *
 * Following the same strategy, we can zip it with the [Schedule.identity] policy to keep only the last provided result by the effect.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
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
 *
 * Finally, if we want to keep all intermediate results, we can zip the policy with [Schedule.collect]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
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
 *
 * ## Repeating an effect until/while it produces a certain value
 *
 * We can make use of the policies doWhile or doUntil to repeat an effect while or until its produced result matches a given predicate.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
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
 *
 * ## Exponential backoff retries
 *
 * A common algorithm to retry effectful operations, as network requests, is the exponential backoff algorithm. There is a scheduling policy that implements this algorithm and can be used as:
 *
 * ```kotlin:ank
 * import kotlin.time.milliseconds
 * import kotlin.time.ExperimentalTime
 * import arrow.fx.coroutines.*
 *
 * @ExperimentalTime
 * val exponential = Schedule.exponential<Unit>(250.milliseconds)
 * ```
 */
public sealed class Schedule<Input, Output> {

  public abstract suspend fun <C> repeatOrElseEither(
    fa: suspend () -> Input,
    orElse: suspend (Throwable, Output?) -> C
  ): Either<C, Output>

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

  /**
   * Changes the output of a schedule. Does not alter the decision of the schedule.
   */
  public abstract fun <B> map(f: (output: Output) -> B): Schedule<Input, B>

  /**
   * Changes the input of the schedule. May alter a schedule's decision if it is based on input.
   */
  public abstract fun <B> contramap(f: suspend (B) -> Input): Schedule<B, Output>

  /**
   * Conditionally checks on both the input and the output whether or not to continue.
   */
  public abstract fun <A : Input> check(pred: suspend (input: A, output: Output) -> Boolean): Schedule<A, Output>

  /**
   * Inverts the decision of a schedule.
   */
  public abstract operator fun not(): Schedule<Input, Output>

  /**
   * Combines with another schedule by combining the result and the delay of the [Decision] with the [zipContinue], [zipDuration] and a [zip] functions
   */
  @ExperimentalTime
  public fun <A : Input, B, C> combine(
    other: Schedule<A, B>,
    zipContinue: (cont: Boolean, otherCont: Boolean) -> Boolean,
    zipDuration: (duration: Duration, otherDuration: Duration) -> Duration,
    zip: (Output, B) -> C
  ): Schedule<A, C> =
    combineNanos(other, zipContinue, { a, b -> zipDuration(a.nanoseconds, b.nanoseconds).inNanoseconds }, zip)

  /**
   * Combines with another schedule by combining the result and the delay of the [Decision] with the functions [zipContinue], [zipDuration] and a [zip] function
   */
  public abstract fun <A : Input, B, C> combineNanos(
    other: Schedule<A, B>,
    zipContinue: (cont: Boolean, otherCont: Boolean) -> Boolean,
    zipDuration: (duration: Double, otherDuration: Double) -> Double,
    zip: (Output, B) -> C
  ): Schedule<A, C>

  /**
   * Always retries a schedule regardless of the decision made prior to invoking this method.
   */
  public abstract fun forever(): Schedule<Input, Output>

  /**
   * Executes one schedule after the other. When the first schedule ends, it continues with the second.
   */
  public abstract infix fun <A : Input, B> andThen(other: Schedule<A, B>): Schedule<A, Either<Output, B>>

  /**
   * Changes the delay of a resulting [Decision] based on the [Output] and the produced delay.
   *
   */
  @ExperimentalTime
  public fun modify(f: suspend (Output, Duration) -> Duration): Schedule<Input, Output> =
    modifyNanos { output, d -> f(output, d.nanoseconds).inNanoseconds }

  public abstract fun modifyNanos(f: suspend (Output, Double) -> Double): Schedule<Input, Output>

  /**
   * Runs an effectful handler on every input. Does not alter the decision.
   */
  public abstract fun logInput(f: suspend (input: Input) -> Unit): Schedule<Input, Output>

  /**
   * Runs an effectful handler on every output. Does not alter the decision.
   */
  public abstract fun logOutput(f: suspend (output: Output) -> Unit): Schedule<Input, Output>

  /**
   * Accumulates the results of a schedule by folding over them effectfully.
   */
  public abstract fun <C> foldLazy(initial: suspend () -> C, f: suspend (acc: C, output: Output) -> C): Schedule<Input, C>

  /**
   * Composes this schedule with the other schedule by piping the output of this schedule
   *  into the input of the other.
   */
  public abstract infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B>

  /**
   * Combines two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  public infix fun <A, B> zip(other: Schedule<A, B>): Schedule<Pair<Input, A>, Pair<Output, B>> =
    zip(other, ::Pair)

  /**
   * Combines two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  public abstract fun <A, B, C> zip(other: Schedule<A, B>, f: (Output, B) -> C): Schedule<Pair<Input, A>, C>

  /**
   * Combines two schedules with different input and output and conditionally choose between the two.
   * Continues when the chosen schedule continues and uses the chosen schedules delay.
   */
  public abstract infix fun <A, B> choose(other: Schedule<A, B>): Schedule<Either<Input, A>, Either<Output, B>>

  public fun void(): Schedule<Input, Unit> =
    map { Unit }

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
   * `untilOutput(f) = whileOutput(f).not()`
   */
  public fun untilOutput(f: suspend (Output) -> Boolean): Schedule<Input, Output> =
    !whileOutput(f)

  /**
   * `untilInput(f) = whileInput(f).not()`
   */
  public fun <A : Input> untilInput(f: suspend (A) -> Boolean): Schedule<A, Output> =
    !whileInput(f)

  public fun <B, C> dimap(f: suspend (B) -> Input, g: (Output) -> C): Schedule<B, C> =
    contramap(f).map(g)

  /**
   * Combines two schedules. Continues only when both continue and chooses the maximum delay.
   */
  public infix fun <A : Input, B> and(other: Schedule<A, B>): Schedule<A, Pair<Output, B>> =
    combineNanos(other, { a, b -> a && b }, { a, b -> max(a, b) }, ::Pair)

  /**
   * Combines two schedules. Continues if one continues and chooses the minimum delay.
   */
  public infix fun <A : Input, B> or(other: Schedule<A, B>): Schedule<A, Pair<Output, B>> =
    combineNanos(other, { a, b -> a || b }, { a, b -> min(a, b) }, ::Pair)

  /**
   * Combines two schedules with [and] but throws away the left schedule's result.
   */
  public infix fun <A : Input, B> zipRight(other: Schedule<A, B>): Schedule<A, B> =
    (this and other).map(Pair<Output, B>::second)

  /**
   * Combines two schedules with [and] but throws away the right schedule's result.
   */
  public infix fun <A : Input, B> zipLeft(other: Schedule<A, B>): Schedule<A, Output> =
    (this and other).map(Pair<Output, B>::first)

  @ExperimentalTime
  public fun delay(f: suspend (duration: Duration) -> Duration): Schedule<Input, Output> =
    modify { _, duration -> f(duration) }

  public fun delayedNanos(f: suspend (duration: Double) -> Double): Schedule<Input, Output> =
    modifyNanos { _, duration -> f(duration) }

  public fun jittered(genRand: suspend () -> Double): Schedule<Input, Output> =
    modifyNanos { _, duration ->
      val n = genRand.invoke()
      (duration * n)
    }

  @JvmName("jitteredDuration")
  @ExperimentalTime
  public fun jittered(genRand: suspend () -> Duration): Schedule<Input, Output> =
    modify { _, duration ->
      val n = genRand.invoke()
      duration.times(n.inNanoseconds)
    }

  /**
   * Add random jitter to a schedule.
   *
   * By requiring Kotlin's [Random] as a parameter, this function is deterministic and testable.
   * The result returned by [Random.nextDouble] between 0.0 and 1.0 is multiplied with the current duration.
   */
  public fun jittered(random: Random = Random.Default): Schedule<Input, Output> =
    jittered(suspend { random.nextDouble(0.0, 1.0) })

  /**
   * Non-effectful version of [foldM].
   */
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
  @Suppress("UNCHECKED_CAST")
  internal class ScheduleImpl<State, Input, Output>(
    val initialState: suspend () -> State,
    val update: suspend (a: Input, s: State) -> Decision<State, Output>
  ) : Schedule<Input, Output>() {

    public override suspend fun <C> repeatOrElseEither(
      fa: suspend () -> Input,
      orElse: suspend (Throwable, Output?) -> C
    ): Either<C, Output> {
      var last: (() -> Output)? = null // We haven't seen any input yet
      var state: State = initialState.invoke()

      while (true) {
        coroutineContext.ensureActive()
        try {
          val a = fa.invoke()
          val step = update(a, state)
          if (!step.cont) return Either.Right(step.finish.value())
          else {
            delay((step.delayInNanos / 1_000_000).toLong())

            // Set state before looping again
            last = { step.finish.value() }
            state = step.state
          }
        } catch (e: Throwable) {
          return Either.Left(orElse(e.nonFatalOrThrow(), last?.invoke()))
        }
      }
    }

    override fun <B> map(f: (output: Output) -> B): Schedule<Input, B> =
      ScheduleImpl(initialState) { i, s -> update(i, s).map(f) }

    override fun <B> contramap(f: suspend (B) -> Input): Schedule<B, Output> =
      ScheduleImpl(initialState) { i, s -> update(f(i), s) }

    override fun <A : Input> check(pred: suspend (input: A, output: Output) -> Boolean): Schedule<A, Output> =
      updated { f ->
        { a: A, s: State ->
          val dec = f(a, s)
          if (dec.cont) pred(a, dec.finish.value()).let { dec.copy(cont = it) }
          else dec
        }
      }

    override fun <A : Input, B, C> combineNanos(
      other: Schedule<A, B>,
      zipContinue: (cont: Boolean, otherCont: Boolean) -> Boolean,
      zipDuration: (duration: Double, otherDuration: Double) -> Double,
      zip: (Output, B) -> C
    ): Schedule<A, C> = (other as ScheduleImpl<Any?, A, B>).let { other ->
      ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s: Pair<State, Any?> ->
        update(i, s.first).combineNanos(other.update(i, s.second), zipContinue, zipDuration, zip)
      }
    }

    override fun forever(): Schedule<Input, Output> = updated { f ->
      { a: Input, s: State ->
        val dec = f(a, s)
        if (dec.cont) dec
        else {
          val state = this@ScheduleImpl.initialState.invoke()
          dec.copy(cont = true, state = state)
        }
      }
    }

    override operator fun not(): Schedule<Input, Output> =
      updated { f ->
        { a: Input, s: State ->
          !f(a, s)
        }
      }

    override fun <A : Input, B> andThen(other: Schedule<A, B>): Schedule<A, Either<Output, B>> =
      ScheduleImpl<Either<State, Any?>, A, Either<Output, B>>(suspend { Either.Left(initialState.invoke()) }) { i, s ->
        (other as ScheduleImpl<Any?, A, B>)
        s.fold(
          { state ->
            val dec = this@ScheduleImpl.update(i, state)
            if (dec.cont) dec.bimap({ it.left() }, { it.left() })
            else {
              val newState = other.initialState.invoke()
              val newDec = other.update(i, newState)
              newDec.bimap({ it.right() }, { it.right() })
            }
          },
          { state ->
            other.update(i, state).bimap({ it.right() }, { it.right() })
          }
        )
      }

    override fun modifyNanos(f: suspend (output: Output, duration: Double) -> Double): Schedule<Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          val step = update(a, s)
          val d = f(step.finish.value(), step.delayInNanos)
          step.copy(delayInNanos = d)
        }
      }

    override fun logInput(f: suspend (input: Input) -> Unit): Schedule<Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          update(a, s).also { f(a) }
        }
      }

    override fun logOutput(f: suspend (output: Output) -> Unit): Schedule<Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          update(a, s).also { f(it.finish.value()) }
        }
      }

    override fun <C> foldLazy(initial: suspend () -> C, f: suspend (acc: C, output: Output) -> C): Schedule<Input, C> =
      ScheduleImpl(suspend { Pair(initialState.invoke(), initial.invoke()) }) { i, s ->
        val dec = update(i, s.first)
        val c = if (dec.cont) f(s.second, dec.finish.value()) else s.second
        dec.bimap({ state -> Pair(state, c) }, { c })
      }

    @Suppress("NAME_SHADOWING")
    override infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B> =
      (other as ScheduleImpl<Any?, Output, B>).let { other ->
        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
          val dec1 = update(i, s.first)
          val dec2 = other.update(dec1.finish.value(), s.second)
          dec1.combineNanos(dec2, { a, b -> a && b }, { a, b -> a + b }, { _, b -> b })
        }
      }

    @Suppress("NAME_SHADOWING")
    override fun <A, B, C> zip(other: Schedule<A, B>, f: (Output, B) -> C): Schedule<Pair<Input, A>, C> =
      (other as ScheduleImpl<Any?, A, B>).let { other ->
        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
          val dec1 = update(i.first, s.first)
          val dec2 = other.update(i.second, s.second)
          dec1.combineNanos(dec2, { a, b -> a && b }, { a, b -> max(a, b) }, f)
        }
      }

    @Suppress("NAME_SHADOWING")
    override infix fun <A, B> choose(other: Schedule<A, B>): Schedule<Either<Input, A>, Either<Output, B>> =
      (other as ScheduleImpl<Any?, A, B>).let { other ->
        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
          i.fold(
            {
              update(it, s.first).mapLeft { state -> Pair(state, s.second) }.map { output -> output.left() }
            },
            {
              other.update(it, s.second).mapLeft { otherState -> Pair(s.first, otherState) }
                .map { otherOutput -> otherOutput.right() }
            }
          )
        }
      }

    /**
     * Schedule state machine update function
     */
    fun <A : Input, B> updated(
      f: (suspend (input: A, state: State) -> Decision<State, Output>) -> (suspend (input: A, state: State) -> Decision<State, B>)
    ): Schedule<A, B> = ScheduleImpl(initialState) { a, s ->
      f { input, state -> update(input, state) }(a, s)
    }

    /**
     * Inspect and change the [Decision] of a [Schedule]. Also given access to the input.
     */
    fun <A : Input, B> reconsider(f: suspend (input: A, desision: Decision<State, Output>) -> Decision<State, B>): Schedule<A, B> =
      updated { update ->
        { a: A, s: State ->
          val dec = update(a, s)
          f(a, dec)
        }
      }

    /**
     * Run an effect with a [Decision]. Does not alter the decision.
     */
    fun <A : Input> onDecision(fa: suspend (input: A, decision: Decision<State, Output>) -> Unit): Schedule<A, Output> =
      updated { f ->
        { a: A, s: State ->
          f(a, s).also { fa(a, it) }
        }
      }
  }

  /**
   * A single decision. Contains the decision to continue, the delay, the new state and the (lazy) result of a Schedule.
   */
  public data class Decision<out A, out B>(val cont: Boolean, val delayInNanos: Double, val state: A, val finish: Eval<B>) {

    @ExperimentalTime
    val duration: Duration
      get() = delayInNanos.nanoseconds

    public operator fun not(): Decision<A, B> =
      copy(cont = !cont)

    public fun <C, D> bimap(f: (A) -> C, g: (B) -> D): Decision<C, D> =
      Decision(cont, delayInNanos, f(state), finish.map(g))

    public fun <C> mapLeft(f: (A) -> C): Decision<C, B> =
      bimap(f, ::identity)

    public fun <D> map(g: (B) -> D): Decision<A, D> =
      bimap(::identity, g)
    public fun <C, D, E> combineNanos(
      other: Decision<C, D>,
      f: (Boolean, Boolean) -> Boolean,
      g: (Double, Double) -> Double,
      zip: (B, D) -> E
    ): Decision<Pair<A, C>, E> = Decision(
      f(cont, other.cont),
      g(delayInNanos, other.delayInNanos),
      Pair(state, other.state),
      finish.flatMap { first -> other.finish.map { second -> zip(first, second) } }
    )

    @ExperimentalTime
    public fun <C, D, E> combine(
      other: Decision<C, D>,
      f: (Boolean, Boolean) -> Boolean,
      g: (Duration, Duration) -> Duration,
      zip: (B, D) -> E
    ): Decision<Pair<A, C>, E> = Decision(
      f(cont, other.cont),
      g(delayInNanos.nanoseconds, other.delayInNanos.nanoseconds).inNanoseconds,
      Pair(state, other.state),
      finish.flatMap { first -> other.finish.map { second -> zip(first, second) } }
    )

    override fun equals(other: Any?): Boolean =
      if (other !is Decision<*, *>) false
      else cont == other.cont &&
        state == other.state &&
        delayInNanos == other.delayInNanos &&
        finish.value() == other.finish.value()

    public companion object {
      public fun <A, B> cont(d: Double, a: A, b: Eval<B>): Decision<A, B> =
        Decision(true, d, a, b)

      public fun <A, B> done(d: Double, a: A, b: Eval<B>): Decision<A, B> =
        Decision(false, d, a, b)

      @ExperimentalTime
      public fun <A, B> cont(d: Duration, a: A, b: Eval<B>): Decision<A, B> =
        cont(d.inNanoseconds, a, b)

      @ExperimentalTime
      public fun <A, B> done(d: Duration, a: A, b: Eval<B>): Decision<A, B> =
        done(d.inNanoseconds, a, b)
    }
  }

  public companion object {

    /**
     * Invoke constructor to manually define a schedule. If you need this, please consider adding it to Arrow or suggest
     *  a change to avoid using this manual method.
     */
    public operator fun <S, A, B> invoke(
      initial: suspend () -> S,
      update: suspend (input: A, state: S) -> Decision<S, B>
    ): Schedule<A, B> = ScheduleImpl(initial, update)

    /**
     * Creates a Schedule that continues without delay and just returns its input.
     */
    public fun <A> identity(): Schedule<A, A> =
      Schedule({ Unit }) { a, s ->
        Decision.cont(0.0, s, Eval.now(a))
      }

    /**
     * Creates a Schedule that continues without delay and always returns Unit.
     */
    public fun <A> unit(): Schedule<A, Unit> =
      identity<A>().void()

    /**
     * Creates a schedule that unfolds effectfully using a seed value [c] and a unfold function [f].
     * This keeps the current state (the current seed) as [State] and runs the unfold function on every
     *  call to update. This schedule always continues without delay and returns the current state.
     */
    public fun <I, A> unfoldLazy(c: suspend () -> A, f: suspend (A) -> A): Schedule<I, A> =
      Schedule(c) { _: I, acc ->
        val a = f(acc)
        Decision.cont(0.0, a, Eval.now(a))
      }

    /**
     * Non-effectful variant of [unfoldLazy]
     */
    public fun <I, A> unfold(c: A, f: (A) -> A): Schedule<I, A> =
      unfoldLazy(suspend { c }) { f(it) }

    /**
     * Creates a Schedule that continues forever and returns the number of iterations.
     */
    public fun <A> forever(): Schedule<A, Int> =
      unfold(0) { it + 1 }

    /**
     * Creates a Schedule that continues n times and returns the number of iterations.
     */
    public fun <A> recurs(n: Int): Schedule<A, Int> =
      Schedule(suspend { 0 }) { _: A, acc ->
        if (acc < n) Decision.cont(0.0, acc + 1, Eval.now(acc + 1))
        else Decision.done(0.0, acc, Eval.now(acc))
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
      Schedule(suspend { arrow.fx.coroutines.never<Unit>() }) { _, _ ->
        Decision(false, 0.0, Unit, Eval.later { throw IllegalArgumentException("Impossible") })
      }

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
    @Suppress("UNCHECKED_CAST")
    @JvmName("delayedNanos")
    public fun <A> delayed(delaySchedule: Schedule<A, Double>): Schedule<A, Double> =
      (delaySchedule.modifyNanos { a, b -> a + b } as ScheduleImpl<Any?, A, Double>)
        .reconsider { _, dec -> dec.copy(finish = Eval.now(dec.delayInNanos)) }

    /**
     * Creates a Schedule that uses another Schedule to generate the delay of this schedule.
     * Continues for as long as [delaySchedule] continues and adds the output of [delaySchedule] to
     *  the delay that [delaySchedule] produced. Also returns the full delay as output.
     *
     * A common use case is to define a unfolding schedule and use the result to change the delay.
     *  For an example see the implementation of [spaced], [linear], [fibonacci] or [exponential]
     */
    @ExperimentalTime
    @JvmName("delayedDuration")
    public fun <A> delayed(delaySchedule: Schedule<A, Duration>): Schedule<A, Duration> =
      (delaySchedule.modify { a, b -> a + b } as ScheduleImpl<Any?, A, Duration>)
        .reconsider { _, dec -> dec.copy(finish = Eval.now(dec.delayInNanos.nanoseconds)) }

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

    public fun <A> delayInNanos(): Schedule<A, Double> =
      (forever<A>() as ScheduleImpl<Int, A, Int>).reconsider { _: A, decision ->
        Decision(
          cont = decision.cont,
          delayInNanos = decision.delayInNanos,
          state = decision.state,
          finish = Eval.now(decision.delayInNanos)
        )
      }

    @ExperimentalTime
    public fun <A> duration(): Schedule<A, Duration> =
      (forever<A>() as ScheduleImpl<Int, A, Int>).reconsider { _: A, decision ->
        Decision(
          cont = decision.cont,
          delayInNanos = decision.delayInNanos,
          state = decision.state,
          finish = Eval.now(decision.delayInNanos.nanoseconds)
        )
      }

    /**
     * Creates a Schedule that returns its decisions.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <A> decision(): Schedule<A, Boolean> =
      (forever<A>() as ScheduleImpl<Int, A, Int>).reconsider { _: A, decision ->
        Decision(
          cont = decision.cont,
          delayInNanos = decision.delayInNanos,
          state = decision.state,
          finish = Eval.now(decision.cont)
        )
      }

    /**
     * Creates a Schedule that continues with a fixed delay.
     *
     * @param interval fixed delay in nanoseconds
     */
    public fun <A> spaced(interval: Double): Schedule<A, Int> =
      forever<A>().delayedNanos { d -> d + interval }

    /**
     * Creates a Schedule that continues with a fixed delay.
     *
     * @param interval fixed delay in [Duration]
     */
    @ExperimentalTime
    public fun <A> spaced(interval: Duration): Schedule<A, Int> =
      forever<A>().delayedNanos { d -> d + interval.inNanoseconds }

    /**
     * Creates a Schedule that continues with increasing delay by adding the last two delays.
     *
     * @param one initial delay in nanoseconds
     */
    public fun <A> fibonacci(one: Double): Schedule<A, Double> =
      delayed(
        unfold<A, Pair<Double, Double>>(Pair(0.0, one)) { (del, acc) ->
          Pair(acc, del + acc)
        }.map { it.first }
      )

    /**
     * Creates a Schedule that continues with increasing delay by adding the last two delays.
     */
    @ExperimentalTime
    public fun <A> fibonacci(one: Duration): Schedule<A, Duration> =
      delayed(
        unfold<A, Pair<Duration, Duration>>(Pair(0.nanoseconds, one)) { (del, acc) ->
          Pair(acc, del + acc)
        }.map { it.first }
      )

    /**
     * Creates a Schedule which increases its delay linearly, by n * base where n is the number of executions.
     *
     * @param base the base delay in nanoseconds
     */
    public fun <A> linear(base: Double): Schedule<A, Double> =
      delayed(forever<A>().map { base * it })

    /**
     * Creates a Schedule which increases its delay linearly, by n * base where n is the number of executions.
     */
    @ExperimentalTime
    public fun <A> linear(base: Duration): Schedule<A, Duration> =
      delayed(forever<A>().map { base * it })

    /**
     * Creates a Schedule that increases its delay exponentially with a given factor and base.
     * Delays can be calculated as [base] * factor ^ n where n is the number of executions.
     *
     * @param base the base delay in nanoseconds
     */
    public fun <A> exponential(base: Double, factor: Double = 2.0): Schedule<A, Double> =
      delayed(forever<A>().map { base * factor.pow(it).roundToInt() })

    /**
     * Creates a Schedule that increases its delay exponentially with a given factor and base.
     * Delays can be calculated as [base] * factor ^ n where n is the number of executions.
     */
    @ExperimentalTime
    public fun <A> exponential(base: Duration, factor: Double = 2.0): Schedule<A, Duration> =
      delayed(forever<A>().map { base * factor.pow(it).roundToInt() })
  }
}

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
public suspend fun <A, B> Schedule<Throwable, B>.retryOrElse(fa: suspend () -> A, orElse: suspend (Throwable, B) -> A): A =
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
  (this as ScheduleImpl<Any?, Throwable, B>)

  var dec: Schedule.Decision<Any?, B>
  var state: Any? = initialState.invoke()

  while (true) {
    coroutineContext.ensureActive()
    try {
      return Either.Right(fa.invoke())
    } catch (e: Throwable) {
      dec = update(e, state)
      state = dec.state

      if (dec.cont) delay((dec.delayInNanos / 1_000_000).toLong())
      else return Either.Left(orElse(e.nonFatalOrThrow(), dec.finish.value()))
    }
  }
}
