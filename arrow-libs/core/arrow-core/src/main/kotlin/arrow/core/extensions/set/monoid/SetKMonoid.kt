package arrow.core.extensions.set.monoid

import arrow.core.SetK
import arrow.core.extensions.SetKMonoid
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.jvm.JvmName

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
fun <A> Collection<SetK<A>>.combineAll(): Set<A> =
    arrow.core.extensions.set.monoid.Set.monoid<A>().run {
  this@combineAll.combineAll() as kotlin.collections.Set<A>
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
  "arrow.core.extensions.set.monoid.Set.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(arg0: List<SetK<A>>): Set<A> = arrow.core.extensions.set.monoid.Set
   .monoid<A>()
   .combineAll(arg0) as kotlin.collections.Set<A>

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: SetKMonoid<Any?> = object : SetKMonoid<Any?> {}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> monoid(): SetKMonoid<A> = monoid_singleton as arrow.core.extensions.SetKMonoid<A>}
