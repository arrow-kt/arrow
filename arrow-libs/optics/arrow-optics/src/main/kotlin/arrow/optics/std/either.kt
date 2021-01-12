package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.extensions.either.traverse.traverse
import arrow.core.fix
import arrow.typeclasses.Applicative

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> Either.Companion.toPValidated(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> = PIso(
  get = { it.fold(::Invalid, ::Valid) },
  reverseGet = Validated<A2, B2>::toEither
)

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> Either.Companion.toValidated(): Iso<Either<A, B>, Validated<A, B>> = toPValidated()

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Traversal.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
fun <L, R> PTraversal.Companion.either(): Traversal<Either<L, R>, R> =
  object : Traversal<Either<L, R>, R> {
    override fun <F> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> =
      with(Either.traverse<L>()) {
        FA.run { s.traverse(FA, f).map { it.fix() } }
      }
  }
