package arrow.optics

import arrow.core.Either
import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.typeclasses.Monoid

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> Either.Companion.toPValidated(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> =
  PIso(
    get = { it.fold(::Invalid, ::Valid) },
    reverseGet = Validated<A2, B2>::toEither
  )

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> Either.Companion.toValidated(): Iso<Either<A, B>, Validated<A, B>> =
  toPValidated()

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Traversal.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
fun <L, R> PTraversal.Companion.either(): Traversal<Either<L, R>, R> =
  PTraversal { s, f -> s.map(f) }

fun <L, R> Fold.Companion.either(): Fold<Either<L, R>, R> =
  object : Fold<Either<L, R>, R> {
    override fun <A> foldMap(M: Monoid<A>, s: Either<L, R>, map: (R) -> A): A =
      s.foldMap(M, map)
  }

fun <L, R> PEvery.Companion.either(): Every<Either<L, R>, R> =
  object : Every<Either<L, R>, R> {
    override fun <A> foldMap(M: Monoid<A>, s: Either<L, R>, map: (R) -> A): A =
      s.foldMap(M, map)

    override fun modify(s: Either<L, R>, map: (focus: R) -> R): Either<L, R> =
      s.map(map)
  }
