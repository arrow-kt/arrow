package arrow.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.fix
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.some
import arrow.core.toT
import arrow.fx.Schedule.Companion.withMonad
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.nanoseconds
import arrow.fx.typeclasses.seconds
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random

class ForSchedule private constructor() {
  companion object
}
typealias ScheduleOf<M, Input, Output> = arrow.Kind3<ForSchedule, M, Input, Output>
typealias SchedulePartialOf<M, Input> = arrow.Kind2<ForSchedule, M, Input>
typealias ScheduleKindedJ<M, Input, Output> = arrow.HkJ3<ForSchedule, M, Input, Output>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <M, Input, Output> ScheduleOf<M, Input, Output>.fix(): Schedule<M, Input, Output> =
  this as Schedule<M, Input, Output>

class ForDecision private constructor() {
  companion object
}
typealias DecisionOf<A, B> = arrow.Kind2<ForDecision, A, B>
typealias DecisionPartialOf<A> = arrow.Kind<ForDecision, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B> DecisionOf<A, B>.fix(): Schedule.Decision<A, B> =
  this as Schedule.Decision<A, B>

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
 * > Because schedules are polymorphic over any [F] that is also a [Monad], constructing a [Schedule] can sometimes mean having to explicitly write the type-parameters. This can be avoided using [Schedule.withMonad] which partially applies the chosen [Monad].
 *
 * Constructing a simple schedule which recurs 10 times until it succeeds:
 * ```kotlin:ank
 * import arrow.fx.ForIO
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.monad.monad
 *
 * fun <A> recurTenTimes() = Schedule.recurs<ForIO, A>(IO.monad(), 10)
 * ```
 *
 * A more complex schedule is best put together using the [withMonad] constructor:
 * ```kotlin:ank
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.monad.monad
 * import arrow.fx.extensions.io.monadDefer.monadDefer
 * import arrow.fx.typeclasses.milliseconds
 * import arrow.fx.typeclasses.seconds
 *
 * fun <A> complexPolicy() =
 *   Schedule.withMonad(IO.monad()) {
 *     exponential<A>(10.milliseconds).whileOutput { it.nanoseconds < 60.seconds.nanoseconds }
 *       .andThen(spaced<A>(60.seconds) and recurs<A>(100)).jittered(IO.monadDefer())
 *       .zipRight(identity<A>().collect())
 *   }
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
 * Assuming we have an effect in [IO], and we want to repeat it 3 times after its first successful execution, we can do:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.concurrent.concurrent
 * import arrow.fx.extensions.io.monad.monad
 * import arrow.fx.fix
 * import arrow.fx.repeat
 *
 * fun main() {
 *   var counter = 0
 *   val io = IO { println("Run: ${counter++}") }
 *   //sampleStart
 *   val res = io.repeat(IO.concurrent(), Schedule.recurs(IO.monad(), 3))
 *   //sampleEnd
 *   println(res.fix().unsafeRunSync())
 * }
 * ```
 *
 * However, when running this new effect, its output will be the number of iterations it has performed, as stated in the documentation of the function. Also notice that we did not handle the error case, there are overloads [repeatOrElse] and [repeatOrElseEither] which offer that capability, [repeat] will just rethrow any error encountered.
 *
 * If we want to discard the values provided by the repetition of the effect, we can combine our policy with [Schedule.unit], using the [zipLeft] or [zipRight] combinators, which will keep just the output of one of the policies:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.ForIO
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.concurrent.concurrent
 * import arrow.fx.extensions.io.monad.monad
 * import arrow.fx.fix
 * import arrow.fx.repeat
 *
 * fun main() {
 *   var counter = 0
 *   val io = IO { println("Run: ${counter++}") }
 *   //sampleStart
 *   val res = io.repeat(IO.concurrent(), Schedule.unit<ForIO, Unit>(IO.monad()).zipLeft(Schedule.recurs(IO.monad(), 3)))
 *
 *   // equal to
 *   val res2 = io.repeat(IO.concurrent(), Schedule.recurs<ForIO, Unit>(IO.monad(), 3).zipRight(Schedule.unit(IO.monad())))
 *
 *   //sampleEnd
 *   println(res.fix().unsafeRunSync())
 *   println(res2.fix().unsafeRunSync())
 * }
 * ```
 *
 * Following the same strategy, we can zip it with the [Schedule.identity] policy to keep only the last provided result by the effect.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.ForIO
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.concurrent.concurrent
 * import arrow.fx.extensions.io.monad.monad
 * import arrow.fx.fix
 * import arrow.fx.repeat
 *
 * fun main() {
 *   var counter = 0
 *   val io = IO { println("Run: ${counter++}"); counter }
 *   //sampleStart
 *   val res = io.repeat(IO.concurrent(), Schedule.identity<ForIO, Int>(IO.monad()).zipLeft(Schedule.recurs(IO.monad(), 3)))
 *
 *   // equal to
 *   val res2 = io.repeat(IO.concurrent(), Schedule.recurs<ForIO, Int>(IO.monad(), 3).zipRight(Schedule.identity<ForIO, Int>(IO.monad())))
 *
 *   //sampleEnd
 *   println(res.fix().unsafeRunSync())
 *   println(res2.fix().unsafeRunSync())
 * }
 * ```
 *
 * Finally, if we want to keep all intermediate results, we can zip the policy with [Schedule.collect]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.ForIO
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.concurrent.concurrent
 * import arrow.fx.extensions.io.monad.monad
 * import arrow.fx.fix
 * import arrow.fx.repeat
 *
 * fun main() {
 *   var counter = 0
 *   val io = IO { println("Run: ${counter++}"); counter }
 *   //sampleStart
 *   val res = io.repeat(IO.concurrent(), Schedule.collect<ForIO, Int>(IO.monad()).zipLeft(Schedule.recurs(IO.monad(), 3)))
 *
 *   // equal to
 *   val res2 = io.repeat(IO.concurrent(), Schedule.recurs<ForIO, Int>(IO.monad(), 3).zipRight(Schedule.collect<ForIO, Int>(IO.monad())))
 *
 *   //sampleEnd
 *   println(res.fix().unsafeRunSync())
 *   println(res2.fix().unsafeRunSync())
 * }
 * ```
 *
 * ## Repeating an effect until/while it produces a certain value
 *
 * We can make use of the policies doWhile or doUntil to repeat an effect while or until its produced result matches a given predicate.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.ForIO
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.concurrent.concurrent
 * import arrow.fx.extensions.io.monad.monad
 * import arrow.fx.fix
 * import arrow.fx.repeat
 *
 * fun main() {
 *   var counter = 0
 *   val io = IO { println("Run: ${counter++}"); counter }
 *   //sampleStart
 *   val res = io.repeat(IO.concurrent(), Schedule.doWhile<ForIO, Int>(IO.monad()) { it <= 3 })
 *   //sampleEnd
 *   println(res.fix().unsafeRunSync())
 * }
 * ```
 *
 * ## Exponential backoff retries
 *
 * A common algorithm to retry effectful operations, as network requests, is the exponential backoff algorithm. There is a scheduling policy that implements this algorithm and can be used as:
 *
 * ```kotlin:ank
 * import arrow.fx.ForIO
 * import arrow.fx.IO
 * import arrow.fx.Schedule
 * import arrow.fx.extensions.io.monad.monad
 * import arrow.fx.typeclasses.milliseconds
 *
 * val exponential = Schedule.exponential<ForIO, Unit>(IO.monad(), 250.milliseconds)
 * ```
 */
