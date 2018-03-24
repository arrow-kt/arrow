package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(StateT::class)
interface StateTFunctorInstance<F, S> : Functor<StateTPartialOf<F, S>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: StateTOf<F, S, A>, f: (A) -> B): StateT<F, S, B> = fa.fix().map(f, FF())

}

@instance(StateT::class)
interface StateTApplicativeInstance<F, S> : StateTFunctorInstance<F, S>, Applicative<StateTPartialOf<F, S>> {

    override fun FF(): Monad<F>

    override fun <A, B> map(fa: StateTOf<F, S, A>, f: (A) -> B): StateT<F, S, B> = fa.fix().map(f, FF())

    override fun <A> pure(a: A): StateT<F, S, A> = StateT(FF().pure({ s: S -> FF().pure(Tuple2(s, a)) }))

    override fun <A, B> Kind<StateTPartialOf<F, S>, A>.ap(ff: Kind<StateTPartialOf<F, S>, (A) -> B>): StateT<F, S, B> =
            fix().ap(ff, FF())

    override fun <A, B> arrow.Kind<arrow.data.StateTPartialOf<F, S>, A>.product(fb: arrow.Kind<arrow.data.StateTPartialOf<F, S>, B>): StateT<F, S, Tuple2<A, B>> =
            this@product.fix().product(fb.fix(), this@StateTApplicativeInstance.FF())

}

@instance(StateT::class)
interface StateTMonadInstance<F, S> : StateTApplicativeInstance<F, S>, Monad<StateTPartialOf<F, S>> {

    override fun <A, B> map(fa: StateTOf<F, S, A>, f: (A) -> B): StateT<F, S, B> = fa.fix().map(f, FF())

    override fun <A, B> Kind<StateTPartialOf<F, S>, A>.flatMap(f: (A) -> Kind<StateTPartialOf<F, S>, B>): StateT<F, S, B> =
            fix().flatMap(f, FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> StateTOf<F, S, Either<A, B>>): StateT<F, S, B> =
            StateT.tailRecM(FF(), a, f)

    override fun <A, B> Kind<StateTPartialOf<F, S>, A>.ap(ff: Kind<StateTPartialOf<F, S>, (A) -> B>): StateT<F, S, B> =
            ff.fix().map2(FF(), fix(), { f, a -> f(a) })

}

@instance(StateT::class)
interface StateTSemigroupKInstance<F, S> : SemigroupK<StateTPartialOf<F, S>> {

    fun FF(): Monad<F>

    fun SS(): SemigroupK<F>

    override fun <A> combineK(x: StateTOf<F, S, A>, y: StateTOf<F, S, A>): StateT<F, S, A> =
            x.fix().combineK(y, FF(), SS())

}

@instance(StateT::class)
interface StateTApplicativeErrorInstance<F, S, E> : StateTApplicativeInstance<F, S>, ApplicativeError<StateTPartialOf<F, S>, E> {
    override fun FF(): MonadError<F, E>

    override fun <A> raiseError(e: E): Kind<StateTPartialOf<F, S>, A> = StateT.lift(FF(), FF().raiseError(e))

    override fun <A> Kind<StateTPartialOf<F, S>, A>.handleErrorWith(f: (E) -> Kind<StateTPartialOf<F, S>, A>): StateT<F, S, A> =
            StateT(FF().pure({ s -> FF().run { runM(FF(), s).handleErrorWith({ e -> f(e).runM(FF(), s) }) } }))
}

@instance(StateT::class)
interface StateTMonadErrorInstance<F, S, E> : StateTApplicativeErrorInstance<F, S, E>, StateTMonadInstance<F, S>, MonadError<StateTPartialOf<F, S>, E>

/* FIXME(paco)
/**
 * Alias for[StateT.Companion.applicative]
 */
fun <S> StateApi.applicative(): Applicative<StateTPartialOf<ForId, S>> = StateT.applicative<ForId, S>(arrow.typeclasses.monad<ForId>(), dummy = Unit)

/**
 * Alias for [StateT.Companion.functor]
 */
fun <S> StateApi.functor(): Functor<StateTPartialOf<ForId, S>> = StateT.functor<ForId, S>(arrow.typeclasses.functor<ForId>(), dummy = Unit)

/**
 * Alias for [StateT.Companion.monad]
 */
fun <S> StateApi.monad(): Monad<StateTPartialOf<ForId, S>> = StateT.monad<ForId, S>(arrow.typeclasses.monad<ForId>(), dummy = Unit)
*/