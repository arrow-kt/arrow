@file:OptIn(ExperimentalTypeInference::class)

package arrow.core

import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.either
import arrow.core.raise.option
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation
import arrow.typeclasses.combine
import kotlin.experimental.ExperimentalTypeInference
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
public class NonEmptyList<out A>(
  public override val head: A,
  public val tail: List<A>
) : AbstractList<A>(), NonEmptyCollection<A> {

  private constructor(list: List<A>) : this(list[0], list.drop(1))

  override val size: Int =
    1 + tail.size

  public val all: List<A>
    get() = toList()

  public override operator fun get(index: Int): A {
    if (index < 0 || index >= size) throw IndexOutOfBoundsException("$index is not in 1..${size - 1}")
    return if (index == 0) head else tail[index - 1]
  }

  override fun isEmpty(): Boolean = false

  public fun toList(): List<A> = listOf(head) + tail

  override fun lastOrNull(): A = when {
    tail.isNotEmpty() -> tail.last()
    else -> head
  }

  @Suppress("OVERRIDE_BY_INLINE")
  public override inline fun <B> map(transform: (A) -> B): NonEmptyList<B> =
    NonEmptyList(transform(head), tail.map(transform))

  override fun <B> flatMap(transform: (A) -> NonEmptyCollection<B>): NonEmptyList<B> =
    transform(head).toNonEmptyList() + tail.flatMap(transform)

  public operator fun plus(l: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    this + l.all

  public override operator fun plus(elements: Iterable<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + elements)

  public override operator fun plus(element: @UnsafeVariance A): NonEmptyList<A> =
    NonEmptyList(all + element)

  public inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    this.tail.fold(f(b, this.head), f)

  public fun <B> coflatMap(f: (NonEmptyList<A>) -> B): NonEmptyList<B> {
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

  public fun extract(): A =
    this.head

  override fun equals(other: Any?): Boolean =
    super.equals(other)

  override fun hashCode(): Int =
    super.hashCode()

  override fun toString(): String =
    "NonEmptyList(${all.joinToString()})"

  public fun <B> align(b: NonEmptyList<B>): NonEmptyList<Ior<A, B>> =
    NonEmptyList(Ior.Both(head, b.head), tail.align(b.tail))

  @Deprecated(SemigroupDeprecation, ReplaceWith("padZip(b, ::identity, ::identity, {a1, a2 -> a1 + a2})"))
  public fun salign(SA: Semigroup<@UnsafeVariance A>, b: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    padZip(b, ::identity, ::identity, SA::combine)

  public fun <B> padZip(other: NonEmptyList<B>): NonEmptyList<Pair<A?, B?>> =
    padZip(other, { it to null }, { null to it }, { a, b -> a to b })

  public inline fun <B, C> padZip(other: NonEmptyList<B>, left: (A) -> C, right: (B) -> C, both: (A, B) -> C): NonEmptyList<C> =
    NonEmptyList(both(head, other.head), tail.padZip(other.tail, left, right, both))

  public companion object {

    @Deprecated(
      "Use toNonEmptyListOrNull instead",
      ReplaceWith(
        "l.toNonEmptyListOrNull().toOption()",
        "import arrow.core.toNonEmptyListOrNull",
        "import arrow.core.toOption"
      )
    )
    @JvmStatic
    public fun <A> fromList(l: List<A>): Option<NonEmptyList<A>> =
      if (l.isEmpty()) None else Some(NonEmptyList(l))

    @Deprecated(
      "Use toNonEmptyListOrNull instead",
      ReplaceWith(
        "l.toNonEmptyListOrNull() ?: throw IndexOutOfBoundsException(\"Empty list doesn't contain element at index 0.\")",
        "import arrow.core.toNonEmptyListOrNull"
      )
    )
    @JvmStatic
    public fun <A> fromListUnsafe(l: List<A>): NonEmptyList<A> =
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
    NonEmptyList(
      map(head, b.head),
      tail.zip(b.tail, map)
    )

  public inline fun <B, C, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    map: (A, B, C) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head, c.head),
      tail.zip(b.tail, c.tail, map)
    )

  public inline fun <B, C, D, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    map: (A, B, C, D) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head, c.head, d.head),
      tail.zip(b.tail, c.tail, d.tail, map)
    )

  public inline fun <B, C, D, E, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<E>,
    map: (A, B, C, D, E) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head, c.head, d.head, e.head),
      tail.zip(b.tail, c.tail, d.tail, e.tail, map)
    )

  public inline fun <B, C, D, E, F, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<E>,
    f: NonEmptyList<F>,
    map: (A, B, C, D, E, F) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head, c.head, d.head, e.head, f.head),
      tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, map)
    )

  public inline fun <B, C, D, E, F, G, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    e: NonEmptyList<E>,
    f: NonEmptyList<F>,
    g: NonEmptyList<G>,
    map: (A, B, C, D, E, F, G) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head, c.head, d.head, e.head, f.head, g.head),
      tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail, map)
    )

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
    NonEmptyList(
      map(head, b.head, c.head, d.head, e.head, f.head, g.head, h.head),
      tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail, h.tail, map)
    )

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
    NonEmptyList(
      map(head, b.head, c.head, d.head, e.head, f.head, g.head, h.head, i.head),
      tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail, h.tail, i.tail, map)
    )

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
    NonEmptyList(
      map(head, b.head, c.head, d.head, e.head, f.head, g.head, h.head, i.head, j.head),
      tail.zip(b.tail, c.tail, d.tail, e.tail, f.tail, g.tail, h.tail, i.tail, j.tail, map)
    )
}

