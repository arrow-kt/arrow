package arrow.free.instances

import arrow.Kind
import arrow.core.FunctionK
import arrow.free.FreeApplicative
import arrow.free.FreeApplicativeOf
import arrow.free.FreeApplicativePartialOf
import arrow.free.fix
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

@instance(FreeApplicative::class)
interface FreeApplicativeFunctorInstance<S> : Functor<FreeApplicativePartialOf<S>> {
    override fun <A, B> map(fa: FreeApplicativeOf<S, A>, f: (A) -> B): FreeApplicative<S, B> = fa.fix().map(f)
}

@instance(FreeApplicative::class)
interface FreeApplicativeApplicativeInstance<S> : FreeApplicativeFunctorInstance<S>, Applicative<FreeApplicativePartialOf<S>> {
    override fun <A> pure(a: A): FreeApplicative<S, A> = FreeApplicative.pure(a)

    override fun <A, B> ap(fa: FreeApplicativeOf<S, A>, ff: FreeApplicativeOf<S, (A) -> B>): FreeApplicative<S, B> =
            fa.fix().ap(ff.fix())

    override fun <A, B> map(fa: FreeApplicativeOf<S, A>, f: (A) -> B): FreeApplicative<S, B> = fa.fix().map(f)
}

data class FreeApplicativeEq<F, G, A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<Kind<FreeApplicativePartialOf<F>, A>> {
    override fun Kind<FreeApplicativePartialOf<F>, A>.eqv(b: Kind<FreeApplicativePartialOf<F>, A>): Boolean =
            fix().foldMap(interpreter, MG) == b.fix().foldMap(interpreter, MG)

    companion object {
        operator fun <F, G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G>): FreeApplicativeEq<F, G, A> =
                FreeApplicativeEq(interpreter, MG)
    }
}
