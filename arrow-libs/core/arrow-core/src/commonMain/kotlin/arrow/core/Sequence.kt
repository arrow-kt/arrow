@file:OptIn(ExperimentalTypeInference::class)

/**
 * <!--- TEST_NAME SequenceKnitTest -->
 */
package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.fold
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation
import arrow.typeclasses.combine
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Adds [kotlin.sequences.zip] support for 3 parameters */
public fun <B, C, D, E> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  map: (B, C, D) -> E
): Sequence<E> = Sequence {
  object : Iterator<E> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()

    override fun next(): E =
      map(iterator1.next(), iterator2.next(), iterator3.next())

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext()
  }
}

/** Adds [kotlin.sequences.zip] support for 4 parameters */
public fun <B, C, D, E, F> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  e: Sequence<E>,
  map: (B, C, D, E) -> F
): Sequence<F> = Sequence {
  object : Iterator<F> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()
    val iterator4 = e.iterator()

    override fun next(): F =
      map(iterator1.next(), iterator2.next(), iterator3.next(), iterator4.next())

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext() && iterator4.hasNext()
  }
}

/** Adds [kotlin.sequences.zip] support for 5 parameters */
public fun <B, C, D, E, F, G> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  e: Sequence<E>,
  f: Sequence<F>,
  map: (B, C, D, E, F) -> G
): Sequence<G> = Sequence {
  object : Iterator<G> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()
    val iterator4 = e.iterator()
    val iterator5 = f.iterator()

    override fun next(): G =
      map(iterator1.next(), iterator2.next(), iterator3.next(), iterator4.next(), iterator5.next())

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext() && iterator4.hasNext() && iterator5.hasNext()
  }
}

/** Adds [kotlin.sequences.zip] support for 6 parameters */
public fun <B, C, D, E, F, G, H> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  e: Sequence<E>,
  f: Sequence<F>,
  g: Sequence<G>,
  map: (B, C, D, E, F, G) -> H
): Sequence<H> = Sequence {
  object : Iterator<H> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()
    val iterator4 = e.iterator()
    val iterator5 = f.iterator()
    val iterator6 = g.iterator()

    override fun next(): H =
      map(iterator1.next(), iterator2.next(), iterator3.next(), iterator4.next(), iterator5.next(), iterator6.next())

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext() && iterator4.hasNext() && iterator5.hasNext() && iterator6.hasNext()
  }
}

/** Adds [kotlin.sequences.zip] support for 7 parameters */
public fun <B, C, D, E, F, G, H, I> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  e: Sequence<E>,
  f: Sequence<F>,
  g: Sequence<G>,
  h: Sequence<H>,
  map: (B, C, D, E, F, G, H) -> I
): Sequence<I> = Sequence {
  object : Iterator<I> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()
    val iterator4 = e.iterator()
    val iterator5 = f.iterator()
    val iterator6 = g.iterator()
    val iterator7 = h.iterator()
    override fun next(): I =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next()
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext() && iterator4.hasNext() && iterator5.hasNext() && iterator6.hasNext() && iterator7.hasNext()
  }
}

/** Adds [kotlin.sequences.zip] support for 8 parameters */
public fun <B, C, D, E, F, G, H, I, J> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  e: Sequence<E>,
  f: Sequence<F>,
  g: Sequence<G>,
  h: Sequence<H>,
  i: Sequence<I>,
  map: (B, C, D, E, F, G, H, I) -> J
): Sequence<J> = Sequence {
  object : Iterator<J> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()
    val iterator4 = e.iterator()
    val iterator5 = f.iterator()
    val iterator6 = g.iterator()
    val iterator7 = h.iterator()
    val iterator8 = i.iterator()
    override fun next(): J =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next()
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext() && iterator4.hasNext() && iterator5.hasNext() && iterator6.hasNext() && iterator7.hasNext() && iterator8.hasNext()
  }
}

/** Adds [kotlin.sequences.zip] support for 9 parameters */
public fun <B, C, D, E, F, G, H, I, J, K> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  e: Sequence<E>,
  f: Sequence<F>,
  g: Sequence<G>,
  h: Sequence<H>,
  i: Sequence<I>,
  j: Sequence<J>,
  map: (B, C, D, E, F, G, H, I, J) -> K
): Sequence<K> = Sequence {
  object : Iterator<K> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()
    val iterator4 = e.iterator()
    val iterator5 = f.iterator()
    val iterator6 = g.iterator()
    val iterator7 = h.iterator()
    val iterator8 = i.iterator()
    val iterator9 = j.iterator()
    override fun next(): K =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next()
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext() && iterator4.hasNext() && iterator5.hasNext() && iterator6.hasNext() && iterator7.hasNext() && iterator8.hasNext() && iterator9.hasNext()
  }
}

