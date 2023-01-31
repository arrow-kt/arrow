@file:Suppress("unused", "FunctionName")

package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.continuations.Raise
import arrow.core.continuations.either
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.experimental.ExperimentalTypeInference

public inline fun <B, C, D, E> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  transform: (B, C, D) -> E
): List<E> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()

  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10)
  )
  val list = ArrayList<E>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next()))
  }
  return list
}

public inline fun <B, C, D, E, F> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  transform: (B, C, D, E) -> F
): List<F> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10)
  )
  val list = ArrayList<F>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  transform: (B, C, D, E, F) -> G
): List<G> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10)
  )
  val list = ArrayList<G>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  transform: (B, C, D, E, F, G) -> H
): List<H> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10)
  )
  val list = ArrayList<H>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  transform: (B, C, D, E, F, G, H) -> I
): List<I> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10)
  )
  val list = ArrayList<I>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next(), hh.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I, J> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  i: Iterable<I>,
  transform: (B, C, D, E, F, G, H, I) -> J
): List<J> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val ii = i.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10),
    i.collectionSizeOrDefault(10)
  )
  val list = ArrayList<J>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next(), hh.next(), ii.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I, J, K> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  i: Iterable<I>,
  j: Iterable<J>,
  transform: (B, C, D, E, F, G, H, I, J) -> K
): List<K> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val ii = i.iterator()
  val jj = j.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10),
    i.collectionSizeOrDefault(10),
    j.collectionSizeOrDefault(10)
  )
  val list = ArrayList<K>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext() && jj.hasNext()) {
    list.add(
      transform(
        bb.next(),
        cc.next(),
        dd.next(),
        ee.next(),
        ff.next(),
        gg.next(),
        hh.next(),
        ii.next(),
        jj.next()
      )
    )
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I, J, K, L> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  i: Iterable<I>,
  j: Iterable<J>,
  k: Iterable<K>,
  transform: (B, C, D, E, F, G, H, I, J, K) -> L
): List<L> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val ii = i.iterator()
  val jj = j.iterator()
  val kk = k.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10),
    i.collectionSizeOrDefault(10),
    j.collectionSizeOrDefault(10),
    k.collectionSizeOrDefault(10)
  )
  val list = ArrayList<L>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext() && jj.hasNext() && kk.hasNext()) {
    list.add(
      transform(
        bb.next(),
        cc.next(),
        dd.next(),
        ee.next(),
        ff.next(),
        gg.next(),
        hh.next(),
        ii.next(),
        jj.next(),
        kk.next()
      )
    )
  }
  return list
}

@PublishedApi
internal fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int =
  if (this is Collection<*>) this.size else default

/**
 * Returns [Either] a [List] containing the results of applying the given [transform] function
 * to each element in the original collection,
 * **or** accumulate all the _logical errors_ that were _raised_ while transforming the collection.
 * The [semigroup] is used to accumulate all the _logical errors_.
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  semigroup: Semigroup<Error>,
  @BuilderInference transform: Raise<Error>.(A) -> B,
): Either<Error, List<B>> =
  fold<A, Either<Error, ArrayList<B>>>(Right(ArrayList(collectionSizeOrDefault(10)))) { acc, a ->
    when (val res = either { transform(a) }) {
      is Right -> when (acc) {
        is Right -> acc.also { acc.value.add(res.value) }
        is Left -> acc
      }

      is Left -> when (acc) {
        is Right -> res
        is Left -> Left(semigroup.append(acc.value, res.value))
      }
    }
  }

/**
 * Returns [Either] a [List] containing the results of applying the given [transform] function
 * to each element in the original collection,
 * **or** accumulate all the _logical errors_ into a [NonEmptyList] that were _raised_ while applying the [transform] function.
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  @BuilderInference transform: Raise<Error>.(A) -> B,
): Either<NonEmptyList<Error>, List<B>> {
  val buffer = mutableListOf<Error>()
  val res = fold<A, Either<MutableList<Error>, ArrayList<B>>>(Right(ArrayList(collectionSizeOrDefault(10)))) { acc, a ->
    when (val res = either { transform(a) }) {
      is Right -> when (acc) {
        is Right -> acc.also { acc.value.add(res.value) }
        is Left -> acc
      }

      is Left -> when (acc) {
        is Right -> Left(buffer.also { it.add(res.value) })
        is Left -> Left(buffer.also { it.add(res.value) })
      }
    }
  }
  return res.mapLeft { NonEmptyList(it[0], it.drop(1)) }
}

/**
 * Flatten a list of [Either] into a single [Either] with a list of values, or accumulates all errors using [combine].
 */
