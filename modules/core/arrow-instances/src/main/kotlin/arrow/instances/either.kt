package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance(Either::class)
interface EitherFunctorInstance<L> : Functor<EitherPartialOf<L>> {
    override fun <A, B> map(fa: EitherOf<L, A>, f: (A) -> B): Either<L, B> = fa.extract().map(f)
}

@instance(Either::class)
interface EitherApplicativeInstance<L> : EitherFunctorInstance<L>, Applicative<EitherPartialOf<L>> {

    override fun <A> pure(a: A): Either<L, A> = Right(a)

    override fun <A, B> map(fa: EitherOf<L, A>, f: (A) -> B): Either<L, B> = fa.extract().map(f)

    override fun <A, B> ap(fa: EitherOf<L, A>, ff: EitherOf<L, (A) -> B>): Either<L, B> =
            fa.extract().ap(ff)
}

@instance(Either::class)
interface EitherMonadInstance<L> : EitherApplicativeInstance<L>, Monad<EitherPartialOf<L>> {

    override fun <A, B> map(fa: EitherOf<L, A>, f: (A) -> B): Either<L, B> = fa.extract().map(f)

    override fun <A, B> ap(fa: EitherOf<L, A>, ff: EitherOf<L, (A) -> B>): Either<L, B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: EitherOf<L, A>, f: (A) -> EitherOf<L, B>): Either<L, B> = fa.extract().flatMap { f(it).extract() }

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<EitherPartialOf<L>, Either<A, B>>): Either<L, B> =
            Either.tailRecM(a, f)
}

@instance(Either::class)
interface EitherApplicativeErrorInstance<L> : EitherApplicativeInstance<L>, ApplicativeError<EitherPartialOf<L>, L> {

    override fun <A> raiseError(e: L): Either<L, A> = Left(e)

    override fun <A> handleErrorWith(fa: Kind<EitherPartialOf<L>, A>, f: (L) -> Kind<EitherPartialOf<L>, A>): Either<L, A> {
        val fea = fa.extract()
        return when (fea) {
            is Either.Left -> f(fea.a).extract()
            is Either.Right -> fea
        }
    }
}

@instance(Either::class)
interface EitherMonadErrorInstance<L> : EitherApplicativeErrorInstance<L>, EitherMonadInstance<L>, MonadError<EitherPartialOf<L>, L>

@instance(Either::class)
interface EitherFoldableInstance<L> : Foldable<EitherPartialOf<L>> {

    override fun <A, B> foldLeft(fa: Kind<EitherPartialOf<L>, A>, b: B, f: (B, A) -> B): B =
            fa.extract().foldLeft(b, f)

    override fun <A, B> foldRight(fa: Kind<EitherPartialOf<L>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.extract().foldRight(lb, f)
}

fun <G, A, B, C> Either<A, B>.traverse(f: (B) -> Kind<G, C>, GA: Applicative<G>): Kind<G, Either<A, C>> =
        this.extract().fold({ GA.pure(Either.Left(it)) }, { GA.map(f(it), { Either.Right(it) }) })

@instance(Either::class)
interface EitherTraverseInstance<L> : EitherFoldableInstance<L>, Traverse<EitherPartialOf<L>> {

    override fun <G, A, B> traverse(fa: Kind<EitherPartialOf<L>, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Kind<EitherPartialOf<L>, B>> =
            fa.extract().traverse(f, GA)
}

@instance(Either::class)
interface EitherSemigroupKInstance<L> : SemigroupK<EitherPartialOf<L>> {

    override fun <A> combineK(x: EitherOf<L, A>, y: EitherOf<L, A>): Either<L, A> =
            x.extract().combineK(y)
}

@instance(Either::class)
interface EitherEqInstance<L, R> : Eq<Either<L, R>> {

    fun EQL(): Eq<L>

    fun EQR(): Eq<R>

    override fun eqv(a: Either<L, R>, b: Either<L, R>): Boolean = when (a) {
        is Either.Left -> when (b) {
            is Either.Left -> EQL().eqv(a.a, b.a)
            is Either.Right -> false
        }
        is Either.Right -> when (b) {
            is Either.Left -> false
            is Either.Right -> EQR().eqv(a.b, b.b)
        }
    }

}