/** Adds [kotlin.sequences.zip] support for 10 parameters */
public fun <B, C, D, E, F, G, H, I, J, K, L> Sequence<B>.zip(
  c: Sequence<C>,
  d: Sequence<D>,
  e: Sequence<E>,
  f: Sequence<F>,
  g: Sequence<G>,
  h: Sequence<H>,
  i: Sequence<I>,
  j: Sequence<J>,
  k: Sequence<K>,
  map: (B, C, D, E, F, G, H, I, J, K) -> L
): Sequence<L> = Sequence {
  object : Iterator<L> {
    val iterator1 = this@zip.iterator()
    val iterator2 = c.iterator()
    val iterator3 = d.iterator()
    val iterator4 = e.iterator()
    val iterator5 = f.iterator()
    val iterator6 = g.iterator()
    val iterator7 = h.iterator()
    val iterator8 = i.iterator()
    val iterator9 = j.iterator()
    val iterator10 = k.iterator()
    override fun next(): L =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next()
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext() && iterator4.hasNext() && iterator5.hasNext() && iterator6.hasNext() && iterator7.hasNext() && iterator8.hasNext() && iterator9.hasNext() && iterator10.hasNext()
  }
}

/**
 * Combines two [Sequence] by returning [Ior.Both] when both [Sequence] have an item,
 * [Ior.Left] when only the first [Sequence] has an item,
 * and [Ior.Right] when only the second [Sequence] has an item.
 *
 * ```kotlin
 * import arrow.core.align
 * import arrow.core.Ior
 * import arrow.core.Ior.Both
 * import arrow.core.Ior.Left
 * import arrow.core.Ior.Right
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   fun Ior<String, Int>.visualise(): String =
 *     fold({ "$it<" }, { ">$it" }, { a, b -> "$a<>$b" })
 *
 *   sequenceOf("A", "B").align(sequenceOf(1, 2, 3)) { ior ->
 *     ior.visualise()
 *   }.toList() shouldBe listOf("A<>1", "B<>2", ">3")
 *
 *   sequenceOf("A", "B", "C").align(sequenceOf(1, 2)) { ior ->
 *     ior.visualise()
 *   }.toList() shouldBe listOf("A<>1", "B<>2", "C<")
 * }
 * ```
 * <!--- KNIT example-sequence-01.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B, C> Sequence<A>.align(seqB: Sequence<B>, fa: (Ior<A, B>) -> C): Sequence<C> =
  alignRec(this, seqB, { fa(Ior.Left(it)) }, { fa(Ior.Right(it)) }) { a, b -> fa(Ior.Both(a, b)) }

/**
 * Combines two [Sequence] by returning [Ior.Both] when both [Sequence] have an item,
 * [Ior.Left] when only the first [Sequence] has an item,
 * and [Ior.Right] when only the second [Sequence] has an item.
 *
 * ```kotlin
 * import arrow.core.align
 * import arrow.core.Ior.Both
 * import arrow.core.Ior.Left
 * import arrow.core.Ior.Right
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   sequenceOf("A", "B")
 *     .align(sequenceOf(1, 2, 3)).toList() shouldBe listOf(Both("A", 1), Both("B", 2), Right(3))
 *
 *   sequenceOf("A", "B", "C")
 *     .align(sequenceOf(1, 2)).toList() shouldBe listOf(Both("A", 1), Both("B", 2), Left("C"))
 * }
 * ```
 * <!--- KNIT example-sequence-02.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B> Sequence<A>.align(seqB: Sequence<B>): Sequence<Ior<A, B>> =
  alignRec(this, seqB, { Ior.Left(it) }, { Ior.Right(it) }) { a, b -> Ior.Both(a, b) }

private fun <X, Y, Z> alignRec(
  ls: Sequence<X>,
  rs: Sequence<Y>,
  left: (X) -> Z,
  right: (Y) -> Z,
  both: (X, Y) -> Z
): Sequence<Z> {
  val lsIterator = ls.iterator()
  val rsIterator = rs.iterator()

  return sequence {
    while (lsIterator.hasNext() && rsIterator.hasNext()) {
      yield(
        both(
          lsIterator.next(),
          rsIterator.next()
        )
      )
    }
    while (lsIterator.hasNext()) yield(left(lsIterator.next()))
    while (rsIterator.hasNext()) yield(right(rsIterator.next()))
  }
}

@Deprecated("use fold instead", ReplaceWith("fold(MA)", "arrow.core.fold"))
public fun <A> Sequence<A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

@Deprecated(
  "This function is actually terminal. Use crosswalk(f:A -> List<B>) instead.",
  ReplaceWith("this.crosswalk{a -> f(a).toList()}")
)
public fun <A, B> Sequence<A>.crosswalk(f: (A) -> Sequence<B>): Sequence<Sequence<B>> =
  fold(emptySequence()) { bs, a ->
    f(a).align(bs) { ior ->
      ior.fold(
        { sequenceOf(it) },
        ::identity,
        { l, r -> sequenceOf(l) + r }
      )
    }
  }

@OverloadResolutionByLambdaReturnType
@JvmName("crosswalkT")
public fun <A, B> Sequence<A>.crosswalk(f: (A) -> Iterable<B>): List<List<B>> =
  fold(emptyList()) { bs, a ->
    f(a).align(bs) { ior ->
      ior.fold(
        { listOf(it) },
        ::identity,
        { l, r -> listOf(l) + r }
      )
    }
  }


@Deprecated(
  "This function is actually terminal. Use crosswalk(f:A -> List<B>) instead.",
  ReplaceWith("this.crosswalk{a -> f(a).toList()}")
)
public fun <A, K, V> Sequence<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, Sequence<V>> =
  fold(emptyMap()) { bs, a ->
    f(a).align(bs) { (_, ior) ->
      ior.fold(
        { sequenceOf(it) },
        ::identity,
        { l, r -> sequenceOf(l) + r }
      )
    }
  }

@Deprecated(
  "This function is actually terminal. Use crosswalkNullList(f) instead.",
  ReplaceWith("this.crosswalk{a -> f(a).toList()}")
)
public fun <A, B> Sequence<A>.crosswalkNull(f: (A) -> B?): Sequence<B>? =
  fold<A, Sequence<B>?>(emptySequence()) { bs, a ->
    Ior.fromNullables(f(a), bs)?.fold(
      { sequenceOf(it) },
      ::identity,
      { l, r -> sequenceOf(l) + r }
    )
  }

public fun <A, B> Sequence<A>.crosswalkNullList(f: (A) -> B?): List<B>? =
  fold<A, List<B>?>(emptyList()) { bs, a ->
    Ior.fromNullables(f(a), bs)?.fold(
      { listOf(it) },
      ::identity,
      { l, r -> listOf(l) + r }
    )
  }

public fun <A> Sequence<Sequence<A>>.flatten(): Sequence<A> =
  flatMap(::identity)

@Deprecated(
  "$MonoidDeprecation\n$NicheAPI",
  ReplaceWith("this.fold(initial){ acc, a -> acc + a }", "arrow.core.sequence")
)
public fun <A> Sequence<A>.fold(MA: Monoid<A>): A = MA.run {
  this@fold.fold(empty()) { acc, a ->
    acc.combine(a)
  }
}

@Deprecated(
  "$MonoidDeprecation\n$NicheAPI",
  ReplaceWith("fold(initial){ acc, a -> acc, f(a) } }", "arrow.core.sequence")
)
public fun <A, B> Sequence<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B = MB.run {
  this@foldMap.fold(empty()) { acc, a ->
    acc.combine(f(a))
  }
}

/**
 * Logical conditional. The equivalent of Prolog's soft-cut.
 * If its first argument succeeds at all, then the results will be
 * fed into the success branch. Otherwise, the failure branch is taken.
 *
 * ```kotlin
 * import arrow.core.ifThen
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    sequenceOf(1,2,3).ifThen(sequenceOf("empty")) { i ->
 *      sequenceOf("$i, ${i + 1}")
 *    }
 *   //sampleEnd
 *   println(result.toList())
 * }
 * ```
 * <!--- KNIT example-sequence-03.kt -->
 */
