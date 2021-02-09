package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

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

fun <A, B> Sequence<A>.ap(ff: Sequence<(A) -> B>): Sequence<B> =
  flatMap { a -> ff.map { f -> f(a) } }

fun <A, B> Sequence<A>.apEval(ff: Eval<Sequence<(A) -> B>>): Eval<Sequence<B>> =
  ff.map { this.ap(it) }

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

fun <E, A> Sequence<Either<E, Sequence<A>>>.flatSequenceEither(): Either<E, Sequence<A>> =
  flatTraverseEither(::identity)

fun <E, A> Sequence<Validated<E, Sequence<A>>>.flatSequenceValidated(semigroup: Semigroup<E>): Validated<E, Sequence<A>> =
  flatTraverseValidated(semigroup, ::identity)

fun <E, A, B> Sequence<A>.flatTraverseEither(f: (A) -> Either<E, Sequence<B>>): Either<E, Sequence<B>> =
  foldRight<A, Either<E, Sequence<B>>>(emptySequence<B>().right()) { a, acc ->
    f(a).ap(acc.map { bs -> { b: Sequence<B> -> b + bs } })
  }

fun <E, A, B> Sequence<A>.flatTraverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, Sequence<B>>): Validated<E, Sequence<B>> =
  foldRight<A, Validated<E, Sequence<B>>>(emptySequence<B>().valid()) { a, acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: Sequence<B> -> b + bs } })
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

inline fun <A, B> Sequence<A>.foldRight(initial: B, operation: (A, B) -> B): B =
  toList().foldRight(initial, operation)

fun <A, B> Sequence<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
  fun Iterator<A>.loop(): Eval<B> =
    if (hasNext()) f(next(), Eval.defer { loop() }) else lb
  return Eval.defer { this.iterator().loop() }
}

/**
 *  Applies [f] to an [A] inside [Sequence] and returns the [Sequence] structure with a pair of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  ```kotlin:ank:playground
 * import arrow.core.fproduct
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   sequenceOf("Hello").fproduct { "$it World" }
 *   //sampleEnd
 *   println(result.toList())
 *  }
 *  ```
 */
fun <A, B> Sequence<A>.fproduct(f: (A) -> B): Sequence<Pair<A, B>> =
  map { a -> a to f(a) }

fun <B> Sequence<Boolean>.ifM(ifFalse: () -> Sequence<B>, ifTrue: () -> Sequence<B>): Sequence<B> =
  flatMap { bool ->
    if (bool) ifTrue() else ifFalse()
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

fun <A, B> Sequence<A>.mapConst(b: B): Sequence<B> =
  map { b }

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
  else SequenceK.mapN(this@replicate, replicate(n - 1, MA)) { a, xs -> MA.run { a + xs } }

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

fun <A, B> Sequence<Either<A, B>>.selectM(f: Sequence<(A) -> B>): Sequence<B> =
  flatMap { it.fold({ a -> f.map { ff -> ff(a) } }, { b -> sequenceOf(b) }) }

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

fun <E> Sequence<Either<E, *>>.sequenceEither_(): Either<E, Unit> =
  traverseEither_(::identity)

fun <E, A> Sequence<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Sequence<A>> =
  traverseValidated(semigroup, ::identity)

fun <E> Sequence<Validated<E, *>>.sequenceValidated_(semigroup: Semigroup<E>): Validated<E, Unit> =
  traverseValidated_(semigroup, ::identity)

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

fun <E, A, B> Sequence<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Sequence<B>> =
  foldRight<A, Either<E, Sequence<B>>>(emptySequence<B>().right()) { a, acc ->
    f(a).ap(acc.map { bs -> { b: B -> sequenceOf(b) + bs } })
  }

fun <E, A> Sequence<A>.traverseEither_(f: (A) -> Either<E, *>): Either<E, Unit> {
  val void = { _: Unit -> { _: Any? -> Unit } }
  return foldRight<A, Either<E, Unit>>(Unit.right()) { a, acc ->
    f(a).ap(acc.map(void))
  }
}

fun <E, A, B> Sequence<A>.traverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, B>): Validated<E, Sequence<B>> =
  foldRight<A, Validated<E, Sequence<B>>>(emptySequence<B>().valid()) { a, acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: B -> sequenceOf(b) + bs } })
  }

fun <E, A> Sequence<A>.traverseValidated_(semigroup: Semigroup<E>, f: (A) -> Validated<E, *>): Validated<E, Unit> =
  foldRight<A, Validated<E, Unit>>(Unit.valid()) { a, acc ->
    f(a).ap(semigroup, acc.map { { Unit } })
  }

/**
 *  Pairs [B] with [A] returning a Sequence<Pair<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.tupleLeft
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf("Hello", "Hello2").tupleLeft("World")
 *   //sampleEnd
 *   println(result.toList())
 *  }
 *  ```
 */
fun <A, B> Sequence<A>.tupleLeft(b: B): Sequence<Pair<B, A>> =
  map { a -> b to a }

/**
 *  Pairs [A] with [B] returning a Sequence<Pair<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.tupleRight
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = sequenceOf("Hello").tupleRight("World")
 *   //sampleEnd
 *   println(result.toList())
 *  }
 *  ```
 */
fun <A, B> Sequence<A>.tupleRight(b: B): Sequence<Pair<A, B>> =
  map { a -> a to b }

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
 * unzips the structure holding the resulting elements in an `Tuple2`
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
  mapConst(Unit)

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

fun <A, B, Z> Sequence<A>.zipEval(other: Eval<Sequence<B>>): Eval<Sequence<Pair<A, B>>> =
  other.map { this.zip(it) }

fun <A, B, Z> Sequence<A>.zipEval(other: Eval<Sequence<B>>, f: (Pair<A, B>) -> Z): Eval<Sequence<Z>> =
  other.map { this.zip(it).map(f) }

fun <A> Semigroup.Companion.sequence(): Semigroup<Sequence<A>> =
  Monoid.sequence()

fun <A> Monoid.Companion.sequence(): Monoid<Sequence<A>> =
  SequenceMonoid as Monoid<Sequence<A>>

object SequenceMonoid : Monoid<Sequence<Any?>> {
  override fun empty(): Sequence<Any?> = emptySequence()
  override fun Sequence<Any?>.combine(b: Sequence<Any?>): Sequence<Any?> = this + b
}
