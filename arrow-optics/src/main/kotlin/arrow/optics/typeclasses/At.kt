package arrow.optics.typeclasses

import arrow.TC
import arrow.core.None
import arrow.core.Option
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.lift
import arrow.typeclass

/**
 * Typeclass that defines a [Lens] from an [S] to an [A] at an index [I]
 *
 * @param S source of [Lens]
 * @param I index that uniquely identifies the focus of the [Lens]
 * @param A focus that is supposed to be unique for a given pair [S] and [I]
 */
@typeclass
interface At<S, I, A> : TC {

    fun at(i: I): Lens<S, A>

    companion object {

        fun <S, I, A> at(AT: At<S, I, A>, i: I): Lens<S, A> = AT.at(i)

        operator fun <S, I, A> invoke(get: (I) -> (S) -> A, set: (I) -> (A) -> (S) -> S): At<S, I, A> = object : At<S, I, A> {
            override fun at(i: I): Lens<S, A> = Lens(get(i), set(i))
        }

        fun <S, U, I, A> fromIso(AT: At<U, I, A>, iso: Iso<S, U>): At<S, I, A> = object : At<S, I, A> {
            override fun at(i: I): Lens<S, A> = iso compose AT.at(i)
        }
    }

}

inline fun <reified S, reified U, reified I, reified A> fromIso(iso: Iso<S, U>, AT: At<U, I, A> = at()): At<S, I, A> = object : At<S, I, A> {
    override fun at(i: I): Lens<S, A> = iso compose AT.at(i)
}

/**
 * Delete a value associated with a key in a Map-like container
 */
fun <S, I, A> At<S, I, Option<A>>.remove(s: S, i: I): S = at(i).set(s, None)

/**
 * Delete a value associated with a key in a Map-like container
 */
fun <S, I, A> At<S, I, Option<A>>.remove(i: I): (S) -> S = at(i).lift { None }