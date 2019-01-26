package arrow.typeclasses

import arrow.core.Option

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semiring)
 */
interface Semiring<A> : Monoid<A> {

    fun zero(): A
    fun one(): A

    fun A.product(b: A): A

    override fun empty(): A = one()

    operator fun A.times(b: A): A =
            this.product(b)

    fun A.maybeProduct(b: A?): A =
            Option.fromNullable(b).fold({ this }, { product(it) })
}