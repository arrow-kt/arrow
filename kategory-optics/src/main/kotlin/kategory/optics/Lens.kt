package kategory.optics

import kategory.Either
import kategory.Functor
import kategory.HK
import kategory.Option
import kategory.Tuple2
import kategory.functor
import kategory.identity
import kategory.toT

/**
 * A [Lens] can be seen as a pair of functions `get: (A) -> B` and `set: (B) -> (A) -> A`
 * - `get: (A) -> B` i.e. from an `A`, we can extract an `B`
 * - `set: (B) -> (A) -> A` i.e. if we replace target value by `B` in an `A`, we obtain another modified `A`
 *
 * @param A the source of a [Lens]
 * @param B the target of a [Lens]
 * @property get from an `A` we can extract a `B`
 * @property set replace the target value by `B` in an `A` so we obtain another modified `A`
 * @constructor Creates a Lens of type `A` with target `B`.
 */
abstract class Lens<A, B> {

    abstract fun get(a: A): B
    abstract fun set(b: B): (A) -> A

    companion object {

        fun <A> id() = Iso.id<A>().asLens()

        fun <A> codiagonal() = Lens<Either<A, A>, A>(
                get = { it.fold(::identity, ::identity) },
                set = { a -> { it.bimap({ a }, { a }) } }
        )

        operator fun <A, B> invoke(get: (A) -> B, set: (B) -> (A) -> A) = object : Lens<A, B>() {
            override fun get(a: A): B = get(a)

            override fun set(b: B): (A) -> A = set(b)
        }
    }

    /**
     * Modify the target of a [Lens] using a function `(B) -> B`
     */
    inline fun modify(f: (B) -> B, a: A): A = set(f(get(a)))(a)

    /**
     * Modify the target of a [Lens] using Functor function
     */
    inline fun <reified F> modifyF(FF: Functor<F> = functor(), f: (B) -> HK<F, B>, a: A): HK<F, A> =
            FF.map(f(get(a)), { set(it)(a) })

    /**
     * Find if the target satisfies the predicate
     */
    inline fun find(crossinline p: (B) -> Boolean): (A) -> Option<B> = {
        val a = get(it)
        if (p(a)) Option.Some(a) else Option.None
    }

    /**
     * Checks if the target of a [Lens] satisfies the predicate
     */
    inline fun exist(crossinline p: (B) -> Boolean): (A) -> Boolean = { p(get(it)) }

    /**
     * Join two [Lens] with the same target
     */
    fun <C> choice(other: Lens<C, B>): Lens<Either<A, C>, B> = Lens(
            { it.fold(this::get, other::get) },
            { b -> { it.bimap(set(b), other.set(b)) } }
    )

    /**
     * Pair two disjoint [Lens]
     */
    fun <C, D> split(other: Lens<C, D>): Lens<Tuple2<A, C>, Tuple2<B, D>> = Lens(
            { (a, c) -> get(a) toT other.get(c) },
            { (b, d) -> { (a, c) -> set(b)(a) toT other.set(d)(c) } }
    )

    /**
     * Convenience method to create a pair of the target and a type C
     */
    fun <C> first(): Lens<Tuple2<A, C>, Tuple2<B, C>> = Lens(
            { (a, c) -> get(a) toT c },
            { (b, c) -> { (a, _) -> set(b)(a) toT c } }
    )

    /**
     * Convenience method to create a pair of a type C and the target
     */
    fun <C> second(): Lens<Tuple2<C, A>, Tuple2<C, B>> = Lens(
            { (c, a) -> c toT get(a) },
            { (c, b) -> { (_, a) -> c toT set(b)(a) } }
    )

    /**
     * Compose a [Lens] with another [Lens]
     */
    infix fun <C> composeLens(l: Lens<B, C>): Lens<A, C> = Lens(
            { a -> l.get(get(a)) },
            { c -> { a -> set(l.set(c)(get(a)))(a) } }
    )

    /** compose an [Iso] as an [Prism] */
    fun <C> composeIso(other: Iso<B, C>): Lens<A, C> =
            composeLens(other.asLens())

    /**
     * plus operator overload to compose lenses
     */
    operator fun <C> plus(other: Lens<B, C>): Lens<A, C> = composeLens(other)

}
