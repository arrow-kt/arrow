package arrow.free.instances

import arrow.*
import arrow.core.Either
import arrow.core.FunctionK
import arrow.free.*
import arrow.typeclasses.*

@instance(Free::class)
interface FreeFunctorInstance<S> : Functor<FreePartialOf<S>> {
    override fun <A, B> map(fa: FreeOf<S, A>, f: (A) -> B): Free<S, B> = fa.fix().map(f)
}

@instance(Free::class)
interface FreeApplicativeInstance<S> : FreeFunctorInstance<S>, Applicative<FreePartialOf<S>> {
    override fun <A> pure(a: A): Free<S, A> = Free.pure(a)

    override fun <A, B> map(fa: FreeOf<S, A>, f: (A) -> B): Free<S, B> = fa.fix().map(f)

    override fun <A, B> ap(fa: FreeOf<S, A>, ff: FreeOf<S, (A) -> B>): Free<S, B> =
            fa.fix().ap(ff.fix())

}

@instance(Free::class)
interface FreeMonadInstance<S> : FreeApplicativeInstance<S>, Monad<FreePartialOf<S>> {

    override fun <A, B> map(fa: FreeOf<S, A>, f: (A) -> B): Free<S, B> = fa.fix().map(f)

    override fun <A, B> ap(fa: FreeOf<S, A>, ff: FreeOf<S, (A) -> B>): Free<S, B> =
            fa.fix().ap(ff.fix())

    override fun <A, B> flatMap(fa: FreeOf<S, A>, f: (A) -> FreeOf<S, B>): Free<S, B> = fa.fix().flatMap { f(it).fix() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FreeOf<S, Either<A, B>>): Free<S, B> = f(a).fix().flatMap {
        when (it) {
            is Either.Left -> tailRecM(it.a, f)
            is Either.Right -> pure(it.b)
        }
    }

}

data class FreeEq<F, G, A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<Kind<FreePartialOf<F>, A>> {
    override fun eqv(a: Kind<FreePartialOf<F>, A>, b: Kind<FreePartialOf<F>, A>): Boolean = a.fix().foldMap(interpreter, MG) == b.fix().foldMap(interpreter, MG)

    companion object {
        inline operator fun <F, reified G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G> = monad(), dummy: Unit = Unit): FreeEq<F, G, A> =
                FreeEq(interpreter, MG)
    }
}
