@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class, ExperimentalStdlibApi::class)
@file:Suppress("API_NOT_AVAILABLE", "RESERVED_MEMBER_INSIDE_VALUE_CLASS")

package arrow.core

import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.core.Ior.Both
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.withError
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmExposeBoxed
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
@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
@JvmInline
public value class NonEmptyList<out E> @PotentiallyUnsafeNonEmptyOperation @PublishedApi internal constructor(
  public val all: List<E>
) : List<E> by all, NonEmptyCollection<E> {
  public constructor(head: E, tail: List<E>): this(buildNonEmptyList<E, _>(tail.size + 1) {
    add(head)
    addAll(tail)
    this
  }.all)

  override fun equals(other: Any?): Boolean = when (other) {
    is NonEmptyList<*> -> this.all == other.all
    else -> this.all == other
  }

  override fun hashCode(): Int = all.hashCode()

  override fun isEmpty(): Boolean = false

  @JvmExposeBoxed @Suppress("USELESS_JVM_EXPOSE_BOXED")
  public fun toList(): List<E> = all

  public val tail: List<E>
    get() = all.subList(1, all.size)

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <K> distinctBy(selector: (E) -> K): NonEmptyList<E> = buildNonEmptyList(size) {
    add(head) // head is always distinct
    val seen = hashSetOf<K>()
    var isFirst = true
    for (e in all) {
      if (seen.add(selector(e)) && !isFirst) add(e)
      isFirst = false
    }
    this
  }

  // These can be simplified by introducing some forEachNonEmpty extension, but we'd need
  // "KT-83404 Smart casts don't propagate past scoping functions" fixed first.
  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <T> map(transform: (E) -> T): NonEmptyList<T> = buildNonEmptyList(size) {
    val iterator = all.iterator()
    do add(transform(iterator.next())) while (iterator.hasNext())
    this
  }

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <T> flatMap(transform: (E) -> NonEmptyCollection<T>): NonEmptyList<T> = buildNonEmptyList(size) {
    val iterator = all.iterator()
    do addAll(transform(iterator.next())) while (iterator.hasNext())
    this
  }

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <T> mapIndexed(transform: (index: Int, E) -> T): NonEmptyList<T> = buildNonEmptyList(size) {
    val iterator = all.iterator()
    var index = 0
    do add(transform(index++, iterator.next())) while (iterator.hasNext())
    this
  }

  public operator fun plus(l: NonEmptyList<@UnsafeVariance E>): NonEmptyList<E> =
    this + l.all

  override fun plus(elements: Iterable<@UnsafeVariance E>): NonEmptyList<E> = buildNonEmptyList(size + elements.collectionSizeOrDefault(10)) {
    addAll(this@NonEmptyList)
    addAll(elements)
    this
  }

  override fun plus(element: @UnsafeVariance E): NonEmptyList<E> = buildNonEmptyList(size + 1) {
    addAll(this@NonEmptyList)
    add(element)
    this
  }

  public inline fun <Acc> foldLeft(b: Acc, f: (Acc, E) -> Acc): Acc {
    contract { callsInPlace(f, InvocationKind.AT_LEAST_ONCE) }
    var accumulator = b
    val iterator = iterator()
    do accumulator = f(accumulator, iterator.next()) while (iterator.hasNext())
    return accumulator
  }

  public inline fun <T> coflatMap(f: (NonEmptyList<E>) -> T): NonEmptyList<T> {
    contract { callsInPlace(f, InvocationKind.AT_LEAST_ONCE) }
    var current = this
    return buildNonEmptyList(all.size + 1) {
      var i = 0
      do {
        add(f(current))
        current = all.subList(++i, all.size).wrapAsNonEmptyListOrNull() ?: break
      } while (true)
      this
    }
  }

  public fun extract(): E =
    this.head

  override fun toString(): String = all.toString()

  public fun <T> align(other: NonEmptyList<T>): NonEmptyList<Ior<E, T>> =
    padZip(other, ::Left, ::Right, ::Both)

  public fun <T> padZip(other: NonEmptyList<T>): NonEmptyList<Pair<E?, T?>> =
    padZip(other, { it to null }, { null to it }, { a, b -> a to b })

  public inline fun <B, C> padZip(other: NonEmptyList<B>, left: (E) -> C, right: (B) -> C, both: (E, B) -> C): NonEmptyList<C> {
    contract { callsInPlace(both, InvocationKind.AT_LEAST_ONCE) }
    val first = iterator()
    val second = other.iterator()
    return buildNonEmptyList(maxOf(size, other.size)) {
      do add(both(first.next(), second.next())) while (first.hasNext() && second.hasNext())
      while (first.hasNext()) add(left(first.next()))
      while (second.hasNext()) add(right(second.next()))
      this
    }
  }

  public companion object {
    @PublishedApi
    internal val unit: NonEmptyList<Unit> =
      nonEmptyListOf(Unit)

    @JvmStatic @JvmExposeBoxed
    public fun <E> of(head: E, vararg t: E): NonEmptyList<E> =
      nonEmptyListOf(head, *t)

    @JvmStatic @JvmExposeBoxed
    public fun <E> of(values: Iterable<E>): NonEmptyList<E> =
      values.toNonEmptyListOrThrow()
  }

  public fun <T> zip(other: NonEmptyList<T>): NonEmptyList<Pair<E, T>> =
    zip(other, ::Pair)

  public inline fun <B, Z> zip(
    b: NonEmptyList<B>,
    map: (E, B) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    val aa = iterator()
    val bb = b.iterator()
    return buildNonEmptyList(minOf(this.size, b.size)) {
      do add(map(aa.next(), bb.next()))
      while (aa.hasNext() && bb.hasNext())
      this
    }
  }

  public inline fun <B, C, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    map: (E, B, C) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size)) {
      do add(map(aa.next(), bb.next(), cc.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext())
      this
    }
  }

  public inline fun <B, C, D, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    map: (E, B, C, D) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    val dd = d.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size, d.size)) {
      do add(map(aa.next(), bb.next(), cc.next(), dd.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext() && dd.hasNext())
      this
    }
  }

  public inline fun <B, C, D, F, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<F>,
    map: (E, B, C, D, F) -> Z
  ): NonEmptyList<Z> {
    contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    val dd = d.iterator()
    val ee = e.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size, d.size, e.size)) {
      do add(map(aa.next(), bb.next(), cc.next(), dd.next(), ee.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext())
      this
    }
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
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    val dd = d.iterator()
    val ee = e.iterator()
    val ff = f.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size, d.size, e.size, f.size)) {
      do add(map(aa.next(), bb.next(), cc.next(), dd.next(), ee.next(), ff.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext())
      this
    }
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
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    val dd = d.iterator()
    val ee = e.iterator()
    val ff = f.iterator()
    val gg = g.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size, d.size, e.size, f.size, g.size)) {
      do add(map(aa.next(), bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext())
      this
    }
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
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    val dd = d.iterator()
    val ee = e.iterator()
    val ff = f.iterator()
    val gg = g.iterator()
    val hh = h.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size, d.size, e.size, f.size, g.size, h.size)) {
      do add(map(aa.next(), bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next(), hh.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext())
      this
    }
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
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    val dd = d.iterator()
    val ee = e.iterator()
    val ff = f.iterator()
    val gg = g.iterator()
    val hh = h.iterator()
    val ii = i.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size, d.size, e.size, f.size, g.size, h.size, i.size)) {
      do add(map(aa.next(), bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next(), hh.next(), ii.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext())
      this
    }
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
    val aa = iterator()
    val bb = b.iterator()
    val cc = c.iterator()
    val dd = d.iterator()
    val ee = e.iterator()
    val ff = f.iterator()
    val gg = g.iterator()
    val hh = h.iterator()
    val ii = i.iterator()
    val jj = j.iterator()
    return buildNonEmptyList(minOf(this.size, b.size, c.size, d.size, e.size, f.size, g.size, h.size, i.size, j.size)) {
      do add(map(aa.next(), bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next(), hh.next(), ii.next(), jj.next()))
      while (aa.hasNext() && bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext() && jj.hasNext())
      this
    }
  }
}

