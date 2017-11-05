package kategory.optics

import kategory.Applicative
import kategory.Either
import kategory.HK
import kategory.Monoid
import kategory.Option
import kategory.PartialFunction
import kategory.Tuple2
import kategory.applicative
import kategory.flatMap
import kategory.getOrElse
import kategory.identity
import kategory.left
import kategory.lift
import kategory.none
import kategory.right
import kategory.some
import kategory.toT

/**
 * [Optional] is a type alias for [POptional] which fixes the type arguments
 * and restricts the [POptional] to monomorphic updates.
 */
typealias Optional<S, A> = POptional<S, S, A, A>

/**
 * An [Optional] is an optic that allows to see into a structure and getting, setting or modifying an optional focus.
 *
 * A (polymorphic) [POptional] is useful when setting or modifying a value for a type with a optional polymorphic focus
 * i.e. POptional<Ior<Int, Double>, Ior<String, Double>, Int, String>
 *
 * A [POptional] can be seen as a weaker [Lens] and [Prism] and combines their weakest functions:
 * - `set: (S, B) -> T` meaning we can focus into an `S` and set a value `B` for a target `A` and obtain a modified source `T`
 * - `getOrModify: (S) -> Either<T, A>` meaning it returns the focus of a [POptional] OR the original value
 *
 * @param S the source of a [POptional]
 * @param T the modified source of a [POptional]
 * @param A the focus of a [POptional]
 * @param B the modified focus of a [POptional]
 */
interface POptional<S, T, A, B> {

    /**
     * Get the modified source of a [POptional]
     */
    fun set(s: S, b: B): T

    /**
     * Get the focus of a [POptional] or return the original value while allowing the type to change if it does not match
     */
    fun getOrModify(s: S): Either<T, A>

    companion object {

        fun <S> id() = Iso.id<S>().asOptional()

        /**
         * [POptional] that takes either [S] or [S] and strips the choice of [S].
         */
        fun <S> codiagonal(): Optional<Either<S, S>, S> = Optional(
                { it.fold({ it.right() }, { it.right() }) },
                { a -> { aa -> aa.bimap({ a }, { a }) } }
        )

        /**
         * Invoke operator overload to create a [POptional] of type `S` with focus `A`.
         * Can also be used to construct [Optional]
         */
        operator fun <S, T, A, B> invoke(getOrModify: (S) -> Either<T, A>, set: (B) -> (S) -> T): POptional<S, T, A, B> = object : POptional<S, T, A, B> {
            override fun getOrModify(s: S): Either<T, A> = getOrModify(s)

            override fun set(s: S, b: B): T = set(b)(s)
        }

        /**
         * Invoke operator overload to create a [POptional] of type `S` with focus `A`.
         * Can also be used to construct [Optional]
         */
        operator fun <S, A> invoke(partialFunction: PartialFunction<S, A>, set: (A) -> (S) -> S): Optional<S, A> = Optional(
                getOrModify = { s -> partialFunction.lift()(s).fold({ s.left() }, { it.right() }) },
                set = set
        )

        /**
         * [POptional] that never sees its focus
         */
        fun <A, B> void(): Optional<A, B> = Optional(
                { it.left() },
                { _ -> ::identity }
        )

    }

