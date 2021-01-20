package arrow.core

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
class ForNonEmptyList private constructor() { companion object }
typealias NonEmptyListOf<A> = arrow.Kind<ForNonEmptyList, A>

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> NonEmptyListOf<A>.fix(): NonEmptyList<A> =
  this as NonEmptyList<A>

typealias Nel<A> = NonEmptyList<A>

/**
 * `NonEmptyList` is a data type used in __Λrrow__ to model ordered lists that guarantee to have at least one value.
 * `NonEmptyList` is available in the `arrow-core-data` module under the `import arrow.core.NonEmptyList`
 *
 * ## of
 *
 * A `NonEmptyList` guarantees the list always has at least 1 element.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * val value =
 * //sampleStart
 *  // NonEmptyList.of() // does not compile
 *  NonEmptyList.of(1, 2, 3, 4, 5) // NonEmptyList<Int>
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## head
 *
 * Unlike `List[0]`, `NonEmptyList.head` it's a safe operation that guarantees no exception throwing.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * val value =
 * //sampleStart
 *  NonEmptyList.of(1, 2, 3, 4, 5).head
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## foldLeft
 *
 * When we fold over a `NonEmptyList`, we turn a `NonEmptyList< A >` into `B` by providing a __seed__ value and a __function__ that carries the state on each iteration over the elements of the list.
 * The first argument is a function that addresses the __seed value__, this can be any object of any type which will then become the resulting typed value.
 * The second argument is a function that takes the current state and element in the iteration and returns the new state after transformations have been applied.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * //sampleStart
 * fun sumNel(nel: NonEmptyList<Int>): Int =
 *  nel.foldLeft(0) { acc, n -> acc + n }
 * val value = sumNel(NonEmptyList.of(1, 1, 1, 1))
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * ## map
 *
 * `map` allows us to transform `A` into `B` in `NonEmptyList< A >`
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * val value =
 * //sampleStart
 *  NonEmptyList.of(1, 1, 1, 1).map { it + 1 }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## flatMap
 *
 * `flatMap` allows us to compute over the contents of multiple `NonEmptyList< * >` values
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * //sampleStart
 * val nelOne: NonEmptyList<Int> = NonEmptyList.of(1)
 * val nelTwo: NonEmptyList<Int> = NonEmptyList.of(2)
 *
 * val value = nelOne.flatMap { one ->
 *  nelTwo.map { two ->
 *    one + two
 *  }
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * ## Monad binding
 *
 * Λrrow allows imperative style comprehensions to make computing over `NonEmptyList` values easy.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import arrow.core.extensions.fx
 *
 * //sampleStart
 * val nelOne: NonEmptyList<Int> = NonEmptyList.of(1)
 * val nelTwo: NonEmptyList<Int> = NonEmptyList.of(2)
 * val nelThree: NonEmptyList<Int> = NonEmptyList.of(3)
 *
 * val value = NonEmptyList.fx {
 *  val (one) = nelOne
 *  val (two) = nelTwo
 *  val (three) = nelThree
 *  one + two + three
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * Monad binding in `NonEmptyList` and other collection related data type can be used as generators
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import arrow.core.extensions.fx
 *
 * val value =
 * //sampleStart
 *  NonEmptyList.fx {
 *    val (x) = NonEmptyList.of(1, 2, 3)
 *    val (y) = NonEmptyList.of(1, 2, 3)
 *   x + y
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## Applicative Builder
 *
 * Λrrow contains methods that allow you to preserve type information when computing over different `NonEmptyList` typed values.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import java.util.UUID
 * import arrow.core.extensions.nonemptylist.apply.map
 *
 * //sampleStart
 * data class Person(val id: UUID, val name: String, val year: Int)
 *
 * // Note each NonEmptyList is of a different type
 * val nelId: NonEmptyList<UUID> = NonEmptyList.of(UUID.randomUUID(), UUID.randomUUID())
 * val nelName: NonEmptyList<String> = NonEmptyList.of("William Alvin Howard", "Haskell Curry")
 * val nelYear: NonEmptyList<Int> = NonEmptyList.of(1926, 1900)
 *
 * val value = map(nelId, nelName, nelYear) { (id, name, year) ->
 *  Person(id, name, year)
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * ### Summary
 *
 * - `NonEmptyList` is __used to model lists that guarantee at least one element__
 * - We can easily construct values of `NonEmptyList` with `NonEmptyList.of`
 * - `foldLeft`, `map`, `flatMap` and others are used to compute over the internal contents of a `NonEmptyList` value.
 * - `fx { ... } comprehensions` can be __used to imperatively compute__ over multiple `NonEmptyList` values in sequence.
 * - `NonEmptyList.applicative().map { ... }` can be used to compute over multiple `NonEmptyList` values preserving type information and __abstracting over arity__ with `map`
 *
 */