@JvmName("nonEmptyListOf")
public fun <E> nonEmptyListOf(head: E, vararg t: E): NonEmptyList<E> = NonEmptyList(head, t.asList())

@JvmName("nel")
@Suppress("NOTHING_TO_INLINE")
public inline fun <E> E.nel(): NonEmptyList<E> = buildNonEmptyList(1) {
  add(this@nel)
  this
}

public operator fun <E : Comparable<E>> NonEmptyList<E>.compareTo(other: NonEmptyList<E>): Int =
  all.compareTo(other.all)

public fun <E> NonEmptyList<NonEmptyList<E>>.flatten(): NonEmptyList<E> =
  this.flatMap(::identity)

public inline fun <E, T : Comparable<T>> NonEmptyList<E>.minBy(selector: (E) -> T): E =
  all.minBy(selector)

public inline fun <E, T : Comparable<T>> NonEmptyList<E>.maxBy(selector: (E) -> T): E =
  all.maxBy(selector)

@Suppress("NOTHING_TO_INLINE")
public inline fun <E : Comparable<E>> NonEmptyList<E>.min(): E =
  all.min()

@Suppress("NOTHING_TO_INLINE")
public inline fun <E : Comparable<E>> NonEmptyList<E>.max(): E =
  all.max()

public fun <A, B> NonEmptyList<Pair<A, B>>.unzip(): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  this.unzip(::identity)