@Deprecated(
  "Use flatMap and ifEmpty instead.\n$NicheAPI",
  ReplaceWith("flatMap(ffa).ifEmpty { fb }")
)
public fun <A, B> Sequence<A>.ifThen(fb: Sequence<B>, ffa: (A) -> Sequence<B>): Sequence<B> =
  flatMap(ffa).ifEmpty { fb }

/**
 * Interleaves the elements of `this` [Sequence] with those of [other] [Sequence].
 * Elements of `this` and [other] are taken in turn, and the resulting list is the concatenation of the interleaved elements.
 * If one [Sequence] is longer than the other, the remaining elements are appended to the end.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val tags = generateSequence { "#" }.take(5)
 *   val numbers = generateSequence(0) { it + 1 }.take(3)
 *   tags.interleave(numbers).toList() shouldBe listOf("#", 0, "#", 1, "#", 2, "#", "#")
 * }
 * ```
 * <!--- KNIT example-sequence-04.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A> Sequence<A>.interleave(other: Sequence<A>): Sequence<A> =
  sequence {
    val lsIterator = this@interleave.iterator()
    val rsIterator = other.iterator()

    while (lsIterator.hasNext() && rsIterator.hasNext()) {
      yield(lsIterator.next())
      yield(rsIterator.next())
    }
    yieldAll(lsIterator)
    yieldAll(rsIterator)
  }

/**
 * Returns a [Sequence] containing the result of applying some transformation `(A?, B) -> C`
 * on a zip, excluding all cases where the right value is null.
 *
 * Example:
 * ```kotlin
 * import arrow.core.leftPadZip
 *
 * //sampleStart
 * val left = sequenceOf(1, 2).leftPadZip(sequenceOf(3)) { l, r -> l?.plus(r) ?: r }    // Result: [4]
 * val right = sequenceOf(1).leftPadZip(sequenceOf(3, 4)) { l, r -> l?.plus(r) ?: r }   // Result: [4, 4]
 * val both = sequenceOf(1, 2).leftPadZip(sequenceOf(3, 4)) { l, r -> l?.plus(r) ?: r } // Result: [4, 6]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 * <!--- KNIT example-sequence-05.kt -->
 */