public fun <A> nonEmptyListOf(head: A, vararg t: A): NonEmptyList<A> =
  NonEmptyList(head, t.asList())

public inline fun <A> A.nel(): NonEmptyList<A> =
  nonEmptyListOf(this)

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
  this.map(f).let { nel ->
    nel.tail.unzip().let {
      NonEmptyList(nel.head.first, it.first) to
        NonEmptyList(nel.head.second, it.second)
    }
  }

@Deprecated(
  "Traverse for Either is being deprecated in favor of Either DSL + NonEmptyList.map.\n$NicheAPI",
  ReplaceWith(
    "let<NonEmptyList<A>, Either<E, NonEmptyList<B>>> { nel -> either<E, NonEmptyList<B>> { nel.map<A, B> { f(it).bind<B>() } } }",
    "arrow.core.raise.either")
)
public inline fun <E, A, B> NonEmptyList<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, NonEmptyList<B>> =
  traverse(f)

@Deprecated(
  "Traverse for Either is being deprecated in favor of Either DSL + NonEmptyList.map.\n$NicheAPI",
  ReplaceWith(
    "let<NonEmptyList<A>, Either<E, NonEmptyList<B>>> { nel -> either<E, NonEmptyList<B>> { nel.map<B> { f(it).bind<B>() } } }",
    "arrow.core.raise.either")
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <E, A, B> NonEmptyList<A>.traverse(f: (A) -> Either<E, B>): Either<E, NonEmptyList<B>> =
  let { nel -> either { nel.map { f(it).bind() } } }

@Deprecated(
  "Sequence for Either is being deprecated in favor of Either DSL + NonEmptyList.map.\n$NicheAPI",
  ReplaceWith("either<E, NonEmptyList<A>> { this.map<A> { it.bind<A>() } }", "arrow.core.raise.either")
)
public fun <E, A> NonEmptyList<Either<E, A>>.sequenceEither(): Either<E, NonEmptyList<A>> =
  sequence()

@Deprecated(
  "Sequence for Either is being deprecated in favor of Either DSL + NonEmptyList.map.\n$NicheAPI",
  ReplaceWith("either<E, NonEmptyList<A>> { this.map<A> { it.bind<A>() } }", "arrow.core.raise.either")
)
public fun <E, A> NonEmptyList<Either<E, A>>.sequence(): Either<E, NonEmptyList<A>> =
  traverse(::identity)

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "this.mapOrAccumulate<E, A, B>({ a, b -> a + b}) { f(it).bind<B>() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public inline fun <E, A, B> NonEmptyList<A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, NonEmptyList<B>> =
  mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { f(it).bind() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "this.mapOrAccumulate<E, A, B>({ a, b -> a + b}) { f(it).bind<B>() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <E, A, B> NonEmptyList<A>.traverse(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, NonEmptyList<B>> =
  mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { f(it).bind() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "this.mapOrAccumulate<E, A>({ e1, e2 -> e1 + e2 }) { it.bind<A>() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public fun <E, A> NonEmptyList<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, NonEmptyList<A>> =
  mapOrAccumulate(semigroup::combine) { it.bind() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "this.mapOrAccumulate<E, A>({ e1, e2 -> e1 + e2 }) { it.bind<A>() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public fun <E, A> NonEmptyList<Validated<E, A>>.sequence(semigroup: Semigroup<E>): Validated<E, NonEmptyList<A>> =
  mapOrAccumulate(semigroup::combine) { it.bind() }.toValidated()

public inline fun <E, A, B> NonEmptyList<A>.mapOrAccumulate(
  combine: (E, E) -> E,
  @BuilderInference transform: RaiseAccumulate<E>.(A) -> B
): Either<E, NonEmptyList<B>> =
  all.mapOrAccumulate(combine, transform).map { requireNotNull(it.toNonEmptyListOrNull()) }

public inline fun <E, A, B> NonEmptyList<A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<E>.(A) -> B
): Either<NonEmptyList<E>, NonEmptyList<B>> =
  all.mapOrAccumulate(transform).map { requireNotNull(it.toNonEmptyListOrNull()) }

@Deprecated(
  "Traverse for Option is being deprecated in favor of Option DSL + NonEmptyList.map.\\n$NicheAPI",
  ReplaceWith(
    "let<NonEmptyList<A>, Option<NonEmptyList<B>>> { nel -> option<NonEmptyList<B>> { nel.map<B> { f(it).bind<B>() } } }",
    "arrow.core.raise.option")
)
public inline fun <A, B> NonEmptyList<A>.traverseOption(f: (A) -> Option<B>): Option<NonEmptyList<B>> =
  traverse(f)

@Deprecated(
  "Traverse for Option is being deprecated in favor of Option DSL + NonEmptyList.map.\\n$NicheAPI",
  ReplaceWith(
    "let<NonEmptyList<A>, Option<NonEmptyList<B>>> { nel -> option<NonEmptyList<B>> { nel.map<B> { f(it).bind<B>() } } }",
    "arrow.core.raise.option")
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B> NonEmptyList<A>.traverse(f: (A) -> Option<B>): Option<NonEmptyList<B>> =
  let { nel -> option { nel.map { f(it).bind() } } }

@Deprecated(
  "Sequence for Option is being deprecated in favor of Option DSL + NonEmptyList.map.\\n$NicheAPI",
  ReplaceWith(
    "option<NonEmptyList<A>> { this.map<A> { it.bind<A>() } }",
    "arrow.core.raise.option")
)
public fun <A> NonEmptyList<Option<A>>.sequenceOption(): Option<NonEmptyList<A>> =
  sequence()

@Deprecated(
  "Sequence for Option is being deprecated in favor of Option DSL + NonEmptyList.map.\\n$NicheAPI",
  ReplaceWith(
    "option<NonEmptyList<A>> { this.map<A> { it.bind<A>() } }",
    "arrow.core.raise.option")
)
public fun <A> NonEmptyList<Option<A>>.sequence(): Option<NonEmptyList<A>> =
  traverse(::identity)

public fun <A> Iterable<A>.toNonEmptyListOrNull(): NonEmptyList<A>? =
  firstOrNull()?.let { NonEmptyList(it, drop(1)) }

public fun <A> Iterable<A>.toNonEmptyListOrNone(): Option<NonEmptyList<A>> =
  toNonEmptyListOrNull().toOption()
