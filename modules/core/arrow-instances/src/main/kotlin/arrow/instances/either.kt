package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance(Either::class)
interface EitherFunctorInstance<L> : Functor<EitherPartialOf<L>> {
    override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)
}

@instance(Either::class)
interface EitherApplicativeInstance<L> : EitherFunctorInstance<L>, Applicative<EitherPartialOf<L>> {

    override fun <A> pure(a: A): Either<L, A> = Right(a)

    override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

    override fun <A, B> Kind<EitherPartialOf<L>, A>.ap(ff: Kind<EitherPartialOf<L>, (A) -> B>): Either<L, B> =
            fix().ap(null, ff)
}

@instance(Either::class)
interface EitherMonadInstance<L> : EitherApplicativeInstance<L>, Monad<EitherPartialOf<L>> {

    override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

    override fun <A, B> Kind<EitherPartialOf<L>, A>.ap(ff: Kind<EitherPartialOf<L>, (A) -> B>): Either<L, B> =
            fix().ap(null, ff)

    override fun <A, B> Kind<EitherPartialOf<L>, A>.flatMap(f: (A) -> Kind<EitherPartialOf<L>, B>): Either<L, B> =
            fix().flatMap(null) { f(it).fix() }

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<EitherPartialOf<L>, Either<A, B>>): Either<L, B> =
            Either.tailRecM(a, f)
}

@instance(Either::class)
interface EitherApplicativeErrorInstance<L> : EitherApplicativeInstance<L>, ApplicativeError<EitherPartialOf<L>, L> {

    override fun <A> raiseError(e: L): Either<L, A> = Left(e)

    override fun <A> Kind<EitherPartialOf<L>, A>.handleErrorWith(f: (L) -> Kind<EitherPartialOf<L>, A>): Either<L, A> {
        val fea = fix()
        return when (fea) {
            is Either.Left -> f(fea.a).fix()
            is Either.Right -> fea
        }
    }
}

@instance(Either::class)
interface EitherMonadErrorInstance<L> : EitherApplicativeErrorInstance<L>, EitherMonadInstance<L>, MonadError<EitherPartialOf<L>, L>

@instance(Either::class)
interface EitherFoldableInstance<L> : Foldable<EitherPartialOf<L>> {

    override fun <A, B> foldLeft(fa: Kind<EitherPartialOf<L>, A>, b: B, f: (B, A) -> B): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: Kind<EitherPartialOf<L>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.fix().foldRight(lb, f)
}

fun <G, A, B, C> Either<A, B>.traverse(f: (B) -> Kind<G, C>, GA: Applicative<G>): Kind<G, Either<A, C>> = GA.run {
    fix().fold({ pure(Either.Left(it)) }, { f(it).map({ Either.Right(it) }) })
}

@instance(Either::class)
interface EitherTraverseInstance<L> : EitherFoldableInstance<L>, Traverse<EitherPartialOf<L>> {

    override fun <G, A, B> Kind<EitherPartialOf<L>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<EitherPartialOf<L>, B>> =
            fix().traverse(f, AP)
}

@instance(Either::class)
interface EitherSemigroupKInstance<L> : SemigroupK<EitherPartialOf<L>> {

    override fun <A> Kind<EitherPartialOf<L>, A>.combineK(y: Kind<EitherPartialOf<L>, A>): Either<L, A> =
            fix().combineK(y)
}

@instance(Either::class)
interface EitherEqInstance<in L, in R> : Eq<Either<L, R>> {

    fun EQL(): Eq<L>

    fun EQR(): Eq<R>

    override fun Either<L, R>.eqv(b: Either<L, R>): Boolean = when (this) {
        is Either.Left -> when (b) {
            is Either.Left -> EQL().run { a.eqv(b.a) }
            is Either.Right -> false
        }
        is Either.Right -> when (b) {
            is Either.Left -> false
            is Either.Right -> EQR().run { this@eqv.b.eqv(b.b) }
        }
    }

}

@instance(Either::class)
interface EitherShowInstance<L, R> : Show<Either<L, R>> {
    override fun Either<L, R>.show(): String =
            toString()
}