public fun <A, B, C> Sequence<A>.leftPadZip(other: Sequence<B>, fab: (A?, B) -> C): Sequence<C> =
  padZip(other) { a: A?, b: B? -> b?.let { fab(a, it) } }.mapNotNull(::identity)

/**
 * Returns a [Sequence<Pair<A?, B>>] containing the zipped values of the two sequences
 * with null for padding on the left.
 *
 * Example:
 * ```kotlin
 * import arrow.core.leftPadZip
 *
 * //sampleStart
 * val padRight = sequenceOf(1, 2).leftPadZip(sequenceOf("a"))        // Result: [Pair(1, "a")]
 * val padLeft = sequenceOf(1).leftPadZip(sequenceOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]
 * val noPadding = sequenceOf(1, 2).leftPadZip(sequenceOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padRight = $padRight")
 *   println("padLeft = $padLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-sequence-06.kt -->
 */
public fun <A, B> Sequence<A>.leftPadZip(other: Sequence<B>): Sequence<Pair<A?, B>> =
  this.leftPadZip(other) { a, b -> a to b }

public fun <A> Sequence<A>.many(): Sequence<Sequence<A>> =
  if (none()) sequenceOf(emptySequence())
  else map { generateSequence { it } }

public fun <A> Sequence<A>.once(): Sequence<A> =
  firstOrNull()?.let { sequenceOf(it) } ?: emptySequence()

/**
 * Returns a [Sequence<Pair<A?, B?>>] containing the zipped values of the two sequences with null for padding.
 *
 * Example:
 * ```kotlin
 * import arrow.core.padZip
 *
 * //sampleStart
 * val padRight = sequenceOf(1, 2).padZip(sequenceOf("a"))       // Result: [Pair(1, "a"), Pair(2, null)]
 * val padLeft = sequenceOf(1).padZip(sequenceOf("a", "b"))      // Result: [Pair(1, "a"), Pair(null, "b")]
 * val noPadding = sequenceOf(1, 2).padZip(sequenceOf("a", "b")) // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padRight = $padRight")
 *   println("padLeft = $padLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-sequence-07.kt -->
 */
public fun <A, B> Sequence<A>.padZip(other: Sequence<B>): Sequence<Pair<A?, B?>> =
  alignRec(
    this,
    other,
    { a -> Pair(a, null) },
    { b -> Pair(null, b) },
    { a, b -> Pair(a, b) }
  )

/**
 * Returns a [Sequence] containing the result of applying some transformation `(A?, B?) -> C` on a zip.
 *
 * ```kotlin
 * import arrow.core.padZip
 *
 * //sampleStart
 * val padZipRight = sequenceOf(1, 2).padZip(sequenceOf(3)) { l, r -> (l?:0) + (r?:0) }  // Result: [4, 2]
 * val padZipLeft = sequenceOf(1).padZip(sequenceOf(3, 4)) { l, r -> (l?:0) + (r?:0) }   // Result: [4, 4]
 * val noPadding = sequenceOf(1, 2).padZip(sequenceOf(3, 4)) { l, r -> (l?:0) + (r?:0) } // Result: [4, 6]
 * //sampleEnd
 *
 * fun main() {
 *   println("padZipRight = $padZipRight")
 *   println("padZipLeft = $padZipLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-sequence-08.kt -->
 */
