package arrow.mtl.instances

import arrow.Kind
import arrow.core.toT
import arrow.data.StateT
import arrow.data.StateTPartialOf
import arrow.instance
import arrow.instances.StateTMonadErrorInstance
import arrow.instances.StateTMonadInstance
import arrow.instances.StateTSemigroupKInstance
import arrow.mtl.typeclasses.MonadCombine
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.SemigroupK

@instance(StateT::class)
interface StateTMonadStateInstance<F, S> : StateTMonadInstance<F, S>, MonadState<StateTPartialOf<F, S>, S> {

  override fun get(): StateT<F, S, S> = StateT.get(FF())

  override fun set(s: S): StateT<F, S, Unit> = StateT.set(FF(), s)

}

@instance(StateT::class)
interface StateTMonadCombineInstance<F, S> : MonadCombine<StateTPartialOf<F, S>>, StateTMonadInstance<F, S>, StateTSemigroupKInstance<F, S> {

  fun MC(): MonadCombine<F>

  override fun FF(): Monad<F> = MC()

  override fun SS(): SemigroupK<F> = MC()

  override fun <A> empty(): Kind<StateTPartialOf<F, S>, A> = liftT(MC().empty())

  fun <A> liftT(ma: Kind<F, A>): StateT<F, S, A> = FF().run {
    StateT(just({ s: S -> ma.map({ a: A -> s toT a }) }))
  }
}

class StateTMtlContext<F, S, E>(val ME: MonadError<F, E>) : StateTMonadStateInstance<F, S>, StateTMonadErrorInstance<F, S, E> {
  override fun FF(): MonadError<F, E> = ME
}

class StateTMtlContextPartiallyApplied<F, S, E>(val ME: MonadError<F, E>) {
  infix fun <A> extensions(f: StateTMtlContext<F, S, E>.() -> A): A =
    f(StateTMtlContext(ME))
}

fun <F, S, E> ForStateT(ME: MonadError<F, E>): StateTMtlContextPartiallyApplied<F, S, E> =
  StateTMtlContextPartiallyApplied(ME)