public inline fun <Error, A> Iterable<Either<Error, A>>.flattenOrAccumulate(combine: (Error, Error) -> Error): Either<Error, List<A>> =
  fold<Either<Error, A>, Either<Error, ArrayList<A>>>(Right(ArrayList(collectionSizeOrDefault(10)))) { acc, res ->
    when (res) {
      is Right -> when (acc) {
        is Right -> acc.also { acc.value.add(res.value) }
        is Left -> acc
      }

      is Left -> when (acc) {
        is Right -> res
        is Left -> Left(combine(acc.value, res.value))
      }
    }
  }

/**
 * Flatten a list of [Either] into a single [Either] with a list of values, or accumulates all errors with into an [NonEmptyList].
 */
public fun <Error, A> Iterable<Either<Error, A>>.flattenOrAccumulate(): Either<NonEmptyList<Error>, List<A>> {
  val buffer = mutableListOf<Error>()
  val res = fold<Either<Error, A>, Either<MutableList<Error>, ArrayList<A>>>(Right(ArrayList(collectionSizeOrDefault(10)))) { acc, res ->
    when (res) {
      is Right -> when (acc) {
        is Right -> acc.also { acc.value.add(res.value) }
        is Left -> acc
      }

      is Left -> when (acc) {
        is Right -> Left(buffer.also { it.add(res.value) })
        is Left -> Left(buffer.also { it.add(res.value) })
      }
    }
  }
  return res.mapLeft { NonEmptyList(it[0], it.drop(1)) }
}

public fun <A> Iterable<A>.void(): List<Unit> =
  map { }

public fun <A, B> Iterable<A>.reduceOrNull(initial: (A) -> B, operation: (acc: B, A) -> B): B? {
  val iterator = this.iterator()
  if (!iterator.hasNext()) return null
  var accumulator: B = initial(iterator.next())
  while (iterator.hasNext()) {
    accumulator = operation(accumulator, iterator.next())
  }
  return accumulator
}

public inline fun <A, B> List<A>.reduceRightNull(
  initial: (A) -> B,
  operation: (A, acc: B) -> B
): B? {
  val iterator = listIterator(size)
  if (!iterator.hasPrevious()) return null
  var accumulator: B = initial(iterator.previous())
  while (iterator.hasPrevious()) {
    accumulator = operation(iterator.previous(), accumulator)
  }
  return accumulator
}

/**
 * Returns a [List<Pair<A?, B?>>] containing the zipped values of the two lists with null for padding.
 *
 * Example:
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val padRight = listOf(1, 2).padZip(listOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]
 * val padLeft = listOf(1).padZip(listOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]
 * val noPadding = listOf(1, 2).padZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padRight = $padRight")
 *   println("padLeft = $padLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-iterable-01.kt -->
 */
public fun <A, B> Iterable<A>.padZip(other: Iterable<B>): List<Pair<A?, B?>> =
  align(other) { ior ->
    ior.fold(
      { it to null },
      { null to it },
      { a, b -> a to b }
    )
  }

