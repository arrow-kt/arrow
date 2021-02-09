package arrow.core.extensions.sequence.monoidK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.extensions.SequenceKMonoidK
import arrow.typeclasses.Monoid

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
fun <A> algebra(): Monoid<Kind<ForSequenceK, A>> = arrow.core.extensions.sequence.monoidK.Sequence
   .monoidK()
   .algebra<A>() as arrow.typeclasses.Monoid<arrow.Kind<arrow.core.ForSequenceK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidK_singleton: SequenceKMonoidK = object : arrow.core.extensions.SequenceKMonoidK
    {}

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
    "MonoidK typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun monoidK(): SequenceKMonoidK = monoidK_singleton}
