package arrow.core.extensions.sequencek.repeat

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKRepeat
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val repeat_singleton: SequenceKRepeat = object : arrow.core.extensions.SequenceKRepeat {}

@JvmName("repeat")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "repeat(a)",
  "arrow.core.SequenceK.repeat"
  ),
  DeprecationLevel.WARNING
)
fun <A> repeat(a: A): SequenceK<A> = arrow.core.SequenceK
   .repeat()
   .repeat<A>(a) as arrow.core.SequenceK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.repeat(): SequenceKRepeat = repeat_singleton
