package arrow.fx.rx2.extensions.observablek.concurrentEffect

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKConcurrentEffect
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
internal val concurrentEffect_singleton: ObservableKConcurrentEffect = object :
    arrow.fx.rx2.extensions.ObservableKConcurrentEffect {}

@JvmName("runAsyncCancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.runAsyncCancellable(
  arg1: Function1<Either<Throwable, A>,
Kind<ForObservableK, Unit>>
): ObservableK<Function0<Unit>> =
    arrow.fx.rx2.ObservableK.concurrentEffect().run {
  this@runAsyncCancellable.runAsyncCancellable<A>(arg1) as
    arrow.fx.rx2.ObservableK<kotlin.Function0<kotlin.Unit>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.concurrentEffect(): ObservableKConcurrentEffect = concurrentEffect_singleton
