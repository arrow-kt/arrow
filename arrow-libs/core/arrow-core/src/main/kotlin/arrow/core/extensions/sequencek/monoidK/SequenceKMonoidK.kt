package arrow.core.extensions.sequencek.monoidK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKMonoidK
import arrow.typeclasses.Monoid

/**
 * cached extension
 */
@PublishedApi()
internal val monoidK_singleton: SequenceKMonoidK = object : arrow.core.extensions.SequenceKMonoidK
    {}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Monoid.sequence<A>()",
    "arrow.core.sequence", "arrow.typeclasses.Monoid"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Monoid<Kind<ForSequenceK, A>> = arrow.core.SequenceK
   .monoidK()
   .algebra<A>() as arrow.typeclasses.Monoid<arrow.Kind<arrow.core.ForSequenceK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "MonoidK typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monoidK(): SequenceKMonoidK = monoidK_singleton
