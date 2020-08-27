package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Eval
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.Schedule.ScheduleImpl
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random

/**
 * # Retrying and repeating effects
 *
 * A common demand when working with effects is to retry or repeat them when certain circumstances happen. Usually, the retrial or repetition does not happen right away; rather, it is done based on a policy. For instance, when fetching content from a network request, we may want to retry it when it fails, using an exponential backoff algorithm, for a maximum of 15 seconds or 5 attempts, whatever happens first.
 *
 * [Schedule] allows you to define and compose powerful yet simple policies, which can be used to either repeat or retry computation.
 *
 * > [Schedule] has been derived from scalaz zio's [Schedule](https://zio.dev/docs/datatypes/datatypes_schedule) datatype and has been adapted to kotlin.
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
 * import arrow.fx.coroutines.*
 *
 * fun <A> complexPolicy(): Schedule<A, List<A>> =
 *   Schedule.exponential<A>(10.milliseconds).whileOutput { it.nanoseconds < 60.seconds.nanoseconds }
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
 * Assuming we have an suspend effect in, and we want to repeat it 3 times after its first successful execution, we can do:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   var counter = 0
 *   //sampleStart
 *   val res = repeat(Schedule.recurs(3)) {
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
 *   val res = repeat(Schedule.unit<Unit>() zipLeft Schedule.recurs(3)) {
 *     println("Run: ${counter++}")
 *   }
 *   // equal to
 *   val res2 = repeat(Schedule.recurs<Unit>(3) zipRight Schedule.unit()) {
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
 *   val res = repeat(Schedule.identity<Int>() zipLeft Schedule.recurs(3)) {
 *     println("Run: ${counter++}"); counter
 *   }
 *   // equal to
 *   val res2 = repeat(Schedule.recurs<Int>(3) zipRight Schedule.identity<Int>()) {
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
 *   val res = repeat(Schedule.collect<Int>() zipLeft Schedule.recurs(3)) {
 *     println("Run: ${counter++}")
 *     counter
 *   }
 *   // equal to
 *   val res2 = repeat(Schedule.recurs<Int>(3) zipRight Schedule.collect<Int>()) {
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
 *   val res = repeat(Schedule.doWhile<Int>{ it <= 3 }) {
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
 * import arrow.fx.coroutines.*
 *
 * val exponential = Schedule.exponential<Unit>(250.milliseconds)
 * ```
 */
sealed class Schedule<Input, Output> {

  /**
   * Change the output of a schedule. Does not alter the decision of the schedule.
   */
  abstract fun <B> map(f: (Output) -> B): Schedule<Input, B>

  /**
   * Change the input of the schedule. May alter a schedules decision if it is based on input.
   */
  abstract fun <B> contramap(f: suspend (B) -> Input): Schedule<B, Output>

  /**
   * Conditionally check on both the input and the output whether or not to continue.
   */
  abstract fun <A : Input> check(pred: suspend (A, Output) -> Boolean): Schedule<A, Output>

  /**
   * Invert the decision of a schedule.
   */
  abstract operator fun not(): Schedule<Input, Output>

  /**
   * Combine with another schedule by combining the result and the delay of the [Decision] with the functions [f] and [g]
   */
  abstract fun <A : Input, B> combineWith(
    other: Schedule<A, B>,
    f: (Boolean, Boolean) -> Boolean,
    g: (Duration, Duration) -> Duration
  ): Schedule<A, Pair<Output, B>>

  /**
   * Always retry a schedule regardless of the decision made prior to invoking this method.
   */
  abstract fun forever(): Schedule<Input, Output>

  /**
   * Execute one schedule after the other. When the first schedule ends, it continues with the second.
   */
  abstract infix fun <A : Input, B> andThen(other: Schedule<A, B>): Schedule<A, Either<Output, B>>

  /**
   * Change the delay of a resulting [Decision] based on the [Output] and the produced delay.
   */
  abstract fun modifyDelay(f: suspend (Output, Duration) -> Duration): Schedule<Input, Output>

