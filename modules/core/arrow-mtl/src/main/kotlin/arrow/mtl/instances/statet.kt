package arrow.mtl.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.instances.*
import arrow.mtl.MonadCombine
import arrow.mtl.MonadState
import arrow.typeclasses.Monad
import arrow.typeclasses.SemigroupK

@instance(StateT::class)
interface StateTMonadStateInstance<F, S> : StateTMonadInstance<F, S>, MonadState<StateTKindPartial<F, S>, S> {

    override fun get(): StateT<F, S, S> = StateT.get(FF())

    override fun set(s: S): StateT<F, S, Unit> = StateT.set(FF(), s)

}

@instance(StateT::class)
interface StateTMonadCombineInstance<F, S> : MonadCombine<StateTKindPartial<F, S>>, StateTMonadInstance<F, S>, StateTSemigroupKInstance<F, S> {

    fun MC(): MonadCombine<F>

    override fun FF(): Monad<F> = MC()

    override fun SS(): SemigroupK<F> = MC()

    override fun <A> empty(): HK<StateTKindPartial<F, S>, A> = liftT(MC().empty())

    fun <A> liftT(ma: HK<F, A>): StateT<F, S, A> = StateT(FF().pure({ s: S -> FF().map(ma, { a: A -> s toT a }) }))
}