package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.compose
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid

/**
 * [Prism] is a type alias for [PPrism] which fixes the type arguments
 * and restricts the [PPrism] to monomorphic updates.
 */
typealias Prism<S, A> = PPrism<S, S, A, A>

typealias ForPrism = ForPPrism
typealias PrismOf<S, A> = PPrismOf<S, S, A, A>
typealias PrismPartialOf<S> = Kind<ForPrism, S>
typealias PrismKindedJ<S, A> = PPrismKindedJ<S, S, A, A>

/**
 * A [Prism] is a loss less invertible optic that can look into a structure and optionally find its focus.
 * Mostly used for finding a focus that is only present under certain conditions i.e. list head Prism<List<Int>, Int>
 *
 * A (polymorphic) [PPrism] is useful when setting or modifying a value for a polymorphic sum type
 * i.e. PPrism<Try<Sting>, Try<Int>, String, Int>
 *
 * A [PPrism] gathers the two concepts of pattern matching and constructor and thus can be seen as a pair of functions:
 * - `getOrModify: A -> Either<A, B>` meaning it returns the focus of a [PPrism] OR the original value
 * - `reverseGet : B -> A` meaning we can construct the source type of a [PPrism] from a focus `B`
 *
 * @param S the source of a [PPrism]
 * @param T the modified source of a [PPrism]
 * @param A the focus of a [PPrism]
 * @param B the modified focus of a [PPrism]
 */
@higherkind
interface PPrism<S, T, A, B> : PPrismOf<S, T, A, B> {

  fun getOrModify(s: S): Either<T, A>
  fun reverseGet(b: B): T

  companion object {

    fun <S> id() = PIso.id<S>().asPrism()

    /**
     * Invoke operator overload to create a [PPrism] of type `S` with focus `A`.
     * Can also be used to construct [Prism]
     */
    operator fun <S, T, A, B> invoke(getOrModify: (S) -> Either<T, A>, reverseGet: (B) -> T) = object : PPrism<S, T, A, B> {
      override fun getOrModify(s: S): Either<T, A> = getOrModify(s)

      override fun reverseGet(b: B): T = reverseGet(b)
    }

    /**
     * A [PPrism] that checks for equality with a given value [a]
     */
    fun <A> only(a: A, EQA: Eq<A>): Prism<A, Unit> = Prism(
      getOrModify = { a2 -> (if (EQA.run { a.eqv(a2) }) Either.Left(a) else Either.Right(Unit)) },
      reverseGet = { a }
    )
  }

  /**
   * Modify the focus of a [PPrism] with an [Applicative] function
   */
  fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> = FA.run {
    getOrModify(s).fold(
      ::just
    ) { f(it).map(::reverseGet) }
  }

  /**
   * Modify the focus of a [PPrism] with an [Applicative] function
   */
  fun <F> liftF(FA: Applicative<F>, f: (A) -> Kind<F, B>): (S) -> Kind<F, T> = FA.run {
    { s ->
      getOrModify(s).fold(
        ::just
      ) { f(it).map(::reverseGet) }
    }
  }

  /**
   * Get the focus or [Option.None] if focus cannot be seen
   */
  fun getOption(s: S): Option<A> = getOrModify(s).toOption()

  /**
   * Set the focus of a [PPrism] with a value
   */
  fun set(s: S, b: B): T = modify(s) { b }

  /**
   * Set the focus of a [PPrism] with a value
   */
  fun setOption(s: S, b: B): Option<T> = modifyOption(s) { b }

  /**
   * Check if a focus can be seen by the [PPrism]
   */
  fun nonEmpty(s: S): Boolean = getOption(s).fold({ false }, { true })

  /**
   * Check if no focus can be seen by the [PPrism]
   */
  fun isEmpty(s: S): Boolean = !nonEmpty(s)

  /**
   * Modify the focus of a [PPrism] with a function
   */
  fun modify(s: S, f: (A) -> B): T = getOrModify(s).fold(::identity) { a -> reverseGet(f(a)) }

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  fun lift(f: (A) -> B): (S) -> T = { s -> getOrModify(s).fold(::identity) { a -> reverseGet(f(a)) } }

