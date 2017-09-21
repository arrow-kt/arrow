package kategory.optics

import kategory.Applicative
import kategory.Const
import kategory.ConstHK
import kategory.ConstKindPartial
import kategory.Either
import kategory.HK
import kategory.Id
import kategory.IntMonoid
import kategory.ListKW
import kategory.Monoid
import kategory.Option
import kategory.applicative
import kategory.identity
import kategory.left
import kategory.map
import kategory.monoid
import kategory.none
import kategory.right
import kategory.some
import kategory.traverse
import kategory.value

/**
 * [Traversal] is a type alias for [PTraversal] which fixes the type arguments
 * and restricts the [PTraversal] to monomorphic updates.
 */
typealias Traversal<S, A> = PTraversal<S, S, A, A>

/**
 * A [Traversal] is an optic that allows to see into a structure with 0 to N targets.
 *
 * [Traversal] is a generalisation of [kategory.Traverse] and can be seen as a representation of modifyF.
 * all methods are written in terms of modifyF
 *
 * @param S the source of a [PTraversal]
 * @param T the modified source of a [PTraversal]
 * @param A the target of a [PTraversal]
 * @param B the modified target of a [PTraversal]
 */
abstract class PTraversal<S, T, A, B> {

    abstract fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> HK<F, B>): HK<F, T>

    /**
     * Modify polymorphically the target of a [PTraversal] with an Applicative function
     */
    inline fun <reified F> modifyF(s: S, crossinline f: (A) -> HK<F, B>): HK<F, T> = modifyF(applicative(), s, { b -> f(b) })

    companion object {
        fun <S> id() = Iso.id<S>().asTraversal()

        fun <S> codiagonal(): Traversal<Either<S, S>, S> = object : Traversal<Either<S, S>, S>() {
            override fun <F> modifyF(FA: Applicative<F>, s: Either<S, S>, f: (S) -> HK<F, S>): HK<F, Either<S, S>> =
                    s.bimap(f, f).fold({ fa -> FA.map(fa, { a -> a.left() }) }, { fa -> FA.map(fa, { a -> a.right() }) })
        }

        inline fun <reified T, S> fromTraversable(TT: kategory.Traverse<T> = traverse()) = object : Traversal<HK<T, S>, S>() {
            override fun <F> modifyF(FA: Applicative<F>, a: HK<T, S>, f: (S) -> HK<F, S>): HK<F, HK<T, S>> = TT.traverse(a, f, FA)
        }

        /**
         * [PTraversal] that points to nothing
         */
        fun <S, A> void() = Optional.void<S, A>().asTraversal()

        /**
         * Composes N lenses horizontally.  Note that although it is possible to pass two or more lenses
         * that point to the same `A`, in practice it considered an unsafe usage (see https://github.com/julien-truffaut/Monocle/issues/379#issuecomment-236374838).
         */
        operator fun <S, A> invoke(vararg lenses: Lens<S, A>): Traversal<S, A> = object : Traversal<S, A>() {
            override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> HK<F, A>): HK<F, S> = lenses.fold(FA.pure(s), { fs, lens ->
                FA.map(f(lens.get(s)), fs, { (b, a) ->
                    lens.set(b)(a)
                })
            })
        }

        operator fun <S, A> invoke(get1: (S) -> A, get2: (S) -> A, set: (A, A, S) -> S): Traversal<S, A> = object : Traversal<S, A>() {
            override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> HK<F, A>): HK<F, S> =
                    FA.map2(f(get1(s)), f(get2(s)), { (b1, b2) -> set(b1, b2, s) })
        }

    }

    /**
     * Map each target to a Monoid and combine the results
     */
    @Suppress("UNUSED_PARAMETER")
    inline fun <reified R> foldMap(FA: Applicative<ConstKindPartial<R>> = applicative(), M: Monoid<R> = monoid(), s: S, crossinline f: (A) -> R): R =
            modifyF(FA, s, { b ->
                Const(f(b))
            }).value()

    /**
     * Modify polymorphically the target of a [PTraversal] with a function [f]
     */
    inline fun modify(s: S, crossinline f: (A) -> B): T = modifyF(Id.applicative(), s, { b -> Id(f(b)) }).value()

    /**
     * Set polymorphically the target of a [PTraversal] with a value
     */
    fun set(s: S, b: B): T = modify(s) { b }

    /**
     * Calculate the number of targets in the [PTraversal]
     */
    fun size(s: S): Int = foldMap(Const.applicative(), IntMonoid, s, { 1 })

    /**
     * Compose a [PTraversal] with a [PTraversal]
     */
    infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = object : PTraversal<S, T, C, D>() {
        override fun <F> modifyF(FA: Applicative<F>, s: S, f: (C) -> HK<F, D>): HK<F, T> =
                this@PTraversal.modifyF(FA, s, { b -> other.modifyF(FA, b, f) })
    }

    /**
     * Plus operator overload to compose traversal
     */
    operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

}

/**
 * Find the first target matching the predicate
 */
inline fun <S, T, A, B> PTraversal<S, T, A, B>.find(s: S, crossinline p: (A) -> Boolean): Option<A> =
        foldMap(Const.applicative(), firstOptionMonoid<A>(), s, { a ->
            if (p(a)) Const(a.some())
            else Const(none())
        }).value

/**
 * Fold using the given [Monoid] instance.
 */
inline fun <S, T, reified A, B> PTraversal<S, T, A, B>.fold(M: Monoid<A> = monoid(), s: S): A =
        foldMap(Const.applicative(), M, s, ::identity)

/**
 * Alias for fold.
 */
inline fun <S, T, reified A, reified B> PTraversal<S, T, A, B>.combineAll(M: Monoid<A> = monoid(), s: S): A = fold(M, s)

/**
 * Get all targets of the [PTraversal]
 */
inline fun <S, T, reified A, reified B> PTraversal<S, T, A, B>.getAll(s: S): ListKW<A> =
        foldMap(Const.applicative(), ListKW.monoid(), s, { ListKW(listOf(it)) })

internal sealed class First

@PublishedApi internal fun <A> firstOptionMonoid() = object : Monoid<Const<Option<A>, First>> {

    override fun empty() = Const<Option<A>, First>(Option.None)

    override fun combine(a: Const<Option<A>, First>, b: Const<Option<A>, First>) =
            if (a.value.fold({ false }, { true })) a else b

}
