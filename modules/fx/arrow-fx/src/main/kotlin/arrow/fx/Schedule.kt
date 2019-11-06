package arrow.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.fix
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.nanoseconds
import arrow.fx.typeclasses.seconds
import arrow.typeclasses.Monad
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class ForSchedule private constructor() {
  companion object
}
typealias ScheduleOf<M, State, Input, Output> = arrow.Kind4<ForSchedule, M, State, Input, Output>
typealias SchedulePartialOf<M, State, Input> = arrow.Kind3<ForSchedule, M, State, Input>
typealias ScheduleKindedJ<M, State, Input, Output> = arrow.HkJ4<ForSchedule, M, State, Input, Output>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <M, State, Input, Output> ScheduleOf<M, State, Input, Output>.fix(): Schedule<M, State, Input, Output> =
  this as Schedule<M, State, Input, Output>

class ForDecision private constructor() {
  companion object
}
typealias DecisionOf<A, B> = arrow.Kind2<ForDecision, A, B>
typealias DecisionPartialOf<A> = arrow.Kind<ForDecision, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B> DecisionOf<A, B>.fix(): Schedule.Decision<A, B> =
  this as Schedule.Decision<A, B>

/**
 * A schedule represents a function Tuple2<State, Input> -> Kind<M, Decision<State, Output>>
 *
 * Which roughly translates to: "A function which given an initial state and an input produces
 *  a decision whether or not to continue with a new state and an output.
 *
 * The schedule interface provides a lot of helper methods to easily build these functions from
 *  small combinators.
 *
 * TODO example here for both an easy and a rather complex schedule (think jittered exponential backoff to like 100s and then constant retrys for a few mins)
 */
interface Schedule<M, State, Input, Output> : ScheduleOf<M, State, Input, Output> {

  /**
   * The monad the schedule operates over. More often than not this will be IO but it is not limited to it.
   */
  fun MF(): Monad<M>

  val initialState: Kind<M, State>

  fun update(a: Input, s: State): Kind<M, Decision<State, Output>>

