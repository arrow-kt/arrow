package arrow.optics.typeclasses

import arrow.TC
import arrow.optics.Iso
import arrow.optics.Optional
import arrow.typeclass

/**
 * [Index] provides an [Optional] for a structure [S] to focus in [A] at a given index [I].
 *
 * @param S source of [Optional]
 * @param I index
 * @param A target of [Optional], [A] is supposed to be unique for a given pair [S] and [I].
 */
@typeclass
interface Index<S, I, A> : TC {

    /**
     * Get the focus [A] for a structure [S] at index [i].
     */
    fun index(i: I): Optional<S, A>

    companion object {

        /**
         * Lift an instance of [Index] using an [Iso].
         */
        fun <S, A, I , B> fromIso(ID: Index<A, I, B>, iso: Iso<S, A>): Index<S, I, B> = object : Index<S, I, B> {
            override fun index(i: I): Optional<S, B> = iso compose ID.index(i)
        }

        /**
         * Get an [Optional] for an index [i] given an [Index].
         */
        fun <S, I, A> index(ID: Index<S, I, A>, i: I): Optional<S, A> = ID.index(i)

    }

}

/**
 * Lift an instance of [Index] using an [Iso].
 */
inline fun <reified S, reified A, reified I, reified B> Index.Companion.fromIso(iso: Iso<S, A>, ID: Index<A, I, B> = arrow.optics.typeclasses.index()): Index<S, I, B> = Index.fromIso(ID, iso)

/**
 * Get an [Optional] for an index [i] given an [Index].
 */
inline fun <reified S, reified I, reified A> Index.Companion.index(i: I, ID: Index<S, I, A> = arrow.optics.typeclasses.index()): Optional<S, A> = ID.index(i)