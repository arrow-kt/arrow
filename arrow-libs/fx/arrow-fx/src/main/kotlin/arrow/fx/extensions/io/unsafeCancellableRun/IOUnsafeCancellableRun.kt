package arrow.fx.extensions.io.unsafeCancellableRun

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.OnCancel
import arrow.fx.extensions.IOUnsafeCancellableRun
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
internal val unsafeCancellableRun_singleton: IOUnsafeCancellableRun = object :
  arrow.fx.extensions.IOUnsafeCancellableRun {}

@JvmName("runNonBlockingCancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
suspend fun <A> unsafe.runNonBlockingCancellable(
  onCancel: OnCancel,
  fa: Function0<Kind<ForIO, A>>,
  cb: Function1<Either<Throwable, A>, Unit>
): Function0<Unit> = arrow.fx.IO.unsafeCancellableRun().run {
  this@runNonBlockingCancellable.runNonBlockingCancellable<A>(onCancel, fa, cb) as
    kotlin.Function0<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.unsafeCancellableRun(): IOUnsafeCancellableRun = unsafeCancellableRun_singleton
