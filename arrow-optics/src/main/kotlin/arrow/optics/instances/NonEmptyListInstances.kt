package arrow.optics

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.data.NonEmptyList

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
fun <A> nelHead(): Lens<NonEmptyList<A>, A> = Lens(
        get = { it.head },
        set = { newHead -> { nel -> NonEmptyList(newHead, nel.tail) } }
)

/**
 * [PIso] that defines equality between [Option] [NonEmptyList] and a regular [List] structure
 */
fun <A, B> pOptionNelToList(): PIso<Option<NonEmptyList<A>>, Option<NonEmptyList<B>>, List<A>, List<B>> = PIso(
        get = { optNel -> optNel.fold({ emptyList() }, { it.all }) },
        reverseGet = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) }
)

/**
 * [Iso] that defines equality between [Option] [NonEmptyList] and a regular [List] structure
 */
fun <A> optionNelToList(): Iso<Option<NonEmptyList<A>>, List<A>> = pOptionNelToList()