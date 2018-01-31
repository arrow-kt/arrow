package arrow.instances

import arrow.HK
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.data.*
import arrow.typeclasses.*

interface EitherTFunctorInstance<F, L> : Functor<EitherTKindPartial<F, L>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> = fa.ev().map({ f(it) }, FF())
}

interface EitherTApplicativeInstance<F, L> : EitherTFunctorInstance<F, L>, Applicative<EitherTKindPartial<F, L>> {

    fun MF(): Monad<F>

    override fun <A> pure(a: A): EitherT<F, L, A> = EitherT.pure(a, MF())

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> = fa.ev().map({ f(it) }, MF())

    override fun <A, B> ap(fa: EitherTKind<F, L, A>, ff: EitherTKind<F, L, (A) -> B>): EitherT<F, L, B> =
            fa.ev().ap(ff, MF())
}

interface EitherTMonadInstance<F, L> : EitherTApplicativeInstance<F, L>, Monad<EitherTKindPartial<F, L>> {

    override fun <A, B> ap(fa: EitherTKind<F, L, A>, ff: EitherTKind<F, L, (A) -> B>): EitherT<F, L, B> =
            fa.ev().ap(ff,
                    MF())

    override fun <A, B> flatMap(fa: EitherTKind<F, L, A>, f: (A) -> EitherTKind<F, L, B>): EitherT<F, L, B> = fa.ev().flatMap({ f(it).ev() }, MF())

    override fun <A, B> tailRecM(a: A, f: (A) -> EitherTKind<F, L, Either<A, B>>): EitherT<F, L, B> =
            EitherT.tailRecM(a, f, MF())
}

interface EitherTApplicativeErrorInstance<F, L> : EitherTApplicativeInstance<F, L>, ApplicativeError<EitherTKindPartial<F, L>, L> {

    override fun <A> handleErrorWith(fa: EitherTKind<F, L, A>, f: (L) -> EitherTKind<F, L, A>): EitherT<F, L, A> =
            EitherT(MF().flatMap(fa.ev().value, {
                when (it) {
                    is Either.Left -> f(it.a).ev().value
                    is Either.Right -> MF().pure(it)
                }
            }))

    override fun <A> raiseError(e: L): EitherT<F, L, A> = EitherT(MF().pure(Left(e)))
}

interface EitherTMonadErrorInstance<F, L> : EitherTApplicativeErrorInstance<F, L>, EitherTMonadInstance<F, L>, MonadError<EitherTKindPartial<F, L>, L>

interface EitherTFoldableInstance<F, L> : Foldable<EitherTKindPartial<F, L>> {

    fun FFF(): Foldable<F>

    override fun <B, C> foldLeft(fa: HK<EitherTKindPartial<F, L>, B>, b: C, f: (C, B) -> C): C = fa.ev().foldLeft(b, f, FFF())

    override fun <B, C> foldRight(fa: HK<EitherTKindPartial<F, L>, B>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> = fa.ev().foldRight(lb, f, FFF())
}

interface EitherTTraverseInstance<F, L> : EitherTFunctorInstance<F, L>, EitherTFoldableInstance<F, L>, Traverse<EitherTKindPartial<F, L>> {

    fun TF(): Traverse<F>

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> = fa.ev().map({ f(it) }, TF())

    override fun <G, B, C> traverse(fa: EitherTKind<F, L, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, EitherT<F, L, C>> =
            fa.ev().traverse(f, GA, TF())
}

interface EitherTSemigroupKInstance<F, L> : SemigroupK<EitherTKindPartial<F, L>> {
    fun MF(): Monad<F>

    override fun <A> combineK(x: EitherTKind<F, L, A>, y: EitherTKind<F, L, A>): EitherT<F, L, A> =
            x.ev().combineK(y, MF())
}
