package arrow.optics.typeclasses

import arrow.HK
import arrow.TC
import arrow.optics.Iso
import arrow.optics.Traversal
import arrow.typeclass
import arrow.typeclasses.Traverse

/**
 * [Each] provides a [Traversal] that can focus into a structure [S] to see all its foci [A].
 *
 * @param S source of the [Traversal].
 * @param A focus of [Traversal].
 */
@typeclass
interface Each<S, A> : TC {

    /**
     * Get a [Traversal] for a structure [S] with focus in [A].
     */
    fun each(): Traversal<S, A>

    companion object {

        /**
         * Lift an instance of [Each] using an [Iso].
         */
        fun <S, A, B> fromIso(iso: Iso<S, A>, EA: Each<A, B>): Each<S, B> = object : Each<S, B> {
            override fun each(): Traversal<S, B> = iso compose EA.each()
        }

        /**
         * Create an instance of [Each] from a [Traverse].
         */
        fun <S, A> fromTraverse(T: Traverse<S>): Each<HK<S, A>, A> = object : Each<HK<S, A>, A> {
            override fun each(): Traversal<HK<S, A>, A> = Traversal.fromTraversable(T)
        }
    }

}

/**
 * Create an instance of [Each] from a [Traverse].
 */
inline fun <reified S, A> Each.Companion.fromTraverse(T: Traverse<S>, dummy: Unit = Unit) = object : Each<HK<S, A>, A> {
    override fun each(): Traversal<HK<S, A>, A> = Traversal.fromTraversable(T)

}