  /**
   * Run a effectful handler on every input. Does not alter the decision.
   */
  abstract fun logInput(f: suspend (Input) -> Unit): Schedule<Input, Output>

  /**
   * Run a effectful handler on every output. Does not alter the decision.
   */
  abstract fun logOutput(f: suspend (Output) -> Unit): Schedule<Input, Output>

  /**
   * Accumulate the results of a schedule by folding over them effectfully.
   */
  abstract fun <C> foldM(
    initial: suspend () -> C,
    f: suspend (C, Output) -> C
  ): Schedule<Input, C>

  /**
   * Compose this schedule with the other schedule by piping the output of this schedule
   *  into the input of the other.
   */
  abstract infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B>

  /**
   * Combine two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  abstract infix fun <A, B> tupled(other: Schedule<A, B>): Schedule<Pair<Input, A>, Pair<Output, B>>

  /**
   * Combine two schedules with different input and output and conditionally choose between the two.
   * Continues when the chosen schedule continues and uses the chosen schedules delay.
   */
  abstract infix fun <A, B> choose(other: Schedule<A, B>): Schedule<Either<Input, A>, Either<Output, B>>

  fun unit(): Schedule<Input, Unit> =
    map { Unit }

  /**
   * Change the result of a [Schedule] to always be [b]
   */
  fun <B> const(b: B): Schedule<Input, B> = map { b }

  /**
   * Continue or stop the schedule based on the output
   */
  fun whileOutput(f: suspend (Output) -> Boolean): Schedule<Input, Output> =
    check { _, output -> f(output) }

  /**
   * Continue or stop the schedule based on the input
   */
  fun <A : Input> whileInput(f: suspend (A) -> Boolean): Schedule<A, Output> =
    check { input, _ -> f(input) }

  /**
   * `untilOutput(f) = whileOutput(f).not()`
   */
  fun untilOutput(f: suspend (Output) -> Boolean): Schedule<Input, Output> =
    !whileOutput(f)

  /**
   * `untilInput(f) = whileInput(f).not()`
   */
  fun <A : Input> untilInput(f: suspend (A) -> Boolean): Schedule<A, Output> =
    !whileInput(f)

  fun <B, C> dimap(f: suspend (B) -> Input, g: (Output) -> C): Schedule<B, C> =
    contramap(f).map(g)

