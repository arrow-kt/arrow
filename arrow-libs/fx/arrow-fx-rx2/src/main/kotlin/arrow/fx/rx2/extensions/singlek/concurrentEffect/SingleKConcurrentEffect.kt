package arrow.fx.rx2.extensions.singlek.concurrentEffect

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKConcurrentEffect
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
internal val concurrentEffect_singleton: SingleKConcurrentEffect = object :
    arrow.fx.rx2.extensions.SingleKConcurrentEffect {}

@JvmName("runAsyncCancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.runAsyncCancellable(
  arg1: Function1<Either<Throwable, A>,
Kind<ForSingleK, Unit>>
): SingleK<Function0<Unit>> =
    arrow.fx.rx2.SingleK.concurrentEffect().run {
  this@runAsyncCancellable.runAsyncCancellable<A>(arg1) as
    arrow.fx.rx2.SingleK<kotlin.Function0<kotlin.Unit>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.concurrentEffect(): SingleKConcurrentEffect = concurrentEffect_singleton
