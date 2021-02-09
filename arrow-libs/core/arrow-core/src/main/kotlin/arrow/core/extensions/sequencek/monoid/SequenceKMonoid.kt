package arrow.core.extensions.sequencek.monoid

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKMonoid

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: SequenceKMonoid<Any?> = object : SequenceKMonoid<Any?> {}

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
    "this.fold(emptySequence()) { acc, l -> acc + l }"
  ),
  DeprecationLevel.WARNING
)
fun <A> Collection<SequenceK<A>>.combineAll(): SequenceK<A> = arrow.core.SequenceK.monoid<A>().run {
  this@combineAll.combineAll() as arrow.core.SequenceK<A>
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
    "arg0.fold(emptySequence()) { acc, l -> acc + l }"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(arg0: List<SequenceK<A>>): SequenceK<A> = arrow.core.SequenceK
   .monoid<A>()
   .combineAll(arg0) as arrow.core.SequenceK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("Monoid.sequence<A>()", "arrow.core.sequence", "arrow.typeclasses.Monoid"),
  level = DeprecationLevel.WARNING
)
inline fun <A> Companion.monoid(): SequenceKMonoid<A> = monoid_singleton as
    arrow.core.extensions.SequenceKMonoid<A>