/**
 * Returns a [List<C>] containing the result of applying some transformation `(A?, B?) -> C`
 * on a zip.
 *
 * Example:
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val padZipRight = listOf(1, 2).padZip(listOf("a")) { l, r -> l to r }     // Result: [Pair(1, "a"), Pair(2, null)]
 * val padZipLeft = listOf(1).padZip(listOf("a", "b")) { l, r -> l to r }    // Result: [Pair(1, "a"), Pair(null, "b")]
 * val noPadding = listOf(1, 2).padZip(listOf("a", "b")) { l, r -> l to r }  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padZipRight = $padZipRight")
 *   println("padZipLeft = $padZipLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-iterable-02.kt -->
 */
public inline fun <A, B, C> Iterable<A>.padZip(other: Iterable<B>, fa: (A?, B?) -> C): List<C> =
  padZip(other).map { fa(it.first, it.second) }

/**
 * Returns a [List<C>] containing the result of applying some transformation `(A?, B) -> C`
 * on a zip, excluding all cases where the right value is null.
 *
 * Example:
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val left = listOf(1, 2).leftPadZip(listOf("a")) { l, r -> l to r }      // Result: [Pair(1, "a")]
 * val right = listOf(1).leftPadZip(listOf("a", "b")) { l, r -> l to r }   // Result: [Pair(1, "a"), Pair(null, "b")]
 * val both = listOf(1, 2).leftPadZip(listOf("a", "b")) { l, r -> l to r } // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 * <!--- KNIT example-iterable-03.kt -->
 */
public inline fun <A, B, C> Iterable<A>.leftPadZip(other: Iterable<B>, fab: (A?, B) -> C): List<C> =
  padZip(other) { a: A?, b: B? -> b?.let { fab(a, it) } }.mapNotNull(::identity)

/**
 * Returns a [List<Pair<A?, B>>] containing the zipped values of the two lists
 * with null for padding on the left.
 *
 * Example:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val padRight = listOf(1, 2).leftPadZip(listOf("a"))        // Result: [Pair(1, "a")]
 * val padLeft = listOf(1).leftPadZip(listOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]
 * val noPadding = listOf(1, 2).leftPadZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padRight = $padRight")
 *   println("padLeft = $padLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-iterable-04.kt -->
 */
public fun <A, B> Iterable<A>.leftPadZip(other: Iterable<B>): List<Pair<A?, B>> =
  this.leftPadZip(other) { a, b -> a to b }

/**
 * Returns a [List<C>] containing the result of applying some transformation `(A, B?) -> C`
 * on a zip, excluding all cases where the left value is null.
 *
 * Example:
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val left = listOf(1, 2).rightPadZip(listOf("a")) { l, r -> l to r }      // Result: [Pair(1, "a"), Pair(null, "b")]
 * val right = listOf(1).rightPadZip(listOf("a", "b")) { l, r -> l to r }   // Result: [Pair(1, "a")]
 * val both = listOf(1, 2).rightPadZip(listOf("a", "b")) { l, r -> l to r } // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 * <!--- KNIT example-iterable-05.kt -->
 */
public inline fun <A, B, C> Iterable<A>.rightPadZip(other: Iterable<B>, fa: (A, B?) -> C): List<C> =
  other.leftPadZip(this) { a, b -> fa(b, a) }

/**
 * Returns a [List<Pair<A, B?>>] containing the zipped values of the two lists
 * with null for padding on the right.
 *
 * Example:
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val padRight = listOf(1, 2).rightPadZip(listOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]
 * val padLeft = listOf(1).rightPadZip(listOf("a", "b"))       // Result: [Pair(1, "a")]
 * val noPadding = listOf(1, 2).rightPadZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padRight = $padRight")
 *   println("padLeft = $padLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-iterable-06.kt -->
 */
public fun <A, B> Iterable<A>.rightPadZip(other: Iterable<B>): List<Pair<A, B?>> =
  this.rightPadZip(other) { a, b -> a to b }

/**
 * Combines two structures by taking the union of their shapes and combining the elements with the given function.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf("A", "B").align(listOf(1, 2, 3)) {
 *      "$it"
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-07.kt -->
 */
