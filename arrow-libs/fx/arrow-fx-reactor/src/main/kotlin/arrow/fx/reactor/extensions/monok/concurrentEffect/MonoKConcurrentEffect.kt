package arrow.fx.reactor.extensions.monok.concurrentEffect

import arrow.Kind
import arrow.core.Either
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKConcurrentEffect
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
internal val concurrentEffect_singleton: MonoKConcurrentEffect = object :
  arrow.fx.reactor.extensions.MonoKConcurrentEffect {}

@JvmName("runAsyncCancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.runAsyncCancellable(
  arg1: Function1<Either<Throwable, A>, Kind<ForMonoK,
      Unit>>
): MonoK<Function0<Unit>> = arrow.fx.reactor.MonoK.concurrentEffect().run {
  this@runAsyncCancellable.runAsyncCancellable<A>(arg1) as
    arrow.fx.reactor.MonoK<kotlin.Function0<kotlin.Unit>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.concurrentEffect(): MonoKConcurrentEffect = concurrentEffect_singleton