public inline fun <A, B, E> NonEmptyList<E>.unzip(f: (E) -> Pair<A, B>): Pair<NonEmptyList<A>, NonEmptyList<B>> {
  contract { callsInPlace(f, InvocationKind.AT_LEAST_ONCE) }
  val size = size
  val listA = MonotoneMutableList<A>(size)
  val listB = MonotoneMutableList<B>(size)
  val iterator = iterator()
  do {
    val (a, b) = f(iterator.next())
    listA.add(a)
    listB.add(b)
  } while (iterator.hasNext())
  return listA.asNonEmptyList() to listB.asNonEmptyList()
}

public inline fun <Error, E, T> NonEmptyList<E>.mapOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<Error, NonEmptyList<T>> = either {
  withError({ it.reduce(combine) }) {
    mapOrAccumulate(this@mapOrAccumulate, transform)
  }
}

public inline fun <Error, E, T> NonEmptyList<E>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<NonEmptyList<Error>, NonEmptyList<T>> = either {
  mapOrAccumulate(this@mapOrAccumulate, transform)
}

/**
 * Returns a [NonEmptyList] that contains a **copy** of the elements in [this].
 */
@JvmName("toNonEmptyListOrNull")
public fun <T> Iterable<T>.toNonEmptyListOrNull(): NonEmptyList<T>? = MonotoneMutableList<T>(collectionSizeOrDefault(10)).run {
  addAll(this@toNonEmptyListOrNull)
  if (isNonEmpty()) asNonEmptyList() else null
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
public fun <T> Iterable<T>.toNonEmptyListOrThrow(): NonEmptyList<T> = toNonEmptyListOrNull() ?: throw IllegalArgumentException("Cannot create NonEmptyList from empty Iterable")

/**
 * Returns a [NonEmptyList] that wraps the given [this], avoiding an additional copy.
 *
 * Any modification made to [this] will also be visible through the returned [NonEmptyList].
 * You are responsible for keeping the non-emptiness invariant at all times.
 */
@PotentiallyUnsafeNonEmptyOperation
public fun <T> List<T>.wrapAsNonEmptyListOrThrow(): NonEmptyList<T> = wrapAsNonEmptyListOrNull() ?: throw IllegalArgumentException("Cannot wrap an empty list as NonEmptyList")
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
