package arrow.core.extensions.sequencek.monoidK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKMonoidK
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "algebra()",
  "arrow.core.SequenceK.algebra"
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
inline fun Companion.monoidK(): SequenceKMonoidK = monoidK_singleton
