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
import arrow.fx.typeclasses.MonadDefer
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
typealias ScheduleOf<F, State, Input, Output> = arrow.Kind4<ForSchedule, F, State, Input, Output>
typealias SchedulePartialOf<F, State, Input> = arrow.Kind3<ForSchedule, F, State, Input>
typealias ScheduleKindedJ<F, State, Input, Output> = arrow.HkJ4<ForSchedule, F, State, Input, Output>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <F, State, Input, Output> ScheduleOf<F, State, Input, Output>.fix(): Schedule<F, State, Input, Output> =
  this as Schedule<F, State, Input, Output>

class ForDecision private constructor() {
  companion object
}
typealias DecisionOf<A, B> = arrow.Kind2<ForDecision, A, B>
typealias DecisionPartialOf<A> = arrow.Kind<ForDecision, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B> DecisionOf<A, B>.fix(): Schedule.Decision<A, B> =
  this as Schedule.Decision<A, B>

/**
 * A schedule represents a function Tuple2<State, Input> -> Kind<F, Decision<State, Output>>
 *
 * Which roughly translates to: "A function which given an initial state and an input produces
 *  a decision wether or not to continue with a new state and an output.
 *
 * The schedule interface provides a lot of helper methods to easily build these functions from
 *  small combinators.
 *
 * TODO example here for both an easy and a rather complex schedule (think jittered exponential backoff to like 100s and then constant retrys for a few mins)
 */
interface Schedule<F, State, Input, Output> : ScheduleOf<F, State, Input, Output> {

  /**
   * The monad the schedule operates over. More often than not this will be IO but it is not limited to it.
   */
  fun MF(): Monad<F>

  val initialState: Kind<F, State>

  fun update(a: Input, s: State): Kind<F, Decision<State, Output>>

