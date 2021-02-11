package arrow.fx.rx2.extensions.maybek.monadError

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKMonadError
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
internal val monadError_singleton: MaybeKMonadError = object :
    arrow.fx.rx2.extensions.MaybeKMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, A>.ensure(arg1: Function0<Throwable>, arg2: Function1<A, Boolean>):
    MaybeK<A> = arrow.fx.rx2.MaybeK.monadError().run {
  this@ensure.ensure<A>(arg1, arg2) as arrow.fx.rx2.MaybeK<A>
}

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.redeemWith(
  arg1: Function1<Throwable, Kind<ForMaybeK, B>>,
  arg2: Function1<A, Kind<ForMaybeK, B>>
): MaybeK<B> = arrow.fx.rx2.MaybeK.monadError().run {
  this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, Either<Throwable, A>>.rethrow(): MaybeK<A> =
    arrow.fx.rx2.MaybeK.monadError().run {
  this@rethrow.rethrow<A>() as arrow.fx.rx2.MaybeK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadError(): MaybeKMonadError = monadError_singleton
