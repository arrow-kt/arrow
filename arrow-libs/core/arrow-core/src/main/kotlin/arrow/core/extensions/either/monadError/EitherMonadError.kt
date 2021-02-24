package arrow.core.extensions.either.monadError

import arrow.Kind
import arrow.core.Either
import arrow.core.ensure as _ensure
import arrow.core.redeemWith as _redeemWith
import arrow.core.flatten as _flatten
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherMonadError
import arrow.core.fix
import kotlin.Any
import kotlin.Boolean
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadError_singleton: EitherMonadError<Any?> = object : EitherMonadError<Any?> {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ensure(arg1, arg2)", "arrow.core.ensure"))
fun <L, A> Kind<Kind<ForEither, L>, A>.ensure(arg1: Function0<L>, arg2: Function1<A, Boolean>):
  Either<L, A> =
    fix()._ensure(arg1, arg2)

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("redeemWith(arg1, arg2)", "arrow.core.redeemWith"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.redeemWith(
  arg1: Function1<L, Kind<Kind<ForEither, L>,
      B>>,
  arg2: Function1<A, Kind<Kind<ForEither, L>, B>>
): Either<L, B> =
  fix()._redeemWith({ arg1(it).fix() }, { arg2(it).fix() })

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatten()", "arrow.core.flatten"))
fun <L, A> Kind<Kind<ForEither, L>, Either<L, A>>.rethrow(): Either<L, A> =
  fix()._flatten()

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("MonadError typeclasses is deprecated. Use concrete methods on Either")
inline fun <L> Companion.monadError(): EitherMonadError<L> = monadError_singleton as
  arrow.core.extensions.EitherMonadError<L>
