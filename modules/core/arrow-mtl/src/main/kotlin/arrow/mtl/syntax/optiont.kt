package arrow.mtl.syntax

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.instances.applicative
import arrow.mtl.instances.*
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse
import arrow.typeclasses.unnest

fun <F, G, A, B> OptionT<F, A>.traverseFilter(f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>, FF: Traverse<F>): Kind<G, OptionT<F, B>> {
    val fa = ComposedTraverseFilter(FF, Option.traverseFilter(), Option.applicative()).traverseFilterC(value, f, GA)
    return GA.map(fa, { OptionT(FF.map(it.unnest(), { it.reify() })) })
}