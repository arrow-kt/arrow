package arrow.fx.rx2.extensions.observablek.monadError

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKMonadError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadError_singleton: ObservableKMonadError = object :
    arrow.fx.rx2.extensions.ObservableKMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.ensure(arg1: Function0<Throwable>, arg2: Function1<A, Boolean>):
    ObservableK<A> = arrow.fx.rx2.ObservableK.monadError().run {
  this@ensure.ensure<A>(arg1, arg2) as arrow.fx.rx2.ObservableK<A>
}

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.redeemWith(
  arg1: Function1<Throwable, Kind<ForObservableK, B>>,
  arg2: Function1<A, Kind<ForObservableK, B>>
): ObservableK<B> =
    arrow.fx.rx2.ObservableK.monadError().run {
  this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.fx.rx2.ObservableK<B>
}

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, Either<Throwable, A>>.rethrow(): ObservableK<A> =
    arrow.fx.rx2.ObservableK.monadError().run {
  this@rethrow.rethrow<A>() as arrow.fx.rx2.ObservableK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadError(): ObservableKMonadError = monadError_singleton