  /**
   * Combine two schedules. Continues only when both continue and chooses the maximum delay.
   */
  infix fun <A : Input, B> and(other: Schedule<A, B>): Schedule<A, Pair<Output, B>> =
    combineWith(other, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules. Continues if one continues and chooses the minimum delay
   */
  infix fun <A : Input, B> or(other: Schedule<A, B>): Schedule<A, Pair<Output, B>> =
    combineWith(other, { a, b -> a || b }, { a, b -> min(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules with [and] but throw away the left schedule's result
   */
  infix fun <A : Input, B> zipRight(other: Schedule<A, B>): Schedule<A, B> =
    (this and other).map { it.second }

  /**
   * Combine two schedules with [and] but throw away the right schedule's result
   */
  infix fun <A : Input, B> zipLeft(other: Schedule<A, B>): Schedule<A, Output> =
    (this and other).map { it.first }

  /**
   * Adjust the delay of a schedule's [Decision]
   */
  fun delayed(f: suspend (Duration) -> Duration): Schedule<Input, Output> =
    modifyDelay { _, duration -> f(duration) }

  /**
   * Add random jitter to a schedule.
   * The argument [genRand] is supposed to be a computation with when run returns
   *  doubles. An example would be the following [IO] `IO { Random.nextDouble() }`.
   *
   * This is done to push the source of randomness to the caller which makes the function
   *  jittered deterministic and testable.
   *
   * The result returned by [genRand] is multiplied with the current duration.
   */
  fun jittered(genRand: suspend () -> Double): Schedule<Input, Output> =
    modifyDelay { _, duration ->
      val n = genRand.invoke()
      (duration.nanoseconds * n).roundToLong().nanoseconds
    }

  fun jittered(): Schedule<Input, Output> =
    jittered(suspend { Random.nextDouble(0.0, 1.0) })

  /**
   * Non-effectful version of [foldM].
   */
  fun <C> fold(initial: C, f: suspend (C, Output) -> C): Schedule<Input, C> =
    foldM(suspend { initial }) { acc, o -> f(acc, o) }

  /**
   * Accumulate the results of every execution to a list
   */
  fun collect(): Schedule<Input, List<Output>> =
    fold(emptyList()) { acc, o -> acc + listOf(o) }

  /**
   * Infix variant of pipe with reversed order.
   */
  infix fun <B> compose(other: Schedule<B, Input>): Schedule<B, Output> =
    (other pipe this)

  // Dependent type emulation
  @Suppress("UNCHECKED_CAST")
  internal class ScheduleImpl<State, Input, Output>(
    val initialState: suspend () -> State,
    val update: suspend (a: Input, s: State) -> Decision<State, Output>
  ) : Schedule<Input, Output>() {

    override fun <B> map(f: (Output) -> B): Schedule<Input, B> =
      ScheduleImpl(initialState) { i, s -> update(i, s).mapRight(f) }

    override fun <B> contramap(f: suspend (B) -> Input): Schedule<B, Output> =
      ScheduleImpl(initialState) { i, s -> update(f(i), s) }

    override fun <A : Input> check(pred: suspend (A, Output) -> Boolean): Schedule<A, Output> =
      updated { f ->
        { a: A, s: State ->
          val dec = f(a, s)
          if (dec.cont) pred(a, dec.finish.value()).let { dec.copy(cont = it) }
          else dec
        }
      }

    override fun <A : Input, B> combineWith(
      other: Schedule<A, B>,
      f: (Boolean, Boolean) -> Boolean,
      g: (Duration, Duration) -> Duration
    ): Schedule<A, Pair<Output, B>> = (other as ScheduleImpl<Any?, A, B>).let { other ->
      ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s: Pair<State, Any?> ->
        update(i, s.first).combineWith(other.update(i, s.second), f, g)
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
        s.fold({ s ->
          val dec = this@ScheduleImpl.update(i, s)
          if (dec.cont) dec.bimap({ it.left() }, { it.left() })
          else {
            val newState = other.initialState.invoke()
            val newDec = other.update(i, newState)
            newDec.bimap({ it.right() }, { it.right() })
          }
        }, { s ->
          other.update(i, s).bimap({ it.right() }, { it.right() })
        })
      }

    override fun modifyDelay(f: suspend (Output, Duration) -> Duration): Schedule<Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          val step = update(a, s)
          val d = f(step.finish.value(), step.delay)
          step.copy(delay = d)
        }
      }

    override fun logInput(f: suspend (Input) -> Unit): Schedule<Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          update(a, s).also { f(a) }
        }
      }

    override fun logOutput(f: suspend (Output) -> Unit): Schedule<Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          update(a, s).also { f(it.finish.value()) }
        }
      }

    override fun <C> foldM(initial: suspend () -> C, f: suspend (C, Output) -> C): Schedule<Input, C> =
      ScheduleImpl(suspend { Pair(initialState.invoke(), initial.invoke()) }) { i, s ->
        val dec = update(i, s.first)
        val c = if (dec.cont) f(s.second, dec.finish.value()) else s.second
        dec.bimap({ s -> Pair(s, c) }, { c })
      }

    override infix fun <B> pipe(other: Schedule<Output, B>): Schedule<Input, B> =
      (other as ScheduleImpl<Any?, Output, B>).let { other ->
        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
          val dec1 = update(i, s.first)
          val dec2 = other.update(dec1.finish.value(), s.second)
          dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> a + b }).mapRight { it.second }
        }
      }

    override infix fun <A, B> tupled(other: Schedule<A, B>): Schedule<Pair<Input, A>, Pair<Output, B>> =
      (other as ScheduleImpl<Any?, A, B>).let { other ->
        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
          val dec1 = update(i.first, s.first)
          val dec2 = other.update(i.second, s.second)
          dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })
        }
      }

    override infix fun <A, B> choose(other: Schedule<A, B>): Schedule<Either<Input, A>, Either<Output, B>> =
      (other as ScheduleImpl<Any?, A, B>).let { other ->
        ScheduleImpl(suspend { Pair(initialState.invoke(), other.initialState.invoke()) }) { i, s ->
          i.fold({
            update(it, s.first).mapLeft { Pair(it, s.second) }.mapRight { it.left() }
          }, {
            other.update(it, s.second).mapLeft { Pair(s.first, it) }.mapRight { it.right() }
          })
        }
      }

    /**
     * Schedule state machine update function
     */
    fun <A : Input, B> updated(
      f: (suspend (A, State) -> Decision<State, Output>) -> (suspend (A, State) -> Decision<State, B>)
    ): Schedule<A, B> = ScheduleImpl(initialState) { a, s ->
      f { i, s -> update(i, s) }(a, s)
    }

    /**
     * Inspect and change the [Decision] of a [Schedule]. Also given access to the input.
     */
    fun <A : Input, B> reconsider(f: suspend (A, Decision<State, Output>) -> Decision<State, B>): Schedule<A, B> =
      updated { update ->
        { a: A, s: State ->
          val dec = update(a, s)
          f(a, dec)
        }
      }

    /**
     * Run an effect with a [Decision]. Does not alter the decision.
     */
    fun <A : Input> onDecision(fa: suspend (A, Decision<State, Output>) -> Unit): Schedule<A, Output> =
      updated { f ->
        { a: A, s: State ->
          f(a, s).also { fa(a, it) }
        }
      }
  }

  /**
   * A single decision. Contains the decision to continue, the delay, the new state and the (lazy) result of a Schedule.
   */
  data class Decision<out A, out B>(val cont: Boolean, val delay: Duration, val state: A, val finish: Eval<B>) {

    operator fun not(): Decision<A, B> =
      copy(cont = !cont)

    fun <C, D> bimap(f: (A) -> C, g: (B) -> D): Decision<C, D> =
      Decision(cont, delay, f(state), finish.map(g))

    fun <C> mapLeft(f: (A) -> C): Decision<C, B> =
      bimap(f, ::identity)

    fun <D> mapRight(g: (B) -> D): Decision<A, D> =
      bimap(::identity, g)

    fun <C, D> combineWith(
      other: Decision<C, D>,
      f: (Boolean, Boolean) -> Boolean,
      g: (Duration, Duration) -> Duration
    ): Decision<Pair<A, C>, Pair<B, D>> = Decision(
      f(cont, other.cont),
      g(delay, other.delay),
      Pair(state, other.state),
      finish.flatMap { first -> other.finish.map { second -> Pair(first, second) } }
    )

    override fun equals(other: Any?): Boolean =
      if (other !is Decision<*, *>) false
      else cont == other.cont &&
        state == other.state &&
        delay.nanoseconds == other.delay.nanoseconds &&
        finish.value() == other.finish.value()

    companion object {
      fun <A, B> cont(d: Duration, a: A, b: Eval<B>): Decision<A, B> = Decision(true, d, a, b)
      fun <A, B> done(d: Duration, a: A, b: Eval<B>): Decision<A, B> = Decision(false, d, a, b)
    }
  }

  companion object {

    /**
     * Invoke constructor to manually define a schedule. If you need this, please consider adding it to arrow or suggest
     *  a change to avoid using this manual method.
     */
    operator fun <S, A, B> invoke(
      initial: suspend () -> S,
      update: suspend (a: A, s: S) -> Decision<S, B>
    ): Schedule<A, B> =
      ScheduleImpl(initial, update)

    /**
     * Creates a schedule that continues without delay and just returns its input.
     */
    fun <A> identity(): Schedule<A, A> = invoke({ Unit }) { a, s ->
      Decision.cont(0.seconds, s, Eval.now(a))
    }

    /**
     * Creates a schedule that continues without delay and always returns Unit
     */
    fun <A> unit(): Schedule<A, Unit> =
      identity<A>().unit()

    /**
     * Create a schedule that unfolds effectfully using a seed value [c] and a unfold function [f].
     * This keeps the current state (the current seed) as [State] and runs the unfold function on every
     *  call to update. This schedule always continues without delay and returns the current state.
     */
    fun <I, A> unfoldM(c: suspend () -> A, f: suspend (A) -> A): Schedule<I, A> =
      invoke(c) { _: I, acc ->
        val a = f(acc)
        Decision.cont(0.seconds, a, Eval.now(a))
      }

    /**
     * Non-effectful variant of [unfoldM]
     */
    fun <I, A> unfold(c: A, f: (A) -> A): Schedule<I, A> =
      unfoldM(suspend { c }) { f(it) }

    /**
     * Create a schedule that continues forever and returns the number of iterations.
     */
    fun <A> forever(): Schedule<A, Int> =
      unfold(0) { it + 1 }

    /**
     * Create a schedule that continues n times and returns the number of iterations.
     */
    fun <A> recurs(n: Int): Schedule<A, Int> =
      invoke(suspend { 0 }) { _: A, acc ->
        if (acc < n) Decision.cont(0.seconds, acc + 1, Eval.now(acc + 1))
        else Decision.done(0.seconds, acc, Eval.now(acc))
      }

    /**
     * Create a schedule that only ever retries once.
     */
    fun <A> once(): Schedule<A, Unit> = recurs<A>(1).unit()

    /**
     * Create a schedule that never retries.
     *
     * Note that this will hang a program if used as a repeat/retry schedule unless cancelled.
     */
    fun <A> never(): Schedule<A, Nothing> =
      invoke(suspend { arrow.fx.coroutines.never<Unit>() }) { _, _ ->
        Decision(false, 0.nanoseconds, Unit, Eval.later { throw IllegalArgumentException("Impossible") })
      }

    /**
     * Create a schedule that uses another schedule to generate the delay of this schedule.
     * Continues for as long as [delaySchedule] continues and adds the output of [delaySchedule] to
     *  the delay that [delaySchedule] produced. Also returns the full delay as output.
     *
     * A common use case is to define a unfolding schedule and use the result to change the delay.
     *  For an example see the implementation of [spaced], [linear], [fibonacci] or [exponential]
     */
    @Suppress("UNCHECKED_CAST")
    fun <A> delayed(delaySchedule: Schedule<A, Duration>): Schedule<A, Duration> =
      (delaySchedule.modifyDelay { a, b -> a + b } as ScheduleImpl<Any?, A, Duration>)
        .reconsider { _, dec -> dec.copy(finish = Eval.now(dec.delay)) }

    /**
     * Create a schedule which collects all it's inputs in a list
     */
    fun <A> collect(): Schedule<A, List<A>> =
      identity<A>().collect()

    /**
     * Create a schedule that continues as long as [đ] returns true.
     */
    fun <A> doWhile(f: suspend (A) -> Boolean): Schedule<A, A> =
      identity<A>().whileInput(f)

    /**
     * Create a schedule that continues until [đ] returns true.
     */
    fun <A> doUntil(f: suspend (A) -> Boolean): Schedule<A, A> =
      identity<A>().untilInput(f)

    /**
     * Create a schedule with an effectful handler on the input.
     */
    fun <A> logInput(f: suspend (A) -> Unit): Schedule<A, A> =
      identity<A>().logInput(f)

    /**
     * Create a schedule with an effectful handler on the output.
     */
    fun <A> logOutput(f: suspend (A) -> Unit): Schedule<A, A> =
      identity<A>().logOutput(f)

    /**
     * Create a schedule that returns its delay.
     */
    @Suppress("UNCHECKED_CAST")
    fun <A> delay(): Schedule<A, Duration> =
      (forever<A>() as ScheduleImpl<Int, A, Int>).reconsider { _: A, decision ->
        Decision(
          cont = decision.cont,
          delay = decision.delay,
          state = decision.state,
          finish = Eval.now(decision.delay)
        )
      }

    /**
     * Create a schedule that returns its decisions
     */
    @Suppress("UNCHECKED_CAST")
    fun <A> decision(): Schedule<A, Boolean> =
      (forever<A>() as ScheduleImpl<Int, A, Int>).reconsider { _: A, decision ->
        Decision(
          cont = decision.cont,
          delay = decision.delay,
          state = decision.state,
          finish = Eval.now(decision.cont)
        )
      }

    /**
     * Create a schedule that continues with fixed delay.
     */
    fun <A> spaced(interval: Duration): Schedule<A, Int> =
      forever<A>().delayed { d -> d + interval }

    /**
     * Create a schedule that continues with increasing delay by adding the last two delays.
     */
    fun <A> fibonacci(one: Duration): Schedule<A, Duration> =
      delayed(
        unfold<A, Pair<Duration, Duration>>(Pair(0.seconds, one)) { (del, acc) ->
          Pair(acc, del + acc)
        }.map { it.first }
      )

    /**
     * Create a schedule which increases its delay linear by n * base where n is the number of
     *  executions.
     */
    fun <A> linear(base: Duration): Schedule<A, Duration> =
      delayed(
        forever<A>().map { base * it }
      )

    /**
     * Create a schedule that increases its delay exponentially with a given factor and base.
     * Delay can be calculated as [base] * factor ^ n where n is the number of executions.
     */
    fun <A> exponential(base: Duration, factor: Double = 2.0): Schedule<A, Duration> =
      delayed(
        forever<A>().map { base * factor.pow(it).roundToInt() }
      )
  }
}

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Returns the last output from the policy or raises an error if a repeat failed.
 */
