package arrow.mtl.extensions

import arrow.Kind
import arrow.core.toT
import arrow.mtl.StateT
import arrow.mtl.StateTPartialOf
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.id.monad.monad
import arrow.core.left
import arrow.core.right
import arrow.mtl.State
import arrow.mtl.extensions.statet.applicative.applicative
import arrow.mtl.extensions.statet.functor.functor
import arrow.mtl.extensions.statet.monad.monad
import arrow.mtl.StateApi
import arrow.mtl.StatePartialOf
import arrow.mtl.StateTOf
import arrow.mtl.fix
import arrow.mtl.runM
import arrow.typeclasses.MonadCombine
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.SemigroupK
import arrow.undocumented
import arrow.extension

@extension
@undocumented
interface StateTFunctor<S, F> : Functor<StateTPartialOf<S, F>> {

  fun FF(): Functor<F>

  override fun <A, B> StateTOf<S, F, A>.map(f: (A) -> B): StateT<S, F, B> =
    fix().map(FF(), f)
}

@extension
@undocumented
interface StateTApplicative<S, F> : Applicative<StateTPartialOf<S, F>>, StateTFunctor<S, F> {

  fun MF(): Monad<F>

  override fun FF(): Functor<F> = MF()

  override fun <A, B> StateTOf<S, F, A>.map(f: (A) -> B): StateT<S, F, B> =
    fix().map(MF(), f)

  override fun <A> just(a: A): StateT<S, F, A> =
    StateT(MF().just({ s: S -> MF().just(Tuple2(s, a)) }))

  override fun <A, B> StateTOf<S, F, A>.ap(ff: StateTOf<S, F, (A) -> B>): StateT<S, F, B> =
    fix().ap(MF(), ff)

  override fun <A, B> StateTOf<S, F, A>.product(fb: StateTOf<S, F, B>): StateT<S, F, Tuple2<A, B>> =
    fix().product(MF(), fb)
}

@extension
@undocumented
interface StateTMonad<S, F> : Monad<StateTPartialOf<S, F>>, StateTApplicative<S, F> {

  override fun MF(): Monad<F>

  override fun <A, B> StateTOf<S, F, A>.map(f: (A) -> B): StateT<S, F, B> =
    fix().map(MF(), f)

  override fun <A, B> StateTOf<S, F, A>.flatMap(f: (A) -> StateTOf<S, F, B>): StateT<S, F, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> StateTOf<S, F, Either<A, B>>): StateT<S, F, B> =
    StateT.tailRecM(MF(), a, f)

  override fun <A, B> StateTOf<S, F, A>.ap(ff: StateTOf<S, F, (A) -> B>): StateT<S, F, B> =
    ff.fix().map2(MF(), this) { f, a -> f(a) }
}

@extension
@undocumented
interface StateTSemigroupK<S, F> : SemigroupK<StateTPartialOf<S, F>> {

  fun FF(): Monad<F>

  fun SS(): SemigroupK<F>

  override fun <A> StateTOf<S, F, A>.combineK(y: StateTOf<S, F, A>): StateT<S, F, A> =
    fix().combineK(FF(), SS(), y)
}

@extension
@undocumented
interface StateTApplicativeError<S, F, E> : ApplicativeError<StateTPartialOf<S, F>, E>, StateTApplicative<S, F> {

  fun ME(): MonadError<F, E>

  override fun FF(): Functor<F> = ME()

  override fun MF(): Monad<F> = ME()

  override fun <A> raiseError(e: E): StateTOf<S, F, A> = ME().run {
    StateT.liftF(this, raiseError(e))
  }

  override fun <A> StateTOf<S, F, A>.handleErrorWith(f: (E) -> StateTOf<S, F, A>): StateT<S, F, A> = ME().run {
    State(this) { s ->
      runM(this, s).handleErrorWith { e ->
        f(e).runM(this, s)
      }
    }
  }
}

@extension
@undocumented
interface StateTMonadError<S, F, E> : MonadError<StateTPartialOf<S, F>, E>, StateTApplicativeError<S, F, E>, StateTMonad<S, F> {

  override fun ME(): MonadError<F, E>

  override fun MF(): Monad<F> = ME()
}

@extension
@undocumented
interface StateTMonadThrow<S, F> : MonadThrow<StateTPartialOf<S, F>>, StateTMonadError<S, F, Throwable> {
  override fun ME(): MonadError<F, Throwable>
}

@extension
@undocumented
interface StateTContravariantInstance<S, F> : Contravariant<StateTPartialOf<S, F>> {

  fun CF(): Contravariant<F>

  fun MF(): Monad<F>