sealed class Schedule<F, Input, Output> : ScheduleOf<F, Input, Output> {
  internal abstract val M: Monad<F>

  /**
   * Change the output of a schedule. Does not alter the decision of the schedule.
   */
  abstract fun <B> map(f: (Output) -> B): Schedule<F, Input, B>

  /**
   * Change the input of the schedule. May alter a schedules decision if it is based on input.
   */
  abstract fun <B> contramap(f: (B) -> Input): Schedule<F, B, Output>

  /**
   * Conditionally check on both the input and the output whether or not to continue.
   */
  abstract fun <A : Input> check(pred: (A, Output) -> Kind<F, Boolean>): Schedule<F, A, Output>

  /**
   * Invert the decision of a schedule.
   */
  abstract operator fun not(): Schedule<F, Input, Output>

  /**
   * Combine with another schedule by combining the result and the delay of the [Decision] with the functions [f] and [g]
   */
  abstract fun <A : Input, B> combineWith(
    other: Schedule<F, A, B>,
    f: (Boolean, Boolean) -> Boolean,
    g: (Duration, Duration) -> Duration
  ): Schedule<F, A, Tuple2<Output, B>>

  /**
   * Always retry a schedule regardless of the decision made prior to invoking this method.
   */
  abstract fun forever(): Schedule<F, Input, Output>

  /**
   * Execute one schedule after the other. When the first schedule ends, it continues with the second.
   */
  abstract infix fun <A : Input, B> andThen(other: Schedule<F, A, B>): Schedule<F, A, Either<Output, B>>

