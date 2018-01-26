package arrow.optics

import arrow.core.*
import arrow.optics.Iso
import arrow.optics.PIso
import arrow.optics.PPrism
import arrow.optics.Prism

/**
 * [PIso] that defines the equality in the kotlin nullable structure and [arrow.Option]
 */
fun <A, B> pNullableToOption(): PIso<A?, B?, Option<A>, Option<B>> = PIso(
        get = { a -> Option.fromNullable(a) },
        reverseGet = { option -> option.fold({ null }, ::identity) }
)

/**
 * [Iso] that defines the equality in the kotlin nullable structure and [arrow.Option]
 */
fun <A> nullableToOption(): Iso<A?, Option<A>> = pNullableToOption()

/**
 * [PPrism] to focus into an [arrow.Option.Some]
 */
fun <A, B> pSomePrism(): PPrism<Option<A>, Option<B>, A, B> = PPrism(
        getOrModify = { option -> option.fold({ Either.Left(None) }, { a -> Either.Right(a) }) },
        reverseGet = { b -> Some(b) }
)

/**
 * [Prism] to focus into an [arrow.Option.Some]
 */
fun <A> somePrism(): Prism<Option<A>, A> = pSomePrism()

/**
 * [Prism] to focus into an [arrow.Option.None]
 */
fun <A> nonePrism(): Prism<Option<A>, Unit> = Prism(
        getOrModify = { option -> option.fold({ Either.Right(Unit) }, { Either.Left(Some(it)) }) },
        reverseGet = { _ -> None }
)

fun <A, B> pOptionToEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> = PIso(
        get = { opt -> opt.fold({ Either.Left(Unit) }, { a -> Either.Right(a) }) },
        reverseGet = { either -> either.fold({ None }, { b -> Some(b) }) }
)

fun <A> optionToEither(): Iso<Option<A>, Either<Unit, A>> = pOptionToEither()