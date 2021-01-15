package arrow.core.extensions.id.repeat

import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.extensions.IdRepeat
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val repeat_singleton: IdRepeat = object : arrow.core.extensions.IdRepeat {}

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
  "arrow.core.Id.repeat"
  ),
  DeprecationLevel.WARNING
)
fun <A> repeat(a: A): Id<A> = arrow.core.Id
   .repeat()
   .repeat<A>(a) as arrow.core.Id<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.repeat(): IdRepeat = repeat_singleton
