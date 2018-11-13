package arrow.optics

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monoid

/**
 * [Lens] is a type alias for [PLens] which fixes the type arguments
 * and restricts the [PLens] to monomorphic updates.
 */
typealias Lens<S, A> = PLens<S, S, A, A>

typealias ForLens = ForPLens
typealias LensOf<S, A> = PLensOf<S, S, A, A>
typealias LensPartialOf<S> = Kind<ForLens, S>
typealias LensKindedJ<S, A> = PLensKindedJ<S, S, A, A>

/**
 * A [Lens] (or Functional Reference) is an optic that can focus into a structure for
 * getting, setting or modifying the focus (target).
 *
 * A (polymorphic) [PLens] is useful when setting or modifying a value for a constructed type
 * i.e. PLens<Tuple2<Double, Int>, Tuple2<String, Int>, Double, String>
 *
 * A [PLens] can be seen as a pair of functions:
 * - `get: (S) -> A` meaning we can focus into an `S` and extract an `A`
 * - `set: (B) -> (S) -> T` meaning we can focus into an `S` and set a value `B` for a target `A` and obtain a modified source `T`
 *
 * @param S the source of a [PLens]
 * @param T the modified source of a [PLens]
 * @param A the focus of a [PLens]
 * @param B the modified focus of a [PLens]
 */
@higherkind
interface PLens<S, T, A, B> : PLensOf<S, T, A, B> {

  fun get(s: S): A
  fun set(s: S, b: B): T

  companion object {

    fun <S> id() = Iso.id<S>().asLens()

    /**
     * [PLens] that takes either [S] or [S] and strips the choice of [S].
     */
    fun <S> codiagonal(): Lens<Either<S, S>, S> = Lens(
      get = { it.fold(::identity, ::identity) },
      set = { s, b -> s.bimap({ b }, { b }) }
    )

    /**
     * Invoke operator overload to create a [PLens] of type `S` with target `A`.
     * Can also be used to construct [Lens]
     */
    operator fun <S, T, A, B> invoke(get: (S) -> A, set: (S, B) -> T) = object : PLens<S, T, A, B> {
      override fun get(s: S): A = get(s)

      override fun set(s: S, b: B): T = set(s, b)
    }
  }

