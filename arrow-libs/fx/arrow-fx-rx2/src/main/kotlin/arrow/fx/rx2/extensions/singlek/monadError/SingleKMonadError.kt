package arrow.fx.rx2.extensions.singlek.monadError

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKMonadError
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
internal val monadError_singleton: SingleKMonadError = object :
  arrow.fx.rx2.extensions.SingleKMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.ensure(arg1: Function0<Throwable>, arg2: Function1<A, Boolean>):
  SingleK<A> = arrow.fx.rx2.SingleK.monadError().run {
    this@ensure.ensure<A>(arg1, arg2) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.redeemWith(
  arg1: Function1<Throwable, Kind<ForSingleK, B>>,
  arg2: Function1<A, Kind<ForSingleK, B>>
): SingleK<B> = arrow.fx.rx2.SingleK.monadError().run {
  this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.fx.rx2.SingleK<B>
}

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, Either<Throwable, A>>.rethrow(): SingleK<A> =
  arrow.fx.rx2.SingleK.monadError().run {
    this@rethrow.rethrow<A>() as arrow.fx.rx2.SingleK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadError(): SingleKMonadError = monadError_singleton
