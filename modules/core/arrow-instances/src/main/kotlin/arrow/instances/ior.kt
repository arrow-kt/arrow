package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(Ior::class)
interface IorFunctorInstance<L> : Functor<IorPartialOf<L>> {
    override fun <A, B> map(fa: IorOf<L, A>, f: (A) -> B): Ior<L, B> = fa.extract().map(f)
}

@instance(Ior::class)
interface IorApplicativeInstance<L> : IorFunctorInstance<L>, Applicative<IorPartialOf<L>> {

    fun SL(): Semigroup<L>

    override fun <A> pure(a: A): Ior<L, A> = Ior.Right(a)

    override fun <A, B> map(fa: IorOf<L, A>, f: (A) -> B): Ior<L, B> = fa.extract().map(f)

    override fun <A, B> ap(fa: IorOf<L, A>, ff: IorOf<L, (A) -> B>): Ior<L, B> =
            fa.extract().ap(ff, SL())
}

@instance(Ior::class)
interface IorMonadInstance<L> : IorApplicativeInstance<L>, Monad<IorPartialOf<L>> {

    override fun <A, B> map(fa: IorOf<L, A>, f: (A) -> B): Ior<L, B> = fa.extract().map(f)

    override fun <A, B> flatMap(fa: IorOf<L, A>, f: (A) -> IorOf<L, B>): Ior<L, B> =
            fa.extract().flatMap({ f(it).extract() }, SL())

    override fun <A, B> ap(fa: IorOf<L, A>, ff: IorOf<L, (A) -> B>): Ior<L, B> =
            fa.extract().ap(ff, SL())

    override fun <A, B> tailRecM(a: A, f: (A) -> IorOf<L, Either<A, B>>): Ior<L, B> =
            Ior.tailRecM(a, f, SL())

}

@instance(Ior::class)
interface IorFoldableInstance<L> : Foldable<IorPartialOf<L>> {

    override fun <B, C> foldLeft(fa: Kind<Kind<ForIor, L>, B>, b: C, f: (C, B) -> C): C = fa.extract().foldLeft(b, f)

    override fun <B, C> foldRight(fa: Kind<Kind<ForIor, L>, B>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fa.extract().foldRight(lb, f)

}

@instance(Ior::class)
interface IorTraverseInstance<L> : IorFoldableInstance<L>, Traverse<IorPartialOf<L>> {

    override fun <G, B, C> traverse(fa: IorOf<L, B>, f: (B) -> Kind<G, C>, GA: Applicative<G>): Kind<G, Ior<L, C>> =
            fa.extract().traverse(f, GA)

}

@instance(Ior::class)
interface IorEqInstance<L, R> : Eq<Ior<L, R>> {

    fun EQL(): Eq<L>

    fun EQR(): Eq<R>

    override fun eqv(a: Ior<L, R>, b: Ior<L, R>): Boolean = when (a) {
        is Ior.Left -> when (b) {
            is Ior.Both -> false
            is Ior.Right -> false
            is Ior.Left -> EQL().eqv(a.value, b.value)
        }
        is Ior.Both -> when (b) {
            is Ior.Left -> false
            is Ior.Both -> EQL().eqv(a.leftValue, b.leftValue) && EQR().eqv(a.rightValue, b.rightValue)
            is Ior.Right -> false
        }
        is Ior.Right -> when (b) {
            is Ior.Left -> false
            is Ior.Both -> false
            is Ior.Right -> EQR().eqv(a.value, b.value)
        }

    }
}
