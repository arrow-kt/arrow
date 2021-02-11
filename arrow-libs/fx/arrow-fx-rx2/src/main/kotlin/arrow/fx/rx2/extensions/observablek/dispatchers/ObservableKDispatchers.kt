package arrow.fx.rx2.extensions.observablek.dispatchers

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKDispatchers
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val dispatchers_singleton: ObservableKDispatchers = object :
    arrow.fx.rx2.extensions.ObservableKDispatchers {}

@JvmName("default")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun default(): CoroutineContext = arrow.fx.rx2.ObservableK
   .dispatchers()
   .default() as kotlin.coroutines.CoroutineContext

@JvmName("io")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun io(): CoroutineContext = arrow.fx.rx2.ObservableK
   .dispatchers()
   .io() as kotlin.coroutines.CoroutineContext

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.dispatchers(): ObservableKDispatchers = dispatchers_singleton
