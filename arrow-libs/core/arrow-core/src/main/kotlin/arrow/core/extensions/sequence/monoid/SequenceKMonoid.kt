package arrow.core.extensions.sequence.monoid

import arrow.core.SequenceK
import arrow.core.extensions.SequenceKMonoid
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName
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
  "combineAll()",
  "arrow.core.combineAll"
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
  "combineAll(arg0)",
  "arrow.core.extensions.sequence.monoid.Sequence.combineAll"
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

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> monoid(): SequenceKMonoid<A> = monoid_singleton as
      arrow.core.extensions.SequenceKMonoid<A>}