  override fun <A, B> Kind<StateTPartialOf<S, F>, A>.contramap(f: (B) -> A): Kind<StateTPartialOf<S, F>, B> =
    StateT(MF()) { s ->
      CF().run {
        runM(MF(), s).contramap { (s, b) ->
          s toT f(b)
        }
      }
    }
}

@extension
@undocumented
interface StateTDivideInstance<S, F> : Divide<StateTPartialOf<S, F>>, StateTContravariantInstance<S, F> {

  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  fun MFF(): Monad<F>
  override fun MF(): Monad<F> = MFF()

  override fun <A, B, Z> divide(fa: Kind<StateTPartialOf<S, F>, A>, fb: Kind<StateTPartialOf<S, F>, B>, f: (Z) -> Tuple2<A, B>): Kind<StateTPartialOf<S, F>, Z> =
    StateT(MF()) { s ->
      DF().divide(fa.runM(MF(), s), fb.runM(MF(), s)) { (s, z) ->
        val (a, b) = f(z)
        (s toT a) toT (s toT b)
      }
    }
}

@extension
@undocumented
interface StateTDivisibleInstance<S, F> : Divisible<StateTPartialOf<S, F>>, StateTDivideInstance<S, F> {
  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()
  fun MFFF(): Monad<F>
  override fun MFF(): Monad<F> = MFFF()

  override fun <A> conquer(): Kind<StateTPartialOf<S, F>, A> =
    StateT(MF()) { DFF().conquer() }
}

@extension
@undocumented
interface StateTDecidableInstante<S, F> : Decidable<StateTPartialOf<S, F>>, StateTDivisibleInstance<S, F> {
  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()
  fun MFFFF(): Monad<F>
  override fun MFFF(): Monad<F> = MFFFF()

  override fun <A, B, Z> choose(fa: Kind<StateTPartialOf<S, F>, A>, fb: Kind<StateTPartialOf<S, F>, B>, f: (Z) -> Either<A, B>): Kind<StateTPartialOf<S, F>, Z> =
    StateT(MF()) { s ->
      DFFF().choose(fa.runM(MF(), s), fb.runM(MF(), s)) { (s, z) ->
        f(z).fold({ a ->
          (s toT a).left()
        }, { b ->
          (s toT b).right()
        })
      }
    }
}

/**
 * Alias for[StateT.Companion.applicative]
 */
fun <S> StateApi.applicative(): Applicative<StateTPartialOf<S, ForId>> = StateT.applicative(Id.monad())

/**
 * Alias for [StateT.Companion.functor]
 */
fun <S> StateApi.functor(): Functor<StateTPartialOf<S, ForId>> = StateT.functor(Id.monad())

/**
 * Alias for [StateT.Companion.monad]
 */
fun <S> StateApi.monad(): Monad<StateTPartialOf<S, ForId>> = StateT.monad(Id.monad())

fun <S, F, A> StateT.Companion.fx(M: Monad<F>, c: suspend MonadSyntax<StateTPartialOf<S, F>>.() -> A): StateT<S, F, A> =
  StateT.monad<S, F>(M).fx.monad(c).fix()

fun <S, A> StateApi.fx(c: suspend MonadSyntax<StatePartialOf<S>>.() -> A): State<S, A> =
  StateApi.monad<S>().fx.monad(c).fix()

@extension
interface StateTMonadState<S, F> : MonadState<StateTPartialOf<S, F>, S>, StateTMonad<S, F> {

  override fun MF(): Monad<F>

  override fun get(): StateT<S, F, S> = StateT.get(MF())

  override fun set(s: S): StateT<S, F, Unit> = StateT.set(MF(), s)
}

@extension
interface StateTMonadCombine<S, F> : MonadCombine<StateTPartialOf<S, F>>, StateTMonad<S, F>, StateTSemigroupK<S, F> {

  fun MC(): MonadCombine<F>

  override fun MF(): Monad<F> = MC()

  override fun FF(): Monad<F> = MC()

  override fun SS(): SemigroupK<F> = MC()

  override fun <A> empty(): Kind<StateTPartialOf<S, F>, A> = liftT(MC().empty())

  fun <A> liftT(ma: Kind<F, A>): StateT<S, F, A> = FF().run {
    StateT(just { s: S -> ma.map { a: A -> s toT a } })
  }

  override fun <A> Kind<StateTPartialOf<S, F>, A>.orElse(b: Kind<StateTPartialOf<S, F>, A>): Kind<StateTPartialOf<S, F>, A> {
    val x = this.fix()
    val y = b.fix()
    return MC().run { StateT(just { s: S -> x.run(this, s).orElse(y.run(this, s)) }) }
  }
}
