package arrow.mtl.extensions

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.id.monad.monad
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.extension
import arrow.mtl.State
import arrow.mtl.StateApi
import arrow.mtl.StatePartialOf
import arrow.mtl.StateT
import arrow.mtl.StateTOf
import arrow.mtl.StateTPartialOf
import arrow.mtl.extensions.statet.applicative.applicative
import arrow.mtl.extensions.statet.functor.functor
import arrow.mtl.extensions.statet.monad.flatMap
import arrow.mtl.extensions.statet.monad.monad
import arrow.mtl.fix
import arrow.mtl.run
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.MonoidK
import arrow.typeclasses.SemigroupK
import arrow.undocumented

@extension
@undocumented
interface StateTFunctor<F, S> : Functor<StateTPartialOf<F, S>> {

  fun FF(): Functor<F>

  override fun <A, B> StateTOf<F, S, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(FF(), f)
}

@extension
@undocumented
interface StateTApplicative<F, S> : Applicative<StateTPartialOf<F, S>>, StateTFunctor<F, S> {

  fun MF(): Monad<F>

  override fun FF(): Functor<F> = MF()

  override fun <A, B> StateTOf<F, S, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(MF(), f)

  override fun <A> just(a: A): StateT<F, S, A> =
    StateT.just(MF(), a)

  override fun <A, B> StateTOf<F, S, A>.ap(ff: StateTOf<F, S, (A) -> B>): StateT<F, S, B> =
    fix().ap(MF(), ff)

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.lazyAp(ff: () -> Kind<StateTPartialOf<F, S>, (A) -> B>): Kind<StateTPartialOf<F, S>, B> =
    flatMap(MF()) { a -> ff().map { f -> f(a) } }
}

@extension
@undocumented
interface StateTMonad<F, S> : Monad<StateTPartialOf<F, S>>, StateTApplicative<F, S> {

  override fun MF(): Monad<F>

  override fun <A, B> StateTOf<F, S, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(MF(), f)

  override fun <A, B> StateTOf<F, S, A>.flatMap(f: (A) -> StateTOf<F, S, B>): StateT<F, S, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> StateTOf<F, S, Either<A, B>>): StateT<F, S, B> =
    StateT.tailRecM(MF(), a, f)

  override fun <A, B> StateTOf<F, S, A>.ap(ff: StateTOf<F, S, (A) -> B>): StateT<F, S, B> =
    fix().ap(MF(), ff.fix())

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.lazyAp(ff: () -> Kind<StateTPartialOf<F, S>, (A) -> B>): Kind<StateTPartialOf<F, S>, B> =
    StateT(AndThen.id<S>().flatMap { s ->
      AndThen(fix().runF).andThen { fa ->
        MF().run {
          fa.lazyAp { ff().run(s).map { (s, f) -> { (_, a): Tuple2<S, A> -> s toT f(a) } } }
        }
      }
    })
}

@extension
@undocumented
interface StateTSemigroupK<F, S> : SemigroupK<StateTPartialOf<F, S>> {
  fun SS(): SemigroupK<F>

  override fun <A> StateTOf<F, S, A>.combineK(y: StateTOf<F, S, A>): StateT<F, S, A> =
    fix().combineK(SS(), y)
}

@extension
@undocumented
interface StateTMonoidK<F, S> : MonoidK<StateTPartialOf<F, S>>, StateTSemigroupK<F, S> {
  fun MF(): Monad<F>
  fun MO(): MonoidK<F>
  override fun SS(): SemigroupK<F> = MO()

  override fun <A> empty(): Kind<StateTPartialOf<F, S>, A> = StateT.liftF(MF(), MO().empty<A>())
}

@extension
@undocumented
interface StateTApplicativeError<F, S, E> : ApplicativeError<StateTPartialOf<F, S>, E>, StateTApplicative<F, S> {

  fun ME(): MonadError<F, E>

  override fun FF(): Functor<F> = ME()

  override fun MF(): Monad<F> = ME()

  override fun <A> raiseError(e: E): StateTOf<F, S, A> = ME().run {
    StateT.liftF(this, raiseError(e))
  }

  override fun <A> StateTOf<F, S, A>.handleErrorWith(f: (E) -> StateTOf<F, S, A>): StateT<F, S, A> =
    StateT(AndThen.id<S>().flatMap { s ->
      AndThen(fix().runF).andThen {
        ME().run {
          it.handleErrorWith { e -> f(e).run(s) }
        }
      }
    })
}

@extension
@undocumented
interface StateTMonadError<F, S, E> : MonadError<StateTPartialOf<F, S>, E>, StateTApplicativeError<F, S, E>, StateTMonad<F, S> {

  override fun ME(): MonadError<F, E>

  override fun MF(): Monad<F> = ME()
}

@extension
@undocumented
interface StateTMonadThrow<F, S> : MonadThrow<StateTPartialOf<F, S>>, StateTMonadError<F, S, Throwable> {
  override fun ME(): MonadError<F, Throwable>
}

@extension
@undocumented
interface StateTContravariantInstance<F, S> : Contravariant<StateTPartialOf<F, S>> {

  fun CF(): Contravariant<F>

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.contramap(f: (B) -> A): Kind<StateTPartialOf<F, S>, B> =
    StateT(AndThen(fix().runF).andThen { fa -> CF().run { fa.contramap { (s, b): Tuple2<S, B> -> s toT f(b) } } })
}

