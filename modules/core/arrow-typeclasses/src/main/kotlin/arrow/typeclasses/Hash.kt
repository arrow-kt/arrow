package arrow.typeclasses

import arrow.TC
import arrow.typeclass

/**
 * A type class used to represent a hashing scheme for objects of a given type.
 *
 * For any two instances `x` and `y` that are considered equivalent under the
 * equivalence relation defined by this object, `hash(x)` should equal `hash(y)`.
 */
@typeclass
interface Hash<in F> : Eq<F>, TC {
    /**
     * @return the hash code of the given object under this hashing scheme.
     */
    fun hash(a: F): Int

    companion object {

        /**
         * Construct a [Hash] instance from a function `(A) -> Int`
         *
         * @param feqv function that defines if two instances of type [A] are equal.
         * @param fhash the hash function
         * @returns a [Hash] instance that is defined by the [fhash] function.
         */
        inline operator fun <F> invoke(crossinline feqv: (F, F) -> Boolean,
                                       crossinline fhash: (F) -> Int): Hash<F> = object : Hash<F> {
            override fun eqv(a: F, b: F): Boolean =
                    feqv(a, b)

            override fun hash(a: F): Int =
                    fhash(a)
        }

        /**
         * Retrieve a [Hash] instance that defines the hash function as `hashCode`
         * and the equivalence relation as `==`,  for type [F].
         *
         * @returns a [Hash] instance with the universal `hashCode` function and the universal equality relation.
         */
        fun any(): Hash<Any> = HashAny

        private object HashAny : Hash<Any> {
            override fun hash(a: Any): Int =
                    a.hashCode()

            override fun eqv(a: Any, b: Any): Boolean = a == b
        }
    }
}
