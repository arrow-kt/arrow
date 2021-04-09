package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Semigroup

typealias Nel<A> = NonEmptyList<A>

/**
 * `NonEmptyList` is a data type used in __Λrrow__ to model ordered lists that guarantee to have at least one value.
 * `NonEmptyList` is available in the `arrow-core` module under the `import arrow.core.NonEmptyList`
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
 * import arrow.core.zip
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
) : AbstractList<A>() {

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

  inline fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> =
    f(head) + tail.flatMap { f(it).all }

  operator fun plus(l: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l.all)

  operator fun plus(l: List<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l)

  operator fun plus(a: @UnsafeVariance A): NonEmptyList<A> =
    NonEmptyList(all + a)

  inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    this.tail.fold(f(b, this.head), f)

  @Deprecated(FoldRightDeprecation)
  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    all.foldRight(lb, f)

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

    @JvmStatic
    fun <A> fromList(l: List<A>): Option<NonEmptyList<A>> =
      if (l.isEmpty()) None else Some(NonEmptyList(l))

    @JvmStatic
    fun <A> fromListUnsafe(l: List<A>): NonEmptyList<A> =
      NonEmptyList(l)

    @PublishedApi
    internal val unit: NonEmptyList<Unit> =
      nonEmptyListOf(Unit)

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
      buf: ArrayList<B>,
      f: (A) -> NonEmptyList<Either<A, B>>,
      v: NonEmptyList<Either<A, B>>
    ) {
      val head: Either<A, B> = v.head
      when (head) {
        is Either.Right -> {
          buf += head.value
          val x = fromList(v.tail)
          when (x) {
            is Some<NonEmptyList<Either<A, B>>> -> go(buf, f, x.value)
            is None -> Unit
          }
        }
        is Either.Left -> go(buf, f, f(head.value) + v.tail)
      }
    }

    @Deprecated(TailRecMDeprecation)
    fun <A, B> tailRecM(a: A, f: (A) -> NonEmptyList<Either<A, B>>): NonEmptyList<B> {
      val buf = ArrayList<B>()
      go(buf, f, f(a))
      return fromListUnsafe(buf)
    }
  }

  fun <B> zip(fb: NonEmptyList<B>): NonEmptyList<Pair<A, B>> =
    zip(fb, ::Pair)

  inline fun <B, Z> zip(
    b: NonEmptyList<B>,
    map: (A, B) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head),
      tail.zip(b.tail, map)
    )

  inline fun <B, C, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    map: (A, B, C) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head, c.head),
      tail.zip(b.tail, c.tail, map)
    )

  inline fun <B, C, D, Z> zip(
    b: NonEmptyList<B>,
    c: NonEmptyList<C>,
    d: NonEmptyList<D>,
    map: (A, B, C, D) -> Z
  ): NonEmptyList<Z> =
    NonEmptyList(
      map(head, b.head, c.head, d.head),
      tail.zip(b.tail, c.tail, d.tail, map)
    )

  inline fun <B, C, D, E, Z> zip(
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

  inline fun <B, C, D, E, F, Z> zip(
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

  inline fun <B, C, D, E, F, G, Z> zip(
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

  inline fun <B, C, D, E, F, G, H, Z> zip(
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

  inline fun <B, C, D, E, F, G, H, I, Z> zip(
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

  inline fun <B, C, D, E, F, G, H, I, J, Z> zip(
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

fun <A> nonEmptyListOf(head: A, vararg t: A): NonEmptyList<A> =
  NonEmptyList(head, t.asList())

inline fun <A> A.nel(): NonEmptyList<A> =
  nonEmptyListOf(this)

operator fun <A : Comparable<A>> NonEmptyList<A>.compareTo(other: NonEmptyList<A>): Int =
  all.compareTo(other.all)

fun <A> NonEmptyList<NonEmptyList<A>>.flatten(): NonEmptyList<A> =
  this.flatMap(::identity)

fun <A, B> NonEmptyList<Pair<A, B>>.unzip(): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  this.unzip(::identity)

fun <A, B, C> NonEmptyList<C>.unzip(f: (C) -> Pair<A, B>): Pair<NonEmptyList<A>, NonEmptyList<B>> =
  this.map(f).let { nel ->
    nel.tail.unzip().let {
      NonEmptyList(nel.head.first, it.first) to
        NonEmptyList(nel.head.second, it.second)
    }
  }

inline fun <E, A, B> NonEmptyList<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, NonEmptyList<B>> {
  val acc = mutableListOf<B>()
  forEach { a ->
    when (val res = f(a)) {
      is Right -> acc.add(res.value)
      is Left -> return@traverseEither res
    }
  }
  // Safe due to traverse laws
  return NonEmptyList.fromListUnsafe(acc).right()
}

fun <E, A> NonEmptyList<Either<E, A>>.sequenceEither(): Either<E, NonEmptyList<A>> =
  traverseEither(::identity)

inline fun <E, A, B> NonEmptyList<A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, NonEmptyList<B>> =
  fold(mutableListOf<B>().valid() as Validated<E, MutableList<B>>) { acc, a ->
    when (val res = f(a)) {
      is Valid -> when (acc) {
        is Valid -> acc.also { it.value.add(res.value) }
        is Invalid -> acc
      }
      is Invalid -> when (acc) {
        is Valid -> res
        is Invalid -> semigroup.run { acc.value.combine(res.value).invalid() }
      }
    }
  }.map { NonEmptyList.fromListUnsafe(it) }

fun <E, A> NonEmptyList<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, NonEmptyList<A>> =
  traverseValidated(semigroup, ::identity)
