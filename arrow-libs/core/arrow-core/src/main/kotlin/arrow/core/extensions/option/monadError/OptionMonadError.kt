package arrow.core.extensions.option.monadError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionMonadError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadError_singleton: OptionMonadError = object :
    arrow.core.extensions.OptionMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "ensure(arg1, arg2)",
  "arrow.core.ensure"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.ensure(arg1: Function0<Unit>, arg2: Function1<A, Boolean>): Option<A> =
    arrow.core.Option.monadError().run {
  this@ensure.ensure<A>(arg1, arg2) as arrow.core.Option<A>
}

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "redeemWith(arg1, arg2)",
  "arrow.core.redeemWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.redeemWith(
  arg1: Function1<Unit, Kind<ForOption, B>>,
  arg2: Function1<A, Kind<ForOption, B>>
): Option<B> = arrow.core.Option.monadError().run {
  this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.core.Option<B>
}

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "rethrow()",
  "arrow.core.rethrow"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, Either<Unit, A>>.rethrow(): Option<A> = arrow.core.Option.monadError().run {
  this@rethrow.rethrow<A>() as arrow.core.Option<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.monadError(): OptionMonadError = monadError_singleton
