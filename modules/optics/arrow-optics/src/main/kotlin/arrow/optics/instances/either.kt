package arrow.optics.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.fix
import arrow.extension
import arrow.instances.either.traverse.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Either.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
fun <L, R> Either.Companion.traversal(): Traversal<Either<L, R>, R> = object : Traversal<Either<L, R>, R> {
  override fun <F> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> = with(Either.traverse<L>()) {
    FA.run { s.traverse(FA, f).map { it.fix() } }
  }
}

/**
 * [Each] instance for [Either] that has focus in each [Either.Right].
 */
@extension
interface EitherEachInstance<L, R> : Each<Either<L, R>, R> {
  override fun each(): Traversal<Either<L, R>, R> =
    Either.traversal()
}
