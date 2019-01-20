package arrow.mtl.extensions

import arrow.Kind
import arrow.core.toT
import arrow.data.StateT
import arrow.data.StateTPartialOf
import arrow.data.extensions.StateTMonad
import arrow.data.extensions.StateTSemigroupK
import arrow.extension
import arrow.mtl.typeclasses.MonadCombine
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Monad
import arrow.typeclasses.SemigroupK

@extension
interface StateTMonadState<F, S> : MonadState<StateTPartialOf<F, S>, S>, StateTMonad<F, S> {

  override fun MF(): Monad<F>

  override fun get(): StateT<F, S, S> = StateT.get(MF())

  override fun set(s: S): StateT<F, S, Unit> = StateT.set(MF(), s)

}

@extension
interface StateTMonadCombine<F, S> : MonadCombine<StateTPartialOf<F, S>>, StateTMonad<F, S>, StateTSemigroupK<F, S> {

  fun MC(): MonadCombine<F>

  override fun MF(): Monad<F> = MC()

  override fun FF(): Monad<F> = MC()

  override fun SS(): SemigroupK<F> = MC()

  override fun <A> empty(): Kind<StateTPartialOf<F, S>, A> = liftT(MC().empty())

  fun <A> liftT(ma: Kind<F, A>): StateT<F, S, A> = FF().run {
    StateT(just({ s: S -> ma.map { a: A -> s toT a } }))
  }
}
