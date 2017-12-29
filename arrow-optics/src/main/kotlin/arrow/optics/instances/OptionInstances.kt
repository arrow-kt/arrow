package arrow.optics

import arrow.*

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
 * [PPrism] to focus into an [arrow.Some]
 */
fun <A, B> pSomePrism(): PPrism<Option<A>, Option<B>, A, B> = PPrism(
        getOrModify = { option -> option.fold({ Left(None) }, { a -> Right(a) }) },
        reverseGet = { b -> Some(b) }
)

/**
 * [Prism] to focus into an [arrow.Some]
 */
fun <A> somePrism(): Prism<Option<A>, A> = pSomePrism()

/**
 * [Prism] to focus into an [arrow.None]
 */
fun <A> nonePrism(): Prism<Option<A>, Unit> = Prism(
        getOrModify = { option -> option.fold({ Right(Unit) }, { Left(Some(it)) }) },
        reverseGet = { _ -> None }
)

fun <A, B> pOptionToEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> = PIso(
        get = { opt -> opt.fold({ Left(Unit) }, { a -> Right(a) }) },
        reverseGet = { either -> either.fold({ None }, { b -> Some(b) }) }
)

fun <A> optionToEither(): Iso<Option<A>, Either<Unit, A>> = pOptionToEither()