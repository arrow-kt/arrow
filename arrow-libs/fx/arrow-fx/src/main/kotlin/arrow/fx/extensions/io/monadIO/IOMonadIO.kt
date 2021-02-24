package arrow.fx.extensions.io.monadIO

import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOMonadIO
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadIO_singleton: IOMonadIO = object : arrow.fx.extensions.IOMonadIO {}

@JvmName("liftIO")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> IO<A>.liftIO(): IO<A> = arrow.fx.IO.monadIO().run {
  this@liftIO.liftIO<A>() as arrow.fx.IO<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.monadIO(): IOMonadIO = monadIO_singleton
