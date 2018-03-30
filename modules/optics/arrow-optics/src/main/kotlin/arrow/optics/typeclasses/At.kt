package arrow.optics.typeclasses

import arrow.core.None
import arrow.core.Option
import arrow.optics.Iso
import arrow.optics.Lens

inline operator fun <S, I, A, R> At<S, I, A>.invoke(ff: At<S, I, A>.() -> R) =
        run(ff)

/**
 * [At] provides a [Lens] for a structure [S] to focus in [A] at a given index [I].
 *
 * @param S source of [Lens]
 * @param I index that uniquely identifies the focus of the [Lens]
 * @param A focus that is supposed to be unique for a given pair [S] and [I].
 */
interface At<S, I, A> {

    /**
     * Get a [Lens] for a structure [S] with focus in [A] at index [i].
     */
    fun at(i: I): Lens<S, A>

    companion object {

        /**
         * Get an [At] for an index [i] given an [Index].
         */
        fun <S, I, A> at(AT: At<S, I, A>, i: I): Lens<S, A> = AT.at(i)

        /**
         * Lift an instance of [At] using an [Iso].
         */
        fun <S, U, I, A> fromIso(AT: At<U, I, A>, iso: Iso<S, U>): At<S, I, A> = object : At<S, I, A> {
            override fun at(i: I): Lens<S, A> = iso compose AT.at(i)
        }
    }

}

/**
 * Delete a value associated with a key in a Map-like container
 */
fun <S, I, A> At<S, I, Option<A>>.remove(s: S, i: I): S = at(i).set(s, None)

/**
 * Lift deletion of a value associated with a key in a Map-like container
 */
fun <S, I, A> At<S, I, Option<A>>.remove(i: I): (S) -> S = at(i).lift { None }
