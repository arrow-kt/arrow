package arrow.fx.extensions.io.unsafeRun

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOUnsafeRun
import arrow.unsafe
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unsafeRun_singleton: IOUnsafeRun = object : arrow.fx.extensions.IOUnsafeRun {}

@JvmName("runBlocking")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
suspend fun <A> unsafe.runBlocking(fa: Function0<Kind<ForIO, A>>): A = arrow.fx.IO.unsafeRun().run {
  this@runBlocking.runBlocking<A>(fa) as A
}

@JvmName("runNonBlocking")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
suspend fun <A> unsafe.runNonBlocking(
  fa: Function0<Kind<ForIO, A>>,
  cb: Function1<Either<Throwable,
    A>, Unit>
): Unit = arrow.fx.IO.unsafeRun().run {
  this@runNonBlocking.runNonBlocking<A>(fa, cb) as kotlin.Unit
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.unsafeRun(): IOUnsafeRun = unsafeRun_singleton
