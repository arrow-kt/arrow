package arrow.optics

import arrow.core.*
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.option.toOption

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

/**
 * [Iso] that defines the equality between and [arrow.Option] and [arrow.Either]
 */
fun <A, B> pOptionToEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> = PIso(
        get = { opt -> opt.fold({ Either.Left(Unit) }, { a -> Either.Right(a) }) },
        reverseGet = { either -> either.fold({ None }, { b -> Some(b) }) }
)

/**
 * [Iso] that defines the equality between and [arrow.Option] and [arrow.Either]
 */
fun <A> optionToEither(): Iso<Option<A>, Either<Unit, A>> = pOptionToEither()

/**
 * [Optional] to safely operate on value inside an [arrow.Option]
 */
fun <A> optionOptional(): Optional<Option<A>, A> = Optional(
        getOrModify = { a -> a.fold({ a.left() }, { it.right() }) },
        set = { a -> { it.fold({ Option.empty() }, { a.toOption() }) } }
)

/**
 * [Optional] to safely operate on a nullable value.
 */
fun <A> nullableOptional(): Optional<A?, A> = Optional(
        getOrModify = { a -> a?.right() ?: a.left() },
        set = { a -> { if (it != null) a else null } }
)