  /**
   * Change the output of a schedule. Does not alter the decision of the schedule.
   */
  fun <B> map(f: (Output) -> B): Schedule<M, State, Input, B> = object : Schedule<M, State, Input, B> {
    override val initialState: Kind<M, State> = this@Schedule.initialState
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: Input, s: State): Kind<M, Decision<State, B>> = MF().run {
      this@Schedule.update(a, s).map { it.mapRight(f) }
    }
  }

  /**
   * Change the input of the schedule. May alter a schedules decision if it is based on input.
   */
  fun <B> contramap(f: (B) -> Input): Schedule<M, State, B, Output> = object : Schedule<M, State, B, Output> {
    override val initialState: Kind<M, State> = this@Schedule.initialState
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: B, s: State): Kind<M, Decision<State, Output>> =
      this@Schedule.update(f(a), s)
  }

  /**
   * Change both output and input of a schedule. May alter a schedules decision if it is based on input.
   */
  fun <B, C> dimap(f: (B) -> Input, g: (Output) -> C): Schedule<M, State, B, C> = contramap(f).map(g)

  /**
   * Always retry a schedule regardless of the decision made prior to invoking this method.
   */
  fun forever(): Schedule<M, State, Input, Output> = updated { f ->
    { a: Input, s: State ->
      MF().run {
        f(a, s).flatMap { dec ->
          if (dec.cont) just(dec)
          else this@Schedule.initialState.map { state -> dec.copy(cont = true, state = state) }
        }
      }
    }
  }

  /**
   * Invert the decision of a schedule.
   */
  operator fun not(): Schedule<M, State, Input, Output> = updated { f ->
    { a: Input, s: State ->
      MF().run {
        f(a, s).map { dec -> !dec }
      }
    }
  }

  /**
   * Conditionally check on both the input and the output whether or not to continue.
   */
  fun <A : Input> check(pred: (A, Output) -> Kind<M, Boolean>): Schedule<M, State, A, Output> =
    updated { f ->
      { a: A, s: State ->
        MF().run {
          f(a, s).flatMap { dec ->
            if (dec.cont) pred(a, dec.finish.value()).map { dec.copy(cont = it) }
            else just(dec)
          }
        }
      }
    }

  fun unit(): Schedule<M, State, Input, Unit> = map { Unit }

  /**
   * Continue or stop the schedule based on the output
   */
  fun whileOutput(f: (Output) -> Boolean): Schedule<M, State, Input, Output> =
    check { _, output -> MF().run { just(f(output)) } }

  /**
   * Continue or stop the schedule based on the input
   */
  fun <A : Input> whileInput(f: (A) -> Boolean): Schedule<M, State, A, Output> =
    check { input, _ -> MF().run { just(f(input)) } }

  /**
   * `untilOutput(f) = whileOutput(f).not()`
   */
  fun untilOutput(f: (Output) -> Boolean): Schedule<M, State, Input, Output> = !whileOutput(f)

  /**
   * `untilInput(f) = whileInput(f).not()`
   */
  fun <A : Input> untilInput(f: (A) -> Boolean): Schedule<M, State, A, Output> = !whileInput(f)

  /**
   * Combine with another schedule by combining the result and the delay of the [Decision] with the functions [f] and [g]
   */
  fun <S, A : Input, B> combineWith(
    other: Schedule<M, S, A, B>,
    f: (Boolean, Boolean) -> Boolean,
    g: (Duration, Duration) -> Duration
  ): Schedule<M, Tuple2<State, S>, A, Tuple2<Output, B>> = object : Schedule<M, Tuple2<State, S>, A, Tuple2<Output, B>> {
    override fun MF(): Monad<M> = this@Schedule.MF()
    override val initialState: Kind<M, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun update(a: A, s: Tuple2<State, S>): Kind<M, Decision<Tuple2<State, S>, Tuple2<Output, B>>> =
      MF().run { MF().tupled(this@Schedule.update(a, s.a), other.update(a, s.b)).map { it.a.combineWith(it.b, f, g) } }
  }

  /**
   * Combine two schedules. Continues only when both continue and chooses the maximum delay.
   */
  infix fun <S, A : Input, B> and(other: Schedule<M, S, A, B>): Schedule<M, Tuple2<State, S>, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules. Continues if one continues and chooses the minimum delay
   */
  infix fun <S, A : Input, B> or(other: Schedule<M, S, A, B>): Schedule<M, Tuple2<State, S>, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a || b }, { a, b -> min(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules with [and] but throw away the left schedule's result
   */
  infix fun <S, A : Input, B> zipRight(other: Schedule<M, S, A, B>): Schedule<M, Tuple2<State, S>, A, B> =
    (this and other).map { it.b }

  /**
   * Combine two schedules with [and] but throw away the right schedule's result
   */
  infix fun <S, A : Input, B> zipLeft(other: Schedule<M, S, A, B>): Schedule<M, Tuple2<State, S>, A, Output> =
    (this and other).map { it.a }

  /**
   * Execute one schedule after the other. When the first schedule ends, it continues with the second.
   */
  infix fun <S, A : Input, B> andThen(other: Schedule<M, S, A, B>): Schedule<M, Either<State, S>, A, Either<Output, B>> = object : Schedule<M, Either<State, S>, A, Either<Output, B>> {
    override val initialState: Kind<M, Either<State, S>> = MF().run { this@Schedule.initialState.map { it.left() } }
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: A, s: Either<State, S>): Kind<M, Decision<Either<State, S>, Either<Output, B>>> =
      MF().run {
        s.fold(ifLeft = { s ->
          this@Schedule.update(a, s).flatMap { dec ->
            if (dec.cont) just(dec.bimap({ it.left() }, { it.left() }))
            else MF().fx.monad {
              val newState = !other.initialState
              val newDec = !other.update(a, newState)
              newDec.bimap({ it.right() }, { it.right() })
            }
          }
        }, ifRight = { s ->
          other.update(a, s).map { it.bimap({ it.right() }, { it.right() }) }
        })
      }
  }

  /**
   * Change the result of a [Schedule] to always be [b]
   */
  fun <B> const(b: B): Schedule<M, State, Input, B> = map { b }

  /**
   * Inspect and change the [Decision] of a [Schedule]. Also given access to the input.
   */
  fun <A : Input, B> reconsiderM(f: (A, Decision<State, Output>) -> Kind<M, Decision<State, B>>): Schedule<M, State, A, B> =
    updated { update ->
      { a: A, s: State ->
        MF().fx.monad {
          val dec = !update(a, s)
          !f(a, dec)
        }
      }
    }

  /**
   * Non-effectful version of [reconsiderM].
   */
  fun <A : Input, B> reconsider(f: (A, Decision<State, Output>) -> Decision<State, B>): Schedule<M, State, A, B> =
    reconsiderM { a, dec -> MF().run { just(f(a, dec)) } }

  /**
   * Run an effect with a [Decision]. Does not alter the decision.
   */
  fun <A : Input> onDecision(fa: (A, Decision<State, Output>) -> Kind<M, Unit>): Schedule<M, State, A, Output> =
    updated { f ->
      { a: A, s: State ->
        MF().run {
          f(a, s).flatTap { dec -> fa(a, dec) }
        }
      }
    }

  /**
   * Change the delay of a resulting [Decision] based on the [Output] and the produced delay.
   */
  fun modifyDelay(f: (Output, Duration) -> Kind<M, Duration>): Schedule<M, State, Input, Output> =
    updated { update ->
      { a: Input, s: State ->
        MF().run {
          update(a, s).flatMap { step ->
            f(step.finish.value(), step.delay).map { d -> step.copy(delay = d) }
          }
        }
      }
    }

  /**
   * Adjust the delay of a schedule's [Decision]
   */
  fun delayed(f: (Duration) -> Duration): Schedule<M, State, Input, Output> =
    modifyDelay { _, duration -> MF().run { just(f(duration)) } }

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
  fun jittered(genRand: Kind<M, Double>): Schedule<M, State, Input, Output> =
    modifyDelay { _, duration ->
      MF().run {
        genRand.map { (duration.nanoseconds * it).roundToLong().nanoseconds }
      }
    }

  /**
   * Run a effectful handler on every input. Does not alter the decision.
   */
  fun logInput(f: (Input) -> Kind<M, Unit>): Schedule<M, State, Input, Output> =
    updated { update ->
      { a: Input, s: State ->
        MF().run {
          update(a, s).productL(f(a))
        }
      }
    }

  /**
   * Run a effectful handler on every output. Does not alter the decision.
   */
  fun logOutput(f: (Output) -> Kind<M, Unit>): Schedule<M, State, Input, Output> =
    updated { update ->
      { a: Input, s: State ->
        MF().run {
          update(a, s).flatTap { dec -> f(dec.finish.value()) }
        }
      }
    }

  /**
   * Accumulate the results of a schedule by folding over them effectfully.
   */
  fun <C> foldM(
    initial: Kind<M, C>,
    f: (C, Output) -> Kind<M, C>
  ): Schedule<M, Tuple2<State, C>, Input, C> = object : Schedule<M, Tuple2<State, C>, Input, C> {
    override val initialState: Kind<M, Tuple2<State, C>> = MF().tupled(this@Schedule.initialState, initial)
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: Input, s: Tuple2<State, C>): Kind<M, Decision<Tuple2<State, C>, C>> =
      MF().fx.monad {
        val dec = !this@Schedule.update(a, s.a)
        val c = !f(s.b, dec.finish.value())
        dec.bimap({ s -> s toT c }, { c })
      }
  }

  /**
   * Non-effectful version of [foldM].
   */
  fun <C> fold(initial: C, f: (C, Output) -> C): Schedule<M, Tuple2<State, C>, Input, C> =
    foldM(MF().just(initial)) { acc, o -> MF().just(f(acc, o)) }

  /**
   * Accumulate the results of every execution to a list
   */
  fun collect(): Schedule<M, Tuple2<State, List<Output>>, Input, List<Output>> =
    fold(emptyList()) { acc, o -> acc + listOf(o) }

  /**
   * Compose this schedule with the other schedule by piping the output of this schedule
   *  into the input of the other.
   */
  infix fun <S, B> pipe(other: Schedule<M, S, Output, B>): Schedule<M, Tuple2<State, S>, Input, B> = object : Schedule<M, Tuple2<State, S>, Input, B> {
    override val initialState: Kind<M, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: Input, s: Tuple2<State, S>): Kind<M, Decision<Tuple2<State, S>, B>> =
      MF().run {
        this@Schedule.update(a, s.a).flatMap { dec1 ->
          other.update(dec1.finish.value(), s.b).map { dec2 ->
            dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> a + b }).mapRight { it.b }
          }
        }
      }
  }

  /**
   * Infix variant of pipe with reversed order.
   */
  infix fun <S, B> compose(other: Schedule<M, S, B, Input>): Schedule<M, Tuple2<S, State>, B, Output> =
    other pipe this

  /**
   * Combine two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  infix fun <S, A, B> tupled(other: Schedule<M, S, A, B>): Schedule<M, Tuple2<State, S>, Tuple2<Input, A>, Tuple2<Output, B>> = object : Schedule<M, Tuple2<State, S>, Tuple2<Input, A>, Tuple2<Output, B>> {
    override val initialState: Kind<M, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: Tuple2<Input, A>, s: Tuple2<State, S>): Kind<M, Decision<Tuple2<State, S>, Tuple2<Output, B>>> =
      MF().map(this@Schedule.update(a.a, s.a), other.update(a.b, s.b)) { (dec1, dec2) ->
        dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })
      }
  }

  /**
   * Combine two schedules with different input and output and conditionally choose between the two.
   * Continues when the chosen schedule continues and uses the chosen schedules delay.
   */
  infix fun <S, A, B> choose(other: Schedule<M, S, A, B>): Schedule<M, Tuple2<State, S>, Either<Input, A>, Either<Output, B>> = object : Schedule<M, Tuple2<State, S>, Either<Input, A>, Either<Output, B>> {
    override val initialState: Kind<M, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: Either<Input, A>, s: Tuple2<State, S>): Kind<M, Decision<Tuple2<State, S>, Either<Output, B>>> =
      MF().run {
        a.fold(ifLeft = {
          this@Schedule.update(it, s.a).map { it.mapLeft { it toT s.b }.mapRight { it.left() } }
        }, ifRight = {
          other.update(it, s.b).map { it.mapLeft { s.a toT it }.mapRight { it.right() } }
        })
      }
  }

  fun <A, B> updated(
    f: ((Input, State) -> Kind<M, Decision<State, Output>>) -> (A, State) -> Kind<M, Decision<State, B>>
  ): Schedule<M, State, A, B> = object : Schedule<M, State, A, B> {
    override val initialState: Kind<M, State> = this@Schedule.initialState
    override fun MF(): Monad<M> = this@Schedule.MF()
    override fun update(a: A, s: State): Kind<M, Decision<State, B>> =
      f { i, s -> this@Schedule.update(i, s) }(a, s)
  }

  companion object {

    /**
     * Invoke constructor to manually define a schedule. If you need this, please consider adding it to arrow or suggest
     *  a change to avoid using this manual method.
     */
    operator fun <M, S, A, B> invoke(MF: Monad<M>, initial: Kind<M, S>, update: (a: A, s: S) -> Kind<M, Decision<S, B>>): Schedule<M, S, A, B> = object : Schedule<M, S, A, B> {
      override val initialState: Kind<M, S> = initial
      override fun MF(): Monad<M> = MF
      override fun update(a: A, s: S): Kind<M, Decision<S, B>> = update(a, s)
    }

    /**
     * Creates a schedule that continues without delay and just returns its input.
     */
    fun <M, A> identity(MM: Monad<M>): Schedule<M, Unit, A, A> = invoke(MM, MM.unit()) { a, s ->
      MM.just(Decision.cont(0.seconds, s, Eval.now(a)))
    }

    /**
     * Create a schedule that unfolds effectfully using a seed value [c] and a unfold function [f].
     * This keeps the current state (the current seed) as [State] and runs the unfold function on every
     *  call to update. This schedule always continues without delay and returns the current state.
     */
    fun <M, A> unfoldM(MM: Monad<M>, c: Kind<M, A>, f: (A) -> Kind<M, A>): Schedule<M, A, Nothing, A> =
      invoke(MM, c) { _, acc -> MM.run { f(acc).map { Decision.cont(0.seconds, it, Eval.now(it)) } } }

    /**
     * Non-effectful variant of [unfoldM]
     */
    fun <M, A> unfold(MM: Monad<M>, c: A, f: (A) -> A): Schedule<M, A, Nothing, A> =
      unfoldM(MM, MM.just(c)) { MM.just(f(it)) }

    /**
     * Create a schedule that continues forever and returns the number of iterations.
     */
    fun <M> forever(MM: Monad<M>): Schedule<M, Int, Nothing, Int> =
      unfold(MM, 0) { it + 1 }

    /**
     * Create a schedule that continues n times and returns the number of iterations.
     */
    fun <M> recurs(MM: Monad<M>, n: Int): Schedule<M, Int, Nothing, Int> =
      forever(MM).whileOutput { it <= n }

    /**
     * Create a schedule that only ever retries once.
     */
    fun <M> once(MM: Monad<M>): Schedule<M, Int, Nothing, Unit> = recurs(MM, 1).unit()

    /**
     * Create a schedule that uses another schedule to generate the delay of this schedule.
     * Continues for as long as [delaySchedule] continues and adds the output of [delaySchedule] to
     *  the delay that [delaySchedule] produced. Also returns the full delay as output.
     *
     * A common use case is to define a unfolding schedule and use the result to change the delay.
     *  For an example see the implementation of [spaced], [linear], [fibonacci] or [exponential]
     */
    fun <M, S, A> delayed(MM: Monad<M>, delaySchedule: Schedule<M, S, A, Duration>): Schedule<M, S, A, Duration> =
      delaySchedule.modifyDelay { a, b -> MM.just(a + b) }
        .reconsider { _, dec -> dec.copy(finish = Eval.now(dec.delay)) }

    /**
     * Create a schedule which collects all it's inputs in a list
     */
    fun <M, A> collect(MM: Monad<M>): Schedule<M, Tuple2<Unit, List<A>>, A, List<A>> =
      identity<M, A>(MM).collect()

    /**
     * Create a schedule that continues as long as [đ] returns true.
     */
    fun <M, A> doWhile(MM: Monad<M>, f: (A) -> Boolean): Schedule<M, Unit, A, A> =
      identity<M, A>(MM).whileInput(f)

    /**
     * Create a schedule that continues until [đ] returns true.
     */
    fun <M, A> doUntil(MM: Monad<M>, f: (A) -> Boolean): Schedule<M, Unit, A, A> =
      identity<M, A>(MM).untilInput(f)

    /**
     * Create a schedule with an effectful handler on the input.
     */
    fun <M, A> logInput(MM: Monad<M>, f: (A) -> Kind<M, Unit>): Schedule<M, Unit, A, A> =
      identity<M, A>(MM).logInput(f)

    /**
     * Create a schedule with an effectful handler on the output.
     */
    fun <M, A> logOutput(MM: Monad<M>, f: (A) -> Kind<M, Unit>): Schedule<M, Unit, A, A> =
      identity<M, A>(MM).logOutput(f)

    /**
     * Create a schedule that returns its delay.
     */
    fun <M> delay(MM: Monad<M>): Schedule<M, Int, Nothing, Duration> =
      forever(MM).reconsider { _: Nothing, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.delay)) }

    /**
     * Create a schedule that returns its decisions
     */
    fun <M> decision(MM: Monad<M>): Schedule<M, Int, Nothing, Boolean> =
      forever(MM).reconsider { _: Nothing, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.cont)) }

    /**
     * Create a schedule that continues with fixed delay.
     */
    fun <M> spaced(MM: Monad<M>, interval: Duration): Schedule<M, Int, Nothing, Int> =
      forever(MM).delayed { d -> d + interval }

    /**
     * Create a schedule that continues with increasing delay by adding the last two delays.
     */
    fun <M> fibonacci(MM: Monad<M>, one: Duration): Schedule<M, Tuple2<Duration, Duration>, Nothing, Duration> =
      delayed(
        MM,
        unfold(MM, 0.seconds toT one) { (del, acc) ->
          del toT del + acc
        }.map { it.a }
      )

    /**
     * Create a schedule which increases its delay linear by n * base where n is the number of
     *  executions.
     */
    fun <M> linear(MM: Monad<M>, base: Duration): Schedule<M, Int, Nothing, Duration> =
      delayed(
        MM,
        forever(MM).map { base * it }
      )

    /**
     * Create a schedule that increases its delay exponentially with a given factor and base.
     * Delay can be calculated as [base] * factor ^ n where n is the number of executions.
     */
    fun <M> exponential(MM: Monad<M>, base: Duration, factor: Double = 2.0): Schedule<M, Int, Nothing, Duration> =
      delayed(
        MM,
        forever(MM).map { base * factor.pow(it).roundToInt() }
      )

    /**
     * Interface with all above methods partially applied to some monad M for convenience.
     */
    interface ScheduleFor<M> {
      fun MM(): Monad<M>

      fun <A> identity(): Schedule<M, Unit, A, A> = identity(MM())
      fun <A> unfoldM(c: Kind<M, A>, f: (A) -> Kind<M, A>): Schedule<M, A, Nothing, A> =
        unfoldM(MM(), c, f)
      fun <A> unfold(c: A, f: (A) -> A): Schedule<M, A, Nothing, A> =
        unfold(MM(), c, f)
      fun forever(): Schedule<M, Int, Nothing, Int> =
        forever(MM())
      fun recurs(n: Int): Schedule<M, Int, Nothing, Int> =
        recurs(MM(), n)
      fun once(): Schedule<M, Int, Nothing, Unit> = once(MM())
      fun <S, A> delayed(delaySchedule: Schedule<M, S, A, Duration>): Schedule<M, S, A, Duration> =
        delayed(MM(), delaySchedule)
      fun <A> collect(): Schedule<M, Tuple2<Unit, List<A>>, A, List<A>> =
        collect(MM())
      fun <A> doWhile(f: (A) -> Boolean): Schedule<M, Unit, A, A> =
        doWhile(MM(), f)
      fun <A> doUntil(f: (A) -> Boolean): Schedule<M, Unit, A, A> =
        doUntil(MM(), f)
      fun <A> logInput(f: (A) -> Kind<M, Unit>): Schedule<M, Unit, A, A> =
        logInput(MM(), f)
      fun <A> logOutput(f: (A) -> Kind<M, Unit>): Schedule<M, Unit, A, A> =
        logOutput(MM(), f)
      fun delay(): Schedule<M, Int, Nothing, Duration> =
        delay(MM())
      fun decision(): Schedule<M, Int, Nothing, Boolean> =
        decision(MM())
      fun spaced(interval: Duration): Schedule<M, Int, Nothing, Int> =
        spaced(MM(), interval)
      fun fibonacci(one: Duration): Schedule<M, Tuple2<Duration, Duration>, Nothing, Duration> =
        fibonacci(MM(), one)
      fun linear(base: Duration): Schedule<M, Int, Nothing, Duration> =
        linear(MM(), base)
      fun exponential(base: Duration, factor: Double = 2.0): Schedule<M, Int, Nothing, Duration> =
        exponential(MM(), base, factor)
    }

    fun <M, State, Input, Output> withMonad(MM: Monad<M>, f: ScheduleFor<M>.() -> Schedule<M, State, Input, Output>): Schedule<M, State, Input, Output> =
      object: ScheduleFor<M> {
        override fun MM(): Monad<M> = MM
      }.f()
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
}
