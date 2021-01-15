package arrow.core.extensions.sequencek.monoidal

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKMonoidal
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "identity()",
  "arrow.core.SequenceK.identity"
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
inline fun Companion.monoidal(): SequenceKMonoidal = monoidal_singleton
