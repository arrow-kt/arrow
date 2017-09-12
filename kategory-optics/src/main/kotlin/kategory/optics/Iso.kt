package kategory.optics

import kategory.Either
import kategory.Functor
import kategory.HK
import kategory.Option
import kategory.Tuple2
import kategory.compose
import kategory.functor
import kategory.identity
import kategory.none
import kategory.some
import kategory.toT

/**
 * An [Iso] defines an isomorphism between a type S and A.
 *
 * An [Iso] is also a valid [Lens], [Prism]
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
    fun reverse(): Iso<B, A> = Iso(this::reverseGet, this::get)

    /**
     * Lift a [Iso] to a Functor level
     */
    inline fun <reified F> mapping(FF: Functor<F> = functor()): Iso<HK<F, A>, HK<F, B>> = Iso(
            { fa -> FF.map(fa, this::get) },
            { fb -> FF.map(fb, this::reverseGet) }
    )

    /**
     * Find if the target satisfies the predicate
     */
    fun find(p: (B) -> Boolean): (A) -> Option<B> = { a ->
        get(a).let { b ->
            if (p(b)) b.some() else none()
        }
    }

    /**
     * check if the target satisfies the predicate
     */
    fun exist(p: (B) -> Boolean): (A) -> Boolean = p compose this::get

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
     * Pair two disjoint [Iso]
     */
    infix fun <C, D> split(other: Iso<C, D>): Iso<Tuple2<A, C>, Tuple2<B, D>> = Iso(
            { (a, c) -> get(a) toT other.get(c) },
            { (b, d) -> reverseGet(b) toT other.reverseGet(d) }
    )

    /**
     * Create a pair of the target and a type C
     */
    fun <C> first(): Iso<Tuple2<A, C>, Tuple2<B, C>> = Iso(
            { (a, c) -> get(a) toT c },
            { (b, c) -> reverseGet(b) toT c }
    )

    /**
     * Create a pair of a type C and the target
     */
    fun <C> second(): Iso<Tuple2<C, A>, Tuple2<C, B>> = Iso(
            { (c, a) -> c toT get(a) },
            { (c, b) -> c toT reverseGet(b) }
    )

    /**
     * Create a sum of the target and a type C
     */
    fun <C> left(): Iso<Either<A, C>, Either<B, C>> = Iso(
            { it.bimap(this::get, ::identity) },
            { it.bimap(this::reverseGet, ::identity) }
    )

    /**
     * Create a sum of a type C and the target
     */
    fun <C> right(): Iso<Either<C, A>, Either<C, B>> = Iso(
            { it.bimap(::identity, this::get) },
            { it.bimap(::identity, this::reverseGet) }
    )

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