suspend fun <A, B> repeat(
  schedule: Schedule<A, B>,
  fa: suspend () -> A
): B = repeatOrElse(schedule, fa) { e, _ -> throw e }

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during repetition.
 */
suspend fun <A, B> repeatOrElse(
  schedule: Schedule<A, B>,
  fa: suspend () -> A,
  orElse: suspend (Throwable, B?) -> B
): B = repeatOrElseEither(schedule, fa, orElse).fold(::identity, ::identity)

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during repetition.
 */
@Suppress("UNCHECKED_CAST")
suspend fun <A, B, C> repeatOrElseEither(
  schedule: Schedule<A, B>,
  fa: suspend () -> A,
  orElse: suspend (Throwable, B?) -> C
): Either<C, B> {
  (schedule as ScheduleImpl<Any?, A, B>)
  var last: (() -> B)? = null // We haven't seen any input yet
  var state: Any? = schedule.initialState.invoke()

  while (true) {
    cancelBoundary()
    try {
      val a = fa.invoke()
      val step = schedule.update(a, state)
      if (!step.cont) return Either.Right(step.finish.value())
      else {
        sleep(step.delay)

        // Set state before looping again
        last = { step.finish.value() }
        state = step.state
      }
    } catch (e: Throwable) {
      return Either.Left(orElse(e.nonFatalOrThrow(), last?.invoke()))
    }
  }
}

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Returns the result of the effect if if it was successful or re-raises the last error encountered when the schedule ends.
 */
suspend fun <A, B> retry(
  schedule: Schedule<A, B>,
  fa: suspend () -> A
): A = retryOrElse(schedule, fa) { e, _ -> throw e }

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
suspend fun <A, B> retryOrElse(
  schedule: Schedule<A, B>,
  fa: suspend () -> A,
  orElse: suspend (Throwable, B) -> A
): A = retryOrElseEither(schedule, fa, orElse).fold(::identity, ::identity)

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
@Suppress("UNCHECKED_CAST")
suspend fun <A, B, C> retryOrElseEither(
  schedule: Schedule<A, B>,
  fa: suspend () -> A,
  orElse: suspend (Throwable, B) -> C
): Either<C, A> {
  (schedule as ScheduleImpl<Any?, Throwable, B>)

  var dec: Schedule.Decision<Any?, B>
  var state: Any? = schedule.initialState.invoke()

  while (true) {
    cancelBoundary()
    try {
      return Either.Right(fa.invoke())
    } catch (e: Throwable) {
      dec = schedule.update(e, state)
      state = dec.state

      if (dec.cont) sleep(dec.delay)
      else return Either.Left(orElse(e.nonFatalOrThrow(), dec.finish.value()))
    }
  }
}
