package arrow.core.extensions.sequence.monoidK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.extensions.SequenceKMonoidK
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "algebra()",
  "arrow.core.extensions.sequence.monoidK.Sequence.algebra"
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

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monoidK(): SequenceKMonoidK = monoidK_singleton}
