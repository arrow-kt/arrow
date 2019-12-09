package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.compose
import arrow.core.identity
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monoid

/**
 * [Iso] is a type alias for [PIso] which fixes the type arguments
 * and restricts the [PIso] to monomorphic updates.
 */
typealias Iso<S, A> = PIso<S, S, A, A>

typealias ForIso = ForPIso
typealias IsoOf<S, A> = PIsoOf<S, S, A, A>
typealias IsoPartialOf<S> = Kind<ForIso, S>
typealias IsoKindedJ<S, A> = PIsoKindedJ<S, S, A, A>

/**
 * An [Iso] is a loss less invertible optic that defines an isomorphism between a type [S] and [A]
 * i.e. a data class and its properties represented by TupleN
 *
 * A (polymorphic) [PIso] is useful when setting or modifying a value for a constructed type
 * i.e. PIso<Option<Int>, Option<String>, Int?, String?>
 *
 * An [PIso] is also a valid [PLens], [PPrism]
 *
 * @param S the source of a [PIso]
 * @param T the modified source of a [PIso]
 * @param A the focus of a [PIso]
 * @param B the modified target of a [PIso]
 */
@higherkind
interface PIso<S, T, A, B> : PIsoOf<S, T, A, B> {

  /**
   * Get the focus of a [PIso]
   */
  fun get(s: S): A

  /**
   * Get the modified focus of a [PIso]
   */
  fun reverseGet(b: B): T

  companion object {

    /**
     * create an [PIso] between any type and itself.
     * Id is the zero element of optics composition, for any optic o of type O (e.g. PLens, Prism, POptional, ...):
     * o compose Iso.id == o
     */
    fun <S> id(): Iso<S, S> = Iso(::identity, ::identity)

    /**
     * Invoke operator overload to create a [PIso] of type `S` with target `A`.
     * Can also be used to construct [Iso]
     */
    operator fun <S, T, A, B> invoke(get: (S) -> (A), reverseGet: (B) -> T) = object : PIso<S, T, A, B> {

      override fun get(s: S): A = get(s)

      override fun reverseGet(b: B): T = reverseGet(b)
    }
  }

  /**
   * Lift a [PIso] to a Functor level
   */
  fun <F> mapping(FF: Functor<F>): PIso<Kind<F, S>, Kind<F, T>, Kind<F, A>, Kind<F, B>> = FF.run {
    PIso(
      { fa -> fa.map(::get) },
      { fb -> fb.map(::reverseGet) }
    )
  }