public inline fun <A, B, C> Iterable<A>.align(b: Iterable<B>, fa: (Ior<A, B>) -> C): List<C> =
  buildList(maxOf(this.collectionSizeOrDefault(10), b.collectionSizeOrDefault(10))) {
    val first = this@align.iterator()
    val second = b.iterator()
    while (first.hasNext() || second.hasNext()) {
      val element: Ior<A, B> = when {
        first.hasNext() && second.hasNext() -> Ior.Both(first.next(), second.next())
        first.hasNext() -> first.next().leftIor()
        second.hasNext() -> second.next().rightIor()
        else -> throw IllegalStateException("this should never happen")
      }
      add(fa(element))
    }
  }

/**
 * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *     listOf("A", "B").align(listOf(1, 2, 3))
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-08.kt -->
 */
public fun <A, B> Iterable<A>.align(b: Iterable<B>): List<Ior<A, B>> =
  this.align(b, ::identity)

/**
 * aligns two structures and combine them with the given [Semigroup.append]
 */
public fun <A> Iterable<A>.salign(
  SG: Semigroup<A>,
  other: Iterable<A>,
): Iterable<A> = SG.run {
  align(other) {
    it.fold(::identity, ::identity) { a, b ->
      a.combine(b)
    }
  }
}

/**
 * unzips the structure holding the resulting elements in an `Pair`
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *      listOf("A" to 1, "B" to 2).unzip()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-09.kt -->
 */
public fun <A, B> Iterable<Pair<A, B>>.unzip(): Pair<List<A>, List<B>> =
  fold(emptyList<A>() to emptyList()) { (l, r), x ->
    l + x.first to r + x.second
  }

/**
 * after applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf("A:1", "B:2", "C:3").unzip { e ->
 *      e.split(":").let {
 *        it.first() to it.last()
 *      }
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-10.kt -->
 */
public inline fun <A, B, C> Iterable<C>.unzip(fc: (C) -> Pair<A, B>): Pair<List<A>, List<B>> =
  map(fc).unzip()

/**
 * splits a union into its component parts.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf(("A" to 1).bothIor(), ("B" to 2).bothIor(), "C".leftIor())
 *      .unalign()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-11.kt -->
 */
public fun <A, B> Iterable<Ior<A, B>>.unalign(): Pair<List<A>, List<B>> =
  fold(emptyList<A>() to emptyList()) { (l, r), x ->
    x.fold(
      { l + it to r },
      { l to r + it },
      { a, b -> l + a to r + b }
    )
  }

/**
 * after applying the given function, splits the resulting union shaped structure into its components parts
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *      listOf(1, 2, 3).unalign {
 *        it.leftIor()
 *      }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-12.kt -->
 */
public inline fun <A, B, C> Iterable<C>.unalign(fa: (C) -> Ior<A, B>): Pair<List<A>, List<B>> =
  map(fa).unalign()

@Deprecated("use fold instead", ReplaceWith("fold(MA)", "arrow.core.fold"))
public fun <A> Iterable<A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

/**
 * Returns the first element as [Some(element)][Some], or [None] if the iterable is empty.
 */
public fun <T> Iterable<T>.firstOrNone(): Option<T> =
  when (this) {
    is Collection -> if (!isEmpty()) {
      Some(first())
    } else {
      None
    }

    else -> {
      iterator().nextOrNone()
    }
  }

private fun <T> Iterator<T>.nextOrNone(): Option<T> =
  if (hasNext()) {
    Some(next())
  } else {
    None
  }

/**
 * Returns the first element as [Some(element)][Some] matching the given [predicate], or [None] if element was not found.
 */
public inline fun <T> Iterable<T>.firstOrNone(predicate: (T) -> Boolean): Option<T> {
  for (element in this) {
    if (predicate(element)) {
      return Some(element)
    }
  }
  return None
}

/**
 * Returns single element as [Some(element)][Some], or [None] if the iterable is empty or has more than one element.
 */