public fun <A, B, C> Sequence<A>.padZip(other: Sequence<B>, fa: (A?, B?) -> C): Sequence<C> =
  alignRec(
    this,
    other,
    { a -> fa(a, null) },
    { b -> fa(null, b) },
    { a, b -> fa(a, b) }
  )

@Deprecated(
  "$SemigroupDeprecation\n$NicheAPI",
  ReplaceWith("Sequence<Sequence<A>> { List<Sequence<A>>(n) { this }.iterator() }")
)
public fun <A> Sequence<A>.replicate(n: Int): Sequence<Sequence<A>> =
  Sequence { List(n) { this@replicate }.iterator() }

@Deprecated(NicheAPI)
public fun <A> Sequence<A>.replicate(n: Int, MA: Monoid<A>): Sequence<A> =
  if (n <= 0) sequenceOf(MA.empty())
  else this@replicate.zip(replicate(n - 1, MA)) { a, xs -> MA.run { a + xs } }

/**
 * Returns a [Sequence<C>] containing the result of applying some transformation `(A, B?) -> C`
 * on a zip, excluding all cases where the left value is null.
 *
 * Example:
 * ```kotlin
 * import arrow.core.rightPadZip
 *
 * //sampleStart
 * val left = sequenceOf(1, 2).rightPadZip(sequenceOf(3)) { l, r -> l + (r?:0) }    // Result: [4, 2]
 * val right = sequenceOf(1).rightPadZip(sequenceOf(3, 4)) { l, r -> l + (r?:0) }   // Result: [4]
 * val both = sequenceOf(1, 2).rightPadZip(sequenceOf(3, 4)) { l, r -> l + (r?:0) } // Result: [4, 6]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 * <!--- KNIT example-sequence-09.kt -->
 */
public fun <A, B, C> Sequence<A>.rightPadZip(other: Sequence<B>, fa: (A, B?) -> C): Sequence<C> =
  other.leftPadZip(this) { a, b -> fa(b, a) }

/**
 * Returns a [Sequence<Pair<A, B?>>] containing the zipped values of the two sequences
 * with null for padding on the right.
 *
 * Example:
 * ```kotlin
 * import arrow.core.rightPadZip
 *
 * //sampleStart
 * val padRight = sequenceOf(1, 2).rightPadZip(sequenceOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]
 * val padLeft = sequenceOf(1).rightPadZip(sequenceOf("a", "b"))       // Result: [Pair(1, "a")]
 * val noPadding = sequenceOf(1, 2).rightPadZip(sequenceOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padRight = $padRight")
 *   println("padLeft = $padLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 * <!--- KNIT example-sequence-10.kt -->
 */
public fun <A, B> Sequence<A>.rightPadZip(other: Sequence<B>): Sequence<Pair<A, B?>> =
  this.rightPadZip(other) { a, b -> a to b }

/**
 * aligns two structures and combine them with the given [combine]
 */
public fun <A> Sequence<A>.salign(
  other: Sequence<A>,
  combine: (A, A) -> A
): Sequence<A> =
  align(other) { it.fold(::identity, ::identity, combine) }

/**
 * aligns two structures and combine them with the given [Semigroup.combine]
 */
@Deprecated(SemigroupDeprecation, ReplaceWith("salign(other, {a, b -> a + b})", "arrow.typeclasses.combine"))
public fun <A> Sequence<A>.salign(
  SG: Semigroup<A>,
  other: Sequence<A>
): Sequence<A> =
  salign(other, SG::combine)

/**
 * Separate the inner [Either] values into the [Either.Left] and [Either.Right].
 *
 * @receiver Iterable of [Either]
 * @return a tuple containing Sequence with [Either.Left] and another Sequence with its [Either.Right] values.
 */
@Deprecated(
  "This function is actually terminal. Use separateEitherToPair instead.",
  ReplaceWith("separateEitherToPair()")
)
public fun <A, B> Sequence<Either<A, B>>.separateEither(): Pair<Sequence<A>, Sequence<B>> =
  fold(sequenceOf<A>() to sequenceOf<B>()) { (lefts, rights), either ->
    when (either) {
      is Left -> lefts + either.value to rights
      is Right -> lefts to rights + either.value
    }
  }