  /**
   * Change the delay of a resulting [Decision] based on the [Output] and the produced delay.
   */
  abstract fun modifyDelay(f: (Output, Duration) -> Kind<F, Duration>): Schedule<F, Input, Output>

  /**
   * Run a effectful handler on every input. Does not alter the decision.
   */
  abstract fun logInput(f: (Input) -> Kind<F, Unit>): Schedule<F, Input, Output>

  /**
   * Run a effectful handler on every output. Does not alter the decision.
   */
  abstract fun logOutput(f: (Output) -> Kind<F, Unit>): Schedule<F, Input, Output>

  /**
   * Accumulate the results of a schedule by folding over them effectfully.
   */
  abstract fun <C> foldM(
    initial: Kind<F, C>,
    f: (C, Output) -> Kind<F, C>
  ): Schedule<F, Input, C>

  /**
   * Compose this schedule with the other schedule by piping the output of this schedule
   *  into the input of the other.
   */
  abstract infix fun <B> pipe(other: Schedule<F, Output, B>): Schedule<F, Input, B>

  /**
   * Combine two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  abstract infix fun <A, B> tupled(other: Schedule<F, A, B>): Schedule<F, Tuple2<Input, A>, Tuple2<Output, B>>

  /**
   * Combine two schedules with different input and output and conditionally choose between the two.
   * Continues when the chosen schedule continues and uses the chosen schedules delay.
   */
  abstract infix fun <A, B> choose(other: Schedule<F, A, B>): Schedule<F, Either<Input, A>, Either<Output, B>>

  fun unit(): Schedule<F, Input, Unit> =
    map { Unit }

  /**
   * Change the result of a [Schedule] to always be [b]
   */
  fun <B> const(b: B): Schedule<F, Input, B> = map { b }

  /**
   * Continue or stop the schedule based on the output
   */
  fun whileOutput(f: (Output) -> Boolean): Schedule<F, Input, Output> =
    check { _, output -> M.just(f(output)) }

  /**
   * Continue or stop the schedule based on the input
   */
  fun <A : Input> whileInput(f: (A) -> Boolean): Schedule<F, A, Output> =
    check { input, _ -> M.just(f(input)) }

  /**
   * `untilOutput(f) = whileOutput(f).not()`
   */
  fun untilOutput(f: (Output) -> Boolean): Schedule<F, Input, Output> =
    !whileOutput(f)

  /**
   * `untilInput(f) = whileInput(f).not()`
   */
  fun <A : Input> untilInput(f: (A) -> Boolean): Schedule<F, A, Output> =
    !whileInput(f)

  fun <B, C> dimap(f: (B) -> Input, g: (Output) -> C): Schedule<F, B, C> = contramap(f).map(g)

