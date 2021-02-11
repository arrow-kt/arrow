package arrow.fx.reactor.extensions.monok.monadError

import arrow.Kind
import arrow.core.Either
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKMonadError
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
internal val monadError_singleton: MonoKMonadError = object :
    arrow.fx.reactor.extensions.MonoKMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.ensure(arg1: Function0<Throwable>, arg2: Function1<A, Boolean>): MonoK<A> =
    arrow.fx.reactor.MonoK.monadError().run {
  this@ensure.ensure<A>(arg1, arg2) as arrow.fx.reactor.MonoK<A>
}

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.redeemWith(
  arg1: Function1<Throwable, Kind<ForMonoK, B>>,
  arg2: Function1<A, Kind<ForMonoK, B>>
): MonoK<B> = arrow.fx.reactor.MonoK.monadError().run {
  this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.fx.reactor.MonoK<B>
}

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, Either<Throwable, A>>.rethrow(): MonoK<A> =
    arrow.fx.reactor.MonoK.monadError().run {
  this@rethrow.rethrow<A>() as arrow.fx.reactor.MonoK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monadError(): MonoKMonadError = monadError_singleton
