package arrow.fx.extensions.io.monoid

import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOMonoid
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Collection<IO<A>>.combineAll(SG: Monoid<A>): IO<A> = arrow.fx.IO.monoid<A>(SG).run {
  this@combineAll.combineAll() as arrow.fx.IO<A>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> combineAll(SG: Monoid<A>, arg0: List<IO<A>>): IO<A> = arrow.fx.IO
   .monoid<A>(SG)
   .combineAll(arg0) as arrow.fx.IO<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <A> Companion.monoid(SG: Monoid<A>): IOMonoid<A> = object :
    arrow.fx.extensions.IOMonoid<A> { override fun SG(): arrow.typeclasses.Monoid<A> = SG }
