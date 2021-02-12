package arrow.fx.extensions.io.dispatchers

import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IODispatchers
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val dispatchers_singleton: IODispatchers = object : arrow.fx.extensions.IODispatchers {}

@JvmName("io")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun io(): CoroutineContext = arrow.fx.IO
   .dispatchers()
   .io() as kotlin.coroutines.CoroutineContext

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.dispatchers(): IODispatchers = dispatchers_singleton
