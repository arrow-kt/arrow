package arrow.free.instances

import arrow.*
import arrow.core.FunctionK
import arrow.free.*
import arrow.typeclasses.*

@instance(FreeApplicative::class)
interface FreeApplicativeFunctorInstance<S> : Functor<FreeApplicativePartialOf<S>> {
    override fun <A, B> map(fa: FreeApplicativeOf<S, A>, f: (A) -> B): FreeApplicative<S, B> = fa.reify().map(f)
}

@instance(FreeApplicative::class)
interface FreeApplicativeApplicativeInstance<S> : FreeApplicativeFunctorInstance<S>, Applicative<FreeApplicativePartialOf<S>> {
    override fun <A> pure(a: A): FreeApplicative<S, A> = FreeApplicative.pure(a)

    override fun <A, B> ap(fa: FreeApplicativeOf<S, A>, ff: FreeApplicativeOf<S, (A) -> B>): FreeApplicative<S, B> =
            fa.reify().ap(ff.reify())

    override fun <A, B> map(fa: FreeApplicativeOf<S, A>, f: (A) -> B): FreeApplicative<S, B> = fa.reify().map(f)
}

data class FreeApplicativeEq<F, G, A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<Kind<FreeApplicativePartialOf<F>, A>> {
    override fun eqv(a: Kind<FreeApplicativePartialOf<F>, A>, b: Kind<FreeApplicativePartialOf<F>, A>): Boolean =
            a.reify().foldMap(interpreter, MG) == b.reify().foldMap(interpreter, MG)

    companion object {
        inline operator fun <F, reified G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G> = monad(), dummy: Unit = Unit): FreeApplicativeEq<F, G, A> =
                FreeApplicativeEq(interpreter, MG)
    }
}