class NonEmptyList<out A>(
  val head: A,
  val tail: List<A>
) : NonEmptyListOf<A>, AbstractList<A>() {

  private constructor(list: List<A>) : this(list[0], list.drop(1))

  override val size: Int =
    1 + tail.size

  val all: List<A>
    get() = toList()

  override operator fun get(index: Int): A {
    if (index < 0 || index >= size) throw IndexOutOfBoundsException("$index is not in 1..${size - 1}")
    return if (index == 0) head else tail[index - 1]
  }

  override fun isEmpty(): Boolean = false

  fun toList(): List<A> = listOf(head) + tail

  inline fun <B> map(f: (A) -> B): NonEmptyList<B> =
    NonEmptyList(f(head), tail.map(f))

  @JvmName("flatMapKind")
  @Deprecated(
    "Kind is deprecated, and will be removed in 0.13.0. Please the flatMap method defined for NonEmptyList instead",
    level = DeprecationLevel.WARNING
  )
  inline fun <B> flatMap(f: (A) -> NonEmptyListOf<B>): NonEmptyList<B> =
    f(head).fix() + tail.flatMap { f(it).fix().all }

  inline fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> =
    f(head) + tail.flatMap { f(it).all }

  @JvmName("apKind")
  @Deprecated(
    "Kind is deprecated, and will be removed in 0.13.0. Please the ap method defined for NonEmptyList instead",
    level = DeprecationLevel.WARNING
  )
  fun <B> ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> =
    fix().flatMap { a -> ff.fix().map { f -> f(a) } }.fix()

  fun <B> ap(ff: NonEmptyList<(A) -> B>): NonEmptyList<B> =
    flatMap { a -> ff.map { f -> f(a) } }

  operator fun plus(l: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l.all)

  operator fun plus(l: List<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l)

  operator fun plus(a: @UnsafeVariance A): NonEmptyList<A> =
    NonEmptyList(all + a)

  inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    this.tail.fold(f(b, this.head), f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    all.k().foldRight(lb, f)

  fun <G, B> traverse(AG: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, NonEmptyList<B>> =
    AG.run { all.k().traverse(AG, f).map { Nel.fromListUnsafe(it) } }

  @JvmName("coflatMapKind")
  @Deprecated(
    "Kind is deprecated, and will be removed in 0.13.0. Please the coflatMap method defined for NonEmptyList instead",
    level = DeprecationLevel.WARNING
  )
  fun <B> coflatMap(f: (NonEmptyListOf<A>) -> B): NonEmptyList<B> {
    val buf = mutableListOf<B>()
    tailrec fun consume(list: List<A>): List<B> =
      if (list.isEmpty()) {
        buf
      } else {
        val tail = list.subList(1, list.size)
        buf += f(NonEmptyList(list[0], tail))
        consume(tail)
      }
    return NonEmptyList(f(this), consume(this.fix().tail))
  }

  fun <B> coflatMap(f: (NonEmptyList<A>) -> B): NonEmptyList<B> {
    val buf = mutableListOf<B>()
    tailrec fun consume(list: List<A>): List<B> =
      if (list.isEmpty()) {
        buf
      } else {
        val tail = list.subList(1, list.size)
        buf += f(NonEmptyList(list[0], tail))
        consume(tail)
      }
    return NonEmptyList(f(this), consume(this.tail))
  }

  fun extract(): A =
    this.head

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other?.javaClass != javaClass) return false

    other as NonEmptyList<*>

    if (all != other.all) return false

    return true
  }

  override fun hashCode(): Int =
    all.hashCode()

  fun show(SA: Show<A>): String =
    "NonEmptyList(${all.k().show(SA)})"

  override fun toString(): String =
    show(Show.any())

  fun <B> align(b: NonEmptyList<B>): NonEmptyList<Ior<A, B>> =
    NonEmptyList(Ior.Both(head, b.head), tail.align(b.tail))

  fun salign(SA: Semigroup<@UnsafeVariance A>, b: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    SA.run {
      NonEmptyList(head.combine(b.head), tail.salign(SA, b.tail).toList())
    }

  fun <B> padZip(other: NonEmptyList<B>): NonEmptyList<Tuple2<A?, B?>> =
    NonEmptyList(Tuple2(head, other.head), tail.padZip(other.tail))

  fun <B> zip(other: NonEmptyList<B>): NonEmptyList<Tuple2<A, B>> =
    NonEmptyList(Tuple2(head, other.head), tail.zip(other.tail).map { Tuple2(it.first, it.second) })

  fun <B, C> zip(other: NonEmptyList<B>, f: (A, B) -> C): NonEmptyList<C> =
    zip(other).map { f(it.a, it.b) }

  companion object {
    operator fun <A> invoke(head: A, vararg t: A): NonEmptyList<A> =
      NonEmptyList(head, t.asList())

    fun <A> of(head: A, vararg t: A): NonEmptyList<A> =
      NonEmptyList(head, t.asList())

    fun <A> fromList(l: List<A>): Option<NonEmptyList<A>> =
      if (l.isEmpty()) None else Some(NonEmptyList(l))

    fun <A> fromListUnsafe(l: List<A>): NonEmptyList<A> =
      NonEmptyList(l)

    fun <A> just(a: A): NonEmptyList<A> =
      of(a)

    val unit: NonEmptyList<Unit> =
      of(Unit)

    inline fun <B, C, D> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      map: (B, C) -> D
    ): NonEmptyList<D> =
      mapN(b, c, unit, unit, unit, unit, unit, unit, unit, unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

    inline fun <B, C, D, E> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      map: (B, C, D) -> E
    ): NonEmptyList<E> =
      mapN(b, c, d, unit, unit, unit, unit, unit, unit, unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

    inline fun <B, C, D, E, F> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      e: NonEmptyList<E>,
      map: (B, C, D, E) -> F
    ): NonEmptyList<F> =
      mapN(b, c, d, e, unit, unit, unit, unit, unit, unit) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

    inline fun <B, C, D, E, F, G> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      e: NonEmptyList<E>,
      f: NonEmptyList<F>,
      map: (B, C, D, E, F) -> G
    ): NonEmptyList<G> =
      mapN(b, c, d, e, f, unit, unit, unit, unit, unit) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

    inline fun <B, C, D, E, F, G, H> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      e: NonEmptyList<E>,
      f: NonEmptyList<F>,
      g: NonEmptyList<G>,
      map: (B, C, D, E, F, G) -> H
    ): NonEmptyList<H> =
      mapN(b, c, d, e, f, g, unit, unit, unit, unit) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

    inline fun <B, C, D, E, F, G, H, I> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      e: NonEmptyList<E>,
      f: NonEmptyList<F>,
      g: NonEmptyList<G>,
      h: NonEmptyList<H>,
      map: (B, C, D, E, F, G, H) -> I
    ): NonEmptyList<I> =
      mapN(b, c, d, e, f, g, h, unit, unit, unit) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

    inline fun <B, C, D, E, F, G, H, I, J> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      e: NonEmptyList<E>,
      f: NonEmptyList<F>,
      g: NonEmptyList<G>,
      h: NonEmptyList<H>,
      i: NonEmptyList<I>,
      crossinline map: (B, C, D, E, F, G, H, I) -> J
    ): NonEmptyList<J> =
      mapN(b, c, d, e, f, g, h, i, unit, unit) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

    inline fun <B, C, D, E, F, G, H, I, J, K> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      e: NonEmptyList<E>,
      f: NonEmptyList<F>,
      g: NonEmptyList<G>,
      h: NonEmptyList<H>,
      i: NonEmptyList<I>,
      j: NonEmptyList<J>,
      map: (B, C, D, E, F, G, H, I, J) -> K
    ): NonEmptyList<K> =
      mapN(b, c, d, e, f, g, h, i, j, unit) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

    inline fun <B, C, D, E, F, G, H, I, J, K, L> mapN(
      b: NonEmptyList<B>,
      c: NonEmptyList<C>,
      d: NonEmptyList<D>,
      e: NonEmptyList<E>,
      f: NonEmptyList<F>,
      g: NonEmptyList<G>,
      h: NonEmptyList<H>,
      i: NonEmptyList<I>,
      j: NonEmptyList<J>,
      k: NonEmptyList<K>,
      map: (B, C, D, E, F, G, H, I, J, K) -> L
    ): NonEmptyList<L> =
      NonEmptyList(
        map(b.head, c.head, d.head, e.head, f.head, g.head, h.head, i.head, j.head, k.head),
        b.tail.flatMap { bb ->
          c.tail.flatMap { cc ->
            d.tail.flatMap { dd ->
              e.tail.flatMap { ee ->
                f.tail.flatMap { ff ->
                  g.tail.flatMap { gg ->
                    h.tail.flatMap { hh ->
                      i.tail.flatMap { ii ->
                        j.tail.flatMap { jj ->
                          k.tail.map { kk ->
                            map(bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      )

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
      buf: ArrayList<B>,
      f: (A) -> Kind<ForNonEmptyList, Either<A, B>>,
      v: NonEmptyList<Either<A, B>>
    ) {
      val head: Either<A, B> = v.head
      when (head) {
        is Either.Right -> {
          buf += head.b
          val x = fromList(v.tail)
          when (x) {
            is Some<NonEmptyList<Either<A, B>>> -> go(buf, f, x.t)
            is None -> Unit
          }
        }
        is Either.Left -> go(buf, f, f(head.a).fix() + v.tail)
      }
    }

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForNonEmptyList, Either<A, B>>): NonEmptyList<B> {
      val buf = ArrayList<B>()
      go(buf, f, f(a).fix())
      return fromListUnsafe(buf)
    }
  }
}

inline fun <A> A.nel(): NonEmptyList<A> =
  NonEmptyList.of(this)

fun <A, G> NonEmptyListOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, NonEmptyList<A>> =
  fix().traverse(GA, ::identity)

@Deprecated(
  "Kind is deprecated, and will be removed in 0.13.0. Please the plus method defined for NonEmptyList instead",
  ReplaceWith(
    "fix().plus(y.fix())",
    "arrow.core.fix", "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyListOf<A>.combineK(y: NonEmptyListOf<A>): NonEmptyList<A> =
  fix().plus(y.fix())

fun <A> NonEmptyList<A>.compare(OA: Order<A>, b: NonEmptyList<A>): Ordering = OA.run {
  align(b) { ior -> ior.fold({ GT }, { LT }, { a1, a2 -> a1.compare(a2) }) }
    .fold(Monoid.ordering())
}

fun <A> NonEmptyList<NonEmptyList<A>>.flatten(): NonEmptyList<A> =
  this.flatMap(::identity)

fun <A, B> NonEmptyList<Either<A, B>>.selectM(f: NonEmptyList<(A) -> B>): NonEmptyList<B> =
  this.flatMap { it.fold({ a -> f.map { ff -> ff(a) } }, { b -> NonEmptyList.just(b) }) }

fun <A, B> NonEmptyList<Tuple2<A, B>>.unzip(): Tuple2<NonEmptyList<A>, NonEmptyList<B>> =
  this.unzipWith(::identity)

fun <A, B, C> NonEmptyList<C>.unzipWith(f: (C) -> Tuple2<A, B>): Tuple2<NonEmptyList<A>, NonEmptyList<B>> =
 this.map(f).let { nel ->
   nel.tail.unzip().bimap(
     { NonEmptyList(nel.head.a, it) },
     { NonEmptyList(nel.head.b, it) })
 }

inline fun <E, A, B> NonEmptyList<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, NonEmptyList<B>> =
  foldRight(f(head).map { NonEmptyList.just(it) }) { a, acc ->
    f(a).ap(acc.map { bs -> { b: B -> NonEmptyList(b) + bs } })
  }

inline fun <E, A, B> NonEmptyList<A>.flatTraverseEither(f: (A) -> Either<E, NonEmptyList<B>>): Either<E, NonEmptyList<B>> =
  foldRight(f(head)) { a, acc ->
    f(a).ap(acc.map { bs -> { b: NonEmptyList<B> -> b + bs } })
  }

inline fun <E, A> NonEmptyList<A>.traverseEither_(f: (A) -> Either<E, *>): Either<E, Unit> {
  val void = { _: Unit -> { _: Any? -> Unit } }
  return foldRight<A, Either<E, Unit>>(Unit.right()) { a, acc ->
    f(a).ap(acc.map(void))
  }
}

fun <E, A> NonEmptyList<Either<E, A>>.sequenceEither(): Either<E, NonEmptyList<A>> =
  traverseEither(::identity)

fun <E, A> NonEmptyList<Either<E, NonEmptyList<A>>>.flatSequenceEither(): Either<E, NonEmptyList<A>> =
  flatTraverseEither(::identity)

fun <E> NonEmptyList<Either<E, *>>.sequenceEither_(): Either<E, Unit> =
  traverseEither_(::identity)

inline fun <E, A, B> NonEmptyList<A>.traverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, B>): Validated<E, NonEmptyList<B>> =
  foldRight(f(head).map { NonEmptyList(it) }) { a, acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: B -> NonEmptyList(b) + bs } })
  }

inline fun <E, A, B> NonEmptyList<A>.flatTraverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, NonEmptyList<B>>): Validated<E, NonEmptyList<B>> =
  foldRight(f(head)) { a, acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: NonEmptyList<B> -> b + bs } })
  }

inline fun <E, A> NonEmptyList<A>.traverseValidated_(semigroup: Semigroup<E>, f: (A) -> Validated<E, *>): Validated<E, Unit> =
  foldRight<A, Validated<E, Unit>>(Unit.valid()) { a, acc ->
    f(a).ap(semigroup, acc.map { { Unit } })
  }

fun <E, A> NonEmptyList<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, NonEmptyList<A>> =
  traverseValidated(semigroup, ::identity)

fun <E, A> NonEmptyList<Validated<E, NonEmptyList<A>>>.flatSequenceValidated(semigroup: Semigroup<E>): Validated<E, NonEmptyList<A>> =
  flatTraverseValidated(semigroup, ::identity)

fun <E> NonEmptyList<Validated<E, *>>.sequenceValidated_(semigroup: Semigroup<E>): Validated<E, Unit> =
  traverseValidated_(semigroup, ::identity)

/**
 * Check if [this@lt] is `lower than` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@lt]
 * @returns true if [this@lt] is `lower than` [b] and false otherwise
 */
fun <A> NonEmptyList<A>.lt(OA: Order<A>, b: NonEmptyList<A>): Boolean =
  compare(OA, b) == LT

/**
 * Check if [this@lte] is `lower than or equal to` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@lte]
 * @returns true if [this@lte] is `lower than or equal to` [b] and false otherwise
 */
fun <A> NonEmptyList<A>.lte(OA: Order<A>, b: NonEmptyList<A>): Boolean =
  compare(OA, b) != GT

/**
 * Check if [this@gt] is `greater than` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@gt]
 * @returns true if [this@gt] is `greater than` [b] and false otherwise
 */
fun <A> NonEmptyList<A>.gt(OA: Order<A>, b: NonEmptyList<A>): Boolean =
  compare(OA, b) == GT

/**
 * Check if [this@gte] is `greater than or equal to` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@gte]
 * @returns true if [this@gte] is `greater than or equal to` [b] and false otherwise
 */
fun <A> NonEmptyList<A>.gte(OA: Order<A>, b: NonEmptyList<A>): Boolean =
  compare(OA, b) != LT

/**
 * Determines the maximum of [this@max] and [b] in terms of order.
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@max]
 * @returns the maximum [this@max] if it is greater than [b] or [b] otherwise
 */
fun <A> NonEmptyList<A>.max(OA: Order<A>, b: NonEmptyList<A>): NonEmptyList<A> =
  if (gt(OA, b)) this else b

/**
 * Determines the minimum of [this@min] and [b] in terms of order.
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@min]
 * @returns the minimum [this@min] if it is less than [b] or [b] otherwise
 */
fun <A> NonEmptyList<A>.min(OA: Order<A>, b: NonEmptyList<A>): NonEmptyList<A> =
  if (lt(OA, b)) this else b

/**
 * Sorts [this@sort] and [b] in terms of order.
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@sort]
 * @returns a sorted [Tuple2] of [this@sort] and [b].
 */
fun <A> NonEmptyList<A>.sort(OA: Order<A>, b: NonEmptyList<A>): Tuple2<NonEmptyList<A>, NonEmptyList<A>> =
  if (gte(OA, b)) Tuple2(this, b) else Tuple2(b, this)

/** Construct an [Eq] instance which use [EQA] to compare the elements of the lists **/
fun <A> Eq.Companion.nonEmptyList(EQA: Eq<A>): Eq<NonEmptyList<A>> =
  NonEmptyListEq(EQA)

fun <A> Hash.Companion.nonEmptyList(HA: Hash<A>): Hash<NonEmptyList<A>> =
  NonEmptyListHash(HA)

fun <A> Order.Companion.nonEmptyList(OA: Order<A>): Order<NonEmptyList<A>> =
  NonEmptyListOrder(OA)

@Suppress("UNCHECKED_CAST")
fun <A> Semigroup.Companion.nonEmptyList(): Semigroup<NonEmptyList<A>> =
  NonEmptyListSemigroup as Semigroup<NonEmptyList<A>>

fun <A> Show.Companion.nonEmptyList(SA: Show<A>): Show<NonEmptyList<A>> =
  NonEmptyListShow(SA)

private class NonEmptyListEq<A>(
  private val EQA: Eq<A>,
) : Eq<NonEmptyList<A>> {
  override fun NonEmptyList<A>.eqv(b: NonEmptyList<A>): Boolean = eqv(EQA, b)
}

private class NonEmptyListHash<A>(
  private val HA: Hash<A>,
) : Hash<NonEmptyList<A>> {
  override fun NonEmptyList<A>.hash(): Int = hash(HA)

  override fun NonEmptyList<A>.hashWithSalt(salt: Int): Int = hashWithSalt(HA, salt)
}

private class NonEmptyListOrder<A>(
  private val OA: Order<A>
) : Order<NonEmptyList<A>> {
  override fun NonEmptyList<A>.compare(b: NonEmptyList<A>): Ordering = compare(OA, b)
}

object NonEmptyListSemigroup : Semigroup<NonEmptyList<Any?>> {
  override fun NonEmptyList<Any?>.combine(b: NonEmptyList<Any?>): NonEmptyList<Any?> =
    NonEmptyList(this.head, this.tail.plus(b))
}

private class NonEmptyListShow<A>(
  private val SA: Show<A>,
) : Show<NonEmptyList<A>> {
  override fun NonEmptyList<A>.show(): String = show(SA)
}
