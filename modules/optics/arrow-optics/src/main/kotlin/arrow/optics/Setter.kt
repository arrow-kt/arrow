package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.data.State
import arrow.higherkind
import arrow.typeclasses.Functor

/**
 * [Setter] is a type alias for [PSetter] which fixes the type arguments
 * and restricts the [PSetter] to monomorphic updates.
 */
typealias Setter<S, A> = PSetter<S, S, A, A>

typealias ForSetter = ForPSetter
typealias SetterOf<S, A> = PSetterOf<S, S, A, A>
typealias SetterPartialOf<S> = Kind<ForSetter, S>
typealias SetterKindedJ<S, A> = PSetterKindedJ<S, S, A, A>

/**
 * A [Setter] is an optic that allows to see into a structure and set or modify its focus.
 *
 * A (polymorphic) [PSetter] is useful when setting or modifying a value for a constructed type
 * i.e. PSetter<List<Int>, List<String>, Int, String>
 *
 * A [PSetter] is a generalisation of a [arrow.Functor].
 * Functor::map   (fa: Kind<F, A>, f: (A) -> B): Kind<F, B>
 * PSetter::modify(s: S,         f: (A) -> B): T
 *
 * @param S the source of a [PSetter]
 * @param T the modified source of a [PSetter]
 * @param A the focus of a [PSetter]
 * @param B the modified focus of a [PSetter]
 */
@higherkind
interface PSetter<S, T, A, B> : PSetterOf<S, T, A, B> {

  /**
   * Modify polymorphically the focus of a [PSetter] with a function [f].
   */
  fun modify(s: S, f: (A) -> B): T

  /**
   * Set polymorphically the focus of a [PSetter] with a value [b].
   */
  fun set(s: S, b: B): T

  companion object {

    fun <S> id() = Iso.id<S>().asSetter()

    /**
     * [PSetter] that takes either S or S and strips the choice of S.
     */
    fun <S> codiagonal(): Setter<Either<S, S>, S> = Setter { aa, f -> aa.bimap(f, f) }

    /**
     * Invoke operator overload to create a [PSetter] of type `S` with target `A`.
     * Can also be used to construct [Setter]
     */
    operator fun <S, T, A, B> invoke(modify: (S, ((A) -> B)) -> T): PSetter<S, T, A, B> = object : PSetter<S, T, A, B> {
      override fun modify(s: S, f: (A) -> B): T = modify(s, f)

      override fun set(s: S, b: B): T = modify(s) { b }
    }

    /**
     * Create a [PSetter] from a [arrow.Functor]
     */
    fun <F, A, B> fromFunctor(FF: Functor<F>): PSetter<Kind<F, A>, Kind<F, B>, A, B> = FF.run {
      PSetter { fs: Kind<F, A>, f -> fs.map(f) }
    }
  }

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  fun lift(f: (A) -> B): (S) -> T = { s -> modify(s) { f(it) } }

  /**
   * Join two [PSetter] with the same target
   */
  infix fun <U, V> choice(other: PSetter<U, V, A, B>): PSetter<Either<S, U>, Either<T, V>, A, B> = PSetter { su, f ->
    su.bimap({ s -> modify(s, f) }, { u -> other.modify(u, f) })
  }

  /**
   * Compose a [PSetter] with a [PSetter]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = PSetter { s, fb ->
    modify(s) { a -> other.modify(a, fb) }
  }

  /**
   * Compose a [PSetter] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): PSetter<S, T, C, D> = compose(other.asSetter())

  /**
   * Compose a [PSetter] with a [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): PSetter<S, T, C, D> = compose(other.asSetter())

  /**
   * Compose a [PSetter] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): PSetter<S, T, C, D> = compose(other.asSetter())

  /**
   * Compose a [PSetter] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PSetter<S, T, C, D> = compose(other.asSetter())

  /**
   * Compose a [PSetter] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PSetter<S, T, C, D> = compose(other.asSetter())

  /**
   * Plus operator overload to compose optionals
   */
  operator fun <C, D> plus(o: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(o)

  operator fun <C, D> plus(o: POptional<A, B, C, D>): PSetter<S, T, C, D> = compose(o)

  operator fun <C, D> plus(o: PPrism<A, B, C, D>): PSetter<S, T, C, D> = compose(o)

  operator fun <C, D> plus(o: PLens<A, B, C, D>): PSetter<S, T, C, D> = compose(o)

  operator fun <C, D> plus(o: PIso<A, B, C, D>): PSetter<S, T, C, D> = compose(o)

  operator fun <C, D> plus(o: PTraversal<A, B, C, D>): PSetter<S, T, C, D> = compose(o)

}

/**
 * Update the focus [A] referenced through the [Setter].
 */
fun <S, A> Setter<S, A>.update_(f: (A) -> A): State<S, Unit> =
  State { s -> Tuple2(modify(s, f), Unit) }

/**
 * Set the focus [A] referenced through the [Setter].
 */
fun <S, A> Setter<S, A>.assign_(a: A): State<S, Unit> =
  update_ { _ -> a }
