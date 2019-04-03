package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.eval.applicative.applicative
import arrow.effects.typeclasses.Duration
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.nanoseconds
import arrow.effects.typeclasses.seconds
import arrow.higherkind
import arrow.typeclasses.Monad
import kotlin.math.*
import kotlin.random.Random

@higherkind
interface Schedule<F, State, Input, Output> : ScheduleOf<F, State, Input, Output> {

  fun MF(): Monad<F>

  val initialState: Kind<F, State>

  fun update(a: Input, s: State): Kind<F, Decision<State, Output>>

  fun <B> map(f: (Output) -> B): Schedule<F, State, Input, B> = object : Schedule<F, State, Input, B> {
    override val initialState: Kind<F, State> = this@Schedule.initialState
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Input, s: State): Kind<F, Decision<State, B>> = MF().run {
      this@Schedule.update(a, s).map { it.mapRight(f) }
    }
  }

  fun <B> contramap(f: (B) -> Input): Schedule<F, State, B, Output> = object : Schedule<F, State, B, Output> {
    override val initialState: Kind<F, State> = this@Schedule.initialState
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: B, s: State): Kind<F, Decision<State, Output>> =
      this@Schedule.update(f(a), s)
  }

  fun <B, C> dimap(f: (B) -> Input, g: (Output) -> C): Schedule<F, State, B, C> = contramap(f).map(g)

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

  operator fun not(): Schedule<F, State, Input, Output> = updated { f ->
    { a: Input, s: State ->
      MF().run {
        f(a, s).map { dec -> !dec }
      }
    }
  }

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

  fun whileOutput(f: (Output) -> Boolean): Schedule<F, State, Input, Output> =
    check { _, output -> MF().run { just(f(output)) } }

  fun <A : Input> whileInput(f: (A) -> Boolean): Schedule<F, State, A, Output> =
    check { input, _ -> MF().run { just(f(input)) } }

  fun untilOutput(f: (Output) -> Boolean): Schedule<F, State, Input, Output> = !whileOutput(f)

  fun <A : Input> untilInput(f: (A) -> Boolean): Schedule<F, State, A, Output> = !whileInput(f)

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

  infix fun <S, A : Input, B> and(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })

  infix fun <S, A : Input, B> or(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, Tuple2<Output, B>> =
    combineWith(other, { a, b -> a || b }, { a, b -> min(a.nanoseconds, b.nanoseconds).nanoseconds })

  infix fun <S, A : Input, B> zipRight(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, B> =
    (this and other).map { it.b }

  infix fun <S, A : Input, B> zipLeft(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, A, Output> =
    (this and other).map { it.a }

  infix fun <S, A : Input, B> andThen(other: Schedule<F, S, A, B>): Schedule<F, Either<State, S>, A, Either<Output, B>> = object : Schedule<F, Either<State, S>, A, Either<Output, B>> {
    override val initialState: Kind<F, Either<State, S>> = MF().run { this@Schedule.initialState.map { it.left() } }
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: A, s: Either<State, S>): Kind<F, Decision<Either<State, S>, Either<Output, B>>> =
      MF().run {
        s.fold(ifLeft = { s ->
          this@Schedule.update(a, s).flatMap { dec ->
            if (dec.cont) just(dec.bimap({ it.left() }, { it.left() }))
            else MF().binding {
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

  fun <B> const(b: B): Schedule<F, State, Input, B> = map { b }

  fun unit(): Schedule<F, State, Input, Unit> = const(Unit)

  fun <A : Input, B> reconsiderM(f: (A, Decision<State, Output>) -> Kind<F, Decision<State, B>>): Schedule<F, State, A, B> =
    updated { update ->
      { a: A, s: State ->
        MF().binding {
          val dec = !update(a, s)
          !f(a, dec)
        }
      }
    }

  fun <A : Input, B> reconsider(f: (A, Decision<State, Output>) -> Decision<State, B>): Schedule<F, State, A, B> =
    reconsiderM { a, dec -> MF().run { just(f(a, dec)) } }

  fun <A : Input> onDecision(fa: (A, Decision<State, Output>) -> Kind<F, Unit>): Schedule<F, State, A, Output> =
    updated { f ->
      { a: A, s: State ->
        MF().run {
          f(a, s).effectM { dec -> fa(a, dec) }
        }
      }
    }

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

  fun delayed(f: (Duration) -> Duration): Schedule<F, State, Input, Output> =
    modifyDelay { _, duration -> MF().run { just(f(duration)) } }

  fun jittered(min: Double = 0.0, max: Double = 0.0, MF: MonadDefer<F>): Schedule<F, State, Input, Output> =
    modifyDelay { _, duration ->
      MF.delay { (duration.nanoseconds * Random.nextDouble(min, max)).roundToLong().nanoseconds }
    }

  fun logInput(f: (Input) -> Kind<F, Unit>): Schedule<F, State, Input, Output> =
    updated { update ->
      { a: Input, s: State ->
        MF().run {
          update(a, s).forEffect(f(a))
        }
      }
    }

  fun logOutput(f: (Output) -> Kind<F, Unit>): Schedule<F, State, Input, Output> =
    updated { update ->
      { a: Input, s: State ->
        MF().run {
          update(a, s).effectM { dec -> f(dec.finish.value()) }
        }
      }
    }

  fun <C> foldM(
    initial: Kind<F, C>,
    f: (C, Output) -> Kind<F, C>
  ): Schedule<F, Tuple2<State, C>, Input, C> = object : Schedule<F, Tuple2<State, C>, Input, C> {
    override val initialState: Kind<F, Tuple2<State, C>> = MF().tupled(this@Schedule.initialState, initial)
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Input, s: Tuple2<State, C>): Kind<F, Decision<Tuple2<State, C>, C>> =
      MF().binding {
        val dec = !this@Schedule.update(a, s.a)
        val c = !f(s.b, dec.finish.value())
        dec.bimap({ s -> s toT c }, { c })
      }
  }

  fun <C> fold(initial: C, f: (C, Output) -> C): Schedule<F, Tuple2<State, C>, Input, C> =
    foldM(MF().just(initial)) { acc, o -> MF().just(f(acc, o)) }

  fun collect(): Schedule<F, Tuple2<State, List<Output>>, Input, List<Output>> =
    fold(emptyList()) { acc, o -> acc + listOf(o) }

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

  infix fun <S, B> compose(other: Schedule<F, S, B, Input>): Schedule<F, Tuple2<S, State>, B, Output> =
    other pipe this

  infix fun <S, A, B> tupled(other: Schedule<F, S, A, B>): Schedule<F, Tuple2<State, S>, Tuple2<Input, A>, Tuple2<Output, B>> = object : Schedule<F, Tuple2<State, S>, Tuple2<Input, A>, Tuple2<Output, B>> {
    override val initialState: Kind<F, Tuple2<State, S>> = MF().tupled(this@Schedule.initialState, other.initialState)
    override fun MF(): Monad<F> = this@Schedule.MF()
    override fun update(a: Tuple2<Input, A>, s: Tuple2<State, S>): Kind<F, Decision<Tuple2<State, S>, Tuple2<Output, B>>> =
      MF().map(this@Schedule.update(a.a, s.a), other.update(a.b, s.b)) { (dec1, dec2) ->
        dec1.combineWith(dec2, { a, b -> a && b }, { a, b -> max(a.nanoseconds, b.nanoseconds).nanoseconds })
      }
  }

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

    fun <F, S, A, B> invoke(MF: MonadDefer<F>, initial: Kind<F, S>, update: (a: A, s: S) -> Kind<F, Decision<S, B>>): Schedule<F, S, A, B> = object: Schedule<F, S, A, B> {
      override val initialState: Kind<F, S> = initial
      override fun MF(): Monad<F> = MF
      override fun update(a: A, s: S): Kind<F, Decision<S, B>> = update(a, s)
    }

    fun <F, A> MonadDefer<F>.identity(): Schedule<F, Unit, A, A> = invoke(this@identity, this@identity.unit()) { a, s ->
      this@identity.just(Decision.cont(0.seconds, s, Eval.now(a)))
    }

    fun <F, A> MonadDefer<F>.unfoldM(c: Kind<F, A>, f: (A) -> Kind<F, A>): Schedule<F, A, *, A> =
      invoke(this, c) { _: Any?, acc -> f(acc).map { Decision.cont(0.seconds, it, Eval.now(it)) } }

    fun <F, A> MonadDefer<F>.unfold(c: A, f: (A) -> A): Schedule<F, A, *, A> =
      unfoldM(just(c)) { just(f(it)) }

    fun <F> MonadDefer<F>.forever(): Schedule<F, Int, *, Int> =
      unfold(0) { it + 1 }

    fun <F> MonadDefer<F>.recurs(n: Int): Schedule<F, Int, *, Int> =
      forever().whileOutput { it <= n }

    fun <F> MonadDefer<F>.once(): Schedule<F, Int, *, Unit> = recurs(1).unit()

    fun <F, S, A> MonadDefer<F>.delayed(delaySchedule: Schedule<F, S, A, Duration>): Schedule<F, S, A, Duration> =
      delaySchedule.modifyDelay { a, b -> just(a + b) }
        .reconsider { _, dec -> dec.copy(finish = Eval.now(dec.delay)) }

    fun <F, A> MonadDefer<F>.collect(): Schedule<F, Tuple2<Unit, List<A>>, A, List<A>> =
      identity<F, A>().collect()

    fun <F, A> MonadDefer<F>.doWhile(f: (A) -> Boolean): Schedule<F, Unit, A, A> =
      identity<F, A>().whileInput(f)

    fun <F, A> MonadDefer<F>.doUntil(f: (A) -> Boolean): Schedule<F, Unit, A, A> =
      identity<F, A>().untilInput(f)

    fun <F, A> MonadDefer<F>.logInput(f: (A) -> Kind<F, Unit>): Schedule<F, Unit, A, A> =
      identity<F, A>().logInput(f)

    fun <F, A> MonadDefer<F>.logOutput(f: (A) -> Kind<F, Unit>): Schedule<F, Unit, A, A> =
      identity<F, A>().logOutput(f)

    fun <F> MonadDefer<F>.delay(): Schedule<F, *, *, Duration> =
      forever().reconsider { _: Any?, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.delay)) }

    fun <F> MonadDefer<F>.decision(): Schedule<F, *, *, Boolean> =
      forever().reconsider { _: Any?, decision -> Decision(cont = decision.cont, delay = decision.delay, state = decision.state, finish = Eval.now(decision.cont)) }

    fun <F> MonadDefer<F>.spaced(interval: Duration): Schedule<F, *, *, Int> =
      forever().delayed { d -> d + interval }

    fun <F> MonadDefer<F>.fibonacci(one: Duration): Schedule<F, *, *, Duration> =
      delayed(
        unfold(0.seconds toT one) { (del, acc) ->
          del toT del + acc
        }.map { it.a }
      )

    fun <F> MonadDefer<F>.linear(base: Duration): Schedule<F, *, *, Duration> =
      delayed(
        forever().map { base * it }
      )

    fun <F> MonadDefer<F>.exponential(base: Duration, factor: Double = 2.0): Schedule<F, *, *, Duration> =
      delayed(
        forever().map { base * factor.pow(it).roundToInt() }
      )
  }

  @higherkind
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