public fun <T> Iterable<T>.singleOrNone(): Option<T> =
  when (this) {
    is Collection -> when (size) {
      1 -> firstOrNone()
      else -> None
    }

    else -> {
      iterator().run { nextOrNone().filter { !hasNext() } }
    }
  }

/**
 * Returns the single element as [Some(element)][Some] matching the given [predicate], or [None] if element was not found or more than one element was found.
 */
public inline fun <T> Iterable<T>.singleOrNone(predicate: (T) -> Boolean): Option<T> {
  val list = mutableListOf<T>()
  for (element in this) {
    if (predicate(element)) {
      if (list.isNotEmpty()) {
        return None
      }
      list.add(element)
    }
  }
  return list.firstOrNone()
}

/**
 * Returns the last element as [Some(element)][Some], or [None] if the iterable is empty.
 */
public fun <T> Iterable<T>.lastOrNone(): Option<T> =
  when (this) {
    is Collection -> if (!isEmpty()) {
      Some(last())
    } else {
      None
    }

    else -> iterator().run {
      if (hasNext()) {
        var last: T
        do last = next() while (hasNext())
        Some(last)
      } else {
        None
      }
    }
  }

/**
 * Returns the last element as [Some(element)][Some] matching the given [predicate], or [None] if no such element was found.
 */
public inline fun <T> Iterable<T>.lastOrNone(predicate: (T) -> Boolean): Option<T> {
  var value: Any? = EmptyValue
  for (element in this) {
    if (predicate(element)) {
      value = element
    }
  }
  return if (value === EmptyValue) None else Some(EmptyValue.unbox(value))
}

/**
 * Returns an element as [Some(element)][Some] at the given [index] or [None] if the [index] is out of bounds of this iterable.
 */
public fun <T> Iterable<T>.elementAtOrNone(index: Int): Option<T> =
  when {
    index < 0 -> None
    this is Collection -> when (index) {
      in indices -> Some(elementAt(index))
      else -> None
    }

    else -> iterator().skip(index).nextOrNone()
  }

private tailrec fun <T> Iterator<T>.skip(count: Int): Iterator<T> =
  when {
    count > 0 && hasNext() -> {
      next()
      skip(count - 1)
    }

    else -> this
  }

/**
 * attempt to split the computation, giving access to the first result.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf("A", "B", "C").split()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-13.kt -->
 */
public fun <A> Iterable<A>.split(): Pair<List<A>, A>? =
  firstOrNull()?.let { first ->
    tail() to first
  }

public fun <A> Iterable<A>.tail(): List<A> =
  drop(1)

/**
 * interleave both computations in a fair way.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val tags = List(10) { "#" }
 *   val result =
 *    tags.interleave(listOf("A", "B", "C"))
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-14.kt -->
 */
public fun <A> Iterable<A>.interleave(other: Iterable<A>): List<A> =
  this.split()?.let { (fa, a) ->
    listOf(a) + other.interleave(fa)
  } ?: other.toList()

/**
 * Fair conjunction. Similarly to interleave
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf(1,2,3).unweave { i -> listOf("$i, ${i + 1}") }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-15.kt -->
 */
public fun <A, B> Iterable<A>.unweave(ffa: (A) -> Iterable<B>): List<B> =
  split()?.let { (fa, a) ->
    ffa(a).interleave(fa.unweave(ffa))
  } ?: emptyList()

/**
 * Logical conditional. The equivalent of Prolog's soft-cut.
 * If its first argument succeeds at all, then the results will be
 * fed into the success branch. Otherwise, the failure branch is taken.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf(1,2,3).ifThen(listOf("empty")) { i ->
 *      listOf("$i, ${i + 1}")
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-16.kt -->
 */
public inline fun <A, B> Iterable<A>.ifThen(fb: Iterable<B>, ffa: (A) -> Iterable<B>): Iterable<B> =
  firstOrNull()?.let { first -> ffa(first) + tail().flatMap(ffa) } ?: fb.toList()

