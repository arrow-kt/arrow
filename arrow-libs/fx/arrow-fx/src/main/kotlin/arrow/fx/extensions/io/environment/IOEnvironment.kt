package arrow.fx.extensions.io.environment

import arrow.fx.ForIO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOEnvironment
import arrow.fx.typeclasses.Dispatchers
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val environment_singleton: IOEnvironment = object : arrow.fx.extensions.IOEnvironment {}

@JvmName("dispatchers")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun dispatchers(): Dispatchers<ForIO> = arrow.fx.IO
   .environment()
   .dispatchers() as arrow.fx.typeclasses.Dispatchers<arrow.fx.ForIO>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.environment(): IOEnvironment = environment_singleton
