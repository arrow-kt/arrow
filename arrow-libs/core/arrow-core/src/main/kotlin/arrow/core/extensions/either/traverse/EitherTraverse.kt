package arrow.core.extensions.either.traverse

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherTraverse
import arrow.core.fix
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Any
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: EitherTraverse<Any?> = object : EitherTraverse<Any?> {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverse or traverseValidated from arrow.core.*")
fun <L, G, A, B> Kind<Kind<ForEither, L>, A>.traverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Kind<Kind<ForEither, L>, B>> = arrow.core.Either.traverse<L>().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForEither, L>, B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequence or sequenceValidated from arrow.core.*")
fun <L, G, A> Kind<Kind<ForEither, L>, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G,
  Kind<Kind<ForEither, L>, A>> = arrow.core.Either.traverse<L>().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForEither,
        L>, A>>
}

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

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. This signature is not valid for Validated.")
fun <L, G, A, B> Kind<Kind<ForEither, L>, A>.flatTraverse(
  arg1: Monad<Kind<ForEither, L>>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<Kind<ForEither, L>, B>>>
): Kind<G, Kind<Kind<ForEither, L>, B>> = arrow.core.Either.traverse<L>().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForEither, L>, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Traverse typeclasses is deprecated. Use concrete methods on Validated")
inline fun <L> Companion.traverse(): EitherTraverse<L> = traverse_singleton as
  arrow.core.extensions.EitherTraverse<L>