@Deprecated("Use mapNotNull and orNull instead.", ReplaceWith("mapNotNull { it.orNull() }"))
public fun <A, B> Iterable<Either<A, B>>.uniteEither(): List<B> =
  mapNotNull { it.orNull() }

/**
 * Separate the inner [Either] values into the [Either.Left] and [Either.Right].
 *
 * @receiver Iterable of Either
 * @return a tuple containing List with [Either.Left] and another List with its [Either.Right] values.
 */
public fun <A, B> Iterable<Either<A, B>>.separateEither(): Pair<List<A>, List<B>> {
  val left = ArrayList<A>(collectionSizeOrDefault(10))
  val right = ArrayList<B>(collectionSizeOrDefault(10))

  for (either in this)
    when (either) {
      is Left -> left.add(either.value)
      is Right -> right.add(either.value)
    }

  return Pair(left, right)
}

public fun <A> Iterable<Iterable<A>>.flatten(): List<A> =
  flatMap(::identity)

/**
 *  Given [A] is a sub type of [B], re-type this value from Iterable<A> to Iterable<B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin
 *  import arrow.core.*
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: Iterable<CharSequence> =
 *     listOf("Hello World").widen()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
public fun <B, A : B> Iterable<A>.widen(): Iterable<B> =
  this

public fun <B, A : B> List<A>.widen(): List<B> =
  this

public fun <A> Iterable<A>.fold(MA: Monoid<A>): A = MA.run {
  this@fold.fold(empty()) { acc, a ->
    acc.combine(a)
  }
}

public fun <A, B> Iterable<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B = MB.run {
  this@foldMap.fold(empty()) { acc, a ->
    acc.combine(f(a))
  }
}

public fun <A, B> Iterable<A>.crosswalk(f: (A) -> Iterable<B>): List<List<B>> =
  fold(emptyList()) { bs, a ->
    f(a).align(bs) { ior ->
      ior.fold(
        { listOf(it) },
        ::identity,
        { l, r -> listOf(l) + r }
      )
    }
  }

public fun <A, K, V> Iterable<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, List<V>> =
  fold(emptyMap()) { bs, a ->
    f(a).align(bs) { (_, ior) ->
      ior.fold(
        { listOf(it) },
        ::identity,
        { l, r -> listOf(l) + r }
      )
    }
  }

public fun <A, B> Iterable<A>.crosswalkNull(f: (A) -> B?): List<B>? =
  fold<A, List<B>?>(emptyList()) { bs, a ->
    Ior.fromNullables(f(a), bs)?.fold(
      { listOf(it) },
      ::identity,
      { l, r -> listOf(l) + r }
    )
  }

@PublishedApi
internal val listUnit: List<Unit> =
  listOf(Unit)

public fun <A> Iterable<A>.replicate(n: Int): List<List<A>> =
  if (n <= 0) emptyList()
  else toList().let { l -> List(n) { l } }

public fun <A> Iterable<A>.replicate(n: Int, MA: Monoid<A>): List<A> =
  if (n <= 0) listOf(MA.empty())
  else this@replicate.zip(replicate(n - 1, MA)) { a, xs -> MA.run { a + xs } }

public operator fun <A : Comparable<A>> Iterable<A>.compareTo(other: Iterable<A>): Int =
  align(other) { ior -> ior.fold({ 1 }, { -1 }, { a1, a2 -> a1.compareTo(a2) }) }
    .fold(0) { acc, i ->
      when (acc) {
        0 -> i
        else -> acc
      }
    }

public infix fun <T> T.prependTo(list: Iterable<T>): List<T> =
  listOf(this) + list

public fun <T> Iterable<Option<T>>.filterOption(): List<T> =
  flatMap { it.fold(::emptyList, ::listOf) }

public fun <T> Iterable<Option<T>>.flattenOption(): List<T> = filterOption()
