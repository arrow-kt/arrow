package arrow.core.extensions.setk.monoid

import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKMonoid
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
internal val monoid_singleton: SetKMonoid<Any?> = object : SetKMonoid<Any?> {}

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
    "combineAll(Monoid.set<A>())",
    "arrow.core.combineAll", "arrow.typeclasses.Monoid", "arrow.core.set"
  ),
  DeprecationLevel.WARNING
)
fun <A> Collection<SetK<A>>.combineAll(): SetK<A> = arrow.core.SetK.monoid<A>().run {
  this@combineAll.combineAll() as arrow.core.SetK<A>
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
    "arg0.combineAll(Monoid.set<A>())",
    "arrow.core.combineAll", "arrow.typeclasses.Monoid", "arrow.core.set"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(arg0: List<SetK<A>>): SetK<A> = arrow.core.SetK
  .monoid<A>()
  .combineAll(arg0) as arrow.core.SetK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Monoid.set<A>()",
    "arrow.core.set",
    "arrow.typeclasses.Monoid"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.monoid(): SetKMonoid<A> = monoid_singleton as
  arrow.core.extensions.SetKMonoid<A>
