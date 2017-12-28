package arrow.syntax.optiont

import arrow.*

fun <F, A, B> OptionT<F, A>.foldLeft(b: B, f: (B, A) -> B, FF: Foldable<F>): B = FF.compose(Option.foldable()).foldLC(value, b, f)

fun <F, A, B> OptionT<F, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>): Eval<B> = FF.compose(Option.foldable()).foldRC(value, lb, f)

fun <F, G, A, B> OptionT<F, A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>, FF: Traverse<F>): HK<G, OptionT<F, B>> {
    val fa = ComposedTraverse(FF, Option.traverse(), Option.applicative()).traverseC(value, f, GA)
    return GA.map(fa, { OptionT(FF.map(it.unnest(), { it.ev() })) })
}