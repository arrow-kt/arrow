package arrow.generic

import arrow.coproduct
import arrow.core.Option
import arrow.core.toOption

@coproduct
data class Coproduct<GENERICS : GenericHolder>(
        val value: Any?,
        val generics: GENERICS
)

//TODO I think we may be able to generate type safe methods for this, as in the type A needs to be included in the Generic Holder somewhere.
//We just need to add methods for each placeholder? Coproduct<Generic2<*, A>> / Coproduct<Generic2<A, *>>> ?
inline fun <reified A> Coproduct<*>.select(): Option<A> = (value as? A).toOption()