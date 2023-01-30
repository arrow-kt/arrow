package arrow.core

import arrow.typeclasses.Semigroup
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

public typealias Nel<A> = NonEmptyList<A>

/**
 * `NonEmptyList` is a data type used in __Λrrow__ to model ordered lists that guarantee to have at least one value.
 *
 * ## Constructing NonEmptyList
 *
 * A `NonEmptyList` guarantees the list always has at least 1 element.
 *
 * ```kotlin
 * import arrow.core.nonEmptyListOf
 * import arrow.core.toNonEmptyListOrNull
 *
 * fun main() {
 *  println(nonEmptyListOf(1, 2, 3, 4, 5))
 *  println(listOf(1, 2, 3).toNonEmptyListOrNull())
 *  println(emptyList<Int>().toNonEmptyListOrNull())
 * }
 * ```
 * <!--- KNIT example-nonemptylist-01.kt -->
 * ```text
 * NonEmptyList(1, 2, 3, 4, 5)
 * NonEmptyList(1, 2, 3)
 * null
 * ```
 *
 * ## head
 *
 * Unlike `List[0]`, `NonEmptyList.head` it's a safe operation that guarantees no exception throwing.
 *
 * ```kotlin
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
 * <!--- KNIT example-nonemptylist-02.kt -->
 *
 * ## foldLeft
 *
 * When we fold over a `NonEmptyList`, we turn a `NonEmptyList< A >` into `B` by providing a __seed__ value and a __function__ that carries the state on each iteration over the elements of the list.
 * The first argument is a function that addresses the __seed value__, this can be any object of any type which will then become the resulting typed value.
 * The second argument is a function that takes the current state and element in the iteration and returns the new state after transformations have been applied.
 *
 * ```kotlin
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
 * <!--- KNIT example-nonemptylist-03.kt -->
 *
 * ## map
 *
 * `map` allows us to transform `A` into `B` in `NonEmptyList< A >`
 *
 * ```kotlin
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
 * <!--- KNIT example-nonemptylist-04.kt -->
 *
 * ## Combining NonEmptyLists
 *
 * ### flatMap
 *
 * `flatMap` allows us to compute over the contents of multiple `NonEmptyList< * >` values
 *
 * ```kotlin
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
 * <!--- KNIT example-nonemptylist-05.kt -->
 *
 * ### zip
 *
 * Λrrow contains methods that allow you to preserve type information when computing over different `NonEmptyList` typed values.
 *
 * ```kotlin
 * import arrow.core.NonEmptyList
 * import arrow.core.nonEmptyListOf
 * import kotlin.random.Random
 *
 * data class Person(val id: Long, val name: String, val year: Int)
 *
 * // Note each NonEmptyList is of a different type
 * val nelId: NonEmptyList<Long> = nonEmptyListOf(Random.nextLong(), Random.nextLong())
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
 * <!--- KNIT example-nonemptylist-06.kt -->
 *
 * ### Summary
 *
 * - `NonEmptyList` is __used to model lists that guarantee at least one element__
 * - We can easily construct values of `NonEmptyList` with `nonEmptyListOf`
 * - `foldLeft`, `map`, `flatMap` and others are used to compute over the internal contents of a `NonEmptyList` value.
 * - `a.zip(b, c) { ... }` can be used to compute over multiple `NonEmptyList` values preserving type information and __abstracting over arity__ with `zip`
 *
 */