public fun <A, B> Sequence<Either<A, B>>.separateEitherToPair(): Pair<List<A>, List<B>> =
  fold(listOf<A>() to listOf<B>()) { (lefts, rights), either ->
    when (either) {
      is Left -> lefts + either.value to rights
      is Right -> lefts to rights + either.value
    }
  }


/**
 * Separate the inner [Validated] values into the [Validated.Invalid] and [Validated.Valid].
 *
 * @receiver Iterable of Validated
 * @return a tuple containing Sequence with [Validated.Invalid] and another Sequence with its [Validated.Valid] values.
 */
@Deprecated(
  "${ValidatedDeprMsg}SemigroupDeprecation\n$NicheAPI",
  ReplaceWith("separateEither()")
)
public fun <A, B> Sequence<Validated<A, B>>.separateValidated(): Pair<Sequence<A>, Sequence<B>> =
  fold(sequenceOf<A>() to sequenceOf<B>()) { (invalids, valids), validated ->
    when (validated) {
      is Valid -> invalids to valids + validated.value
      is Invalid -> invalids + validated.value to valids
    }
  }

public fun <E, A> Sequence<Either<E, A>>.sequence(): Either<E, List<A>> =
  traverse(::identity)

@Deprecated(
  "sequenceEither is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence().map { it.asSequence() }", "arrow.core.sequence")
)
public fun <E, A> Sequence<Either<E, A>>.sequenceEither(): Either<E, Sequence<A>> =
  sequence().map { it.asSequence() }

public fun <A> Sequence<Option<A>>.sequence(): Option<List<A>> =
  traverse(::identity)

@Deprecated(
  "sequenceOption is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence().map { it.asSequence() }", "arrow.core.sequence")
)
public fun <A> Sequence<Option<A>>.sequenceOption(): Option<Sequence<A>> =
  sequence().map { it.asSequence() }

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate(semigroup::combine) { it.bind() }.toValidated()",
    "arrow.core.mapOrAccumulate",
    "arrow.typeclasses.combine"
  )
)
public fun <E, A> Sequence<Validated<E, A>>.sequence(semigroup: Semigroup<E>): Validated<E, List<A>> =
  mapOrAccumulate(semigroup::combine) { it.bind() }.toValidated()

@Deprecated(
  "sequenceValidated is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence(semigroup).map { it.asSequence() }", "arrow.core.sequence")
)
public fun <E, A> Sequence<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Sequence<A>> =
  sequence(semigroup).map { it.asSequence() }

@Deprecated("some is being deprecated in favor of map", ReplaceWith("map { generateSequence { this } }"))
public fun <A> Sequence<A>.some(): Sequence<Sequence<A>> =
  if (none()) emptySequence()
  else map { generateSequence { it } }

/**
 * attempt to split the computation, giving access to the first result.
 *
 * ```kotlin
 * import arrow.core.split
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   sequenceOf("A", "B", "C").split()?.let { (tail, head) ->
 *     head shouldBe "A"
 *     tail.toList() shouldBe listOf("B", "C")
 *   }
 *   emptySequence<String>().split() shouldBe null
 * }
 * ```
 * <!--- KNIT example-sequence-11.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A> Sequence<A>.split(): Pair<Sequence<A>, A>? =
  firstOrNull()?.let { first ->
    Pair(tail(), first)
  }

/** Alias for drop(1) */
public fun <A> Sequence<A>.tail(): Sequence<A> =
  drop(1)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public fun <E, A, B> Sequence<A>.traverse(f: (A) -> Either<E, B>): Either<E, List<B>> {
  // Note: Using a mutable list here avoids the stackoverflows one can accidentally create when using
  //  Sequence.plus instead. But we don't convert the sequence to a list beforehand to avoid
  //  forcing too much of the sequence to be evaluated.
  val acc = mutableListOf<B>()
  forEach { a ->
    when (val res = f(a)) {
      is Right -> acc.add(res.value)
      is Left -> return@traverse res
    }
  }
  return acc.toList().right()
}

@Deprecated(
  "traverseEither is being renamed to traverse to simplify the Arrow API",
  ReplaceWith("traverse(f).map { it.asSequence() }", "arrow.core.traverse")
)
public fun <E, A, B> Sequence<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Sequence<B>> =
  traverse(f).map { it.asSequence() }

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public fun <A, B> Sequence<A>.traverse(f: (A) -> Option<B>): Option<List<B>> {
  // Note: Using a mutable list here avoids the stackoverflows one can accidentally create when using
  //  Sequence.plus instead. But we don't convert the sequence to a list beforehand to avoid
  //  forcing too much of the sequence to be evaluated.
  val acc = mutableListOf<B>()
  forEach { a ->
    when (val res = f(a)) {
      is Some -> acc.add(res.value)
      is None -> return@traverse res
    }
  }
  return Some(acc)
}

