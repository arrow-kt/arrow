package kategory.optics

import kategory.Option
import kategory.identity
import kategory.left
import kategory.none
import kategory.right
import kategory.some

/**
 * [PIso] that defines the equality in the kotlin nullable structure and [kategory.Option]
 */
fun <A, B> pNullableToOption(): PIso<A?, B?, Option<A>, Option<B>> = PIso(
        get = { a -> Option.fromNullable(a) },
        reverseGet = { option -> option.fold({ null }, ::identity) }
)

/**
 * [Iso] that defines the equality in the kotlin nullable structure and [kategory.Option]
 */
fun <A> nullableToOption(): Iso<A?, Option<A>> = pNullableToOption()

/**
 * [PPrism] to focus into an [kategory.Option.Some]
 */
fun <A, B> pSome(): PPrism<Option<A>, Option<B>, A, B> = PPrism(
        getOrModify = { option -> option.fold({ none<B>().left() }, { a -> a.right() }) },
        reverseGet = { b -> b.some() }
)

/**
 * [Prism] to focus into an [kategory.Option.Some]
 */
fun <A> some(): Prism<Option<A>, A> = pSome()

/**
 * [Prism] to focus into an [kategory.Option.None]
 */
fun <A> none(): Prism<Option<A>, Unit> = Prism(
        getOrModify = { option -> option.fold({ Unit.right() }, { it.some().left() }) },
        reverseGet = { _ -> none() }
)