@JvmInline
public value class NonEmptyList<out A> @PublishedApi internal constructor(
  public val all: List<A>
) : List<A> by all {

  public constructor(head: A, tail: List<A>): this(listOf(head) + tail)

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun equals(other: Any?): Boolean = when (other) {
    is NonEmptyList<*> -> this.all == other.all
    else -> this.all == other
  }

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun hashCode(): Int = all.hashCode()

  override fun isEmpty(): Boolean = false

  public fun toList(): List<A> = all

  public val head: A
    get() = all.first()

  public val tail: List<A>
    get() = all.drop(1)

  public inline fun <B> map(f: (A) -> B): NonEmptyList<B> =
    NonEmptyList(all.map(f))

  public inline fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> =
    NonEmptyList(all.flatMap { f(it).all })

  public operator fun plus(l: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l.all)

  public operator fun plus(l: List<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l)

  public operator fun plus(a: @UnsafeVariance A): NonEmptyList<A> =
    NonEmptyList(all + a)

  public inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    all.fold(b, f)

  public fun <B> coflatMap(f: (NonEmptyList<A>) -> B): NonEmptyList<B> =
    buildList {
      var current = all
      while (current.isNotEmpty()) {
        add(f(NonEmptyList(current)))
        current = current.drop(1)
      }
    }.let(::NonEmptyList)

  public fun extract(): A =
    this.head

  override fun toString(): String =
    "NonEmptyList(${all.joinToString()})"

  public fun <B> align(b: NonEmptyList<B>): NonEmptyList<Ior<A, B>> =
    NonEmptyList(all.align(b))

  public fun salign(SA: Semigroup<@UnsafeVariance A>, b: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all.salign(SA, b).toList())

  public fun <B> padZip(other: NonEmptyList<B>): NonEmptyList<Pair<A?, B?>> =
    NonEmptyList(all.padZip(other))

  public companion object {

    @JvmStatic @JvmName("of")
    public fun <A> of(first: A, vararg tail: A): NonEmptyList<A> =
      NonEmptyList(listOf(first) + tail)

    @JvmStatic @JvmName("ofOrNull")
    public fun <A> ofOrNull(list: Iterable<A>): NonEmptyList<A>? =
      list.firstOrNull()?.let { NonEmptyList(list.toList()) }

    @Deprecated(
      "Use toNonEmptyListOrNull instead",
      ReplaceWith(
        "l.toNonEmptyListOrNull().toOption()",
        "import arrow.core.toNonEmptyListOrNull",
        "import arrow.core.toOption"
      )
    )
    @JvmStatic @JvmName("fromList")
    public fun <A> fromList(l: List<A>): Option<NonEmptyList<A>> =
      if (l.isEmpty()) None else Some(NonEmptyList(l))

    @Deprecated(
      "Use toNonEmptyListOrNull instead",
      ReplaceWith(
        "l.toNonEmptyListOrNull() ?: throw IndexOutOfBoundsException(\"Empty list doesn't contain element at index 0.\")",
        "import arrow.core.toNonEmptyListOrNull"
      )
    )
    @JvmStatic @JvmName("fromListUnsafe")
    public inline fun <A> fromListUnsafe(l: List<A>): NonEmptyList<A> =
      NonEmptyList(l)

    @PublishedApi
    internal val unit: NonEmptyList<Unit> =
      nonEmptyListOf(Unit)
  }

  public fun <B> zip(fb: NonEmptyList<B>): NonEmptyList<Pair<A, B>> =
    zip(fb, ::Pair)

  public inline fun <B, Z> zip(
    b: NonEmptyList<B>,
    map: (A, B) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(all.zip(b.all, map))

  public inline fun <B, C, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    map: (A, B, C) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(all.zip(b.all, c.all, map))

  public inline fun <B, C, D, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    map: (A, B, C, D) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(all.zip(b.all, c.all, d.all, map))

  public inline fun <B, C, D, E, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<E>,
    map: (A, B, C, D, E) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(all.zip(b.all, c.all, d.all, e.all, map))

  public inline fun <B, C, D, E, F, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<E>,
    f: NonEmptyList<F>,
    map: (A, B, C, D, E, F) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(all.zip(b.all, c.all, d.all, e.all, f.all, map))

  public inline fun <B, C, D, E, F, G, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<E>,
    f: NonEmptyList<F>,
    g: NonEmptyList<G>,
    map: (A, B, C, D, E, F, G) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(all.zip(b.all, c.all, d.all, e.all, f.all, g.all, map))

  public inline fun <B, C, D, E, F, G, H, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<E>,
    f: NonEmptyList<F>,
    g: NonEmptyList<G>,
    h: NonEmptyList<H>,
    map: (A, B, C, D, E, F, G, H) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, map))

  public inline fun <B, C, D, E, F, G, H, I, Z> zip(
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
    NonEmptyList(all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, map))

  public inline fun <B, C, D, E, F, G, H, I, J, Z> zip(
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
    NonEmptyList(all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, j.all, map))
}

@JvmName("nonEmptyListOf")
public fun <A> nonEmptyListOf(head: A, vararg t: A): NonEmptyList<A> =
  NonEmptyList(listOf(head) + t)

@JvmName("nel")
public inline fun <A> A.nel(): NonEmptyList<A> =
  NonEmptyList(listOf(this))

public operator fun <A : Comparable<A>> NonEmptyList<A>.compareTo(other: NonEmptyList<A>): Int =
  all.compareTo(other.all)

public fun <A> NonEmptyList<NonEmptyList<A>>.flatten(): NonEmptyList<A> =
  this.flatMap(::identity)

public inline fun <A, B : Comparable<B>> NonEmptyList<A>.minBy(selector: (A) -> B): A =
  minByOrNull(selector)!!

public inline fun <A, B : Comparable<B>> NonEmptyList<A>.maxBy(selector: (A) -> B): A =
  maxByOrNull(selector)!!

public inline fun <T : Comparable<T>> NonEmptyList<T>.min(): T =
  minOrNull()!!

public inline fun <T : Comparable<T>> NonEmptyList<T>.max(): T =
  maxOrNull()!!

public fun <A, B> NonEmptyList<Pair<A, B>>.unzip(): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  this.unzip(::identity)

public fun <A, B, C> NonEmptyList<C>.unzip(f: (C) -> Pair<A, B>): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  all.unzip(f).let { (a, b) -> NonEmptyList(a) to NonEmptyList(b) }

public fun <A> Iterable<A>.toNonEmptyListOrNull(): NonEmptyList<A>? =
  NonEmptyList.ofOrNull(this)

public fun <A> Iterable<A>.toNonEmptyListOrNone(): Option<NonEmptyList<A>> =
  toNonEmptyListOrNull().toOption()