    /**
     * Modify the focus of a [POptional] with an Applicative function [f]
     */
    fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> HK<F, B>): HK<F, T> = getOrModify(s).fold(
            FA::pure,
            { FA.map(f(it), { set(s, it) }) }
    )

    /**
     * Lift a function [f]: `(A) -> HK<F, B> to the context of `S`: `(S) -> HK<F, T>`
     */
    fun <F> liftF(FA: Applicative<F>, f: (A) -> HK<F, B>): (S) -> HK<F, T> = { s ->
        modifyF(FA, s, f)
    }

    /**
     * Get the focus of a [POptional] or [Option.None] if the is not there
     */
    fun getOption(a: S): Option<A> = getOrModify(a).toOption()

    /**
     * Set the focus of a [POptional] with a value.
     * @return [Option.None] if the [POptional] is not matching
     */
    fun setOption(s: S, b: B): Option<T> = modifiyOption(s) { b }

    /**
     * Check if there is no focus
     */
    fun isEmpty(s: S): Boolean = !nonEmpty(s)

    /**
     * Check if there is a focus
     */
    fun nonEmpty(s: S): Boolean = getOption(s).fold({ false }, { true })

    /**
     * Join two [POptional] with the same focus [B]
     */
    infix fun <S1, T1> choice(other: POptional<S1, T1, A, B>): POptional<Either<S, S1>, Either<T, T1>, A, B> =
            POptional(
                    { ss -> ss.fold({ getOrModify(it).bimap({ it.left() }, ::identity) }, { other.getOrModify(it).bimap({ it.right() }, ::identity) }) },
                    { b -> { it.bimap({ s -> this.set(s, b) }, { s -> other.set(s, b) }) } }
            )

    /**
     * Create a product of the [POptional] and a type [C]
     */
    fun <C> first(): POptional<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> =
            POptional(
                    { (s, c) -> getOrModify(s).bimap({ it toT c }, { it toT c }) },
                    { (b, c) -> { (s, c2) -> setOption(s, b).fold({ set(s, b) toT c2 }, { it toT c }) } }
            )

    /**
     * Create a product of a type [C] and the [POptional]
     */
    fun <C> second(): POptional<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> =
            POptional(
                    { (c, s) -> getOrModify(s).bimap({ c toT it }, { c toT it }) },
                    { (c, b) -> { (c2, s) -> setOption(s, b).fold({ c2 toT set(s, b) }, { c toT it }) } }
            )

    /**
     * Compose a [POptional] with a [POptional]
     */
    infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = POptional(
            { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(s, it) }, ::identity) } },
            { d -> { s -> modify(s) { a -> other.set(a, d) } } }
    )

    /**
     * Compose a [POptional] with a [PPrism]
     */
    infix fun <C, D> compose(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = compose(other.asOptional())

    /**
     * Compose a [POptional] with a [PLens]
     */
    infix fun <C, D> compose(other: PLens<A, B, C, D>): POptional<S, T, C, D> = compose(other.asOptional())

    /**
     * Compose a [POptional] with a [PIso]
     */
    infix fun <C, D> compose(other: PIso<A, B, C, D>): POptional<S, T, C, D> = compose(other.asOptional())

    /**
     * Compose a [POptional] with a [PIso]
     */
    infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

    /**
     * Compose a [POptional] with a [Fold]
     */
    infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

    /**
     * Compose a [POptional] with a [Fold]
     */
    infix fun <C> compose(other: Getter<A, C>): Fold<S, C> = asFold() compose other

    /**
     * Compose a [POptional] with a [PTraversal]
     */
    infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = asTraversal() compose other

    /**
     * Plus operator overload to compose optionals
     */
    operator fun <C, D> plus(o: POptional<A, B, C, D>): POptional<S, T, C, D> = compose(o)

    operator fun <C, D> plus(o: PPrism<A, B, C, D>): POptional<S, T, C, D> = compose(o)

    operator fun <C, D> plus(o: PLens<A, B, C, D>): POptional<S, T, C, D> = compose(o)

    operator fun <C, D> plus(o: PIso<A, B, C, D>): POptional<S, T, C, D> = compose(o)

    operator fun <C, D> plus(o: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(o)

    operator fun <C> plus(o: Fold<A, C>): Fold<S, C> = compose(o)

    operator fun <C> plus(o: Getter<A, C>): Fold<S, C> = compose(o)

    operator fun <C, D> plus(o: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(o)

    /**
     * View a [POptional] as a [PSetter]
     */
    fun asSetter(): PSetter<S, T, A, B> = PSetter { f -> { s -> modify(s, f) } }

    /**
     * View a [POptional] as a [Fold]
     */
    fun asFold() = object : Fold<S, A> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R = getOption(s).map(f).getOrElse(M::empty)
    }

    /**
     * View a [POptional] as a [PTraversal]
     */
    fun asTraversal(): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
        override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> HK<F, B>): HK<F, T> = getOrModify(s).fold(
                FA::pure,
                { FA.map(f(it), { b -> set(s, b) }) }
        )
    }

}

/**
 * Modify the focus of a [POptional] with a function [f]
 */
inline fun <S, T, A, B> POptional<S, T, A, B>.modify(s: S, crossinline f: (A) -> B): T = getOrModify(s).fold(::identity, { a -> set(s, f(a)) })

/**
 * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T`
 */
inline fun <S, T, A, B> POptional<S, T, A, B>.lift(crossinline f: (A) -> B): (S) -> T = { s -> modify(s, f) }

/**
 * Modify the focus of a [POptional] with an [Applicative] function [f]
 */
inline fun <S, T, A, B, reified F> POptional<S, T, A, B>.modifyF(s: S, crossinline f: (A) -> HK<F, B>, FA: Applicative<F> = applicative()): HK<F, T> =
        modifyF(FA, s) { a -> f(a) }

/**
 * Lift a function [f]: `(A) -> HK<F, B> to the context of `S`: `(S) -> HK<F, T>` with an [Applicative] function [f]
 */
inline fun <S, T, A, B, reified F> POptional<S, T, A, B>.liftF(crossinline f: (A) -> HK<F, B>, FA: Applicative<F> = applicative()): (S) -> HK<F, T> = liftF(FA) { a -> f(a) }

/**
 * Modify the focus of a [POptional] with a function [f]
 * @return [Option.None] if the [POptional] is not matching
 */
inline fun <S, T, A, B> POptional<S, T, A, B>.modifiyOption(s: S, crossinline f: (A) -> B): Option<T> = getOption(s).map({ set(s, f(it)) })

/**
 * Find the focus that satisfies the predicate [p]
 */
inline fun <S, T, A, B> POptional<S, T, A, B>.find(s: S, crossinline p: (A) -> Boolean): Option<A> =
        getOption(s).flatMap { b -> if (p(b)) b.some() else none() }

/**
 * Check if there is a focus and it satisfies the predicate [p]
 */
inline fun <S, T, A, B> POptional<S, T, A, B>.exists(s: S, crossinline p: (A) -> Boolean): Boolean = getOption(s).fold({ false }, p)

/**
 * Check if there is no focus or the target satisfies the predicate [p]
 */
inline fun <S, T, A, B> POptional<S, T, A, B>.all(s: S, crossinline p: (A) -> Boolean): Boolean = getOption(s).fold({ true }, p)
