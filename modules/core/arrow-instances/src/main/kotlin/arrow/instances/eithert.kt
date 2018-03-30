package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.data.*
import arrow.typeclasses.*

interface EitherTFunctorInstance<F, L> : Functor<EitherTPartialOf<F, L>> {

    fun FF(): Functor<F>

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(FF(), { f(it) })
}

interface EitherTApplicativeInstance<F, L> : EitherTFunctorInstance<F, L>, Applicative<EitherTPartialOf<F, L>> {

    fun MF(): Monad<F>

    override fun <A> pure(a: A): EitherT<F, L, A> = EitherT.pure(MF(), a)

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(this@EitherTApplicativeInstance.MF(), { f(it) })

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.ap(ff: Kind<EitherTPartialOf<F, L>, (A) -> B>): EitherT<F, L, B> =
            fix().ap(MF(), ff)
}

interface EitherTMonadInstance<F, L> : EitherTApplicativeInstance<F, L>, Monad<EitherTPartialOf<F, L>> {

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(MF(), { f(it) })

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.ap(ff: Kind<EitherTPartialOf<F, L>, (A) -> B>): EitherT<F, L, B> =
            fix().ap(MF(), ff)

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.flatMap(f: (A) -> Kind<EitherTPartialOf<F, L>, B>): EitherT<F, L, B> = fix().flatMap(MF(), { f(it).fix() })

    override fun <A, B> tailRecM(a: A, f: (A) -> EitherTOf<F, L, Either<A, B>>): EitherT<F, L, B> =
            EitherT.tailRecM(MF(), a, f)
}

interface EitherTApplicativeErrorInstance<F, L> : EitherTApplicativeInstance<F, L>, ApplicativeError<EitherTPartialOf<F, L>, L> {

    override fun <A> Kind<EitherTPartialOf<F, L>, A>.handleErrorWith(f: (L) -> Kind<EitherTPartialOf<F, L>, A>): EitherT<F, L, A> = MF().run {
        EitherT(fix().value.flatMap({
            when (it) {
                is Either.Left -> f(it.a).fix().value
                is Either.Right -> pure(it)
            }
        }))
    }

    override fun <A> raiseError(e: L): EitherT<F, L, A> = EitherT(MF().pure(Left(e)))
}

interface EitherTMonadErrorInstance<F, L> : EitherTApplicativeErrorInstance<F, L>, EitherTMonadInstance<F, L>, MonadError<EitherTPartialOf<F, L>, L>

interface EitherTFoldableInstance<F, L> : Foldable<EitherTPartialOf<F, L>> {

    fun FFF(): Foldable<F>

    override fun <B, C> Kind<EitherTPartialOf<F, L>, B>.foldLeft(b: C, f: (C, B) -> C): C = fix().foldLeft(b, f, FFF())

    override fun <B, C> Kind<EitherTPartialOf<F, L>, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fix().foldRight(lb, f, FFF())
}

interface EitherTTraverseInstance<F, L> : EitherTFunctorInstance<F, L>, EitherTFoldableInstance<F, L>, Traverse<EitherTPartialOf<F, L>> {

    fun TF(): Traverse<F>

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(TF(), { f(it) })

    override fun <G, B, C> Kind<EitherTPartialOf<F, L>, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, EitherT<F, L, C>> =
            fix().traverse(f, AP, TF())
}

interface EitherTSemigroupKInstance<F, L> : SemigroupK<EitherTPartialOf<F, L>> {
    fun MF(): Monad<F>

    override fun <A> Kind<EitherTPartialOf<F, L>, A>.combineK(y: Kind<EitherTPartialOf<F, L>, A>): EitherT<F, L, A> =
            fix().combineK(MF(), y)
}