  /**
   * Change the output of a schedule. Does not alter the decision of the schedule.
   */
  fun <B> map(f: (Output) -> B): Schedule<F, State, Input, B> = object : Schedule<F, State, Input, B> {
    override val initialState: Kind<F, State> = this@Schedule.initialState
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Input, s: State): Kind<F, Decision<State, B>> = MF().run {
      this@Schedule.update(a, s).map { it.mapRight(f) }
    }
  }

  /**
   * Change the input of the schedule. May alter a schedules decision if it is based on input.
   */
  fun <B> contramap(f: (B) -> Input): Schedule<F, State, B, Output> = object : Schedule<F, State, B, Output> {
    override val initialState: Kind<F, State> = this@Schedule.initialState
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: B, s: State): Kind<F, Decision<State, Output>> =
      this@Schedule.update(f(a), s)
  }

  /**
   * Change both output and input of a schedule. May alter a schedules decision if it is based on input.
   */
  fun <B, C> dimap(f: (B) -> Input, g: (Output) -> C): Schedule<F, State, B, C> = contramap(f).map(g)

  /**
   * Always retry a schedule regardless of the decision made prior to invoking this method.
   */
  fun forever(): Schedule<F, State, Input, Output> = updated { f ->
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
  operator fun not(): Schedule<F, State, Input, Output> = updated { f ->
    { a: Input, s: State ->
      MF().run {
        f(a, s).map { dec -> !dec }
      }
    }
  }

  /**
   * Conditionally check on both the input and the output whether or not to continue.
   */
  fun <A : Input> check(pred: (A, Output) -> Kind<F, Boolean>): Schedule<F, State, A, Output> =
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

  fun unit(): Schedule<F, State, Input, Unit> = map { Unit }

  /**
   * Continue or stop the schedule based on the output
   */
  fun whileOutput(f: (Output) -> Boolean): Schedule<F, State, Input, Output> =
    check { _, output -> MF().run { just(f(output)) } }

  /**
   * Continue or stop the schedule based on the input
   */
  fun <A : Input> whileInput(f: (A) -> Boolean): Schedule<F, State, A, Output> =
    check { input, _ -> MF().run { just(f(input)) } }

  /**
   * `untilOutput(f) = whileOutput(f).not()`
   */
  fun untilOutput(f: (Output) -> Boolean): Schedule<F, State, Input, Output> = !whileOutput(f)

  /**
   * `untilInput(f) = whileInput(f).not()`
   */
  fun <A : Input> untilInput(f: (A) -> Boolean): Schedule<F, State, A, Output> = !whileInput(f)

  /**
   * Combine with another schedule by combining the result and the delay of the [Decision] with the functions [f] and [g]
   */
  fun <S, A : Input, B> combineWith(
    other: Schedule<F, S, A, B>,
    f: (Boolean, Boolean) -> Boolean,
    g: (Duration, Duration) -> Duration
  ): Schedule<F, Tuple2<State, S>, A, Tuple2<Output, B>> = object : Schedule<F, Tuple2<State, S>, A, Tuple2<Output, B>> {
    override fun MF(): Monad<F> = this@Schedule.MF()
    override val initialState: Kind<F, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun update(a: A, s: Tuple2<State, S>): Kind<F, Decision<Tuple2<State, S>, Tuple2<Output, B>>> =
      MF().run { MF().tupled(this@Schedule.update(a, s.a), other.update(a, s.b)).map { it.a.combineWith(it.b, f, g) } }
  }

  /**
   * Combine two schedules. Continues only when both continue and chooses the maximum delay.
   */
  infix fun <S, A : Input, B> and(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules. Continues if one continues and chooses the minimum delay
   */
  infix fun <S, A : Input, B> or(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a || b }, { a, b -> min(a.nanoseconds, b.nanoseconds).nanoseconds })

  /**
   * Combine two schedules with [and] but throw away the left schedule's result
   */
  infix fun <S, A : Input, B> zipRight(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, B> =
    (this and other).map { it.b }

  /**
   * Combine two schedules with [and] but throw away the right schedule's result
   */
  infix fun <S, A : Input, B> zipLeft(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, Output> =
    (this and other).map { it.a }

  /**
   * Execute one schedule after the other. When the first schedule ends, it continues with the second.
   */
  infix fun <S, A : Input, B> andThen(other: Schedule<F, S, A, B>): Schedule<F, Either<State, S>, A, Either<Output, B>> = object : Schedule<F, Either<State, S>, A, Either<Output, B>> {
    override val initialState: Kind<F, Either<State, S>> = MF().run { this@Schedule.initialState.map { it.left() } }
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: A, s: Either<State, S>): Kind<F, Decision<Either<State, S>, Either<Output, B>>> =
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
  fun <B> const(b: B): Schedule<F, State, Input, B> = map { b }

  /**
   * Inspect and change the [Decision] of a [Schedule]. Also given access to the input.
   */
  fun <A : Input, B> reconsiderM(f: (A, Decision<State, Output>) -> Kind<F, Decision<State, B>>): Schedule<F, State, A, B> =
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
  fun <A : Input, B> reconsider(f: (A, Decision<State, Output>) -> Decision<State, B>): Schedule<F, State, A, B> =
    reconsiderM { a, dec -> MF().run { just(f(a, dec)) } }

  /**
   * Run an effect with a [Decision]. Does not alter the decision.
   */
  fun <A : Input> onDecision(fa: (A, Decision<State, Output>) -> Kind<F, Unit>): Schedule<F, State, A, Output> =
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
  fun modifyDelay(f: (Output, Duration) -> Kind<F, Duration>): Schedule<F, State, Input, Output> =
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
  fun delayed(f: (Duration) -> Duration): Schedule<F, State, Input, Output> =
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
  fun jittered(genRand: Kind<F, Double>): Schedule<F, State, Input, Output> =
    modifyDelay { _, duration ->
      MF().run {
        genRand.map { (duration.nanoseconds * it).roundToLong().nanoseconds }
      }
    }

  /**
   * Run a effectful handler on every input. Does not alter the decision.
   */
  fun logInput(f: (Input) -> Kind<F, Unit>): Schedule<F, State, Input, Output> =
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
  fun logOutput(f: (Output) -> Kind<F, Unit>): Schedule<F, State, Input, Output> =
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
    initial: Kind<F, C>,
    f: (C, Output) -> Kind<F, C>
  ): Schedule<F, Tuple2<State, C>, Input, C> = object : Schedule<F, Tuple2<State, C>, Input, C> {
    override val initialState: Kind<F, Tuple2<State, C>> = MF().tupled(this@Schedule.initialState, initial)
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Input, s: Tuple2<State, C>): Kind<F, Decision<Tuple2<State, C>, C>> =
      MF().fx.monad {
        val dec = !this@Schedule.update(a, s.a)
        val c = !f(s.b, dec.finish.value())
        dec.bimap({ s -> s toT c }, { c })
      }
  }

  /**
   * Non-effectful version of [foldM].
   */
  fun <C> fold(initial: C, f: (C, Output) -> C): Schedule<F, Tuple2<State, C>, Input, C> =
    foldM(MF().just(initial)) { acc, o -> MF().just(f(acc, o)) }

  /**
   * Accumulate the results of every execution to a list
   */
  fun collect(): Schedule<F, Tuple2<State, List<Output>>, Input, List<Output>> =
    fold(emptyList()) { acc, o -> acc + listOf(o) }

  /**
   * Compose this schedule with the other schedule by piping the output of this schedule
   *  into the input of the other.
   */
  infix fun <S, B> pipe(other: Schedule<F, S, Output, B>): Schedule<F, Tuple2<State, S>, Input, B> = object : Schedule<F, Tuple2<State, S>, Input, B> {
    override val initialState: Kind<F, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Input, s: Tuple2<State, S>): Kind<F, Decision<Tuple2<State, S>, B>> =
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
  infix fun <S, B> compose(other: Schedule<F, S, B, Input>): Schedule<F, Tuple2<S, State>, B, Output> =
    other pipe this

  /**
   * Combine two with different input and output using and. Continues when both continue and uses the maximum delay.
   */
  infix fun <S, A, B> tupled(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, Tuple2<Input, A>, Tuple2<Output, B>> = object : Schedule<F, Tuple2<State, S>, Tuple2<Input, A>, Tuple2<Output, B>> {
    override val initialState: Kind<F, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Tuple2<Input, A>, s: Tuple2<State, S>): Kind<F, Decision<Tuple2<State, S>, Tuple2<Output, B>>> =
      MF().map(this@Schedule.update(a.a, s.a), other.update(a.b, s.b)) { (dec1, dec2) ->
        dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })
      }
  }

  /**
   * Combine two schedules with different input and output and conditionally choose between the two.
   * Continues when the chosen schedule continues and uses the chosen schedules delay.
   */
  infix fun <S, A, B> choose(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, Either<Input, A>, Either<Output, B>> = object : Schedule<F, Tuple2<State, S>, Either<Input, A>, Either<Output, B>> {
    override val initialState: Kind<F, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Either<Input, A>, s: Tuple2<State, S>): Kind<F, Decision<Tuple2<State, S>, Either<Output, B>>> =
      MF().run {
        a.fold(ifLeft = {
          this@Schedule.update(it, s.a).map { it.mapLeft { it toT s.b }.mapRight { it.left() } }
        }, ifRight = {
          other.update(it, s.b).map { it.mapLeft { s.a toT it }.mapRight { it.right() } }
        })
      }
  }

  fun <A, B> updated(
    f: ((Input, State) -> Kind<F, Decision<State, Output>>) -> (A, State) -> Kind<F, Decision<State, B>>
  ): Schedule<F, State, A, B> = object : Schedule<F, State, A, B> {
    override val initialState: Kind<F, State> = this@Schedule.initialState
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: A, s: State): Kind<F, Decision<State, B>> =
      f { i, s -> this@Schedule.update(i, s) }(a, s)
  }

  companion object {

    /**
     * Invoke constructor to manually define a schedule. If you need this, please consider adding it to arrow or suggest
     *  a change to avoid using this manual method.
     */
    fun <F, S, A, B> invoke(MF: Monad<F>, initial: Kind<F, S>, update: (a: A, s: S) -> Kind<F, Decision<S, B>>): Schedule<F, S, A, B> = object : Schedule<F, S, A, B> {
      override val initialState: Kind<F, S> = initial
      override fun MF(): Monad<F> = MF
      override fun update(a: A, s: S): Kind<F, Decision<S, B>> = update(a, s)
    }

    /**
     * Creates a schedule that continues without delay and just returns its input.
     */
    fun <F, A> Monad<F>.identity(): Schedule<F, Unit, A, A> = invoke(this@identity, this@identity.unit()) { a, s ->
      this@identity.just(Decision.cont(0.seconds, s, Eval.now(a)))
    }

    /**
     * Create a schedule that unfolds effectfully using a seed value [c] and a unfold function [f].
     * This keeps the current state (the current seed) as [State] and runs the unfold function on every
     *  call to update. This schedule always continues without delay and returns the current state.
     */
    fun <F, A> Monad<F>.unfoldM(c: Kind<F, A>, f: (A) -> Kind<F, A>): Schedule<F, A, *, A> =
      invoke(this, c) { _: Any?, acc -> f(acc).map { Decision.cont(0.seconds, it, Eval.now(it)) } }

    /**
     * Non-effectful variant of [unfoldM]
     */
    fun <F, A> Monad<F>.unfold(c: A, f: (A) -> A): Schedule<F, A, *, A> =
      unfoldM(just(c)) { just(f(it)) }

    /**
     * Create a schedule that continues forever and returns the number of iterations.
     */
    fun <F> Monad<F>.forever(): Schedule<F, Int, *, Int> =
      unfold(0) { it + 1 }

    /**
     * Create a schedule that continues n times and returns the number of iterations.
     */
    fun <F> Monad<F>.recurs(n: Int): Schedule<F, Int, *, Int> =
      forever().whileOutput { it <= n }

    /**
     * Create a schedule that only ever retries once.
     */
    fun <F> Monad<F>.once(): Schedule<F, Int, *, Unit> = recurs(1).unit()

    /**
     * Create a schedule that uses another schedule to generate the delay of this schedule.
     * Continues for as long as [delaySchedule] continues and adds the output of [delaySchedule] to
     *  the delay that [delaySchedule] produced. Also returns the full delay as output.
     *
     * A common use case is to define a unfolding schedule and use the result to change the delay.
     *  For an example see the implementation of [spaced], [linear], [fibonacci] or [exponential]
     */
    fun <F, S, A> Monad<F>.delayed(delaySchedule: Schedule<F, S, A, Duration>): Schedule<F, S, A, Duration> =
      delaySchedule.modifyDelay { a, b -> just(a + b) }
        .reconsider { _, dec -> dec.copy(finish = Eval.now(dec.delay)) }

    /**
     * Create a schedule which collects all it's inputs in a list
     */
    fun <F, A> Monad<F>.collect(): Schedule<F, Tuple2<Unit, List<A>>, A, List<A>> =
      identity<F, A>().collect()

    /**
     * Create a schedule that continues as long as [đ] returns true.
     */
    fun <F, A> Monad<F>.doWhile(f: (A) -> Boolean): Schedule<F, Unit, A, A> =
      identity<F, A>().whileInput(f)

    /**
     * Create a schedule that continues until [đ] returns true.
     */
    fun <F, A> Monad<F>.doUntil(f: (A) -> Boolean): Schedule<F, Unit, A, A> =
      identity<F, A>().untilInput(f)

    /**
     * Create a schedule with an effectful handler on the input.
     */
    fun <F, A> Monad<F>.logInput(f: (A) -> Kind<F, Unit>): Schedule<F, Unit, A, A> =
      identity<F, A>().logInput(f)

    /**
     * Create a schedule with an effectful handler on the output.
     */
    fun <F, A> Monad<F>.logOutput(f: (A) -> Kind<F, Unit>): Schedule<F, Unit, A, A> =
      identity<F, A>().logOutput(f)

    /**
     * Create a schedule that returns its delay.
     */
    fun <F> Monad<F>.delay(): Schedule<F, *, *, Duration> =
      forever().reconsider { _: Any?, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.delay)) }

    /**
     * Create a schedule that returns its decisions
     */
    fun <F> Monad<F>.decision(): Schedule<F, *, *, Boolean> =
      forever().reconsider { _: Any?, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.cont)) }

    /**
     * Create a schedule that continues with fixed delay.
     */
    fun <F> Monad<F>.spaced(interval: Duration): Schedule<F, *, *, Int> =
      forever().delayed { d -> d + interval }

    /**
     * Create a schedule that continues with increasing delay by adding the last two delays.
     */
    fun <F> Monad<F>.fibonacci(one: Duration): Schedule<F, *, *, Duration> =
      delayed(
        unfold(0.seconds toT one) { (del, acc) ->
          del toT del + acc
        }.map { it.a }
      )

    /**
     * Create a schedule which increases its delay linear by n * base where n is the number of
     *  executions.
     */
    fun <F> Monad<F>.linear(base: Duration): Schedule<F, *, *, Duration> =
      delayed(
        forever().map { base * it }
      )

    /**
     * Create a schedule that increases its delay exponentially with a given factor and base.
     * Delay can be calculated as [base] * factor ^ n where n is the number of executions.
     */
    fun <F> Monad<F>.exponential(base: Duration, factor: Double = 2.0): Schedule<F, *, *, Duration> =
      delayed(
        forever().map { base * factor.pow(it).roundToInt() }
      )
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
