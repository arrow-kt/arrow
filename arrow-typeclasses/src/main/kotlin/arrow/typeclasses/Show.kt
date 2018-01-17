package arrow.typeclasses

import arrow.*

/**
 * A type class used to get a textual representation for an instance of type [A] in a type safe way.
 *
 */
@typeclass
interface Show<in A> : TC {

    /**
     * Given an object [a] of type [A] it returns its textual representation.
     *
     * @param a object of type [A].
     * @returns a [String] representing [a].
     */
    fun show(a: A): String

    companion object {

        /**
         * Construct a [Show] instance from a function `(A) -> String`
         *
         * @param fshow function that defines a textual representation for type [A].
         * @returns a [Show] instance that is defined by the [fshow] function.
         */
        operator inline fun <A> invoke(crossinline fshow: (A) -> String): Show<A> = object : Show<A> {
            override fun show(a: A): String =
                    fshow(a)
        }

        /**
         * Construct a [Show] instance using object `toString`.
         *
         * @returns a [Show] instance that is defined by the [A] `toString` method.
         */
        fun <A> fromToString(): Show<A> = object : Show<A> {
            override fun show(a: A): String =
                    a.toString()
        }

        /**
         * Retrieve a [Show] that defines the textual representation as toString() for type [A].
         */
        fun any(): Show<Any?> = ShowAny

        private object ShowAny : Show<Any?> {
            override fun show(a: Any?): String =
                    a.toString()
        }
    }
}