  /**
   * Combine two schedules. Continues only when both continue and chooses the maximum delay.
   */
  infix fun <A : Input, B> and(other: Schedule<F, A, B>): Schedule<F, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules. Continues if one continues and chooses the minimum delay
   */
  infix fun <A : Input, B> or(other: Schedule<F, A, B>): Schedule<F, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a || b }, { a, b -> min(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules with [and] but throw away the left schedule's result
   */
  infix fun <A : Input, B> zipRight(other: Schedule<F, A, B>): Schedule<F, A, B> =
    (this and other).map { it.b }

  /**
   * Combine two schedules with [and] but throw away the right schedule's result
   */
  infix fun <A : Input, B> zipLeft(other: Schedule<F, A, B>): Schedule<F, A, Output> =
    (this and other).map { it.a }

  /**
   * Adjust the delay of a schedule's [Decision]
   */
  fun delayed(f: (Duration) -> Duration): Schedule<F, Input, Output> =
    modifyDelay { _, duration -> M.run { just(f(duration)) } }

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
  fun jittered(genRand: Kind<F, Double>): Schedule<F, Input, Output> =
    modifyDelay { _, duration ->
      M.run {
        genRand.map { (duration.nanoseconds * it).roundToLong().nanoseconds }
      }
    }

  fun jittered(MF: MonadDefer<F>): Schedule<F, Input, Output> =
    jittered(MF.later { Random.nextDouble(0.0, 1.0) })

  /**
   * Non-effectful version of [foldM].
   */
  fun <C> fold(initial: C, f: (C, Output) -> C): Schedule<F, Input, C> =
    foldM(M.just(initial)) { acc, o -> M.just(f(acc, o)) }

  /**
   * Accumulate the results of every execution to a list
   */
  fun collect(): Schedule<F, Input, List<Output>> =
    fold(emptyList()) { acc, o -> acc + listOf(o) }

  /**
   * Infix variant of pipe with reversed order.
   */
  infix fun <B> compose(other: Schedule<F, B, Input>): Schedule<F, B, Output> =
    other pipe this

  internal class ScheduleImpl<F, State, Input, Output>(
    override val M: Monad<F>,
    val initialState: Kind<F, State>,
    val update: (a: Input, s: State) -> Kind<F, Decision<State, Output>>
  ) : Schedule<F, Input, Output>() {
    override fun <B> map(f: (Output) -> B): Schedule<F, Input, B> =
      ScheduleImpl(M, initialState) { i, s -> M.run { update(i, s).map { it.mapRight(f) } } }

    override fun <B> contramap(f: (B) -> Input): Schedule<F, B, Output> =
      ScheduleImpl(M, initialState) { i, s -> update(f(i), s) }

    override fun <A : Input> check(pred: (A, Output) -> Kind<F, Boolean>): Schedule<F, A, Output> =
      updated { f ->
        { a: A, s: State ->
          M.run {
            f(a, s).flatMap { dec ->
              if (dec.cont) pred(a, dec.finish.value()).map { dec.copy(cont = it) }
              else just(dec)
            }
          }
        }
      }

    override fun <A : Input, B> combineWith(
      other: Schedule<F, A, B>,
      f: (Boolean, Boolean) -> Boolean,
      g: (Duration, Duration) -> Duration
    ): Schedule<F, A, Tuple2<Output, B>> = (other as ScheduleImpl<F, Any?, A, B>).let { other ->
      ScheduleImpl(M, M.tupled(initialState, other.initialState)) { i, s: Tuple2<State, Any?> ->
        M.run {
          M.map(
            update(i, s.a),
            other.update(i, s.b)
          ) { it.a.combineWith(it.b, f, g) }
        }
      }
    }

    override fun forever(): Schedule<F, Input, Output> = updated { f ->
      { a: Input, s: State ->
        M.run {
          f(a, s).flatMap { dec ->
            if (dec.cont) just(dec)
            else this@ScheduleImpl.initialState.map { state -> dec.copy(cont = true, state = state) }
          }
        }
      }
    }

    override operator fun not(): Schedule<F, Input, Output> =
      updated { f ->
        { a: Input, s: State ->
          M.run { f(a, s).map { dec -> !dec } }
        }
      }

    override fun <A : Input, B> andThen(other: Schedule<F, A, B>): Schedule<F, A, Either<Output, B>> =
      ScheduleImpl<F, Either<State, Any?>, A, Either<Output, B>>(M, M.run { initialState.map(::Left) }) { i, s ->
        (other as ScheduleImpl<F, Any?, A, B>)
        M.run {
          s.fold({ s ->
            this@ScheduleImpl.update(i, s).flatMap { dec ->
              if (dec.cont) just(dec.bimap({ it.left() }, { it.left() }))
              else M.fx.monad {
                val newState = !other.initialState
                val newDec = !other.update(i, newState)
                newDec.bimap({ it.right() }, { it.right() })
              }
            }
          }, { s ->
            other.update(i, s).map { it.bimap({ it.right() }, { it.right() }) }
          })
        }
      }

    override fun modifyDelay(f: (Output, Duration) -> Kind<F, Duration>): Schedule<F, Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          M.run {
            update(a, s).flatMap { step ->
              f(step.finish.value(), step.delay).map { d -> step.copy(delay = d) }
            }
          }
        }
      }

    override fun logInput(f: (Input) -> Kind<F, Unit>): Schedule<F, Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          M.run {
            update(a, s).productL(f(a))
          }
        }
      }

    override fun logOutput(f: (Output) -> Kind<F, Unit>): Schedule<F, Input, Output> =
      updated { update ->
        { a: Input, s: State ->
          M.run {
            update(a, s).flatTap { dec -> f(dec.finish.value()) }
          }
        }
      }

    override fun <C> foldM(initial: Kind<F, C>, f: (C, Output) -> Kind<F, C>): Schedule<F, Input, C> =
      ScheduleImpl(M, M.tupled(initialState, initial)) { i, s ->
        M.fx.monad {
          val dec = !update(i, s.a)
          val c = !f(s.b, dec.finish.value())
          dec.bimap({ s -> s toT c }, { c })
        }
      }

    override infix fun <B> pipe(other: Schedule<F, Output, B>): Schedule<F, Input, B> =
      (other as ScheduleImpl<F, Any?, Output, B>).let { other ->
        ScheduleImpl(M, M.tupled(initialState, other.initialState)) { i, s ->
          M.run {
            update(i, s.a).flatMap { dec1 ->
              other.update(dec1.finish.value(), s.b).map { dec2 ->
                dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> a + b }).mapRight { it.b }
              }
            }
          }
        }
      }

    override infix fun <A, B> tupled(other: Schedule<F, A, B>): Schedule<F, Tuple2<Input, A>, Tuple2<Output, B>> =
      (other as ScheduleImpl<F, Any?, A, B>).let { other ->
        ScheduleImpl(M, M.tupled(initialState, other.initialState)) { i, s ->
          M.map(update(i.a, s.a), other.update(i.b, s.b)) { (dec1, dec2) ->
            dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })
          }
        }
      }

    override infix fun <A, B> choose(other: Schedule<F, A, B>): Schedule<F, Either<Input, A>, Either<Output, B>> =
      (other as ScheduleImpl<F, Any?, A, B>).let { other ->
        ScheduleImpl(M, M.tupled(initialState, other.initialState)) { i, s ->
          M.run {
            i.fold({
              update(it, s.a).map { it.mapLeft { it toT s.b }.mapRight { it.left() } }
            }, {
              other.update(it, s.b).map { it.mapLeft { s.a toT it }.mapRight { it.right() } }
            })
          }
        }
      }

    fun <A : Input, B> updated(
      f: ((A, State) -> Kind<F, Decision<State, Output>>) -> (A, State) -> Kind<F, Decision<State, B>>
    ): Schedule<F, A, B> = ScheduleImpl(M, initialState) { a, s ->
      f { i, s -> update(i, s) }(a, s)
    }

    /**
     * Inspect and change the [Decision] of a [Schedule]. Also given access to the input.
     */
    fun <A : Input, B> reconsiderM(f: (A, Decision<State, Output>) -> Kind<F, Decision<State, B>>): Schedule<F, A, B> =
      updated { update ->
        { a: A, s: State ->
          M.fx.monad {
            val dec = !update(a, s)
            !f(a, dec)
          }
        }
      }

    /**
     * Non-effectful version of [reconsiderM].
     */
    fun <A : Input, B> reconsider(f: (A, Decision<State, Output>) -> Decision<State, B>): Schedule<F, A, B> =
      reconsiderM { a, dec -> M.just(f(a, dec)) }

    /**
     * Run an effect with a [Decision]. Does not alter the decision.
     */
    fun <A : Input> onDecision(fa: (A, Decision<State, Output>) -> Kind<F, Unit>): Schedule<F, A, Output> =
      updated { f ->
        { a: A, s: State ->
          M.run {
            f(a, s).flatTap { dec -> fa(a, dec) }
          }
        }
      }
  }

  /**
   * A single decision. Contains the decision to continue, the delay, the new state and the (lazy) result of a Schedule.
   */
  data class Decision<out A, out B>(val cont: Boolean, val delay: Duration, val state: A, val finish: Eval<B>) : DecisionOf<A, B> {

    operator fun not(): Decision<A, B> = copy(cont = !cont)

    fun <C, D> bimap(f: (A) -> C, g: (B) -> D): Decision<C, D> = Decision(cont, delay, f(state), finish.map(g))

    fun <C> mapLeft(f: (A) -> C): Decision<C, B> = bimap(f, ::identity)

    fun <D> mapRight(g: (B) -> D): Decision<A, D> = bimap(::identity, g)

    fun <C, D> combineWith(
      other: Decision<C, D>,
      f: (Boolean, Boolean) -> Boolean,
      g: (Duration, Duration) -> Duration
    ): Decision<Tuple2<A, C>, Tuple2<B, D>> = Decision(
      f(cont, other.cont),
      g(delay, other.delay),
      Tuple2(state, other.state),
      Eval.applicative().tupled(finish, other.finish).fix()
    )

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
    operator fun <F, S, A, B> invoke(M: Monad<F>, initial: Kind<F, S>, update: (a: A, s: S) -> Kind<F, Decision<S, B>>): Schedule<F, A, B> =
      ScheduleImpl(M, initial, update)

    /**
     * Creates a schedule that continues without delay and just returns its input.
     */
    fun <F, A> identity(M: Monad<F>): Schedule<F, A, A> = invoke(M, M.unit()) { a, s ->
      M.just(Decision.cont(0.seconds, s, Eval.now(a)))
    }

    /**
     * Creates a schedule that continues without delay and always returns Unit
     */
    fun <F, A> unit(M: Monad<F>): Schedule<F, A, Unit> =
      identity<F, A>(M).unit()

    /**
     * Create a schedule that unfolds effectfully using a seed value [c] and a unfold function [f].
     * This keeps the current state (the current seed) as [State] and runs the unfold function on every
     *  call to update. This schedule always continues without delay and returns the current state.
     */
    fun <F, I, A> unfoldM(M: Monad<F>, c: Kind<F, A>, f: (A) -> Kind<F, A>): Schedule<F, I, A> =
      invoke(M, c) { _: I, acc -> M.run { f(acc).map { Decision.cont(0.seconds, it, Eval.now(it)) } } }

    /**
     * Non-effectful variant of [unfoldM]
     */
    fun <F, I, A> unfold(M: Monad<F>, c: A, f: (A) -> A): Schedule<F, I, A> =
      unfoldM(M, M.just(c)) { M.just(f(it)) }

    /**
     * Create a schedule that continues forever and returns the number of iterations.
     */
    fun <F, A> forever(M: Monad<F>): Schedule<F, A, Int> =
      unfold(M, 0) { it + 1 }

    /**
     * Create a schedule that continues n times and returns the number of iterations.
     */
    fun <F, A> recurs(M: Monad<F>, n: Int): Schedule<F, A, Int> =
      forever<F, A>(M).whileOutput { it <= n }

    /**
     * Create a schedule that only ever retries once.
     */
    fun <F, A> once(M: Monad<F>): Schedule<F, A, Unit> = recurs<F, A>(M, 1).unit()

    /**
     * Create a schedule that never retries.
     * This is a difference with zio, where they define never as a schedule that itself never executes.
     */
    fun <F, A> never(M: Monad<F>): Schedule<F, A, Nothing> =
      recurs<F, A>(M, 0).map { throw IllegalStateException("Impossible") }

    /**
     * Create a schedule that uses another schedule to generate the delay of this schedule.
     * Continues for as long as [delaySchedule] continues and adds the output of [delaySchedule] to
     *  the delay that [delaySchedule] produced. Also returns the full delay as output.
     *
     * A common use case is to define a unfolding schedule and use the result to change the delay.
     *  For an example see the implementation of [spaced], [linear], [fibonacci] or [exponential]
     */
    fun <F, A> delayed(M: Monad<F>, delaySchedule: Schedule<F, A, Duration>): Schedule<F, A, Duration> =
      (delaySchedule.modifyDelay { a, b -> M.just(a + b) } as ScheduleImpl<F, Any?, A, Duration>)
        .reconsider { _, dec -> dec.copy(finish = Eval.now(dec.delay)) }

    /**
     * Create a schedule which collects all it's inputs in a list
     */
    fun <F, A> collect(M: Monad<F>): Schedule<F, A, List<A>> =
      identity<F, A>(M).collect()

    /**
     * Create a schedule that continues as long as [đ] returns true.
     */
    fun <F, A> doWhile(M: Monad<F>, f: (A) -> Boolean): Schedule<F, A, A> =
      identity<F, A>(M).whileInput(f)

    /**
     * Create a schedule that continues until [đ] returns true.
     */
    fun <F, A> doUntil(M: Monad<F>, f: (A) -> Boolean): Schedule<F, A, A> =
      identity<F, A>(M).untilInput(f)

    /**
     * Create a schedule with an effectful handler on the input.
     */
    fun <F, A> logInput(MM: Monad<F>, f: (A) -> Kind<F, Unit>): Schedule<F, A, A> =
      identity<F, A>(MM).logInput(f)

    /**
     * Create a schedule with an effectful handler on the output.
     */
    fun <F, A> logOutput(M: Monad<F>, f: (A) -> Kind<F, Unit>): Schedule<F, A, A> =
      identity<F, A>(M).logOutput(f)

    /**
     * Create a schedule that returns its delay.
     */
    fun <F, A> delay(M: Monad<F>): Schedule<F, A, Duration> =
      (forever<F, A>(M) as ScheduleImpl<F, Int, A, Int>).reconsider { _: A, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.delay)) }

    /**
     * Create a schedule that returns its decisions
     */
    fun <F, A> decision(M: Monad<F>): Schedule<F, A, Boolean> =
      (forever<F, A>(M) as ScheduleImpl<F, Int, A, Int>).reconsider { _: A, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.cont)) }

    /**
     * Create a schedule that continues with fixed delay.
     */
    fun <F, A> spaced(M: Monad<F>, interval: Duration): Schedule<F, A, Int> =
      forever<F, A>(M).delayed { d -> d + interval }

    /**
     * Create a schedule that continues with increasing delay by adding the last two delays.
     */
    fun <F, A> fibonacci(M: Monad<F>, one: Duration): Schedule<F, A, Duration> =
      delayed(
        M,
        unfold<F, A, Tuple2<Duration, Duration>>(M, 0.seconds toT one) { (del, acc) ->
          acc toT del + acc
        }.map { it.a }
      )

    /**
     * Create a schedule which increases its delay linear by n * base where n is the number of
     *  executions.
     */
    fun <F, A> linear(M: Monad<F>, base: Duration): Schedule<F, A, Duration> =
      delayed(
        M,
        forever<F, A>(M).map { base * it }
      )

    /**
     * Create a schedule that increases its delay exponentially with a given factor and base.
     * Delay can be calculated as [base] * factor ^ n where n is the number of executions.
     */
    fun <F, A> exponential(M: Monad<F>, base: Duration, factor: Double = 2.0): Schedule<F, A, Duration> =
      delayed(
        M,
        forever<F, A>(M).map { base * factor.pow(it).roundToInt() }
      )

    /**
     * Interface with all above methods partially applied to some monad M for convenience.
     */
    interface ScheduleFor<M> {
      fun MM(): Monad<M>

      fun <A> identity(): Schedule<M, A, A> = identity(MM())

      fun <A, I> unfoldM(c: Kind<M, A>, f: (A) -> Kind<M, A>): Schedule<M, I, A> =
        unfoldM(MM(), c, f)

      fun <A, I> unfold(c: A, f: (A) -> A): Schedule<M, I, A> =
        unfold(MM(), c, f)

      fun <A> forever(): Schedule<M, A, Int> =
        forever(MM())

      fun <A> unit(): Schedule<M, A, Unit> = unit(MM())

      fun <A> recurs(n: Int): Schedule<M, A, Int> =
        recurs(MM(), n)

      fun <A> once(): Schedule<M, A, Unit> = once(MM())
      fun <S, A> delayed(delaySchedule: Schedule<M, A, Duration>): Schedule<M, A, Duration> =
        delayed(MM(), delaySchedule)

      fun <A> collect(): Schedule<M, A, List<A>> =
        collect(MM())

      fun <A> doWhile(f: (A) -> Boolean): Schedule<M, A, A> =
        doWhile(MM(), f)

      fun <A> doUntil(f: (A) -> Boolean): Schedule<M, A, A> =
        doUntil(MM(), f)

      fun <A> logInput(f: (A) -> Kind<M, Unit>): Schedule<M, A, A> =
        logInput(MM(), f)

      fun <A> logOutput(f: (A) -> Kind<M, Unit>): Schedule<M, A, A> =
        logOutput(MM(), f)

      fun <A> delay(): Schedule<M, A, Duration> =
        delay(MM())

      fun <A> decision(): Schedule<M, A, Boolean> =
        decision(MM())

      fun <A> spaced(interval: Duration): Schedule<M, A, Int> =
        spaced(MM(), interval)

      fun <A> fibonacci(one: Duration): Schedule<M, A, Duration> =
        fibonacci(MM(), one)

      fun <A> linear(base: Duration): Schedule<M, A, Duration> =
        linear(MM(), base)

      fun <A> exponential(base: Duration, factor: Double = 2.0): Schedule<M, A, Duration> =
        exponential(MM(), base, factor)

      fun <A> never(): Schedule<M, A, Nothing> = never(MM())
    }

    /**
     * Build a schedule with functions that have the `Monad` already partially applied. Prefer this to the general combinators as soon as you create more than one schedule and combine it somehow.
     */
    fun <M, Input, Output> withMonad(MM: Monad<M>, f: ScheduleFor<M>.() -> Schedule<M, Input, Output>): Schedule<M, Input, Output> =
      object : ScheduleFor<M> {
        override fun MM(): Monad<M> = MM
      }.f()
  }
}

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Returns the last output from the policy or raises an error if a repeat failed.
 */