@Deprecated(
  "traverseOption is being renamed to traverse to simplify the Arrow API",
  ReplaceWith("traverse(f).map { it.asSequence() }", "arrow.core.traverse")
)
public fun <A, B> Sequence<A>.traverseOption(f: (A) -> Option<B>): Option<Sequence<B>> =
  traverse(f).map { it.asSequence() }

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "this.mapOrAccumulate<E, A, B>({e1, e2 -> e1 + e2}) { f(it).bind<E, B>() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public fun <E, A, B> Sequence<A>.traverse(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, List<B>> =
  mapOrAccumulate(semigroup::combine) { f(it).bind() }.toValidated()

public fun <Error, A, B> Sequence<A>.mapOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): Either<Error, List<B>> {
  var left: Any? = EmptyValue
  val right = mutableListOf<B>()
  for (item in this)
    fold({ transform(RaiseAccumulate(this), item) }, { errors -> left = EmptyValue.combine(left, errors.reduce(combine), combine) }, { b -> right.add(b) })
  return if (left !== EmptyValue) EmptyValue.unbox<Error>(left).left() else right.right()
}

public fun <Error, A, B> Sequence<A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): Either<NonEmptyList<Error>, List<B>> {
  val left = mutableListOf<Error>()
  val right = mutableListOf<B>()
  for (item in this)
    fold({ transform(RaiseAccumulate(this), item) }, { errors -> left.addAll(errors) }, { b -> right.add(b) })
  return left.toNonEmptyListOrNull()?.left() ?: right.right()
}

@Deprecated(
  "traverseValidated is being renamed to traverse to simplify the Arrow API",
  ReplaceWith(
    "mapOrAccumulate{e1, e2 -> e1 + e2} { f(it).bind() }.toValidated().map { it.asSequence() }",
    "`arrow.core.mapOrAccumulate`"
  )
)
public fun <E, A, B> Sequence<A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, Sequence<B>> =
  traverse(semigroup, f).map { it.asSequence() }

@Deprecated(
  "This function is actually terminal. Use unalignToPair instead.",
  ReplaceWith("unalignToPair()")
)
public fun <A, B> Sequence<Ior<A, B>>.unalign(): Pair<Sequence<A>, Sequence<B>> =
  fold(emptySequence<A>() to emptySequence()) { (l, r), x ->
    x.fold(
      { l + it to r },
      { l to r + it },
      { a, b -> l + a to r + b }
    )
  }

/**
 * splits an union into its component parts.
 *
 * ```kotlin
 * import arrow.core.bothIor
 * import arrow.core.leftIor
 * import arrow.core.unalignToPair
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf(("A" to 1).bothIor(), ("B" to 2).bothIor(), "C".leftIor()).unalignToPair()
 *   //sampleEnd
 *   println("(${result.first}, ${result.second})")
 * }
 * ```
 * <!--- KNIT example-sequence-12.kt -->
 */
public fun <A, B> Sequence<Ior<A, B>>.unalignToPair(): Pair<List<A>, List<B>> =
  fold(emptyList<A>() to emptyList()) { (l, r), x ->
    x.fold(
      { l + it to r },
      { l to r + it },
      { a, b -> l + a to r + b }
    )
  }


@Deprecated(
  "This function is actually terminal. Use unalignToPair instead.",
  ReplaceWith("unalignToPair(fa)")
)
public fun <A, B, C> Sequence<C>.unalign(fa: (C) -> Ior<A, B>): Pair<Sequence<A>, Sequence<B>> =
  map(fa).unalign()

/**
 * after applying the given function, splits the resulting union shaped structure into its components parts
 *
 * ```kotlin
 * import arrow.core.leftIor
 * import arrow.core.unalignToPair
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf(1, 2, 3).unalignToPair { it.leftIor() }
 *   //sampleEnd
 *   println("(${result.first.toList()}, ${result.second.toList()})")
 * }
 * ```
 * <!--- KNIT example-sequence-13.kt -->
 */
public fun <A, B, C> Sequence<C>.unalignToPair(fa: (C) -> Ior<A, B>): Pair<List<A>, List<B>> =
  map(fa).unalignToPair()

@Deprecated(
  NicheAPI + "Prefer using flatMap + fold",
  ReplaceWith(
    "flatMap { either -> either.fold<Sequence<B>>({ emptySequence() }, { b -> sequenceOf(b) }) }"
  )
)
public fun <A, B> Sequence<Either<A, B>>.uniteEither(): Sequence<B> =
  flatMap { either ->
    either.fold({ emptySequence() }, { b -> sequenceOf(b) })
  }

