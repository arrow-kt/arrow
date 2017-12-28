package arrow.syntax.eithert

import arrow.*

fun <F, A, B, C> EitherT<F, A, B>.foldLeft(b: C, f: (C, B) -> C, FF: Foldable<F>): C = FF.compose(Either.foldable<A>()).foldLC(value, b, f)

fun <F, A, B, C> EitherT<F, A, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>, FF: Foldable<F>): Eval<C> = FF.compose(Either.foldable<A>()).foldRC(value, lb, f)

fun <F, A, B, G, C> EitherT<F, A, B>. traverse(f: (B) -> HK<G, C>, GA: Applicative<G>, FF: Traverse<F>): HK<G, EitherT<F, A, C>> {
    val fa: HK<G, HK<Nested<F, EitherKindPartial<A>>, C>> = ComposedTraverse(FF, Either.traverse(), Either.monad<A>()).traverseC(value, f, GA)
    return GA.map(fa, { EitherT(FF.map(it.unnest(), { it.ev() })) })
}