fun <F, A, B> Kind<F, A>.repeat(
  CF: Concurrent<F>,
  schedule: Schedule<F, A, B>
): Kind<F, B> = repeatOrElse(CF, schedule) { e, _ -> CF.raiseError(e) }

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Returns the last output from the policy or raises an error if a repeat failed.
 */
fun <F, E, A, B> Kind<F, A>.repeat(
  ME: MonadError<F, E>,
  T: Timer<F>,
  schedule: Schedule<F, A, B>
): Kind<F, B> = repeatOrElse(ME, T, schedule) { e, _ -> ME.raiseError(e) }

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during repetition.
 */
fun <F, A, B> Kind<F, A>.repeatOrElse(
  CF: Concurrent<F>,
  schedule: Schedule<F, A, B>,
  orElse: (Throwable, Option<B>) -> Kind<F, B>
): Kind<F, B> = CF.run { repeatOrElseEither(CF, schedule, orElse).map { it.fold(::identity, ::identity) } }

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during repetition.
 */
fun <F, E, A, B> Kind<F, A>.repeatOrElse(
  ME: MonadError<F, E>,
  T: Timer<F>,
  schedule: Schedule<F, A, B>,
  orElse: (E, Option<B>) -> Kind<F, B>
): Kind<F, B> = ME.run { repeatOrElseEither(ME, T, schedule, orElse).map { it.fold(::identity, ::identity) } }

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during repetition.
 */
