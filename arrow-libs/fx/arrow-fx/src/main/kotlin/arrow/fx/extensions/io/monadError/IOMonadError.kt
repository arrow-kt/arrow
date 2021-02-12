package arrow.fx.extensions.io.monadError

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOMonadError
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
internal val monadError_singleton: IOMonadError = object : arrow.fx.extensions.IOMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.ensure(arg1: Function0<Throwable>, arg2: Function1<A, Boolean>): IO<A> =
  arrow.fx.IO.monadError().run {
    this@ensure.ensure<A>(arg1, arg2) as arrow.fx.IO<A>
  }

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.redeemWith(
  arg1: Function1<Throwable, Kind<ForIO, B>>,
  arg2: Function1<A,
    Kind<ForIO, B>>
): IO<B> = arrow.fx.IO.monadError().run {
  this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.fx.IO<B>
}

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, Either<Throwable, A>>.rethrow(): IO<A> = arrow.fx.IO.monadError().run {
  this@rethrow.rethrow<A>() as arrow.fx.IO<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.monadError(): IOMonadError = monadError_singleton
