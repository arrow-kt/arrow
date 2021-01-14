package arrow.optics.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.fix
import arrow.core.extensions.either.traverse.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Either.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.traversal",
  ReplaceWith("Either.traversal<L, R>()", "arrow.core.Either", "arrow.optics.traversal"),
  DeprecationLevel.WARNING)
fun <L, R> Either.Companion.traversal(): Traversal<Either<L, R>, R> = object : Traversal<Either<L, R>, R> {
  override fun <F> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> = with(Either.traverse<L>()) {
    FA.run { s.traverse(FA, f).map { it.fix() } }
  }
}

/**
 * [Each] instance for [Either] that has focus in each [Either.Right].
 */
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith("Either.traversal<L, R>()", "arrow.core.Either", "arrow.optics.traversal"),
  DeprecationLevel.WARNING)
interface EitherEach<L, R> : Each<Either<L, R>, R> {
  override fun each(): Traversal<Either<L, R>, R> =
    Either.traversal()
}
