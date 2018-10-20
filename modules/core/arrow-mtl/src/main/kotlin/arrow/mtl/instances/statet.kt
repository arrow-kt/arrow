package arrow.mtl.instances

import arrow.Kind
import arrow.core.toT
import arrow.data.IndexedStateT
import arrow.data.IndexedStateTPartialOf
import arrow.data.StateT
import arrow.data.StateTPartialOf
import arrow.instance
import arrow.instances.*
import arrow.mtl.typeclasses.MonadCombine
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.SemigroupK

@instance(IndexedStateT::class)
interface IndexedStateTMonadStateInstance<F, S> : IndexedStateTMonadInstance<F, S>, MonadState<IndexedStateTPartialOf<F, S, S>, S> {

  override fun get(): IndexedStateT<F, S, S, S> = IndexedStateT.get(FF())

  override fun set(s: S): IndexedStateT<F, S, S, Unit> = IndexedStateT.set(FF(), s)

}

@instance(IndexedStateT::class)
interface IndexedStateTMonadCombineInstance<F, S> : MonadCombine<IndexedStateTPartialOf<F, S, S>>, IndexedStateTMonadInstance<F, S>, IndexedStateTSemigroupKInstance<F, S, S> {

  fun MC(): MonadCombine<F>

  override fun FF(): Monad<F> = MC()

  override fun SS(): SemigroupK<F> = MC()

  override fun <A> empty(): Kind<StateTPartialOf<F, S>, A> = liftT(MC().empty())

  fun <A> liftT(ma: Kind<F, A>): StateT<F, S, A> = FF().run {
    StateT(just({ s: S -> ma.map { a: A -> s toT a } }))
  }
}

class StateTMtlContext<F, S, E>(val ME: MonadError<F, E>) : IndexedStateTMonadStateInstance<F, S>, IndexedStateTMonadErrorInstance<F, S, E> {
  override fun FF(): MonadError<F, E> = ME
}

class StateTMtlContextPartiallyApplied<F, S, E>(val ME: MonadError<F, E>) {
  infix fun <A> extensions(f: StateTMtlContext<F, S, E>.() -> A): A =
    f(StateTMtlContext(ME))
}

fun <F, S, E> ForStateT(ME: MonadError<F, E>): StateTMtlContextPartiallyApplied<F, S, E> =
  StateTMtlContextPartiallyApplied(ME)