package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.defaultSalt
import kotlin.collections.flatMap as _flatMap

object MapInstances

object SortedMapInstances

fun <K, A, B> Map<K, A>.flatMap(f: (Map.Entry<K, A>) -> Map<K, B>): Map<K, B> =
  _flatMap { entry ->
    f(entry)[entry.key]?.let { Pair(entry.key, it) }.asIterable()
  }.toMap()

fun <K, A, B> Map<K, A>.ap(ff: Map<K, (A) -> B>): Map<K, B> =
  ff.flatMap { (_, f) -> this.mapValues { (_, a) -> f(a) } }

inline fun <K, E, A, B> Map<K, A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Map<K, B>> =
  foldRight(emptyMap<K, B>().right()) { (k, a), acc: Either<E, Map<K, B>> ->
    f(a).ap(acc.map { bs: Map<K, B> -> { b: B -> mapOf(k to b) + bs } })
  }

inline fun <K, E, A, B> Map<K, A>.flatTraverseEither(f: (A) -> Either<E, Map<K, B>>): Either<E, Map<K, B>> =
  foldRight<K, A, Either<E, Map<K, B>>>(emptyMap<K, B>().right()) { (_, a), acc ->
    f(a).ap(acc.map { bs -> { b: Map<K, B> -> b + bs } })
  }

inline fun <K, E, A> Map<K, A>.traverseEither_(f: (A) -> Either<E, *>): Either<E, Unit> {
  val void = { _: Unit -> { _: Any? -> Unit } }
  return foldRight(Either.unit) { (_, a): Map.Entry<K, A>, acc: Either<E, Unit> ->
    f(a).ap(acc.map(void))
  }
}

fun <K, E, A> Map<K, Either<E, A>>.sequenceEither(): Either<E, Map<K, A>> =
  traverseEither(::identity)

fun <K, E, A> Map<K, Either<E, Map<K, A>>>.flatSequenceEither(): Either<E, Map<K, A>> =
  flatTraverseEither(::identity)

fun <K, E> Map<K, Either<E, *>>.sequenceEither_(): Either<E, Unit> =
  traverseEither_(::identity)

inline fun <K, E, A, B> Map<K, A>.traverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, B>): Validated<E, Map<K, B>> =
  foldRight<K, A, Validated<E, Map<K, B>>>(emptyMap<K, B>().valid()) { (k, a), acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: B -> mapOf(k to b) + bs } })
  }

inline fun <K, E, A, B> Map<K, A>.flatTraverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, Map<K, B>>): Validated<E, Map<K, B>> =
  foldRight<K, A, Validated<E, Map<K, B>>>(emptyMap<K, B>().valid()) { (_, a), acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: Map<K, B> -> b + bs } })
  }

inline fun <K, E, A> Map<K, A>.traverseValidated_(semigroup: Semigroup<E>, f: (A) -> Validated<E, *>): Validated<E, Unit> {
  val void = { _: Unit -> { _: Any? -> Unit } }
  return foldRight<K, A, Validated<E, Unit>>(Unit.valid()) { (_, a), acc ->
    f(a).ap(semigroup, acc.map(void))
  }
}

fun <K, E, A> Map<K, Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Map<K, A>> =
  traverseValidated(semigroup, ::identity)

fun <K, E, A> Map<K, Validated<E, Map<K, A>>>.flatSequenceValidated(semigroup: Semigroup<E>): Validated<E, Map<K, A>> =
  flatTraverseValidated(semigroup, ::identity)

fun <K, E> Map<K, Validated<E, *>>.sequenceValidated_(semigroup: Semigroup<E>): Validated<E, Unit> =
  traverseValidated_(semigroup, ::identity)

fun <K, A, B> Map<K, A>.fproduct(f: (A) -> B): Map<K, Tuple2<A, B>> =
  mapValues { (_, a) -> Tuple2(a, f(a)) }

fun <K, A> Map<K, A>.void(): Map<K, Unit> =
  mapValues { Unit }

fun <K, A, B> Map<K, A>.tupleLeft(b: B): Map<K, Tuple2<B, A>> =
  mapValues { (_, a) -> Tuple2(b, a) }

fun <K, A, B> Map<K, A>.tupleRight(b: B): Map<K, Tuple2<A, B>> =
  mapValues { (_, a) -> Tuple2(a, b) }

fun <K, B, A : B> Map<K, A>.widen(): Map<K, B> =
  this