  /**
   * Modify the focus of a [PPrism] with a function
   */
  fun modifyOption(s: S, f: (A) -> B): Option<T> = getOption(s).map { b -> reverseGet(f(b)) }

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> Option<T>`
   */
  fun liftOption(f: (A) -> B): (S) -> Option<T> = { s -> getOption(s).map { b -> reverseGet(f(b)) } }

  /**
   * Find the focus that satisfies the predicate
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> = getOption(s).flatMap { a -> if (p(a)) Some(a) else None }

  /**
   * Check if there is a focus and it satisfies the predicate
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = getOption(s).fold({ false }, p)

  /**
   * Check if there is no focus or the focus satisfies the predicate
   */
  fun all(s: S, p: (A) -> Boolean): Boolean = getOption(s).fold({ true }, p)

  /**
   * Create a product of the [PPrism] and a type [C]
   */
  fun <C> first(): PPrism<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = PPrism(
    { (s, c) -> getOrModify(s).bimap({ it toT c }, { it toT c }) },
    { (b, c) -> reverseGet(b) toT c }
  )

  /**
   * Create a product of a type [C] and the [PPrism]
   */
  fun <C> second(): PPrism<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> = PPrism(
    { (c, s) -> getOrModify(s).bimap({ c toT it }, { c toT it }) },
    { (c, b) -> c toT reverseGet(b) }
  )

  /**
   * Create a sum of the [PPrism] and a type [C]
   */
  fun <C> left(): PPrism<Either<S, C>, Either<T, C>, Either<A, C>, Either<B, C>> = PPrism(
    { it.fold({ a -> getOrModify(a).bimap({ Either.Left(it) }, { Either.Left(it) }) }, { c -> Either.Right(Either.Right(c)) }) },
    {
      when (it) {
        is Either.Left -> Either.Left(reverseGet(it.a))
        is Either.Right -> Either.Right(it.b)
      }
    }
  )

  /**
   * Create a sum of a type [C] and the [PPrism]
   */
  fun <C> right(): PPrism<Either<C, S>, Either<C, T>, Either<C, A>, Either<C, B>> = PPrism(
    { it.fold({ c -> Either.Right(Either.Left(c)) }, { s -> getOrModify(s).bimap({ Either.Right(it) }, { Either.Right(it) }) }) },
    { it.map(this::reverseGet) }
  )

  /**
   * Compose a [PPrism] with another [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = PPrism(
    getOrModify = { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(s, it) }, ::identity) } },
    reverseGet = this::reverseGet compose other::reverseGet
  )

  /**
   * Compose an [Iso] as an [PPrism]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PPrism<S, T, C, D> = compose(other.asPrism())

  /**
   * Compose a [PPrism] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

  /**
   * Compose a [PPrism] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

  /**
   * Compose a [PPrism] with a [PSetter]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose a [PPrism] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Compose a [PPrism] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = asTraversal() compose other

  /**
   * Plus operator overload to compose lenses
   */
  operator fun <C, D> plus(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): POptional<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PIso<A, B, C, D>): PPrism<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  /**
   * View a [PPrism] as an [POptional]
   */
  fun asOptional(): POptional<S, T, A, B> = POptional(
    this::getOrModify
  ) { s, b -> set(s, b) }

  /**
   * View a [PPrism] as a [PSetter]
   */
  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

  /**
   * View a [PPrism] as a [Fold]
   */
  fun asFold(): Fold<S, A> = object : Fold<S, A> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R = getOption(s).map(f).getOrElse(M::empty)
  }

  /**
   * View a [PPrism] as a [PTraversal]
   */
  fun asTraversal(): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
    override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> = FA.run {
      getOrModify(s).fold(
        ::just
      ) { f(it).map(this@PPrism::reverseGet) }
    }
  }
}

/**
 * Invoke operator overload to create a [PPrism] of type `S` with a focus `A` where `A` is a subtype of `S`
 * Can also be used to construct [Prism]
 */
@Suppress("FunctionName")
fun <S, A> Prism(getOption: (S) -> Option<A>, reverseGet: (A) -> S): Prism<S, A> = Prism(
  getOrModify = { getOption(it).toEither { it } },
  reverseGet = { reverseGet(it) }
)
