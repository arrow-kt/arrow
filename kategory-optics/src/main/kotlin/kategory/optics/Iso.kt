package kategory.optics

import kategory.*

/**
 * An [Iso] defines an isomorphism between a type S and A:
 *
 *             get
 *     -------------------->
 *   S                       A
 *     <--------------------
 *          reverseGet
 *
 * A [Iso] is also a valid [Lens], [Prism]
 *
 * @param A the source of a [Iso]
 * @param B the target of a [Iso]
 */
abstract class Iso<A, B> {

    /**
     * Get the target of a [Iso]
     */
    abstract fun get(a: A): B

    /**
     * Get the modified source of a [Iso]
     */
    abstract fun reverseGet(b: B): A

    companion object {

        /**
         * create an [Iso] between any type and itself. id is the zero element of optics composition, for all optics o of type O (e.g. Lens, Iso, Prism, ...):
         * o      composeIso Iso.id == o
         * Iso.id composeO   o        == o (replace composeO by composeLens, composeIso, composePrism, ...)
         */
        fun <A> id() = Iso<A, A>(::identity, ::identity)

        operator fun <A, B> invoke(get: (A) -> (B), reverseGet: (B) -> A) = object : Iso<A, B>() {

            override fun get(a: A): B = get(a)

            override fun reverseGet(b: B): A = reverseGet(b)
        }
    }

    /**
     * Reverse a [Iso]: the source becomes the target and the target becomes the source
     */
    fun reverse(): Iso<B, A> = Iso(this::reverseGet,this::get)

    /**
     * Modify polymorphically the target of a [Iso] with a function
     */
    inline fun modify(crossinline f: (B) -> B): (A) -> A = { reverseGet(f(get(it))) }

    /**
     * Modify polymorphically the target of a [Iso] with a Functor function
     */
    inline fun <reified F> modifyF(FF: Functor<F> = functor(), f: (B) -> HK<F, B>, a: A): HK<F, A> =
            FF.map(f(get(a)), this::reverseGet)

    /**
     * Set polymorphically the target of a [Iso] with a value
     */
    fun set(b: B): (A) -> (A) = { reverseGet(b) }

    /**
     * Compose a [Iso] with a [Iso]
     */
    infix fun <C> composeIso(other: Iso<B, C>): Iso<A, C> = Iso(
            other::get compose this::get,
            this::reverseGet compose other::reverseGet
    )

    /**
     * Plus operator overload to compose lenses
     */
    operator fun <C> plus(other: Iso<B, C>): Iso<A, C> = composeIso(other)

    /**
     * View a [Iso] as a [Prism]
     */
    fun asPrism(): Prism<A, B> = Prism(
            { a -> Either.Right(get(a)) },
            this::reverseGet
    )

    /**
     * View a [Iso] as a [Lens]
     */
    fun asLens(): Lens<A, B> = Lens(this::get, this::set)
}