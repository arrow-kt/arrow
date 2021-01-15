package arrow.core.extensions.endo.monoid

import arrow.core.Endo
import arrow.core.Endo.Companion
import arrow.core.extensions.EndoMonoid
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: EndoMonoid<Any?> = object : EndoMonoid<Any?> {}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll()",
  "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> Collection<Endo<A>>.combineAll(): Endo<A> = arrow.core.Endo.monoid<A>().run {
  this@combineAll.combineAll() as arrow.core.Endo<A>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll(arg0)",
  "arrow.core.Endo.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(arg0: List<Endo<A>>): Endo<A> = arrow.core.Endo
   .monoid<A>()
   .combineAll(arg0) as arrow.core.Endo<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.monoid(): EndoMonoid<A> = monoid_singleton as
    arrow.core.extensions.EndoMonoid<A>