fun <F, A, B, C> Kind<F, A>.repeatOrElseEither(
  CF: Concurrent<F>,
  schedule: Schedule<F, A, B>,
  orElse: (Throwable, Option<B>) -> Kind<F, C>
): Kind<F, Either<C, B>> = repeatOrElseEither(CF, Timer(CF), schedule, orElse)

/**
 * Run this effect once and, if it succeeded, decide using the passed policy if the effect should be repeated and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during repetition.
 */
fun <F, E, A, B, C> Kind<F, A>.repeatOrElseEither(
  ME: MonadError<F, E>,
  T: Timer<F>,
  schedule: Schedule<F, A, B>,
  orElse: (E, Option<B>) -> Kind<F, C>
): Kind<F, Either<C, B>> = ME.run {
  (schedule as Schedule.ScheduleImpl<F, Any?, A, B>)

  fun loop(last: A, state: Any?): Kind<F, Either<C, B>> =
    schedule.update(last, state)
      .flatMap { desc ->
        if (desc.cont)
          flatMap { a -> T.sleep(desc.delay).flatMap { loop(a, desc.state) } }
            .handleErrorWith { e -> orElse(e, desc.finish.value().some()).map { Left(it) } }
        else just(desc.finish.value().right())
      }

  return flatMap { a -> schedule.initialState.flatMap { b -> loop(a, b) } }
    .handleErrorWith { e -> orElse(e, None).map { Left(it) } }
}

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Returns the result of the effect if if it was successful or re-raises the last error encountered when the schedule ends.
 */
