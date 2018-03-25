package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.data.*
import arrow.typeclasses.*

interface EitherTFunctorInstance<F, L> : Functor<EitherTPartialOf<F, L>> {

    fun FF(): Functor<F>

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map({ f(it) }, FF())
}

interface EitherTApplicativeInstance<F, L> : EitherTFunctorInstance<F, L>, Applicative<EitherTPartialOf<F, L>> {

    fun MF(): Monad<F>

    override fun <A> pure(a: A): EitherT<F, L, A> = EitherT.pure(a, MF())

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map({ f(it) }, this@EitherTApplicativeInstance.MF())

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.ap(ff: Kind<EitherTPartialOf<F, L>, (A) -> B>): EitherT<F, L, B> =
            fix().ap(ff, MF())
}

interface EitherTMonadInstance<F, L> : EitherTApplicativeInstance<F, L>, Monad<EitherTPartialOf<F, L>> {

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map({ f(it) }, MF())

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.ap(ff: Kind<EitherTPartialOf<F, L>, (A) -> B>): EitherT<F, L, B> =
            fix().ap(ff, MF())

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.flatMap(f: (A) -> Kind<EitherTPartialOf<F, L>, B>): EitherT<F, L, B> = fix().flatMap({ f(it).fix() }, MF())

    override fun <A, B> tailRecM(a: A, f: (A) -> EitherTOf<F, L, Either<A, B>>): EitherT<F, L, B> =
            EitherT.tailRecM(a, f, MF())
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

    override fun <B, C> foldLeft(fa: Kind<EitherTPartialOf<F, L>, B>, b: C, f: (C, B) -> C): C = fa.fix().foldLeft(b, f, FFF())

    override fun <B, C> foldRight(fa: Kind<EitherTPartialOf<F, L>, B>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> = fa.fix().foldRight(lb, f, FFF())
}

interface EitherTTraverseInstance<F, L> : EitherTFunctorInstance<F, L>, EitherTFoldableInstance<F, L>, Traverse<EitherTPartialOf<F, L>> {

    fun TF(): Traverse<F>

    override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map({ f(it) }, TF())

    override fun <G, B, C> traverse(AP: Applicative<G>, fa: Kind<EitherTPartialOf<F, L>, B>, f: (B) -> Kind<G, C>): Kind<G, EitherT<F, L, C>> =
            fa.fix().traverse(f, AP, TF())
}

interface EitherTSemigroupKInstance<F, L> : SemigroupK<EitherTPartialOf<F, L>> {
    fun MF(): Monad<F>

    override fun <A> combineK(x: EitherTOf<F, L, A>, y: EitherTOf<F, L, A>): EitherT<F, L, A> =
            x.fix().combineK(y, MF())
}
