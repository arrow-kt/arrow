package arrow.free.instances

import arrow.*
import arrow.core.Either
import arrow.core.FunctionK
import arrow.free.*
import arrow.typeclasses.*

@instance(Free::class)
interface FreeFunctorInstance<S> : Functor<FreeKindPartial<S>> {
    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): Free<S, B> = fa.reify().map(f)
}

@instance(Free::class)
interface FreeApplicativeInstance<S> : FreeFunctorInstance<S>, Applicative<FreeKindPartial<S>> {
    override fun <A> pure(a: A): Free<S, A> = Free.pure(a)

    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): Free<S, B> = fa.reify().map(f)

    override fun <A, B> ap(fa: FreeKind<S, A>, ff: FreeKind<S, (A) -> B>): Free<S, B> =
            fa.reify().ap(ff.reify())

}

@instance(Free::class)
interface FreeMonadInstance<S> : FreeApplicativeInstance<S>, Monad<FreeKindPartial<S>> {

    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): Free<S, B> = fa.reify().map(f)

    override fun <A, B> ap(fa: FreeKind<S, A>, ff: FreeKind<S, (A) -> B>): Free<S, B> =
            fa.reify().ap(ff.reify())

    override fun <A, B> flatMap(fa: FreeKind<S, A>, f: (A) -> FreeKind<S, B>): Free<S, B> = fa.reify().flatMap { f(it).reify() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FreeKind<S, Either<A, B>>): Free<S, B> = f(a).reify().flatMap {
        when (it) {
            is Either.Left -> tailRecM(it.a, f)
            is Either.Right -> pure(it.b)
        }
    }

}

data class FreeEq<F, G, A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<Kind<FreeKindPartial<F>, A>> {
    override fun eqv(a: Kind<FreeKindPartial<F>, A>, b: Kind<FreeKindPartial<F>, A>): Boolean = a.reify().foldMap(interpreter, MG) == b.reify().foldMap(interpreter, MG)

    companion object {
        inline operator fun <F, reified G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G> = monad(), dummy: Unit = Unit): FreeEq<F, G, A> =
                FreeEq(interpreter, MG)
    }
}
