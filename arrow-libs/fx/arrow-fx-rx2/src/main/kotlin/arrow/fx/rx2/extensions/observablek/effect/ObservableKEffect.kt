package arrow.fx.rx2.extensions.observablek.effect

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKEffect
import kotlin.Deprecated
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
internal val effect_singleton: ObservableKEffect = object :
    arrow.fx.rx2.extensions.ObservableKEffect {}

@JvmName("runAsync")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.runAsync(
  arg1: Function1<Either<Throwable, A>, Kind<ForObservableK,
Unit>>
): ObservableK<Unit> = arrow.fx.rx2.ObservableK.effect().run {
  this@runAsync.runAsync<A>(arg1) as arrow.fx.rx2.ObservableK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.effect(): ObservableKEffect = effect_singleton
