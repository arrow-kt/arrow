package arrow.core.extensions.sequence.monoidal

import arrow.core.extensions.SequenceKMonoidal
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
    "emptySequence<A>()"
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
    "Monoidal typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun monoidal(): SequenceKMonoidal = monoidal_singleton}
