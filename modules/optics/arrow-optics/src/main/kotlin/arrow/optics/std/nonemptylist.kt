package arrow.optics

import arrow.core.*
import arrow.data.NonEmptyList
import arrow.data.NonEmptyListOf
import arrow.data.fix

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
fun <A> nelHead(): Lens<NonEmptyListOf<A>, A> = Lens(
        get = { it.fix().head },
        set = { newHead -> { nel -> NonEmptyList(newHead, nel.fix().tail) } }
)

/**
 * [PIso] that defines equality between [Option] [NonEmptyList] and a regular [List] structure
 */
fun <A, B> pOptionNelToList(): PIso<OptionOf<NonEmptyListOf<A>>, OptionOf<NonEmptyListOf<B>>, List<A>, List<B>> = PIso(
        get = { optNel -> optNel.fix().fold({ emptyList() }, { it.fix().all }) },
        reverseGet = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) }
)

/**
 * [Iso] that defines equality between [Option] [NonEmptyList] and a regular [List] structure
 */
fun <A> optionNelToList(): Iso<OptionOf<NonEmptyListOf<A>>, List<A>> = pOptionNelToList()
