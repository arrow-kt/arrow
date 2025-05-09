@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core

import arrow.core.raise.RaiseAccumulate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.collections.unzip as stdlibUnzip

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
public value class NonEmptyList<out E> @PublishedApi internal constructor(
  public val all: List<E>
) : List<E> by all, NonEmptyCollection<E> {

  public constructor(head: E, tail: List<E>): this(listOf(head) + tail)

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun equals(other: Any?): Boolean = when (other) {
    is NonEmptyList<*> -> this.all == other.all
    else -> this.all == other
  }

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun hashCode(): Int = all.hashCode()

  override fun isEmpty(): Boolean = false

  public fun toList(): List<E> = all

  public override val head: E
    get() = all.first()

  public val tail: List<E>
    get() = all.drop(1)

  override fun lastOrNull(): E = when {
    tail.isNotEmpty() -> tail.last()
    else -> head
  }

  @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
  public override inline fun distinct(): NonEmptyList<E> =
    NonEmptyList(all.distinct())

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <K> distinctBy(selector: (E) -> K): NonEmptyList<E> =
    NonEmptyList(all.distinctBy(selector))

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <T> map(transform: (E) -> T): NonEmptyList<T> =
    NonEmptyList(all.map(transform))

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <T> flatMap(transform: (E) -> NonEmptyCollection<T>): NonEmptyList<T> =
    NonEmptyList(all.flatMap(transform))

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <T> mapIndexed(transform: (index: Int, E) -> T): NonEmptyList<T> =
    NonEmptyList(transform(0, head), tail.mapIndexed { ix, e -> transform(ix + 1, e) })

  public operator fun plus(l: NonEmptyList<@UnsafeVariance E>): NonEmptyList<E> =
    this + l.all

  public override operator fun plus(elements: Iterable<@UnsafeVariance E>): NonEmptyList<E> =
    NonEmptyList(all + elements)

  public override operator fun plus(element: @UnsafeVariance E): NonEmptyList<E> =
    NonEmptyList(all + element)

  public inline fun <Acc> foldLeft(b: Acc, f: (Acc, E) -> Acc): Acc {
    contract { callsInPlace(f, InvocationKind.AT_LEAST_ONCE) }
    var accumulator = f(b, head)
    for (element in tail) accumulator = f(accumulator, element)
    return accumulator
  }

  public inline fun <T> coflatMap(f: (NonEmptyList<E>) -> T): NonEmptyList<T> {
    contract { callsInPlace(f, InvocationKind.AT_LEAST_ONCE) }
    var current = this
    return buildList {
      do {
        add(f(current))
        current = current.drop(1).toNonEmptyListOrNull() ?: break
      } while (true)
    }.let(::NonEmptyList)
  }

  public fun extract(): E =
    this.head

  override fun toString(): String =
    "NonEmptyList(${all.joinToString()})"

  public fun <T> align(other: NonEmptyList<T>): NonEmptyList<Ior<E, T>> =
    NonEmptyList(all.align(other))

  public fun <T> padZip(other: NonEmptyList<T>): NonEmptyList<Pair<E?, T?>> =
    padZip(other, { it to null }, { null to it }, { a, b -> a to b })

  public inline fun <B, C> padZip(other: NonEmptyList<B>, left: (E) -> C, right: (B) -> C, both: (E, B) -> C): NonEmptyList<C> {
    contract { callsInPlace(both, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(both(head, other.head), tail.padZip(other.tail, left, right) { a, b -> both(a, b) })
  }

  public companion object {
    @PublishedApi
    internal val unit: NonEmptyList<Unit> =
      nonEmptyListOf(Unit)
  }

  public fun <T> zip(other: NonEmptyList<T>): NonEmptyList<Pair<E, T>> =
    zip(other, ::Pair)

  public inline fun <B, Z> zip(
    b: NonEmptyList<B>,
    map: (E, B) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head), tail.zip(b.tail) { a, bb -> map(a, bb) })
  }

  public inline fun <B, C, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    map: (E, B, C) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head), tail.zip(b.tail, c.tail) { a, bb, cc -> map(a, bb, cc) })
  }

  public inline fun <B, C, D, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    map: (E, B, C, D) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head, d.head), tail.zip(b.tail, c.tail, d.tail) { a, bb, cc, dd -> map(a, bb, cc, dd) })
  }

  public inline fun <B, C, D, F, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<F>,
    map: (E, B, C, D, F) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head, d.head, e.head), tail.zip(b.tail, c.tail, d.tail, e.tail) { a, bb, cc, dd, ee -> map(a, bb, cc, dd, ee) })
  }

  public inline fun <B, C, D, F, G, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<F>,
    f: NonEmptyList<G>,
    map: (E, B, C, D, F, G) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head, d.head, e.head, f.head), tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail) { a, bb, cc, dd, ee, ff -> map(a, bb, cc, dd, ee, ff) })
  }

  public inline fun <B, C, D, F, G, H, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<F>,
    f: NonEmptyList<G>,
    g: NonEmptyList<H>,
    map: (E, B, C, D, F, G, H) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head, d.head, e.head, f.head, g.head), tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail) { a, bb, cc, dd, ee, ff, gg -> map(a, bb, cc, dd, ee, ff, gg) })
  }

  public inline fun <B, C, D, F, G, H, I, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<F>,
    f: NonEmptyList<G>,
    g: NonEmptyList<H>,
    h: NonEmptyList<I>,
    map: (E, B, C, D, F, G, H, I) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head, d.head, e.head, f.head, g.head, h.head), tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail, h.tail) { a, bb, cc, dd, ee, ff, gg, hh -> map(a, bb, cc, dd, ee, ff, gg, hh) })
  }

  public inline fun <B, C, D, F, G, H, I, J, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<F>,
    f: NonEmptyList<G>,
    g: NonEmptyList<H>,
    h: NonEmptyList<I>,
    i: NonEmptyList<J>,
    map: (E, B, C, D, F, G, H, I, J) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head, d.head, e.head, f.head, g.head, h.head, i.head), tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail, h.tail, i.tail) { a, bb, cc, dd, ee, ff, gg, hh, ii -> map(a, bb, cc, dd, ee, ff, gg, hh, ii) })
  }

  public inline fun <B, C, D, F, G, H, I, J, K, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<F>,
    f: NonEmptyList<G>,
    g: NonEmptyList<H>,
    h: NonEmptyList<I>,
    i: NonEmptyList<J>,
    j: NonEmptyList<K>,
    map: (E, B, C, D, F, G, H, I, J, K) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    return NonEmptyList(map(head, b.head, c.head, d.head, e.head, f.head, g.head, h.head, i.head, j.head), tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail, h.tail, i.tail, j.tail) { a, bb, cc, dd, ee, ff, gg, hh, ii, jj -> map(a, bb, cc, dd, ee, ff, gg, hh, ii, jj) })
  }
}

