package arrow.mtl.syntax

import arrow.Kind
import arrow.core.Option
import arrow.core.applicative
import arrow.core.fix
import arrow.core.traverseFilter
import arrow.data.OptionT
import arrow.mtl.instances.ComposedTraverseFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse
import arrow.typeclasses.unnest

fun <F, G, A, B> OptionT<F, A>.traverseFilter(f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>, FF: Traverse<F>): Kind<G, OptionT<F, B>> = GA.run {
    val fa = ComposedTraverseFilter(FF, Option.traverseFilter(), Option.applicative()).traverseFilterC(value, f, GA)
    fa.map({ OptionT(FF.run { it.unnest().map({ it.fix() }) }) })
}