fun <K, A, B> Map<K, A>.filterMap(f: (A) -> B?): Map<K, B> {
  val destination = LinkedHashMap<K, B>(mapCapacity(size))
  for ((key, a) in this) {
    f(a)?.let { l -> destination.put(key, l) }
  }
  return destination
}

/**
 * Returns a Map containing all elements that are instances of specified type parameter R.
 */
inline fun <K, reified R> Map<K, *>.filterIsInstance(): Map<K, R> =
  filterMap { it as? R }

/**
 * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    mapOf("1" to 1, "2" to 2).align(mapOf("1" to 1, "2" to 2, "3" to 3))
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B> Map<K, A>.align(b: Map<K, B>): Map<K, Ior<A, B>> =
  (keys + b.keys).mapNotNull { key ->
    Ior.fromNullables(this[key], b[key])?.let { key toT it }
  }.toMap()

/**
 * Combines two structures by taking the union of their shapes and combining the elements with the given function.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    mapOf("1" to 1, "2" to 2).align(mapOf("1" to 1, "2" to 2, "3" to 3)) { (_, a) ->
 *      "$a"
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B, C> Map<K, A>.align(b: Map<K, B>, fa: (Map.Entry<K, Ior<A, B>>) -> C): Map<K, C> =
  this.align(b).mapValues(fa)

/**
 * aligns two structures and combine them with the given Semigroups '+'
 */
fun <K, A> Map<K, A>.salign(SG: Semigroup<A>, other: Map<K, A>): Map<K, A> = SG.run {
  align(other) { (_, ior) ->
    ior.fold(::identity, ::identity) { a, b ->
      a.combine(b)
    }
  }
}

/**
 * Align two structures as in zip, but filling in blanks with null.
 */
fun <K, A, B> Map<K, A>.padZip(other: Map<K, B>): Map<K, Tuple2<A?, B?>> =
  align(other) { (_, ior) ->
    ior.fold(
      { it toT null },
      { null toT it },
      { a, b -> a toT b }
    )
  }

fun <K, A, B, C> Map<K, A>.padZip(other: Map<K, B>, fa: (K, A?, B?) -> C): Map<K, C> =
  align(other) { (k, ior) ->
    ior.fold(
      { fa(k, it, null) },
      { fa(k, null, it) },
      { a, b -> fa(k, a, b) }
    )
  }

