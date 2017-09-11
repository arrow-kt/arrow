package kategory.optics

import kategory.*

/**
 * An [Iso] defines an isomorphism between a type S and A:
 * <pre>
 *             get
 *     -------------------->
 *   S                       A
 *     <--------------------
 *          reverseGet
 * </pre>
 * A [Iso] is also a valid [Getter], [Fold], [Lens], [Prism], [Optional], [Traversal] and [Setter]
 *
 *
 * @tparam A the source of a [Iso]
 * @tparam B the target of a [Iso]
 */
abstract class Iso<A, B> {

    /** get the target of a [ISO] */
    abstract fun get(a: A): B

    /** get the modified source of a [ISO] */
    abstract fun reverseGet(b: B): A

    companion object {
        operator fun <A, B> invoke(get: (A) -> (B), reverseGet: (B) -> A) = object : Iso<A, B>() {

            override fun get(a: A): B = get(a)

            override fun reverseGet(b: B): A = reverseGet(b)
        }
    }

    /** reverse a [ISO]: the source becomes the target and the target becomes the source */
    fun reverse(): Iso<B, A> = object : Iso<B, A>() {
        /** get the target of a [ISO] */
        override fun get(b: B): A = reverseGet(b)

        /** get the modified source of a [ISO] */
        override fun reverseGet(a: A): B = get(a)
    }

    /** modify polymorphically the target of a [ISO] with a function */
    inline fun modify(crossinline f: (B) -> B): (A) -> A = { reverseGet(f(get(it))) }

    /** modify polymorphically the target of a [ISO] with a Functor function */
    inline fun <reified F> modifyF(FF: Functor<F> = functor(), f: (B) -> HK<F, B>, a: A): HK<F, A> =
            FF.map(f(get(a)), this::reverseGet)

    /** set polymorphically the target of a [Iso] with a value */
    fun set(b: B): (A) -> (A) = { reverseGet(b) }

    /** compose a [[PIso]] with a [[PIso]] */
    infix fun <C> composeIso(other: Iso<B, C>): Iso<A, C> = Iso<A, C>(
            { a -> other.get(get(a)) },
            { c -> reverseGet(other.reverseGet(c)) }
    )

    /** view a [[PIso]] as a [[PPrism]] */
    fun asPrism(): Prism<A, B> = Prism(
            { a -> Either.Right(get(a)) },
            { b -> reverseGet(b) }
    )

    /** view a [[PIso]] as a [[PLens]] */
    fun asLens(): Lens<A, B> = Lens(
            { a -> get(a) },
            { b -> set(b) }
    )
}