  /**
   * Modify polymorphically the target of a [PIso] with a Functor function
   */
  fun <F> modifyF(FF: Functor<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> = FF.run {
    f(get(s)).map(::reverseGet)
  }

  /**
   * Lift a function [f] with a functor: `(A) -> Kind<F, B> to the context of `S`: `(S) -> Kind<F, T>`
   */
  fun <F> liftF(FF: Functor<F>, f: (A) -> Kind<F, B>): (S) -> Kind<F, T> = FF.run {
    { s -> f(get(s)).map(::reverseGet) }
  }

  /**
   * Reverse a [PIso]: the source becomes the target and the target becomes the source
   */
  fun reverse(): PIso<B, A, T, S> = PIso(this::reverseGet, this::get)

  /**
   * Find if the focus satisfies the predicate
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> = get(s).let { aa ->
    if (p(aa)) Some(aa) else None
  }

  /**
   * Set polymorphically the focus of a [PIso] with a value
   */
  fun set(b: B): T = reverseGet(b)

  /**
   * Pair two disjoint [PIso]
   */
  infix fun <S1, T1, A1, B1> split(other: PIso<S1, T1, A1, B1>): PIso<Tuple2<S, S1>, Tuple2<T, T1>, Tuple2<A, A1>, Tuple2<B, B1>> = PIso(
    { (a, c) -> get(a) toT other.get(c) },
    { (b, d) -> reverseGet(b) toT other.reverseGet(d) }
  )

  /**
   * Create a pair of the [PIso] and a type [C]
   */
  fun <C> first(): PIso<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = Iso(
    { (a, c) -> get(a) toT c },
    { (b, c) -> reverseGet(b) toT c }
  )

  /**
   * Create a pair of a type [C] and the [PIso]
   */
  fun <C> second(): PIso<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> = PIso(
    { (c, a) -> c toT get(a) },
    { (c, b) -> c toT reverseGet(b) }
  )

  /**
   * Create a sum of the [PIso] and a type [C]
   */
  fun <C> left(): PIso<Either<S, C>, Either<T, C>, Either<A, C>, Either<B, C>> = PIso(
    { it.bimap(this::get, ::identity) },
    { it.bimap(this::reverseGet, ::identity) }
  )

  /**
   * Create a sum of a type [C] and the [PIso]
   */
  fun <C> right(): PIso<Either<C, S>, Either<C, T>, Either<C, A>, Either<C, B>> = PIso(
    { it.bimap(::identity, this::get) },
    { it.bimap(::identity, this::reverseGet) }
  )

  /**
   * Compose a [PIso] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PIso<S, T, C, D> = PIso(
    other::get compose this::get,
    this::reverseGet compose other::reverseGet
  )

  /**
   * Compose a [PIso] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): PLens<S, T, C, D> = asLens() compose other

  /**
   * Compose a [PIso] with a [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = asPrism() compose other

  /**
   * Compose a [PIso] with a [Getter]
   */
  infix fun <C> compose(other: Getter<A, C>): Getter<S, C> = asGetter() compose other

  /**
   * Compose a [PIso] with a [PSetter]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose a [PIso] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

  /**
   * Compose a [PIso] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Compose a [PIso] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = asTraversal() compose other

  /**
   * Plus operator overload to compose lenses
   */
  operator fun <C, D> plus(other: PIso<A, B, C, D>): PIso<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): PLens<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Getter<A, C>): Getter<S, C> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  /**
   * View a [PIso] as a [PPrism]
   */
  fun asPrism(): PPrism<S, T, A, B> = PPrism(
    { a -> Either.Right(get(a)) },
    this::reverseGet
  )

  /**
   * View a [PIso] as a [PLens]
   */
  fun asLens(): PLens<S, T, A, B> = PLens(this::get) { _, b -> set(b) }

  /**
   * View a [PIso] as a [Getter]
   */
  fun asGetter(): Getter<S, A> = Getter(this::get)

  /**
   * View a [PIso] as a [POptional]
   */
  fun asOptional(): POptional<S, T, A, B> = POptional(
    { s -> Either.Right(get(s)) },
    { _, b -> set(b) }
  )

  /**
   * View a [PIso] as a [PSetter]
   */
  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

  /**
   * View a [PIso] as a [Fold]
   */
  fun asFold(): Fold<S, A> = object : Fold<S, A> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R = f(get(s))
  }

  /**
   * View a [PIso] as a [PTraversal]
   */
  fun asTraversal(): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
    override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> = FA.run {
      f(get(s)).map(this@PIso::reverseGet)
    }
  }

  /**
   * Check if the focus satisfies the predicate
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = p(get(s))

  /**
   * Modify polymorphically the focus of a [PIso] with a function
   */
  fun modify(s: S, f: (A) -> B): T = reverseGet(f(get(s)))

  /**
   * Modify polymorphically the focus of a [PIso] with a function
   */
  fun lift(f: (A) -> B): (S) -> T = { s -> reverseGet(f(get(s))) }

  /**
   * Lift a function [f] with a functor: `(A) -> Kind<F, B> to the context of `S`: `(S) -> Kind<F, T>`
   */
  fun <F> liftF(FF: Functor<F>, dummy: Unit = Unit, f: (A) -> Kind<F, B>): (S) -> Kind<F, T> =
    liftF(FF) { a -> f(a) }
}
