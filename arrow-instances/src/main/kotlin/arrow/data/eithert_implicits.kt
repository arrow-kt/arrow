package arrow.data

import arrow.HK
import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.*

object EitherTFunctorInstanceImplicits {
    fun <F, L> instance(FF: Functor<F>): EitherTFunctorInstance<F, L> = object : EitherTFunctorInstance<F, L> {
        override fun FF(): Functor<F> = FF
    }
}

object EitherTApplicativeInstanceImplicits {
    fun <F, L> instance(MF: Monad<F>): EitherTApplicativeInstance<F, L> = object : EitherTApplicativeInstance<F, L> {
        override fun FF(): Functor<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

object EitherTMonadInstanceImplicits {

    fun <F, L> instance(MF: Monad<F>): EitherTMonadInstance<F, L> = object : EitherTMonadInstance<F, L> {
        override fun FF(): Functor<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

object EitherTApplicativeErrorInstanceImplicits {

    fun <F, L> instance(MF: Monad<F>): EitherTApplicativeErrorInstance<F, L> = object : EitherTApplicativeErrorInstance<F, L> {
        override fun FF(): Functor<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

object EitherTMonadErrorInstanceImplicits {

    fun <F, L> instance(MF: Monad<F>): EitherTMonadErrorInstance<F, L> = object : EitherTMonadErrorInstance<F, L> {
        override fun FF(): Functor<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

fun <F, A, B, C> EitherT<F, A, B>.foldLeft(b: C, f: (C, B) -> C, FF: Foldable<F>): C = FF.compose(Either.foldable<A>()).foldLC(value, b, f)

fun <F, A, B, C> EitherT<F, A, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>, FF: Foldable<F>): Eval<C> = FF.compose(Either.foldable<A>()).foldRC(value, lb, f)

fun <F, A, B, G, C> EitherT<F, A, B>. traverse(f: (B) -> HK<G, C>, GA: Applicative<G>, FF: Traverse<F>): HK<G, EitherT<F, A, C>> {
    val fa: HK<G, HK<Nested<F, EitherKindPartial<A>>, C>> = ComposedTraverse(FF, Either.traverse(), Either.monad<A>()).traverseC(value, f, GA)
    return GA.map(fa, { EitherT(FF.map(it.unnest(), { it.ev() })) })
}

object EitherTFoldableInstanceImplicits {

    fun <F, L> instance(FF: Foldable<F>): EitherTFoldableInstance<F, L> = object : EitherTFoldableInstance<F, L> {
        override fun FFF(): Foldable<F> = FF
    }
}

object EitherTTraverseInstanceImplicits {

    fun <F, L> instance(TF: Traverse<F>): EitherTTraverseInstance<F, L> = object : EitherTTraverseInstance<F, L> {
        override fun FFF(): Foldable<F> = TF

        override fun FF(): Functor<F> = TF

        override fun TF(): Traverse<F> = TF
    }
}

object EitherTSemigroupKInstanceImplicits {

    fun <F, L> instance(MF: Monad<F>): EitherTSemigroupKInstance<F, L> = object : EitherTSemigroupKInstance<F, L> {
        override fun MF(): Monad<F> = MF
    }
}

inline fun <reified F, L> EitherT.Companion.functor(FF: Functor<F> = functor<F>()): Functor<EitherTKindPartial<F, L>> =
        EitherTFunctorInstanceImplicits.instance(FF)

inline fun <reified F, L> EitherT.Companion.applicative(MF: Monad<F> = monad<F>()): Applicative<EitherTKindPartial<F, L>> =
        EitherTApplicativeInstanceImplicits.instance(MF)

inline fun <reified F, L> EitherT.Companion.monad(MF: Monad<F> = monad<F>()): Monad<EitherTKindPartial<F, L>> =
        EitherTMonadInstanceImplicits.instance(MF)

inline fun <reified F, L> EitherT.Companion.applicativeError(MF: Monad<F> = monad<F>()): ApplicativeError<EitherTKindPartial<F, L>, L> =
        EitherTApplicativeErrorInstanceImplicits.instance(MF)

inline fun <reified F, L> EitherT.Companion.monadError(MF: Monad<F> = monad<F>()): MonadError<EitherTKindPartial<F, L>, L> =
        EitherTMonadErrorInstanceImplicits.instance(MF)

inline fun <reified F, A> EitherT.Companion.traverse(FF: Traverse<F> = traverse<F>()): Traverse<EitherTKindPartial<F, A>> =
        EitherTTraverseInstanceImplicits.instance(FF)

inline fun <reified F, A> EitherT.Companion.foldable(FF: Traverse<F> = traverse<F>()): Foldable<EitherTKindPartial<F, A>> =
        EitherTFoldableInstanceImplicits.instance(FF)

inline fun <reified F, L> EitherT.Companion.semigroupK(MF: Monad<F> = monad<F>()): SemigroupK<EitherTKindPartial<F, L>> =
        EitherTSemigroupKInstanceImplicits.instance(MF)
