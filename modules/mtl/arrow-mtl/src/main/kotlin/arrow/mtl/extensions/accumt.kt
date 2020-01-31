package arrow.mtl.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.extension
import arrow.mtl.AccumT
import arrow.mtl.AccumTPartialOf
import arrow.mtl.ForAccumT
import arrow.mtl.fix
import arrow.mtl.typeclasses.MonadReader
import arrow.mtl.typeclasses.MonadState
import arrow.mtl.typeclasses.MonadTrans
import arrow.mtl.typeclasses.MonadWriter
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid

@extension
interface AccumTFunctor<S, F> : Functor<AccumTPartialOf<S, F>> {

  fun FF(): Functor<F>

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.map(f: (A) -> B): Kind<AccumTPartialOf<S, F>, B> =
    this.fix().map(FF(), f)
}

@extension
interface AccumTApplicative<S, F> : Applicative<AccumTPartialOf<S, F>> {
  fun MS(): Monoid<S>
  fun MF(): Monad<F>

  override fun <A> just(a: A): Kind<AccumTPartialOf<S, F>, A> =
    AccumT.just(MS(), MF(), a)

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.ap(ff: Kind<AccumTPartialOf<S, F>, (A) -> B>): Kind<AccumTPartialOf<S, F>, B> =
    fix().ap(MS(), MF(), ff)
}

@extension
interface AccumTMonad<S, F> : Monad<AccumTPartialOf<S, F>>, AccumTApplicative<S, F> {

  override fun MS(): Monoid<S>
  override fun MF(): Monad<F>

  override fun <A> just(a: A): Kind<AccumTPartialOf<S, F>, A> =
    AccumT.just(MS(), MF(), a)

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.flatMap(f: (A) -> Kind<AccumTPartialOf<S, F>, B>): Kind<AccumTPartialOf<S, F>, B> =
    this.fix().flatMap(MS(), MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<AccumTPartialOf<S, F>, Either<A, B>>): Kind<AccumTPartialOf<S, F>, B> =
    AccumT.tailRecM(MF(), a, f)

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.ap(ff: Kind<AccumTPartialOf<S, F>, (A) -> B>): Kind<AccumTPartialOf<S, F>, B> =
    fix().ap(MS(), MF(), ff)
}

@extension
interface AccumtTMonadTrans<S> : MonadTrans<Kind<ForAccumT, S>> {

  fun MS(): Monoid<S>

  override fun <G, A> Kind<G, A>.liftT(MG: Monad<G>): Kind2<Kind<ForAccumT, S>, G, A> =
    AccumT { _: S ->
      MG.run {
        map { a ->
          MS().empty() toT a
        }
      }
    }
}

@extension
interface AccumTAlternative<S, F> : Alternative<AccumTPartialOf<S, F>>, AccumTApplicative<S, F> {

  fun AF(): Alternative<F>
  override fun MF(): Monad<F>
  override fun MS(): Monoid<S>

  override fun <A> Kind<AccumTPartialOf<S, F>, A>.orElse(b: Kind<AccumTPartialOf<S, F>, A>): Kind<AccumTPartialOf<S, F>, A> =
    (this.fix() to b.fix()).let { (ls, rs) ->
      AccumT(AndThen.id<S>().flatMap { s ->
        AF().run {
          AndThen(ls.accumT).andThen { it.orElse(rs.runAccumT(s)) }
        }
      })
    }

  override fun <A> empty(): Kind<AccumTPartialOf<S, F>, A> =
    AccumT.liftF(MS(), AF(), AF().empty())
}

@extension
interface AccumTApplicativeError<S, F, E> : ApplicativeError<AccumTPartialOf<S, F>, E>, AccumTApplicative<S, F> {
  fun ME(): MonadError<F, E>

  override fun MS(): Monoid<S>
  override fun MF(): Monad<F> = ME()

  override fun <A> raiseError(e: E): Kind<AccumTPartialOf<S, F>, A> =
    AccumT.liftF(MS(), MF(), ME().raiseError(e))

  override fun <A> Kind<AccumTPartialOf<S, F>, A>.handleErrorWith(f: (E) -> Kind<AccumTPartialOf<S, F>, A>): Kind<AccumTPartialOf<S, F>, A> =
    this.fix().let { accumT ->
      AccumT(AndThen.id<S>().flatMap { s ->
        AndThen(accumT.accumT).andThen {
          ME().run {
            it.handleErrorWith { e ->
              f(e).fix().runAccumT(s)
            }
          }
        }
      })
    }
}

@extension
interface AccumTMonadError<S, F, E> : MonadError<AccumTPartialOf<S, F>, E>, AccumTApplicativeError<S, F, E>, AccumTMonad<S, F> {
  override fun MS(): Monoid<S>
  override fun ME(): MonadError<F, E>
  override fun MF(): Monad<F> = ME()
}

@extension
interface AccumTMonadState<S, W, F> : MonadState<AccumTPartialOf<W, F>, S>, AccumTMonad<W, F> {
  fun MSF(): MonadState<F, S>
  override fun MF(): Monad<F> = MSF()
  override fun MS(): Monoid<W>

  override fun get(): Kind<AccumTPartialOf<W, F>, S> =
    AccumT.liftF(MS(), MSF(), MSF().get())

  override fun set(s: S): Kind<AccumTPartialOf<W, F>, Unit> =
    AccumT.liftF(MS(), MSF(), MSF().set(s))
}

@extension
interface AccumTMonadReader<S, W, F> : MonadReader<AccumTPartialOf<W, F>, S>, AccumTMonad<W, F> {
  fun MR(): MonadReader<F, S>
  override fun MF(): Monad<F> = MR()
  override fun MS(): Monoid<W>

  override fun ask(): Kind<AccumTPartialOf<W, F>, S> =
    AccumT.liftF(MS(), MR(), MR().ask())

  override fun <A> Kind<AccumTPartialOf<W, F>, A>.local(f: (S) -> S): Kind<AccumTPartialOf<W, F>, A> =
    MR().run {
      AccumT(AndThen(fix().accumT).andThen { it.local(f) })
    }
}

@extension
interface AccumTMonadWriter<S, W, F> : MonadWriter<AccumTPartialOf<W, F>, S>, AccumTMonad<W, F> {
  fun MW(): MonadWriter<F, S>
  override fun MF(): Monad<F> = MW()
  override fun MS(): Monoid<W>

  override fun <A> writer(aw: Tuple2<S, A>): Kind<AccumTPartialOf<W, F>, A> =
    AccumT.liftF(MS(), MW(), MW().writer(aw))

  override fun <A> Kind<AccumTPartialOf<W, F>, A>.listen(): Kind<AccumTPartialOf<W, F>, Tuple2<S, A>> =
    MW().run {
      AccumT(AndThen(fix().accumT).andThen {
        it.listen().map { (w, sa) ->
          val (s, a) = sa
          Tuple2(s, Tuple2(w, a))
        }
      })
    }

  override fun <A> Kind<AccumTPartialOf<W, F>, Tuple2<(S) -> S, A>>.pass(): Kind<AccumTPartialOf<W, F>, A> =
    MW().run {
      AccumT(AndThen(fix().accumT).andThen {
        it.map { (s, fa) ->
          val (f, a) = fa
          Tuple2(f, Tuple2(s, a))
        }.pass()
      })
    }
}
