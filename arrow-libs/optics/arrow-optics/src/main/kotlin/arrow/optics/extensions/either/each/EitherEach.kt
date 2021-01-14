package arrow.optics.extensions.either.each

import arrow.core.Either
import arrow.core.Either.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.EitherEach

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: EitherEach<Any?, Any?> = object : EitherEach<Any?, Any?> {}

@JvmName("each")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith(
    "Traversal.either<L, R>()",
    "arrow.optics.Traversal", "arrow.optics.either"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> each(): PTraversal<Either<L, R>, Either<L, R>, R, R> = arrow.core.Either
   .each<L, R>()
   .each() as arrow.optics.PTraversal<arrow.core.Either<L, R>, arrow.core.Either<L, R>, R, R>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith(
    "Traversal.either<L, R>()",
    "arrow.optics.Traversal", "arrow.optics.either"
  ),
  DeprecationLevel.WARNING)
inline fun <L, R> Companion.each(): EitherEach<L, R> = each_singleton as
    arrow.optics.extensions.EitherEach<L, R>
