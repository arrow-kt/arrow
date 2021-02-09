package arrow.core.extensions.sequence.monoid

import arrow.core.SequenceK
import arrow.core.extensions.SequenceKMonoid
import kotlin.sequences.Sequence

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
fun <A> Collection<SequenceK<A>>.combineAll(): Sequence<A> =
    arrow.core.extensions.sequence.monoid.Sequence.monoid<A>().run {
  this@combineAll.combineAll() as kotlin.sequences.Sequence<A>
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
fun <A> combineAll(arg0: List<SequenceK<A>>): Sequence<A> =
    arrow.core.extensions.sequence.monoid.Sequence
   .monoid<A>()
   .combineAll(arg0) as kotlin.sequences.Sequence<A>

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: SequenceKMonoid<Any?> = object : SequenceKMonoid<Any?> {}

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "@extension kinded projected functions are deprecated",
    ReplaceWith("Monoid.sequence<A>()", "arrow.core.sequence", "arrow.typeclasses.Monoid"),
    level = DeprecationLevel.WARNING
  )
  inline fun <A> monoid(): SequenceKMonoid<A> = monoid_singleton as
      arrow.core.extensions.SequenceKMonoid<A>}
