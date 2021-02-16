package arrow.core.extensions.either.applicative

import arrow.Kind
import arrow.core.Either
import arrow.core.replicate as _replicate
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherApplicative
import arrow.core.fix
import arrow.core.right
import arrow.typeclasses.Monoid
import kotlin.Any
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: EitherApplicative<Any?> = object : EitherApplicative<Any?> {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.Right(this)", "arrow.core.Either"))
fun <L, A> A.just(): Either<L, A> =
  right()

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.Right(Unit)", "arrow.core.Either"))
fun <L> unit(): Either<L, Unit> =
  Either.Right(Unit)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.map(arg1: Function1<A, B>): Either<L, B> =
  fix().map(arg1)

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(arg1)"))
fun <L, A> Kind<Kind<ForEither, L>, A>.replicate(arg1: Int): Either<L, List<A>> =
  fix().replicate(arg1)

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(arg1, arg2)", "arrow.core.replicate"))
fun <L, A> Kind<Kind<ForEither, L>, A>.replicate(arg1: Int, arg2: Monoid<A>): Either<L, A> =
  fix()._replicate(arg1, arg2)

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Either")
inline fun <L> Companion.applicative(): EitherApplicative<L> = applicative_singleton as
  arrow.core.extensions.EitherApplicative<L>