/**
 * Splits a union into its component parts.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    mapOf(
 *      "first" to ("A" toT 1).bothIor(),
 *      "second" to ("B" toT 2).bothIor(),
 *      "third" to "C".leftIor()
 *    ).unalign()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B> Map<K, Ior<A, B>>.unalign(): Tuple2<Map<K, A>, Map<K, B>> =
  entries.fold(emptyMap<K, A>() toT emptyMap()) { (ls, rs), (k, v) ->
    v.fold(
      { a -> ls.plus(k to a) toT rs },
      { b -> ls toT rs.plus(k to b) },
      { a, b -> ls.plus(k to a) toT rs.plus(k to b) })
  }

/**
 * after applying the given function, splits the resulting union shaped structure into its components parts
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *      mapOf("1" to 1, "2" to 2, "3" to 3)
 *        .unalign { it.leftIor() }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B, C> Map<K, C>.unalign(fa: (Map.Entry<K, C>) -> Ior<A, B>): Tuple2<Map<K, A>, Map<K, B>> =
  mapValues(fa).unalign()

/**
 * Unzips the structure holding the resulting elements in an `Tuple2`
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *      mapOf("first" to ("A" toT 1), "second" to ("B" toT 2)).unzip()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B> Map<K, Tuple2<A, B>>.unzip(): Tuple2<Map<K, A>, Map<K, B>> =
  entries.fold(emptyMap<K, A>() toT emptyMap()) { (ls, rs), (k, v) ->
    ls.plus(k to v.a) toT rs.plus(k to v.b)
  }

/**
 * After applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    mapOf("first" to "A:1", "second" to "B:2", "third" to "C:3").unzip { (_, e) ->
 *      e.split(":").let {
 *        it.first() toT it.last()
 *      }
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B, C> Map<K, C>.unzip(fc: (Map.Entry<K, C>) -> Tuple2<A, B>): Tuple2<Map<K, A>, Map<K, B>> =
  mapValues(fc).unzip()

/**
 * Combines to structures by taking the intersection of their shapes
 * and using `Tuple2` to hold the elements.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    mapOf(1 to "A", 2 to "B").zip(mapOf(1 to "1", 2 to "2", 3 to "3"))
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B> Map<K, A>.zip(other: Map<K, B>): Map<K, Pair<A, B>> =
  keys.intersect(other.keys).mapNotNull { key ->
    Nullable.mapN(this[key], other[key]) { a, b -> key to (a to b) }
  }.toMap()

/**
 * Combines to structures by taking the intersection of their shapes
 * and combining the elements with the given function.
 *
 * ```kotlin:ank
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    mapOf(1 to "A", 2 to "B").zip(mapOf(1 to "1", 2 to "2", 3 to "3")) {
 *      key, a, b -> "$key -> $a # $b"
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B, C> Map<K, A>.zip(other: Map<K, B>, f: (K, A, B) -> C): Map<K, C> =
  keys.intersect(other.keys).mapNotNull { key ->
    Nullable.mapN(this[key], other[key]) { a, b -> key to f(key, a, b) }
  }.toMap()

fun <K, A> Map<K, A>.eqv(EQK: Eq<K>, EQA: Eq<A>, b: Map<K, A>): Boolean =
  if (keys.eqv(EQK, b.keys)) EQA.run {
    keys.map { key ->
      b[key]?.let { getValue(key).eqv(it) } ?: false
    }.fold(true) { b1, b2 -> b1 && b2 }
  } else false

fun <K, A> Map<K, A>.neqv(EQK: Eq<K>, EQA: Eq<A>, b: Map<K, A>): Boolean =
  !eqv(EQK, EQA, b)

fun <K, A> Eq.Companion.map(EQK: Eq<K>, EQA: Eq<A>): Eq<Map<K, A>> =
  MapEq(EQK, EQA)

private class MapEq<K, A>(
  private val EQK: Eq<K>,
  private val EQA: Eq<A>
) : Eq<Map<K, A>> {
  override fun Map<K, A>.eqv(b: Map<K, A>): Boolean =
    eqv(EQK, EQA, b)
}

fun <K, A> Map<K, A>.hashWithSalt(HK: Hash<K>, HA: Hash<A>, salt: Int): Int =
  values.toHashSet().hashWithSalt(HA, salt)
    .let { hash -> keys.hashWithSalt(HK, hash) }

fun <K, A> Map<K, A>.hash(HK: Hash<K>, HA: Hash<A>): Int =
  hashWithSalt(HK, HA, defaultSalt)

fun <K, A> Hash.Companion.map(HK: Hash<K>, HA: Hash<A>): Hash<Map<K, A>> =
  MapHash(HK, HA)

private class MapHash<K, A>(
  private val HK: Hash<K>,
  private val HA: Hash<A>
) : Hash<Map<K, A>> {
  override fun Map<K, A>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HK, HA, salt)
}

fun <K, A> Map<K, A>.show(SK: Show<K>, SA: Show<A>): String =
  "Map(${toList().k().show(Show { show(SK, SA) })})"

fun <K, A> Show.Companion.map(SK: Show<K>, SA: Show<A>): Show<Map<K, A>> =
  MapShow(SK, SA)

private class MapShow<K, A>(
  private val SK: Show<K>,
  private val SA: Show<A>
) : Show<Map<K, A>> {
  override fun Map<K, A>.show(): String = show(SK, SA)
}

fun <K, A> Map<K, A>.combine(SG: Semigroup<A>, b: Map<K, A>): Map<K, A> = with(SG) {
  if (size < b.size) foldLeft(b) { my, (k, b) -> my + Pair(k, b.maybeCombine(my[k])) }
  else b.foldLeft(this@combine) { my, (k, a) -> my + Pair(k, a.maybeCombine(my[k])) }
}

fun <K, A> Iterable<Map<K, A>>.combineAll(SG: Semigroup<A>): Map<K, A> =
  fold(emptyMap()) { acc, map -> acc.combine(SG, map) }

fun <K, A> Monoid.Companion.map(SG: Semigroup<A>): Monoid<Map<K, A>> =
  MapMonoid(SG)

private class MapMonoid<K, A>(private val SG: Semigroup<A>) : Monoid<Map<K, A>> {
  override fun empty(): Map<K, A> = emptyMap()

  override fun Map<K, A>.combine(b: Map<K, A>): Map<K, A> =
    combine(SG, b)
}
