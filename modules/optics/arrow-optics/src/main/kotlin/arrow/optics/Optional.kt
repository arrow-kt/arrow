package arrow.optics

import arrow.Kind
import arrow.core.*
import arrow.data.State
import arrow.data.map
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monoid

/**
 * [Optional] is a type alias for [POptional] which fixes the type arguments
 * and restricts the [POptional] to monomorphic updates.
 */
typealias Optional<S, A> = POptional<S, S, A, A>

typealias ForOptional = ForPOptional
typealias OptionalOf<S, A> = POptionalOf<S, S, A, A>
typealias OptionalPartialOf<S> = Kind<ForOptional, S>
typealias OptionalKindedJ<S, A> = POptionalKindedJ<S, S, A, A>

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
@higherkind
interface POptional<S, T, A, B> : POptionalOf<S, T, A, B> {

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
      { it.fold({ Either.Right(it) }, { Either.Right(it) }) },
      { aa, a -> aa.bimap({ a }, { a }) }
    )

    /**
     * Invoke operator overload to create a [POptional] of type `S` with focus `A`.
     * Can also be used to construct [Optional]
     */
    operator fun <S, T, A, B> invoke(getOrModify: (S) -> Either<T, A>, set: (S, B) -> T): POptional<S, T, A, B> = object : POptional<S, T, A, B> {
      override fun getOrModify(s: S): Either<T, A> = getOrModify(s)

      override fun set(s: S, b: B): T = set(s, b)
    }

    /**
     * Invoke operator overload to create a [POptional] of type `S` with focus `A`.
     * Can also be used to construct [Optional]
     */
    operator fun <S, A> invoke(partialFunction: PartialFunction<S, A>, set: (S, A) -> S): Optional<S, A> = Optional(
      getOrModify = { s -> partialFunction.lift()(s).fold({ Either.Left(s) }, { Either.Right(it) }) },
      set = set
    )

    /**
     * [POptional] that never sees its focus
     */
    fun <A, B> void(): Optional<A, B> = Optional(
      { Either.Left(it) },
      { s, _ -> s }
    )

  }

  /**
   * Modify the focus of a [POptional] with an Applicative function [f]
   */
  fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> = FA.run {
    getOrModify(s).fold(
      ::just
    ) { f(it).map { set(s, it) } }
  }

  /**
   * Lift a function [f]: `(A) -> Kind<F, B> to the context of `S`: `(S) -> Kind<F, T>`
   */
  fun <F> liftF(FA: Applicative<F>, f: (A) -> Kind<F, B>): (S) -> Kind<F, T> = { s ->
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
      { ss -> ss.fold({ getOrModify(it).bimap({ Either.Left(it) }, ::identity) }, { other.getOrModify(it).bimap({ Either.Right(it) }, ::identity) }) },
      { s, b -> s.bimap({ ss -> this.set(ss, b) }, { ss -> other.set(ss, b) }) }
    )

  /**
   * Create a product of the [POptional] and a type [C]
   */
  fun <C> first(): POptional<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> =
    POptional(
      { (s, c) -> getOrModify(s).bimap({ it toT c }, { it toT c }) },
      { (s, c2), (b, c) -> setOption(s, b).fold({ set(s, b) toT c2 }, { it toT c }) }
    )

  /**
   * Create a product of a type [C] and the [POptional]
   */
  fun <C> second(): POptional<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> =
    POptional(
      { (c, s) -> getOrModify(s).bimap({ c toT it }, { c toT it }) },
      { (c2, s), (c, b) -> setOption(s, b).fold({ c2 toT set(s, b) }, { c toT it }) }
    )

  /**
   * Compose a [POptional] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = POptional(
    { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(s, it) }, ::identity) } },
    { s, d -> modify(s) { a -> other.set(a, d) } }
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
  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

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
    override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
      this@POptional.modifyF(FA, s, f)
  }

  /**
   * Modify the focus of a [POptional] with a function [f]
   */
  fun modify(s: S, f: (A) -> B): T = getOrModify(s).fold(::identity) { a -> set(s, f(a)) }

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  fun lift(f: (A) -> B): (S) -> T = { s -> modify(s, f) }

  /**
   * Modify the focus of a [POptional] with a function [f]
   * @return [Option.None] if the [POptional] is not matching
   */
  fun modifiyOption(s: S, f: (A) -> B): Option<T> = getOption(s).map { set(s, f(it)) }

  /**
   * Find the focus that satisfies the predicate [p]
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> =
    getOption(s).flatMap { b -> if (p(b)) Some(b) else None }

  /**
   * Check if there is a focus and it satisfies the predicate [p]
   */
  fun exists(s: S, p: (A) -> Boolean): Boolean = getOption(s).fold({ false }, p)

  /**
   * Check if there is no focus or the target satisfies the predicate [p]
   */
  fun all(s: S, p: (A) -> Boolean): Boolean = getOption(s).fold({ true }, p)

  /**
   * Extracts the focus [A] viewed through the [POptional].
   */
  fun extract(): State<S, Option<A>> = State { s -> Tuple2(s, getOption(s)) }

  /**
   * Transforms a [POptional] into a [State].
   * Alias for [extract].
   */
  fun toState(): State<S, Option<A>> = extract()

  /**
   * Extract and map the focus [A] viewed through the [POptional] and applies [f] to it.
   */
  fun <C> extractMap(f: (A) -> C): State<S, Option<C>> = extract().map { it.map(f) }

}

/**
 * Update the focus [A] viewed through the [Optional] and returns its *new* value.
 */
fun <S, A> Optional<S, A>.update(f: (A) -> A): State<S, Option<A>> =
  updateOld(f).map { it.map(f) }

/**
 * Update the focus [A] viewed through the [Optional] and returns its *old* value.
 */
fun <S, A> Optional<S, A>.updateOld(f: (A) -> A): State<S, Option<A>> =
  State { s -> Tuple2(modify(s, f), getOption(s)) }

/**
 * Update the focus [A] viewed through the [Optional] and ignores both values.
 */
fun <S, A> Optional<S, A>.update_(f: (A) -> A): State<S, Unit> =
  State { s -> Tuple2(modify(s, f), kotlin.Unit) }

/**
 * Assign the focus [A] viewed through the [Optional] and returns its *new* value.
 */
fun <S, A> Optional<S, A>.assign(a: A): State<S, Option<A>> =
  update { _ -> a }

/**
 * Assign the value focus [A] through the [Optional] and returns its *old* value.
 */
fun <S, A> Optional<S, A>.assignOld(a: A): State<S, Option<A>> =
  updateOld { _ -> a }

/**
 * Assign the focus [A] viewed through the [Optional] and ignores both values.
 */
fun <S, A> Optional<S, A>.assign_(a: A): State<S, Unit> =
  update_ { _ -> a }