@extension
@undocumented
interface StateTDivideInstance<F, S> : Divide<StateTPartialOf<F, S>>, StateTContravariantInstance<F, S> {

  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  override fun <A, B, Z> divide(fa: Kind<StateTPartialOf<F, S>, A>, fb: Kind<StateTPartialOf<F, S>, B>, f: (Z) -> Tuple2<A, B>): Kind<StateTPartialOf<F, S>, Z> =
    StateT(AndThen(fa.fix().runF).flatMap { fa ->
      AndThen(fb.fix().runF).andThen { fb ->
        DF().divide(fa, fb) { (s, z): Tuple2<S, Z> ->
          val (a, b) = f(z)
          (s toT a) toT (s toT b)
        }
      }
    })
}

@extension
@undocumented
interface StateTDivisibleInstance<F, S> : Divisible<StateTPartialOf<F, S>>, StateTDivideInstance<F, S> {
  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()

  override fun <A> conquer(): Kind<StateTPartialOf<F, S>, A> =
    StateT { DFF().conquer() }
}

@extension
@undocumented
interface StateTDecidableInstante<F, S> : Decidable<StateTPartialOf<F, S>>, StateTDivisibleInstance<F, S> {
  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()

  override fun <A, B, Z> choose(fa: Kind<StateTPartialOf<F, S>, A>, fb: Kind<StateTPartialOf<F, S>, B>, f: (Z) -> Either<A, B>): Kind<StateTPartialOf<F, S>, Z> =
    StateT(AndThen(fa.fix().runF).flatMap { fa ->
      AndThen(fb.fix().runF).andThen { fb ->
        DFFF().choose(fa, fb) { (s, z): Tuple2<S, Z> ->
          f(z).fold({ a ->
            (s toT a).left()
          }, { b ->
            (s toT b).right()
          })
        }
      }
    })
}

/**
 * Alias for[StateT.Companion.applicative]
 */
fun <S> StateApi.applicative(): Applicative<StateTPartialOf<ForId, S>> = StateT.applicative(Id.monad())

/**
 * Alias for [StateT.Companion.functor]
 */
fun <S> StateApi.functor(): Functor<StateTPartialOf<ForId, S>> = StateT.functor(Id.monad())

/**
 * Alias for [StateT.Companion.monad]
 */
fun <S> StateApi.monad(): Monad<StateTPartialOf<ForId, S>> = StateT.monad(Id.monad())

fun <F, S, A> StateT.Companion.fx(M: Monad<F>, c: suspend MonadSyntax<StateTPartialOf<F, S>>.() -> A): StateT<F, S, A> =
  StateT.monad<F, S>(M).fx.monad(c).fix()

fun <S, A> StateApi.fx(c: suspend MonadSyntax<StatePartialOf<S>>.() -> A): State<S, A> =
  StateApi.monad<S>().fx.monad(c).fix()

@extension
interface StateTMonadState<F, S> : MonadState<StateTPartialOf<F, S>, S>, StateTMonad<F, S> {

  override fun MF(): Monad<F>

  override fun get(): StateT<F, S, S> = StateT.get(MF())

  override fun set(s: S): StateT<F, S, Unit> = StateT.set(MF(), s)
}

@extension
interface StateTMonadCombine<F, S> : MonadCombine<StateTPartialOf<F, S>>, StateTMonad<F, S>, StateTAlternative<F, S> {

  fun MC(): MonadCombine<F>
  override fun AF(): Alternative<F> = MC()

  override fun MF(): Monad<F> = MC()

  override fun FF(): Monad<F> = MC()
  override fun MO(): MonoidK<F> = MC()

  override fun <A> empty(): Kind<StateTPartialOf<F, S>, A> = liftT(MC().empty())

  fun <A> liftT(ma: Kind<F, A>): StateT<F, S, A> = FF().run {
    StateT { s: S -> ma.map { a: A -> s toT a } }
  }
}

@extension
interface StateTAlternative<F, S> : Alternative<StateTPartialOf<F, S>>, StateTMonoidK<F, S>, StateTApplicative<F, S> {
  override fun MF(): Monad<F>
  override fun MO(): MonoidK<F> = AF()
  fun AF(): Alternative<F>

  override fun <A> empty(): Kind<StateTPartialOf<F, S>, A> = StateT.liftF(AF(), AF().empty<A>())

  override fun <A> Kind<StateTPartialOf<F, S>, A>.orElse(b: Kind<StateTPartialOf<F, S>, A>): Kind<StateTPartialOf<F, S>, A> =
    StateT(AndThen(fix().runF).flatMap { fa ->
      AndThen(b.fix().runF).andThen { fb ->
        AF().run { fa.orElse(fb) }
      }
    })

  // Note: This can stackoverflow if `F` is not stacksafe, which is a tradeoff for having true short-circuit
  override fun <A> Kind<StateTPartialOf<F, S>, A>.lazyOrElse(b: () -> Kind<StateTPartialOf<F, S>, A>): Kind<StateTPartialOf<F, S>, A> =
    StateT(AndThen.id<S>().flatMap { s ->
      AndThen(fix().runF).andThen { fa ->
        AF().run { fa.lazyOrElse { b().fix().run(s) } }
      }
    })

  override fun <A> StateTOf<F, S, A>.combineK(y: StateTOf<F, S, A>): StateT<F, S, A> =
    orElse(y).fix()
}
