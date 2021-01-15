package arrow.core.extensions.sequence.monoidal

import arrow.core.extensions.SequenceKMonoidal
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

@JvmName("identity")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "identity()",
  "arrow.core.extensions.sequence.monoidal.Sequence.identity"
  ),
  DeprecationLevel.WARNING
)
fun <A> identity(): Sequence<A> = arrow.core.extensions.sequence.monoidal.Sequence
   .monoidal()
   .identity<A>() as kotlin.sequences.Sequence<A>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: SequenceKMonoidal = object :
    arrow.core.extensions.SequenceKMonoidal {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monoidal(): SequenceKMonoidal = monoidal_singleton}
