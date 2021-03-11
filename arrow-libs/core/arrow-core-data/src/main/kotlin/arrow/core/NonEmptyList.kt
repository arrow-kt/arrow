package arrow.core

import arrow.Kind
import arrow.KindDeprecation
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
class ForNonEmptyList private constructor() {
  companion object
}

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias NonEmptyListOf<A> = arrow.Kind<ForNonEmptyList, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A> NonEmptyListOf<A>.fix(): NonEmptyList<A> =
  this as NonEmptyList<A>

typealias Nel<A> = NonEmptyList<A>

/**
 * `NonEmptyList` is a data type used in __Λrrow__ to model ordered lists that guarantee to have at least one value.
 * `NonEmptyList` is available in the `arrow-core-data` module under the `import arrow.core.NonEmptyList`
 *
 * ## nonEmptyListOf
 *
 * A `NonEmptyList` guarantees the list always has at least 1 element.
 *
 * ```kotlin:ank:playground
 * import arrow.core.nonEmptyListOf
 *
 * val value =
 * //sampleStart
 *  // nonEmptyListOf() // does not compile
 *  nonEmptyListOf(1, 2, 3, 4, 5) // NonEmptyList<Int>
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
 * import arrow.core.nonEmptyListOf
 *
 * val value =
 * //sampleStart
 *  nonEmptyListOf(1, 2, 3, 4, 5).head
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
 * import arrow.core.nonEmptyListOf
 *
 * //sampleStart
 * fun sumNel(nel: NonEmptyList<Int>): Int =
 *  nel.foldLeft(0) { acc, n -> acc + n }
 * val value = sumNel(nonEmptyListOf(1, 1, 1, 1))
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
 * import arrow.core.nonEmptyListOf
 *
 * val value =
 * //sampleStart
 *  nonEmptyListOf(1, 1, 1, 1).map { it + 1 }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## Combining NonEmptyLists
 *
 * ### flatMap
 *
 * `flatMap` allows us to compute over the contents of multiple `NonEmptyList< * >` values
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import arrow.core.nonEmptyListOf
 *
 * //sampleStart
 * val nelOne: NonEmptyList<Int> = nonEmptyListOf(1, 2, 3)
 * val nelTwo: NonEmptyList<Int> = nonEmptyListOf(4, 5)
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
 * ### zip
 *
 * Λrrow contains methods that allow you to preserve type information when computing over different `NonEmptyList` typed values.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import arrow.core.nonEmptyListOf
 * import java.util.UUID
 *
 * //sampleStart
 * data class Person(val id: UUID, val name: String, val year: Int)
 *
 * // Note each NonEmptyList is of a different type
 * val nelId: NonEmptyList<UUID> = nonEmptyListOf(UUID.randomUUID(), UUID.randomUUID())
 * val nelName: NonEmptyList<String> = nonEmptyListOf("William Alvin Howard", "Haskell Curry")
 * val nelYear: NonEmptyList<Int> = nonEmptyListOf(1926, 1900)
 *
 * val value = nelId.zip(nelName, nelYear) { id, name, year ->
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
 * - We can easily construct values of `NonEmptyList` with `nonEmptyListOf`
 * - `foldLeft`, `map`, `flatMap` and others are used to compute over the internal contents of a `NonEmptyList` value.
 * - `a.zip(b, c) { ... }` can be used to compute over multiple `NonEmptyList` values preserving type information and __abstracting over arity__ with `zip`
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
    "Kind is deprecated, and will be removed in 0.13.0. Please use the flatMap method defined for NonEmptyList instead",
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

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>): String =
    "NonEmptyList(${all.k().show(SA)})"

  override fun toString(): String =
    "NonEmptyList(${all.joinToString()})"

  fun <B> align(b: NonEmptyList<B>): NonEmptyList<Ior<A, B>> =
    NonEmptyList(Ior.Both(head, b.head), tail.align(b.tail))

  fun salign(SA: Semigroup<@UnsafeVariance A>, b: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    SA.run {
      NonEmptyList(head.combine(b.head), tail.salign(SA, b.tail).toList())
    }

  fun <B> padZip(other: NonEmptyList<B>): NonEmptyList<Pair<A?, B?>> =
    NonEmptyList(head to other.head, tail.padZip(other.tail))

  companion object {

    @Deprecated(
      "Renamed to nonEmptyListOf to align with Kotlin Std standards",
      ReplaceWith("nonEmptyListOf(head, t)", "arrow.core.nonEmptyListOf")
    )
    operator fun <A> invoke(head: A, vararg t: A): NonEmptyList<A> =
      NonEmptyList(head, t.asList())

    @Deprecated(
      "Renamed to nonEmptyListOf to align with Kotlin Std standards",
      ReplaceWith("nonEmptyListOf(head, t)", "arrow.core.nonEmptyListOf")
    )
    fun <A> of(head: A, vararg t: A): NonEmptyList<A> =
      NonEmptyList(head, t.asList())

    fun <A> fromList(l: List<A>): Option<NonEmptyList<A>> =
      if (l.isEmpty()) None else Some(NonEmptyList(l))

    fun <A> fromListUnsafe(l: List<A>): NonEmptyList<A> =
      NonEmptyList(l)

    @Deprecated(
      "just is deprecated, and will be removed in 0.13.0. Please use nonEmptyListOf instead.",
      ReplaceWith(
        "nonEmptyListOf(a)",
        "arrow.core.NonEmptyList"
      ),
      DeprecationLevel.WARNING
    )
    fun <A> just(a: A): NonEmptyList<A> =
      of(a)

    @PublishedApi
    internal val unit: NonEmptyList<Unit> =
      of(Unit)

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

fun <A> nonEmptyListOf(head: A, vararg t: A): NonEmptyList<A> =
  NonEmptyList(head, t.asList())

inline fun <A> A.nel(): NonEmptyList<A> =
  nonEmptyListOf(this)

fun <A, G> NonEmptyListOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, NonEmptyList<A>> =
  fix().traverse(GA, ::identity)

@Deprecated(
  "Kind is deprecated, and will be removed in 0.13.0. Please the plus method defined for NonEmptyList instead",
  ReplaceWith(
    "fix().plus(y.fix())",
    "arrow.core.fix",
    "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyListOf<A>.combineK(y: NonEmptyListOf<A>): NonEmptyList<A> =
  fix().plus(y.fix())

operator fun <A : Comparable<A>> NonEmptyList<A>.compareTo(other: NonEmptyList<A>): Int =
  all.compareTo(other.all)

fun <A> NonEmptyList<NonEmptyList<A>>.flatten(): NonEmptyList<A> =
  this.flatMap(::identity)

fun <A, B> NonEmptyList<Either<A, B>>.selectM(f: NonEmptyList<(A) -> B>): NonEmptyList<B> =
  this.flatMap { it.fold({ a -> f.map { ff -> ff(a) } }, { b -> NonEmptyList.just(b) }) }

fun <A, B> NonEmptyList<Pair<A, B>>.unzip(): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  this.unzip(::identity)

fun <A, B, C> NonEmptyList<C>.unzip(f: (C) -> Pair<A, B>): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  this.map(f).let { nel ->
    nel.tail.unzip().let {
      NonEmptyList(nel.head.first, it.first) to
        NonEmptyList(nel.head.second, it.second)
    }
  }

inline fun <E, A, B> NonEmptyList<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, NonEmptyList<B>> =
  foldRight(f(head).map { NonEmptyList.just(it) }) { a, acc ->
    f(a).ap(acc.map { bs -> { b: B -> NonEmptyList(b) + bs } })
  }

fun <E, A> NonEmptyList<Either<E, A>>.sequenceEither(): Either<E, NonEmptyList<A>> =
  traverseEither(::identity)

inline fun <E, A, B> NonEmptyList<A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, NonEmptyList<B>> =
  foldRight(f(head).map { NonEmptyList(it) }) { a, acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: B -> NonEmptyList(b) + bs } })
  }

fun <E, A> NonEmptyList<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, NonEmptyList<A>> =
  traverseValidated(semigroup, ::identity)

@Suppress("UNCHECKED_CAST")
fun <A> Semigroup.Companion.nonEmptyList(): Semigroup<NonEmptyList<A>> =
  NonEmptyListSemigroup as Semigroup<NonEmptyList<A>>

object NonEmptyListSemigroup : Semigroup<NonEmptyList<Any?>> {
  override fun NonEmptyList<Any?>.combine(b: NonEmptyList<Any?>): NonEmptyList<Any?> =
    NonEmptyList(this.head, this.tail.plus(b))
}

fun <A, B> NonEmptyList<A>.zip(fb: NonEmptyList<B>): NonEmptyList<Pair<A, B>> =
  zip(fb, ::Pair)

inline fun <A, B, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  map: (A, B) -> Z
): NonEmptyList<Z> =
  zip(
    b,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit
  ) { aa, bb, _, _, _, _, _, _, _, _ ->
    map(aa, bb)
  }

inline fun <A, B, C, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  map: (A, B, C) -> Z
): NonEmptyList<Z> =
  zip(
    b, c,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit
  ) { aa, bb, cc, _, _, _, _, _, _, _ ->
    map(aa, bb, cc)
  }

inline fun <A, B, C, D, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  d: NonEmptyList<D>,
  map: (A, B, C, D) -> Z
): NonEmptyList<Z> =
  zip(
    b, c, d,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit
  ) { aa, bb, cc, dd, _, _, _, _, _, _ ->
    map(aa, bb, cc, dd)
  }

inline fun <A, B, C, D, E, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  d: NonEmptyList<D>,
  e: NonEmptyList<E>,
  map: (A, B, C, D, E) -> Z
): NonEmptyList<Z> =
  zip(
    b, c, d, e,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit
  ) { aa, bb, cc, dd, ee, _, _, _, _, _ ->
    map(aa, bb, cc, dd, ee)
  }

inline fun <A, B, C, D, E, F, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  d: NonEmptyList<D>,
  e: NonEmptyList<E>,
  f: NonEmptyList<F>,
  map: (A, B, C, D, E, F) -> Z
): NonEmptyList<Z> =
  zip(
    b, c, d, e, f,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit
  ) { aa, bb, cc, dd, ee, ff, _, _, _, _ ->
    map(aa, bb, cc, dd, ee, ff)
  }

inline fun <A, B, C, D, E, F, G, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  d: NonEmptyList<D>,
  e: NonEmptyList<E>,
  f: NonEmptyList<F>,
  g: NonEmptyList<G>,
  map: (A, B, C, D, E, F, G) -> Z
): NonEmptyList<Z> =
  zip(
    b, c, d, e, f, g,
    NonEmptyList.unit,
    NonEmptyList.unit,
    NonEmptyList.unit
  ) { aa, bb, cc, dd, ee, ff, gg, _, _, _ ->
    map(aa, bb, cc, dd, ee, ff, gg)
  }

inline fun <A, B, C, D, E, F, G, H, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  d: NonEmptyList<D>,
  e: NonEmptyList<E>,
  f: NonEmptyList<F>,
  g: NonEmptyList<G>,
  h: NonEmptyList<H>,
  map: (A, B, C, D, E, F, G, H) -> Z
): NonEmptyList<Z> =
  zip(
    b, c, d, e, f, g, h,
    NonEmptyList.unit, NonEmptyList.unit
  ) { aa, bb, cc, dd, ee, ff, gg, hh, _, _ ->
    map(aa, bb, cc, dd, ee, ff, gg, hh)
  }

inline fun <A, B, C, D, E, F, G, H, I, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  d: NonEmptyList<D>,
  e: NonEmptyList<E>,
  f: NonEmptyList<F>,
  g: NonEmptyList<G>,
  h: NonEmptyList<H>,
  i: NonEmptyList<I>,
  map: (A, B, C, D, E, F, G, H, I) -> Z
): NonEmptyList<Z> =
  zip(
    b, c, d, e, f, g, h, i,
    NonEmptyList.unit
  ) { aa, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
    map(aa, bb, cc, dd, ee, ff, gg, hh, ii)
  }

inline fun <A, B, C, D, E, F, G, H, I, J, Z> NonEmptyList<A>.zip(
  b: NonEmptyList<B>,
  c: NonEmptyList<C>,
  d: NonEmptyList<D>,
  e: NonEmptyList<E>,
  f: NonEmptyList<F>,
  g: NonEmptyList<G>,
  h: NonEmptyList<H>,
  i: NonEmptyList<I>,
  j: NonEmptyList<J>,
  map: (A, B, C, D, E, F, G, H, I, J) -> Z
): NonEmptyList<Z> =
  NonEmptyList(
    map(head, b.head, c.head, d.head, e.head, f.head, g.head, h.head, i.head, j.head),
    tail.flatMap { aa ->
      b.tail.flatMap { bb ->
        c.tail.flatMap { cc ->
          d.tail.flatMap { dd ->
            e.tail.flatMap { ee ->
              f.tail.flatMap { ff ->
                g.tail.flatMap { gg ->
                  h.tail.flatMap { hh ->
                    i.tail.flatMap { ii ->
                      j.tail.map { jj ->
                        map(aa, bb, cc, dd, ee, ff, gg, hh, ii, jj)
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
