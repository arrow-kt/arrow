package arrow.typeclasses

import arrow.*


/**
 * A type class used to get a textual representation for an instance of type [F] in a type safe way.
 *
 */
@typeclass
interface Show<in F> : TC {

    /**
     * Given an object [a] of type [F] it returns its textual representation.
     *
     * @param a object of type [F].
     * @returns a [String] representing [a].
     */
    fun show(a: F): String

    companion object {

        /**
         * Construct a [Show] instance from a function `(F) -> String`
         *
         * @param fshow function that defines a textual representation for type [F].
         * @returns a [Show] instance that is defined by the [fshow] function.
         */
        operator inline fun <F> invoke(crossinline fshow: (F) -> String): Show<F> = object : Show<F> {
            override fun show(a: F): String =
                    fshow(a)
        }

        /**
         * Construct a [Show] instance using object `toString`.
         *
         * @param a object of type [F].
         * @returns a [Show] instance that is defined by the [F] `toString` method.
         */
        fun <F> fromToString(a: F): Show<F> = object : Show<F> {
            override fun show(a: F): String =
                    a.toString()
        }

        /**
         * Retrieve a [Show] that defines the textual representation as toString() for type [F].
         */
        fun any(): Show<Any?> = ShowAny

        private object ShowAny : Show<Any?> {
            override fun show(a: Any?): String =
                    a.toString()
        }
    }
}