@Deprecated(
  ValidatedDeprMsg,
  ReplaceWith(
    "flatMap { validated -> validated.toEither().fold<Sequence<B>>({ emptySequence() }, { b -> sequenceOf(b) })}",
    "arrow.core.traverse"
  )
)
public fun <A, B> Sequence<Validated<A, B>>.uniteValidated(): Sequence<B> =
  flatMap { validated ->
    validated.fold({ emptySequence() }, { b -> sequenceOf(b) })
  }

/**
 * Fair conjunction. Similarly to interleave
 *
 * ```kotlin
 * import arrow.core.unweave
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf(1,2,3).unweave { i -> sequenceOf("$i, ${i + 1}") }
 *   //sampleEnd
 *   println(result.toList())
 * }
 * ```
 * <!--- KNIT example-sequence-14.kt -->
 */
public fun <A, B> Sequence<A>.unweave(ffa: (A) -> Sequence<B>): Sequence<B> =
  split()?.let { (fa, a) ->
    ffa(a).interleave(fa.unweave(ffa))
  } ?: emptySequence()

@Deprecated(
  "This function is actually terminal. Use unzipToPair instead.",
  ReplaceWith("unzipToPair()")
)
public fun <A, B> Sequence<Pair<A, B>>.unzip(): Pair<Sequence<A>, Sequence<B>> =
  fold(emptySequence<A>() to emptySequence()) { (l, r), x ->
    l + x.first to r + x.second
  }

/**
 * unzips the structure holding the resulting elements in an `Pair`
 *
 * ```kotlin
 * import arrow.core.unzipToPair
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf("A" to 1, "B" to 2).unzipToPair()
 *   //sampleEnd
 *   println("(${result.first}, ${result.second})")
 * }
 * ```
 * <!--- KNIT example-sequence-15.kt -->
 */
public fun <A, B> Sequence<Pair<A, B>>.unzipToPair(): Pair<List<A>, List<B>> =
  fold(emptyList<A>() to emptyList()) { (l, r), x ->
    l + x.first to r + x.second
  }


@Deprecated(
  "This function is actually terminal. Use unzipToPair instead.",
  ReplaceWith("unzipToPair(fc)")
)
public fun <A, B, C> Sequence<C>.unzip(fc: (C) -> Pair<A, B>): Pair<Sequence<A>, Sequence<B>> =
  map(fc).unzip()

/**
 * after applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin
 * import arrow.core.unzipToPair
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    sequenceOf("A:1", "B:2", "C:3").unzipToPair { e ->
 *      e.split(":").let {
 *        it.first() to it.last()
 *      }
 *    }
 *   //sampleEnd
 *   println("(${result.first}, ${result.second})")
 * }
 * ```
 * <!--- KNIT example-sequence-16.kt -->
 */
public fun <A, B, C> Sequence<C>.unzipToPair(fc: (C) -> Pair<A, B>): Pair<List<A>, List<B>> =
  map(fc).unzipToPair()


@Deprecated(
  "void is being deprecated in favor of simple Iterable.map.\n$NicheAPI",
  ReplaceWith("map { }")
)
public fun <A> Sequence<A>.void(): Sequence<Unit> =
  map { Unit }

/**
 * Given [A] is a subtype of [B], re-type this value from Sequence<A> to Sequence<B>
 *
 * ```kotlin
 * import arrow.core.widen
 *
 * fun main(args: Array<String>) {
 *   val original: Sequence<String> = sequenceOf("Hello World")
 *   val result: Sequence<CharSequence> = original.widen()
 * }
 * ```
 * <!--- KNIT example-sequence-17.kt -->
 */
public fun <B, A : B> Sequence<A>.widen(): Sequence<B> =
  this

/**
 * Filters out all elements that are [None],
 * and unwraps the remaining elements [Some] values.
 *
 * ```kotlin
 * import arrow.core.None
 * import arrow.core.Some
 * import arrow.core.filterOption
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 * generateSequence(0) { it + 1 }
 *   .map { if (it % 2 == 0) Some(it) else None }
 *   .filterOption()
 *   .take(5)
 *   .toList() shouldBe listOf(0, 2, 4, 6, 8)
 * }
 * ```
 * <!--- KNIT example-sequence-18.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A> Sequence<Option<A>>.filterOption(): Sequence<A> =
  sequence {
    forEach { option ->
      option.fold({ }, { a -> yield(a) })
    }
  }
