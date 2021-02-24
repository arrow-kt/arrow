package arrow.core.extensions.endo.monoid

import arrow.core.Endo
import arrow.core.Endo.Companion
import arrow.core.extensions.EndoMonoid

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
    "if (isEmpty()) Endo(::identity) else reduce { a, b -> a.combine(b) }",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <A> Collection<Endo<A>>.combineAll(): Endo<A> =
  arrow.core.Endo.monoid<A>().run {
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
    "if (arg0.isEmpty()) Endo(::identity) else arg0.reduce { a, b -> a.combine(b) }",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(arg0: List<Endo<A>>): Endo<A> =
  arrow.core.Endo
    .monoid<A>()
    .combineAll(arg0) as arrow.core.Endo<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Extension projections are deprecated. Use endo on Monoid.",
  ReplaceWith("Monoid.endo()", "arrow.typeclasses.Monoid", "arrow.core.endo")
)
inline fun <A> Companion.monoid(): EndoMonoid<A> = monoid_singleton as
  arrow.core.extensions.EndoMonoid<A>