@JvmName("nonEmptyListOf")
public fun <E> nonEmptyListOf(head: E, vararg t: E): NonEmptyList<E> =
  NonEmptyList(listOf(head) + t)

@JvmName("nel")
@Suppress("NOTHING_TO_INLINE")
public inline fun <E> E.nel(): NonEmptyList<E> =
  NonEmptyList(listOf(this))

public operator fun <E : Comparable<E>> NonEmptyList<E>.compareTo(other: NonEmptyList<E>): Int =
  all.compareTo(other.all)

public fun <E> NonEmptyList<NonEmptyList<E>>.flatten(): NonEmptyList<E> =
  this.flatMap(::identity)

public inline fun <E, T : Comparable<T>> NonEmptyList<E>.minBy(selector: (E) -> T): E =
  minByOrNull(selector)!!

public inline fun <E, T : Comparable<T>> NonEmptyList<E>.maxBy(selector: (E) -> T): E =
  maxByOrNull(selector)!!

@Suppress("NOTHING_TO_INLINE")
public inline fun <E : Comparable<E>> NonEmptyList<E>.min(): E =
  minOrNull()!!

@Suppress("NOTHING_TO_INLINE")
public inline fun <E : Comparable<E>> NonEmptyList<E>.max(): E =
  maxOrNull()!!

public fun <A, B> NonEmptyList<Pair<A, B>>.unzip(): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  this.unzip(::identity)

@Suppress("WRONG_INVOCATION_KIND")
public inline fun <A, B, E> NonEmptyList<E>.unzip(f: (E) -> Pair<A, B>): Pair<NonEmptyList<A>, NonEmptyList<B>> {
  contract { callsInPlace(f, InvocationKind.AT_LEAST_ONCE) }
  return map { f(it) }.stdlibUnzip().let { (l1, l2) ->
    l1.toNonEmptyListOrNull()!! to l2.toNonEmptyListOrNull()!!
  }
}

public inline fun <Error, E, T> NonEmptyList<E>.mapOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<Error, NonEmptyList<T>> =
  all.mapOrAccumulate(combine, transform).map { requireNotNull(it.toNonEmptyListOrNull()) }

public inline fun <Error, E, T> NonEmptyList<E>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<NonEmptyList<Error>, NonEmptyList<T>> =
  all.mapOrAccumulate(transform).map { requireNotNull(it.toNonEmptyListOrNull()) }

/**
 * Returns a [NonEmptyList] that contains a **copy** of the elements in [this].
 */
@JvmName("toNonEmptyListOrNull")
public fun <T> Iterable<T>.toNonEmptyListOrNull(): NonEmptyList<T>? {
  val iter = iterator()
  if (!iter.hasNext()) return null
  return NonEmptyList(Iterable { iter }.toList())
}

/**
 * Returns a [NonEmptyList] that contains a **copy** of the elements in [this].
 */
@JvmName("toNonEmptyListOrNone")
public fun <T> Iterable<T>.toNonEmptyListOrNone(): Option<NonEmptyList<T>> =
  toNonEmptyListOrNull().toOption()

/**
 * Returns a [NonEmptyList] that contains a **copy** of the elements in [this].
 */
@JvmName("toNonEmptyListOrThrow")
public fun <T> Iterable<T>.toNonEmptyListOrThrow(): NonEmptyList<T> {
  val iter = iterator()
  require(iter.hasNext())
  return NonEmptyList(Iterable { iter }.toList())
}

/**
 * Returns a [NonEmptyList] that wraps the given [this], avoiding an additional copy.
 *
 * Any modification made to [this] will also be visible through the returned [NonEmptyList].
 * You are responsible for keeping the non-emptiness invariant at all times.
 */
@PotentiallyUnsafeNonEmptyOperation
public fun <T> List<T>.wrapAsNonEmptyListOrThrow(): NonEmptyList<T> {
  require(isNotEmpty())
  return NonEmptyList(this)
}

/**
 * Returns a [NonEmptyList] that wraps the given [this], avoiding an additional copy.
 *
 * Any modification made to [this] will also be visible through the returned [NonEmptyList].
 * You are responsible for keeping the non-emptiness invariant at all times.
 */
@PotentiallyUnsafeNonEmptyOperation
public fun <T> List<T>.wrapAsNonEmptyListOrNull(): NonEmptyList<T>? = when {
  isEmpty() -> null
  else -> NonEmptyList(this)
}
