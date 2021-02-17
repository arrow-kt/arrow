package arrow.core.extensions.sequencek.monoidal

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKMonoidal

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: SequenceKMonoidal = object :
  arrow.core.extensions.SequenceKMonoidal {}

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
fun <A> identity(): SequenceK<A> = arrow.core.SequenceK
  .monoidal()
  .identity<A>() as arrow.core.SequenceK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Monoidal typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monoidal(): SequenceKMonoidal = monoidal_singleton
