package arrow.fx.extensions.io.concurrentEffect

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IODefaultConcurrentEffect
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
internal val concurrentEffect_singleton: IODefaultConcurrentEffect = object :
  arrow.fx.extensions.IODefaultConcurrentEffect {}

@JvmName("runAsyncCancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.runAsyncCancellable(cb: Function1<Either<Throwable, A>, Kind<ForIO, Unit>>):
  IO<Function0<Unit>> = arrow.fx.IO.concurrentEffect().run {
    this@runAsyncCancellable.runAsyncCancellable<A>(cb) as arrow.fx.IO<kotlin.Function0<kotlin.Unit>>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.concurrentEffect(): IODefaultConcurrentEffect = concurrentEffect_singleton
