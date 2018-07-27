package arrow.generic

import arrow.coproduct
import arrow.core.Option
import arrow.core.toOption

@coproduct
data class Coproduct(val value: Any?)

inline fun <reified A> Coproduct.select(): Option<A> = (value as? A).toOption()