package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

fun <B, C, D, E> Sequence<B>.zip(
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

fun <B, C, D, E, F> Sequence<B>.zip(
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

fun <B, C, D, E, F, G> Sequence<B>.zip(
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

fun <B, C, D, E, F, G, H> Sequence<B>.zip(
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

fun <B, C, D, E, F, G, H, I> Sequence<B>.zip(
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

fun <B, C, D, E, F, G, H, I, J> Sequence<B>.zip(
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

fun <B, C, D, E, F, G, H, I, J, K> Sequence<B>.zip(
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

fun <B, C, D, E, F, G, H, I, J, K, L> Sequence<B>.zip(
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
 * Combines two structures by taking the union of their shapes and combining the elements with the given function.
 *
 * ```kotlin:ank:playground
 * import arrow.core.align
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    sequenceOf("A", "B").align(sequenceOf(1, 2, 3)) {
 *      "$it"
 *    }
 *   //sampleEnd
 *   println(result.toList())
 * }
 * ```
 */
fun <A, B, C> Sequence<A>.align(b: Sequence<B>, fa: (Ior<A, B>) -> C): Sequence<C> =
  this.align(b).map(fa)

/**
 * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
 *
 * ```kotlin:ank:playground
 * import arrow.core.align
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *     sequenceOf("A", "B").align(sequenceOf(1, 2, 3))
 *   //sampleEnd
 *   println(result.toList())
 * }
 * ```
 */
fun <A, B> Sequence<A>.align(b: Sequence<B>): Sequence<Ior<A, B>> =
  alignRec(this, b)

private fun <X, Y> alignRec(ls: Sequence<X>, rs: Sequence<Y>): Sequence<Ior<X, Y>> {
  val lsIterator = ls.iterator()
  val rsIterator = rs.iterator()

  return sequence {
    while (lsIterator.hasNext() && rsIterator.hasNext()) {
      yield(
        Ior.Both(
          lsIterator.next(),
          rsIterator.next()
        )
      )
    }
    while (lsIterator.hasNext()) yield(lsIterator.next().leftIor())
    while (rsIterator.hasNext()) yield(rsIterator.next().rightIor())
  }
}

fun <A> Sequence<A>.combineAll(MA: Monoid<A>): A = MA.run {
  this@combineAll.fold(empty()) { acc, a ->
    acc.combine(a)
  }
}

fun <A, B> Sequence<A>.crosswalk(f: (A) -> Sequence<B>): Sequence<Sequence<B>> =
  fold(emptySequence()) { bs, a ->
    f(a).align(bs) { ior ->
      ior.fold(
        { sequenceOf(it) },
        ::identity,
        { l, r -> sequenceOf(l) + r }
      )
    }
  }

fun <A, K, V> Sequence<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, Sequence<V>> =
  fold(emptyMap()) { bs, a ->
    f(a).align(bs) { (_, ior) ->
      ior.fold(
        { sequenceOf(it) },
        ::identity,
        { l, r -> sequenceOf(l) + r }
      )
    }
  }

fun <A, B> Sequence<A>.crosswalkNull(f: (A) -> B?): Sequence<B>? =
  fold<A, Sequence<B>?>(emptySequence()) { bs, a ->
    Ior.fromNullables(f(a), bs)?.fold(
      { sequenceOf(it) },
      ::identity,
      { l, r -> sequenceOf(l) + r }
    )
  }

fun <A> Sequence<Sequence<A>>.flatten(): Sequence<A> =
  flatMap(::identity)

fun <A> Sequence<A>.fold(MA: Monoid<A>): A = MA.run {
  this@fold.fold(empty()) { acc, a ->
    acc.combine(a)
  }
}

fun <A, B> Sequence<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B = MB.run {
  this@foldMap.fold(empty()) { acc, a ->
    acc.combine(f(a))
  }
}

@Deprecated(FoldRightDeprecation)
fun <A, B> Sequence<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
  fun Iterator<A>.loop(): Eval<B> =
    if (hasNext()) f(next(), Eval.defer { loop() }) else lb
  return Eval.defer { this.iterator().loop() }
}

/**
 * Logical conditional. The equivalent of Prolog's soft-cut.
 * If its first argument succeeds at all, then the results will be
 * fed into the success branch. Otherwise, the failure branch is taken.
 *
 * ```kotlin:ank:playground
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
 */
fun <A, B> Sequence<A>.ifThen(fb: Sequence<B>, ffa: (A) -> Sequence<B>): Sequence<B> =
  split()?.let { (fa, a) ->
    ffa(a) + fa.flatMap(ffa)
  } ?: fb

/**
 * interleave both computations in a fair way.
 *
 * ```kotlin:ank:playground
 * import arrow.core.interleave
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val tags = generateSequence { "#" }.take(10)
 *   val result =
 *    tags.interleave(sequenceOf("A", "B", "C"))
 *   //sampleEnd
 *   println(result.toList())
 * }
 */
fun <A> Sequence<A>.interleave(other: Sequence<A>): Sequence<A> =
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
 * Returns a [Sequence<C>] containing the result of applying some transformation `(A?, B) -> C`
 * on a zip, excluding all cases where the right value is null.
 *
 * Example:
 * ```kotlin:ank:playground
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
 */
fun <A, B, C> Sequence<A>.leftPadZip(other: Sequence<B>, fab: (A?, B) -> C): Sequence<C> =
  padZip(other) { a: A?, b: B? -> b?.let { fab(a, it) } }.mapNotNull(::identity)

/**
 * Returns a [Sequence<Pair<A?, B>>] containing the zipped values of the two sequences
 * with null for padding on the left.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.leftPadZip
 *
 * //sampleStart
 * val padRight = sequenceOf(1, 2).leftPadZip(sequenceOf("a"))        // Result: [Pair(1, "a")]
 * val padLeft = sequenceOf(1).leftPadZip(sequenceOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]
 * val noPadding = sequenceOf(1, 2).leftPadZip(sequenceOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 */
fun <A, B> Sequence<A>.leftPadZip(other: Sequence<B>): Sequence<Pair<A?, B>> =
  this.leftPadZip(other) { a, b -> a to b }

fun <A> Sequence<A>.many(): Sequence<Sequence<A>> =
  if (none()) sequenceOf(emptySequence())
  else map { generateSequence { it } }

fun <A> Sequence<A>.once(): Sequence<A> =
  firstOrNull()?.let { sequenceOf(it) } ?: emptySequence()

/**
 * Returns a [Sequence<Pair<A?, B?>>] containing the zipped values of the two sequences with null for padding.
 *
 * Example:
 * ```kotlin:ank:playground
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
 */
fun <A, B> Sequence<A>.padZip(other: Sequence<B>): Sequence<Pair<A?, B?>> =
  align(other) { ior ->
    ior.fold(
      { it to null },
      { null to it },
      { a, b -> a to b }
    )
  }

/**
 * Returns a [Sequence<C>] containing the result of applying some transformation `(A?, B?) -> C`
 * on a zip.
 *
 * Example:
 * ```kotlin:ank:playground
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
 */
fun <A, B, C> Sequence<A>.padZip(other: Sequence<B>, fa: (A?, B?) -> C): Sequence<C> =
  padZip(other).map { fa(it.first, it.second) }

@Deprecated(FoldRightDeprecation)
fun <A, B> Sequence<A>.reduceRightEvalOrNull(
  initial: (A) -> B,
  operation: (A, acc: Eval<B>) -> Eval<B>
): Eval<B?> =
  toList().reduceRightEvalOrNull(initial, operation)

fun <A> Sequence<A>.replicate(n: Int): Sequence<Sequence<A>> =
  if (n <= 0) emptySequence()
  else this.let { l -> Sequence { List(n) { l }.iterator() } }

fun <A> Sequence<A>.replicate(n: Int, MA: Monoid<A>): Sequence<A> =
  if (n <= 0) sequenceOf(MA.empty())
  else this@replicate.zip(replicate(n - 1, MA)) { a, xs -> MA.run { a + xs } }

/**
 * Returns a [Sequence<C>] containing the result of applying some transformation `(A, B?) -> C`
 * on a zip, excluding all cases where the left value is null.
 *
 * Example:
 * ```kotlin:ank:playground
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
 */
fun <A, B, C> Sequence<A>.rightPadZip(other: Sequence<B>, fa: (A, B?) -> C): Sequence<C> =
  other.leftPadZip(this) { a, b -> fa(b, a) }

/**
 * Returns a [Sequence<Pair<A, B?>>] containing the zipped values of the two sequences
 * with null for padding on the right.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.rightPadZip
 *
 * //sampleStart
 * val padRight = sequenceOf(1, 2).rightPadZip(sequenceOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]
 * val padLeft = sequenceOf(1).rightPadZip(sequenceOf("a", "b"))       // Result: [Pair(1, "a")]
 * val noPadding = sequenceOf(1, 2).rightPadZip(sequenceOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 */
fun <A, B> Sequence<A>.rightPadZip(other: Sequence<B>): Sequence<Pair<A, B?>> =
  this.rightPadZip(other) { a, b -> a to b }

/**
 * aligns two structures and combine them with the given [Semigroup.combine]
 */
fun <A> Sequence<A>.salign(
  SG: Semigroup<A>,
  other: Sequence<A>
): Sequence<A> = SG.run {
  align(other) {
    it.fold(::identity, ::identity) { a, b ->
      a.combine(b)
    }
  }
}

/**
 * Separate the inner [Either] values into the [Either.Left] and [Either.Right].
 *
 * @receiver Iterable of Validated
 * @return a tuple containing Sequence with [Either.Left] and another Sequence with its [Either.Right] values.
 */
fun <A, B> Sequence<Either<A, B>>.separateEither(): Pair<Sequence<A>, Sequence<B>> {
  val asep = flatMap { gab -> gab.fold({ sequenceOf(it) }, { emptySequence() }) }
  val bsep = flatMap { gab -> gab.fold({ emptySequence() }, { sequenceOf(it) }) }
  return asep to bsep
}

/**
 * Separate the inner [Validated] values into the [Validated.Invalid] and [Validated.Valid].
 *
 * @receiver Iterable of Validated
 * @return a tuple containing Sequence with [Validated.Invalid] and another Sequence with its [Validated.Valid] values.
 */
fun <A, B> Sequence<Validated<A, B>>.separateValidated(): Pair<Sequence<A>, Sequence<B>> {
  val asep = flatMap { gab -> gab.fold({ sequenceOf(it) }, { emptySequence() }) }
  val bsep = flatMap { gab -> gab.fold({ emptySequence() }, { sequenceOf(it) }) }
  return asep to bsep
}

fun <E, A> Sequence<Either<E, A>>.sequenceEither(): Either<E, Sequence<A>> =
  traverseEither(::identity)

fun <E, A> Sequence<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Sequence<A>> =
  traverseValidated(semigroup, ::identity)

fun <A> Sequence<A>.some(): Sequence<Sequence<A>> =
  if (none()) emptySequence()
  else map { generateSequence { it } }

/**
 * attempt to split the computation, giving access to the first result.
 *
 * ```kotlin:ank:playground
 * import arrow.core.split
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf("A", "B", "C").split()
 *   //sampleEnd
 *   result?.let { println("(${it.first.toList()}, ${it.second.toList()})") }
 * }
 */
fun <A> Sequence<A>.split(): Pair<Sequence<A>, A>? =
  firstOrNull()?.let { first ->
    Pair(tail(), first)
  }

fun <A> Sequence<A>.tail(): Sequence<A> =
  drop(1)

fun <E, A, B> Sequence<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Sequence<B>> {
  // Note: Using a mutable list here avoids the stackoverflows one can accidentally create when using
  //  Sequence.plus instead. But we don't convert the sequence to a list beforehand to avoid
  //  forcing too much of the sequence to be evaluated.
  val acc = mutableListOf<B>()
  forEach { a ->
    when (val res = f(a)) {
      is Right -> acc.add(res.value)
      is Left -> return@traverseEither res
    }
  }
  return acc.asSequence().right()
}

fun <E, A, B> Sequence<A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, Sequence<B>> = fold(mutableListOf<B>().valid() as Validated<E, MutableList<B>>) { acc, a ->
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
}.map { it.asSequence() }

/**
 * splits an union into its component parts.
 *
 * ```kotlin:ank:playground
 * import arrow.core.bothIor
 * import arrow.core.leftIor
 * import arrow.core.unalign
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf(("A" to 1).bothIor(), ("B" to 2).bothIor(), "C".leftIor()).unalign()
 *   //sampleEnd
 *   println("(${result.first.toList()}, ${result.second.toList()})")
 * }
 * ```
 */
fun <A, B> Sequence<Ior<A, B>>.unalign(): Pair<Sequence<A>, Sequence<B>> =
  fold(emptySequence<A>() to emptySequence()) { (l, r), x ->
    x.fold(
      { l + it to r },
      { l to r + it },
      { a, b -> l + a to r + b }
    )
  }

/**
 * after applying the given function, splits the resulting union shaped structure into its components parts
 *
 * ```kotlin:ank:playground
 * import arrow.core.leftIor
 * import arrow.core.unalign
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf(1, 2, 3).unalign { it.leftIor() }
 *   //sampleEnd
 *   println("(${result.first.toList()}, ${result.second.toList()})")
 * }
 * ```
 */
fun <A, B, C> Sequence<C>.unalign(fa: (C) -> Ior<A, B>): Pair<Sequence<A>, Sequence<B>> =
  map(fa).unalign()

fun <A, B> Sequence<Either<A, B>>.uniteEither(): Sequence<B> =
  flatMap { either ->
    either.fold({ emptySequence() }, { b -> sequenceOf(b) })
  }

fun <A, B> Sequence<Validated<A, B>>.uniteValidated(): Sequence<B> =
  flatMap { validated ->
    validated.fold({ emptySequence() }, { b -> sequenceOf(b) })
  }

/**
 * Fair conjunction. Similarly to interleave
 *
 * ```kotlin:ank:playground
 * import arrow.core.unweave
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf(1,2,3).unweave { i -> sequenceOf("$i, ${i + 1}") }
 *   //sampleEnd
 *   println(result.toList())
 * }
 */
fun <A, B> Sequence<A>.unweave(ffa: (A) -> Sequence<B>): Sequence<B> =
  split()?.let { (fa, a) ->
    ffa(a).interleave(fa.unweave(ffa))
  } ?: emptySequence()

/**
 * unzips the structure holding the resulting elements in an `Pair`
 *
 * ```kotlin:ank:playground
 * import arrow.core.unzip
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf("A" to 1, "B" to 2).unzip()
 *   //sampleEnd
 *   println("(${result.first.toList()}, ${result.second.toList()})")
 * }
 * ```
 */
fun <A, B> Sequence<Pair<A, B>>.unzip(): Pair<Sequence<A>, Sequence<B>> =
  fold(emptySequence<A>() to emptySequence()) { (l, r), x ->
    l + x.first to r + x.second
  }

/**
 * after applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin:ank:playground
 * import arrow.core.unzip
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    sequenceOf("A:1", "B:2", "C:3").unzip { e ->
 *      e.split(":").let {
 *        it.first() to it.last()
 *      }
 *    }
 *   //sampleEnd
 *   println("(${result.first.toList()}, ${result.second.toList()})")
 * }
 * ```
 */
fun <A, B, C> Sequence<C>.unzip(fc: (C) -> Pair<A, B>): Pair<Sequence<A>, Sequence<B>> =
  map(fc).unzip()

fun <A> Sequence<A>.void(): Sequence<Unit> =
  map { Unit }

/**
 *  Given [A] is a sub type of [B], re-type this value from Sequence<A> to Sequence<B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.widen
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: Sequence<CharSequence> =
 *     sequenceOf("Hello World").widen()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
fun <B, A : B> Sequence<A>.widen(): Sequence<B> =
  this