  /**
   * Modify the focus of a [PLens] using Functor function
   */
  fun <F> modifyF(FF: Functor<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> = FF.run {
    f(get(s)).map { b -> set(s, b) }
  }

  /**
   * Lift a function [f]: `(A) -> Kind<F, B> to the context of `S`: `(S) -> Kind<F, T>`
   */
  fun <F> liftF(FF: Functor<F>, f: (A) -> Kind<F, B>): (S) -> Kind<F, T> = { s -> modifyF(FF, s, f) }

  /**
   * Join two [PLens] with the same focus in [A]
   */
  infix fun <S1, T1> choice(other: PLens<S1, T1, A, B>): PLens<Either<S, S1>, Either<T, T1>, A, B> = PLens(
    { ss -> ss.fold(this::get, other::get) },
    { ss, b -> ss.bimap({ s -> set(s, b) }, { s -> other.set(s, b) }) }
  )

  /**
   * Pair two disjoint [PLens]
   */
  infix fun <S1, T1, A1, B1> split(other: PLens<S1, T1, A1, B1>): PLens<Tuple2<S, S1>, Tuple2<T, T1>, Tuple2<A, A1>, Tuple2<B, B1>> =
    PLens(
      { (s, c) -> get(s) toT other.get(c) },
      { (s, s1), (b, b1) -> set(s, b) toT other.set(s1, b1) }
    )

  /**
   * Create a product of the [PLens] and a type [C]
   */
  fun <C> first(): PLens<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = PLens(
    { (s, c) -> get(s) toT c },
    { (s, _) , (b, c) -> set(s, b) toT c }
  )

  /**
   * Create a product of a type [C] and the [PLens]
   */
  fun <C> second(): PLens<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> = PLens(
    { (c, s) -> c toT get(s) },
    { (_, s) , (c, b) -> c toT set(s, b) }
  )

  /**
   * Compose a [PLens] with another [PLens]
   */
  infix fun <C, D> compose(l: PLens<A, B, C, D>): PLens<S, T, C, D> = Lens(
    { a -> l.get(get(a)) },
    { s, c -> set(s, l.set(get(s), c)) }
  )

  /**
   * Compose a [PLens] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

  /**
   * Compose an [PLens] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PLens<S, T, C, D> = compose(other.asLens())

  /**
   * Compose an [PLens] with a [Getter]
   */
  infix fun <C> compose(other: Getter<A, C>): Getter<S, C> = asGetter() compose other

  /**
   * Compose an [PLens] with a [PSetter]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose an [PLens] with a [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

  /**
   * Compose an [PLens] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Compose an [PLens] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = asTraversal() compose other

  /**
   * Plus operator overload to compose lenses
   */
  operator fun <C, D> plus(other: PLens<A, B, C, D>): PLens<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PIso<A, B, C, D>): PLens<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Getter<A, C>): Getter<S, C> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  /**
   * View [PLens] as a [Getter]
   */
  fun asGetter(): Getter<S, A> = Getter(this::get)

  /**
   * View a [PLens] as a [POptional]
   */
  fun asOptional(): POptional<S, T, A, B> = POptional(
    { s -> Either.Right(get(s)) },
    { s, b -> set(s, b) }
  )

  /**
   * View a [PLens] as a [PSetter]
   */
  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

  /**
   * View a [PLens] as a [Fold]
   */
  fun asFold(): Fold<S, A> = object : Fold<S, A> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R = f(get(s))
  }

  /**
   * View a [PLens] as a [PTraversal]
   */
  fun asTraversal(): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
    override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> = FA.run {
      f(get(s)).map { b -> this@PLens.set(s, b) }
    }
  }

  /**
   * Modify the focus of s [PLens] using s function `(A) -> B`
   */
  fun modify(s: S, f: (A) -> B): T = set(s, f(get(s)))

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  fun lift(f: (A) -> B): (S) -> T = { s -> modify(s, f) }

  /**
   * Find a focus that satisfies the predicate
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> = get(s).let { a ->
    if (p(a)) Some(a) else None
  }

  /**
   * Verify if the focus of a [PLens] satisfies the predicate
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = p(get(s))

  /**
   * Extracts the value viewed through the [get] function.
   */
  fun ask(): Reader<S, A> = Reader(::get)

  /**
   * Transforms a [PLens] into a [Reader]. Alias for [ask].
   */
  fun toReader(): Reader<S, A> = ask()

  /**
   * Extracts the value viewed through the [get] and applies [f] to it.
   *
   * @param f function to apply to the focus.
   */
  fun <C> asks(f: (A) -> C): Reader<S, C> = ask().map(f)

  /**
   * Extracts the focus [A] viewed through the [PLens].
   */
  fun extract(): State<S, A> = State { s -> Tuple2(s, get(s)) }

  /**
   * Transforms a [PLens] into a [State].
   * Alias for [extract].
   */
  fun toState(): State<S, A> = extract()

  /**
   * Extracts and maps the focus [A] viewed through the [PLens] and applies [f] to it.
   */
  fun <C> extractMap(f: (A) -> C): State<S, C> = extract().map(f)

}

/**
 * Update the focus [A] viewed through the [Lens] and returns its *new* value.
 */
fun <S, A> Lens<S, A>.update(f: (A) -> A): State<S, A> = State { s ->
  val b = f(get(s))
  Tuple2(set(s, b), b)
}

/**
 * Update the focus [A] viewed through the [Lens] and returns its *old* value.
 */
fun <S, A> Lens<S, A>.updateOld(f: (A) -> A): State<S, A> = State { s ->
  Tuple2(modify(s, f), get(s))
}

/**
 * Modify the focus [A] viewed through the [Lens] and ignores both values.
 */
fun <S, A> Lens<S, A>.update_(f: (A) -> A): State<S, Unit> =
  State { s -> Tuple2(modify(s, f), Unit) }

/**
 * Assign the focus [A] viewed through the [Lens] and returns its *new* value.
 */
fun <S, A> Lens<S, A>.assign(a: A): State<S, A> = update { _ -> a }

/**
 * Assign the value focus [A] through the [Lens] and returns its *old* value.
 */
fun <S, A> Lens<S, A>.assignOld(a: A): State<S, A> = updateOld { _ -> a }

/**
 * Assign the focus [A] viewed through the [Lens] and ignores both values.
 */
fun <S, A> Lens<S, A>.assign_(a: A): State<S, Unit> = update_ { _ -> a }
