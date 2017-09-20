package kategory.optics

import kategory.Either
import kategory.Option
import kategory.Tuple2
import kategory.compose
import kategory.identity
import kategory.none
import kategory.some
import kategory.toT

/**
 * A [Getter] can be seen as a glorified get method between a type A and a type B.
 *
 * @param A the source of a [Getter]
 * @param B the target of a [Getter]
 */
abstract class Getter<A, B> {
    /**
     * Get the target of a [Getter]
     */
    abstract fun get(a: A): B

    companion object {

        fun <A> id() = Iso.id<A>().asGetter()

        fun <A> codiagonal(): Getter<Either<A, A>, A> = Getter { aa -> aa.fold(::identity, ::identity) }

        operator fun <A, B> invoke(get: (A) -> B) = object : Getter<A, B>() {
            override fun get(a: A): B = get(a)
        }
    }

    /**
     * Find if the target satisfies the predicate.
     */
    inline fun find(crossinline p: (B) -> Boolean): (A) -> Option<B> = { a ->
        get(a).let { b ->
            if (p(b)) b.some() else none()
        }
    }

    /**
     * Check if the target satisfies the predicate
     */
    fun exist(p: (B) -> Boolean): (A) -> Boolean = p compose this::get

    /**
     * join two [Getter] with the same target
     */
    fun <C> choice(other: Getter<C, B>): Getter<Either<A, C>, B> = Getter { a ->
        a.fold(this::get, other::get)
    }

    /**
     * Pair two disjoint [Getter]
     */
    fun <C, D> split(other: Getter<C, D>): Getter<Tuple2<A, C>, Tuple2<B, D>> = Getter { (a, c) ->
        get(a) toT other.get(c)
    }

    fun <C> zip(other: Getter<A, C>): Getter<A, Tuple2<B, C>> = Getter { a ->
        get(a) toT other.get(a)
    }

    fun <C> first(): Getter<Tuple2<A, C>, Tuple2<B, C>> = Getter { (a, c) ->
        get(a) toT c
    }

    fun <C> second(): Getter<Tuple2<C, A>, Tuple2<C, B>> = Getter { (c, a) ->
        c toT get(a)
    }

    fun <C> left(): Getter<Either<A, C>, Either<B, C>> = Getter { ac ->
        ac.bimap(this::get, ::identity)
    }

    fun <C> right(): Getter<Either<C, A>, Either<C, B>> = Getter { ca ->
        ca.map(this::get)
    }

    /**
     * Compose a [Getter] with a [Getter]
     */
    infix fun <C> composeGetter(other: Getter<B, C>): Getter<A, C> = Getter(other::get compose this::get)

    /**
     * Compose a [Getter] with a [Lens]
     */
    infix fun <C> composeLens(other: Lens<B,C>): Getter<A,C> = Getter(other::get compose this::get)

    /**
     * Compose a [Getter] with a [Iso]
     */
    infix fun <C> composeIso(other: Iso<B,C>): Getter<A,C> = Getter(other::get compose this::get)

    /**
     * Plus operator overload to compose optionals
     */
    operator fun <C> plus(other: Getter<B, C>): Getter<A, C> = composeGetter(other)

    operator fun <C> plus(other: Lens<B,C>): Getter<A, C> = composeLens(other)

    operator fun <C> plus(other: Iso<B,C>): Getter<A, C> = composeIso(other)

}