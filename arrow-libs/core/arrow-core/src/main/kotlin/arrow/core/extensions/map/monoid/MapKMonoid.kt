package arrow.core.extensions.map.monoid

import arrow.core.MapK
import arrow.core.extensions.MapKMonoid
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.Map
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
    "combineAll(SG)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Collection<MapK<K, A>>.combineAll(SG: Semigroup<A>): Map<K, A> =
  arrow.core.extensions.map.monoid.Map.monoid<K, A>(SG).run {
    this@combineAll.combineAll() as kotlin.collections.Map<K, A>
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
    "arg0.combineAll(SG)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> combineAll(SG: Semigroup<A>, arg0: List<MapK<K, A>>): Map<K, A> =
  arrow.core.extensions.map.monoid.Map
    .monoid<K, A>(SG)
    .combineAll(arg0) as kotlin.collections.Map<K, A>

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("@extension projected functions are deprecated", ReplaceWith("Monoid.map(SG)", "arrow.core.map", "arrow.core.Monoid"))
  inline fun <K, A> monoid(SG: Semigroup<A>): MapKMonoid<K, A> = object :
    arrow.core.extensions.MapKMonoid<K, A> {
    override fun SG(): arrow.typeclasses.Semigroup<A> =
      SG
  }
}
