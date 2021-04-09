package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Semigroup
import kotlin.collections.flatMap as _flatMap

/**
 * Combines to structures by taking the intersection of their shapes
 * and using `Pair` to hold the elements.
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
  zip(other) { _, a, b -> Pair(a, b) }

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
inline fun <Key, A, B, C> Map<Key, A>.zip(other: Map<Key, B>, map: (Key, A, B) -> C): Map<Key, C> {
  val destination = LinkedHashMap<Key, C>(size)
  for ((key, bb) in this) {
    Nullable.zip(other[key]) { cc -> map(key, bb, cc) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  map: (Key, B, C, D) -> E
): Map<Key, E> {
  val destination = LinkedHashMap<Key, E>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key]) { cc, dd -> map(key, bb, cc, dd) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E, F> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  map: (Key, B, C, D, E) -> F
): Map<Key, F> {
  val destination = LinkedHashMap<Key, F>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key], e[key]) { cc, dd, ee -> map(key, bb, cc, dd, ee) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E, F, G> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  map: (Key, B, C, D, E, F) -> G
): Map<Key, G> {
  val destination = LinkedHashMap<Key, G>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key], e[key], f[key]) { cc, dd, ee, ff -> map(key, bb, cc, dd, ee, ff) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E, F, G, H> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  map: (Key, B, C, D, E, F, G) -> H
): Map<Key, H> {
  val destination = LinkedHashMap<Key, H>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key], e[key], f[key], g[key]) { cc, dd, ee, ff, gg -> map(key, bb, cc, dd, ee, ff, gg) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E, F, G, H, I> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  h: Map<Key, H>,
  map: (Key, B, C, D, E, F, G, H) -> I
): Map<Key, I> {
  val destination = LinkedHashMap<Key, I>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key], e[key], f[key], g[key], h[key]) { cc, dd, ee, ff, gg, hh -> map(key, bb, cc, dd, ee, ff, gg, hh) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E, F, G, H, I, J> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  h: Map<Key, H>,
  i: Map<Key, I>,
  map: (Key, B, C, D, E, F, G, H, I) -> J
): Map<Key, J> {
  val destination = LinkedHashMap<Key, J>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key], e[key], f[key], g[key], h[key], i[key]) { cc, dd, ee, ff, gg, hh, ii -> map(key, bb, cc, dd, ee, ff, gg, hh, ii) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E, F, G, H, I, J, K> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  h: Map<Key, H>,
  i: Map<Key, I>,
  j: Map<Key, J>,
  map: (Key, B, C, D, E, F, G, H, I, J) -> K
): Map<Key, K> {
  val destination = LinkedHashMap<Key, K>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key], e[key], f[key], g[key], h[key], i[key], j[key]) { cc, dd, ee, ff, gg, hh, ii, jj -> map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

inline fun <Key, B, C, D, E, F, G, H, I, J, K, L> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  h: Map<Key, H>,
  i: Map<Key, I>,
  j: Map<Key, J>,
  k: Map<Key, K>,
  map: (Key, B, C, D, E, F, G, H, I, J, K) -> L
): Map<Key, L> {
  val destination = LinkedHashMap<Key, L>(size)
  for ((key, bb) in this) {
    Nullable.zip(c[key], d[key], e[key], f[key], g[key], h[key], i[key], j[key], k[key]) { cc, dd, ee, ff, gg, hh, ii, jj, kk ->
      map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
    }?.let { l -> destination.put(key, l) }
  }
  return destination
}

fun <K, A, B> Map<K, A>.flatMap(f: (Map.Entry<K, A>) -> Map<K, B>): Map<K, B> =
  _flatMap { entry ->
    f(entry)[entry.key]?.let { Pair(entry.key, it) }.asIterable()
  }.toMap()

inline fun <K, E, A, B> Map<K, A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Map<K, B>> {
  val acc = mutableMapOf<K, B>()
  forEach { (k, v) ->
    when (val res = f(v)) {
      is Right -> acc[k] = res.value
      is Left -> return@traverseEither res
    }
  }
  return acc.right()
}

fun <K, E, A> Map<K, Either<E, A>>.sequenceEither(): Either<E, Map<K, A>> =
  traverseEither(::identity)

inline fun <K, E, A, B> Map<K, A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, Map<K, B>> {
  return foldLeft(mutableMapOf<K, B>().valid() as Validated<E, MutableMap<K, B>>) { acc, (k, v) ->
    when (val res = f(v)) {
      is Valid -> when (acc) {
        is Valid -> acc.also { it.value[k] = res.value }
        is Invalid -> acc
      }
      is Invalid -> when (acc) {
        is Valid -> res
        is Invalid -> semigroup.run { acc.value.combine(res.value).invalid() }
      }
    }
  }
}

fun <K, E, A> Map<K, Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Map<K, A>> =
  traverseValidated(semigroup, ::identity)

fun <K, A> Map<K, A>.void(): Map<K, Unit> =
  mapValues { Unit }

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
    Ior.fromNullables(this[key], b[key])?.let { key to it }
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
fun <K, A, B> Map<K, A>.padZip(other: Map<K, B>): Map<K, Pair<A?, B?>> =
  align(other) { (_, ior) ->
    ior.fold(
      { it to null },
      { null to it },
      { a, b -> a to b }
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
 *      "first" to ("A" to 1).bothIor(),
 *      "second" to ("B" to 2).bothIor(),
 *      "third" to "C".leftIor()
 *    ).unalign()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B> Map<K, Ior<A, B>>.unalign(): Pair<Map<K, A>, Map<K, B>> =
  entries.fold(emptyMap<K, A>() to emptyMap()) { (ls, rs), (k, v) ->
    v.fold(
      { a -> ls.plus(k to a) to rs },
      { b -> ls to rs.plus(k to b) },
      { a, b -> ls.plus(k to a) to rs.plus(k to b) }
    )
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
fun <K, A, B, C> Map<K, C>.unalign(fa: (Map.Entry<K, C>) -> Ior<A, B>): Pair<Map<K, A>, Map<K, B>> =
  mapValues(fa).unalign()

/**
 * Unzips the structure holding the resulting elements in an `Pair`
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *      mapOf("first" to ("A" to 1), "second" to ("B" to 2)).unzip()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B> Map<K, Pair<A, B>>.unzip(): Pair<Map<K, A>, Map<K, B>> =
  entries.fold(emptyMap<K, A>() to emptyMap()) { (ls, rs), (k, v) ->
    ls.plus(k to v.first) to rs.plus(k to v.second)
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
 *        it.first() to it.last()
 *      }
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <K, A, B, C> Map<K, C>.unzip(fc: (Map.Entry<K, C>) -> Pair<A, B>): Pair<Map<K, A>, Map<K, B>> =
  mapValues(fc).unzip()

fun <K, A> Map<K, A>.combine(SG: Semigroup<A>, b: Map<K, A>): Map<K, A> = with(SG) {
  if (size < b.size) foldLeft(b) { my, (k, b) -> my + Pair(k, b.maybeCombine(my[k])) }
  else b.foldLeft(this@combine) { my, (k, a) -> my + Pair(k, a.maybeCombine(my[k])) }
}

fun <K, A> Iterable<Map<K, A>>.combineAll(SG: Semigroup<A>): Map<K, A> =
  fold(emptyMap()) { acc, map -> acc.combine(SG, map) }

@Deprecated("Map<K, A>.foldRight is being deprecated because its functionality differs from other definitions of foldRight within arrow.")
inline fun <K, A, B> Map<K, A>.foldRight(b: B, f: (Map.Entry<K, A>, B) -> B): B =
  this.entries.reversed().fold(b) { x, y: Map.Entry<K, A> -> f(y, x) }

inline fun <K, A, B> Map<K, A>.foldLeft(b: B, f: (B, Map.Entry<K, A>) -> B): B {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}

internal fun <K, A> Pair<K, A>?.asIterable(): Iterable<Pair<K, A>> =
  when (this) {
    null -> emptyList()
    else -> listOf(this)
  }