fun <F, A, B> Kind<F, A>.retry(
  CF: Concurrent<F>,
  schedule: Schedule<F, Throwable, B>
): Kind<F, A> = retryOrElse(CF, schedule) { e, _ -> CF.raiseError(e) }

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Returns the result of the effect if if it was successful or re-raises the last error encountered when the schedule ends.
 */
fun <F, E, A, B> Kind<F, A>.retry(
  ME: MonadError<F, E>,
  T: Timer<F>,
  schedule: Schedule<F, E, B>
): Kind<F, A> = retryOrElse(ME, T, schedule) { e, _ -> ME.raiseError(e) }

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
fun <F, A, B> Kind<F, A>.retryOrElse(
  CF: Concurrent<F>,
  schedule: Schedule<F, Throwable, B>,
  orElse: (Throwable, B) -> Kind<F, A>
): Kind<F, A> = CF.run { retryOrElseEither(CF, schedule, orElse).map { it.fold(::identity, ::identity) } }

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
fun <F, E, A, B> Kind<F, A>.retryOrElse(
  ME: MonadError<F, E>,
  T: Timer<F>,
  schedule: Schedule<F, E, B>,
  orElse: (E, B) -> Kind<F, A>
): Kind<F, A> = ME.run { retryOrElseEither(ME, T, schedule, orElse).map { it.fold(::identity, ::identity) } }

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
fun <F, A, B, C> Kind<F, A>.retryOrElseEither(
  CF: Concurrent<F>,
  schedule: Schedule<F, Throwable, B>,
  orElse: (Throwable, B) -> Kind<F, C>
): Kind<F, Either<C, A>> = retryOrElseEither(CF, Timer(CF), schedule, orElse)

/**
 * Run an effect and, if it fails, decide using the passed policy if the effect should be retried and if so, with how much delay.
 * Also offers a function to handle errors if they are encountered during retrial.
 */
fun <F, E, A, B, C> Kind<F, A>.retryOrElseEither(
  ME: MonadError<F, E>,
  T: Timer<F>,
  schedule: Schedule<F, E, B>,
  orElse: (E, B) -> Kind<F, C>
): Kind<F, Either<C, A>> = ME.run {
  (schedule as Schedule.ScheduleImpl<F, Any?, E, B>)

  fun loop(state: Any?): Kind<F, Either<C, A>> =
    flatMap { just(it.right()) }
      .handleErrorWith { e ->
        schedule.update(e, state)
          .flatMap { dec ->
            if (dec.cont) T.sleep(dec.delay).followedBy(loop(dec.state))
            else orElse(e, dec.finish.value()).map(::Left)
          }
      }

  schedule.initialState.flatMap(::loop)
}
