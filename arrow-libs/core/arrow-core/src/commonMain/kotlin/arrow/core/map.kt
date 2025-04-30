@file:OptIn(ExperimentalTypeInference::class)

package arrow.core

import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.mapValuesOrAccumulate
import kotlin.experimental.ExperimentalTypeInference

/**
 * Combines to structures by taking the intersection of their shapes
 * and using `Pair` to hold the elements.
 *
 * ```kotlin
 * import arrow.core.zip
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   mapOf(1 to "A", 2 to "B")
 *     .zip(mapOf(1 to "1", 2 to "2", 3 to "3")) shouldBe mapOf(1 to Pair("A", "1"), 2 to Pair("B", "2"))
 * }
 * ```
 * <!--- KNIT example-map-01.kt -->
 * <!--- lines.isEmpty() -->
 */
public fun <K, A, B> Map<K, A>.zip(other: Map<K, B>): Map<K, Pair<A, B>> =
  zip(other) { _, a, b -> Pair(a, b) }

/**
 * Combines to structures by taking the intersection of their shapes
 * and combining the elements with the given function.
 *
 * ```kotlin
 * import arrow.core.zip
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   mapOf(1 to "A", 2 to "B").zip(mapOf(1 to "1", 2 to "2", 3 to "3")) {
 *     _, a, b -> "$a ~ $b"
 *   } shouldBe mapOf(1 to "A ~ 1", 2 to "B ~ 2")
 * }
 * ```
 * <!--- KNIT example-map-02.kt -->
 * <!--- lines.isEmpty() -->
 */
@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, B, C> Map<Key, A>.zip(other: Map<Key, B>, map: (Key, A, B) -> C): Map<Key, C> =
  buildMap(size) {
    this@zip.forEach { (key, bb) ->
      if (other.containsKey(key)) {
        put(key, map(key, bb, other[key] as B))
      }
    }
  }

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  map: (Key, B, C, D) -> E
): Map<Key, E> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D

      put(key, map(key, bb, cc, dd))
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E, F> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  map: (Key, B, C, D, E) -> F
): Map<Key, F> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key) && e.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D
      val ee = e[key] as E

      put(key, map(key, bb, cc, dd, ee))
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E, F, G> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  map: (Key, B, C, D, E, F) -> G
): Map<Key, G> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key) && e.containsKey(key) && f.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D
      val ee = e[key] as E
      val ff = f[key] as F

      put(key, map(key, bb, cc, dd, ee, ff))
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E, F, G, H> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  map: (Key, B, C, D, E, F, G) -> H
): Map<Key, H> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key) && e.containsKey(key) && f.containsKey(key) && g.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D
      val ee = e[key] as E
      val ff = f[key] as F
      val gg = g[key] as G

      put(key, map(key, bb, cc, dd, ee, ff, gg))
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E, F, G, H, I> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  h: Map<Key, H>,
  map: (Key, B, C, D, E, F, G, H) -> I
): Map<Key, I> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key) && e.containsKey(key) && f.containsKey(key) && g.containsKey(key) && h.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D
      val ee = e[key] as E
      val ff = f[key] as F
      val gg = g[key] as G
      val hh = h[key] as H

      put(key, map(key, bb, cc, dd, ee, ff, gg, hh))
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E, F, G, H, I, J> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  h: Map<Key, H>,
  i: Map<Key, I>,
  map: (Key, B, C, D, E, F, G, H, I) -> J
): Map<Key, J> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key) && e.containsKey(key) && f.containsKey(key) && g.containsKey(key) && h.containsKey(key) && i.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D
      val ee = e[key] as E
      val ff = f[key] as F
      val gg = g[key] as G
      val hh = h[key] as H
      val ii = i[key] as I

      put(
        key, map(key, bb, cc, dd, ee, ff, gg, hh, ii)
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E, F, G, H, I, J, K> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  h: Map<Key, H>,
  i: Map<Key, I>,
  j: Map<Key, J>,
  map: (Key, B, C, D, E, F, G, H, I, J) -> K
): Map<Key, K> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key) && e.containsKey(key) && f.containsKey(key) && g.containsKey(key) && h.containsKey(key) && i.containsKey(key) && j.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D
      val ee = e[key] as E
      val ff = f[key] as F
      val gg = g[key] as G
      val hh = h[key] as H
      val ii = i[key] as I
      val jj = j[key] as J

      put(key, map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj))
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, B, C, D, E, F, G, H, I, J, K, L> Map<Key, B>.zip(
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
): Map<Key, L> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (c.containsKey(key) && d.containsKey(key) && e.containsKey(key) && f.containsKey(key) && g.containsKey(key) && h.containsKey(key) && i.containsKey(key) && j.containsKey(key) && k.containsKey(key)) {
      val cc = c[key] as C
      val dd = d[key] as D
      val ee = e[key] as E
      val ff = f[key] as F
      val gg = g[key] as G
      val hh = h[key] as H
      val ii = i[key] as I
      val jj = j[key] as J
      val kk = k[key] as K

      put(key, map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj, kk))
    }
  }
}

/**
 * Transform every [Map.Entry] of the original [Map] using [f],
 * only keeping the [Map.Entry] of the transformed map that match the input [Map.Entry].
 */
@Suppress("UNCHECKED_CAST")
public fun <K, A, B> Map<K, A>.flatMapValues(f: (Map.Entry<K, A>) -> Map<K, B>): Map<K, B> =
  buildMap {
    this@flatMapValues.forEach { entry ->
      val nestedMap = f(entry)
      if (nestedMap.containsKey(entry.key)) {
        put(entry.key, nestedMap[entry.key] as B)
      }
    }
  }

@Deprecated(
  message = "Deprecated to allow for future alignment with stdlib Map#map returning List",
  replaceWith = ReplaceWith("mapValuesOrAccumulate(combine, transform)"),
)
public inline fun <K, E, A, B> Map<K, A>.mapOrAccumulate(
  combine: (E, E) -> E,
  @BuilderInference transform: RaiseAccumulate<E>.(Map.Entry<K, A>) -> B
): Either<E, Map<K, B>> = mapValuesOrAccumulate(combine, transform)

@Deprecated(
  message = "Deprecated to allow for future alignment with stdlib Map#map returning List",
  replaceWith = ReplaceWith("mapValuesOrAccumulate(transform)"),
)
public inline fun <K, E, A, B> Map<K, A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<E>.(Map.Entry<K, A>) -> B
): Either<NonEmptyList<E>, Map<K, B>> = mapValuesOrAccumulate(transform)

public inline fun <K, E, A, B> Map<K, A>.mapValuesOrAccumulate(
  combine: (E, E) -> E,
  @BuilderInference transform: RaiseAccumulate<E>.(Map.Entry<K, A>) -> B
): Either<E, Map<K, B>> = either {
  mapValuesOrAccumulate(this@mapValuesOrAccumulate, combine, transform)
}

public inline fun <K, E, A, B> Map<K, A>.mapValuesOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<E>.(Map.Entry<K, A>) -> B
): Either<NonEmptyList<E>, Map<K, B>> = either {
  mapValuesOrAccumulate(this@mapValuesOrAccumulate, transform)
}

public inline fun <K, A, B> Map<K, A>.mapValuesNotNull(transform: (Map.Entry<K, A>) -> B?): Map<K, B> =
  buildMap {
    this@mapValuesNotNull.forEach { entry ->
      transform(entry)?.let { put(entry.key, it) }
    }
  }

public fun <K, A> Map<K, Option<A>>.filterOption(): Map<K, A> =
  buildMap {
    this@filterOption.forEach { (key, option) ->
      option.fold({ }, { put(key, it) })
    }
  }

/**
 * Returns a Map containing all elements that are instances of specified type parameter R.
 */
@Suppress("UNCHECKED_CAST")
public inline fun <K, reified R> Map<K, *>.filterIsInstance(): Map<K, R> =
  filterValues { it is R } as Map<K, R>

/**
 * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val res = mapOf(1 to 1, 2 to 2).align(mapOf(1 to "1", 2 to "2", 3 to "3"))
 *   res shouldBe mapOf(1 to Ior.Both(1, "1"), 2 to Ior.Both(2, "2"), 3 to Ior.Right("3"))
 * }
 * ```
 * <!--- KNIT example-map-03.kt -->
 * <!--- lines.isEmpty() -->
 */
public fun <K, A, B> Map<K, A>.align(b: Map<K, B>): Map<K, Ior<A, B>> =
  padZip(b, { _, a -> Ior.Left(a) }, { _, bb -> Ior.Right(bb) }) { _, a, bb -> Ior.Both(a, bb) }

/**
 * Combines two structures by taking the union of their shapes and combining the elements with the given function.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   mapOf("1" to 1, "2" to 2)
 *     .align(mapOf("1" to 1, "2" to 2, "3" to 3)) { (_, a) ->
 *       "$a"
 *     } shouldBe mapOf("1" to "Ior.Both(1, 1)", "2" to Ior.Both(2, 2), "3" to Ior.Right(3))
 * }
 * ```
 * <!--- KNIT example-map-04.kt -->
 * <!--- lines.isEmpty() -->
 */
public fun <K, A, B, C> Map<K, A>.align(b: Map<K, B>, fa: (Map.Entry<K, Ior<A, B>>) -> C): Map<K, C> =
  padZip(
    b,
    { k, a -> fa(Entry(k, Ior.Left(a))) },
    { k, bb -> fa(Entry(k, Ior.Right(bb))) }
  ) { k, a, bb -> fa(Entry(k, Ior.Both(a, bb))) }

private class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V> {
  override fun hashCode(): Int = key.hashCode() xor value.hashCode()
  override fun toString(): String = "$key=$value"
  override fun equals(other: Any?): Boolean =
    other is Map.Entry<*, *> && other.key == key && other.value == value
}

public fun <K, A> Map<K, A>.salign(other: Map<K, A>, combine: (A, A) -> A): Map<K, A> =
  padZip(other, { _, a -> a }, { _, b -> b }) { _, a, b -> combine(a, b) }

/**
 * Align two structures as in zip, but filling in blanks with null.
 */
public fun <K, A, B> Map<K, A>.padZip(other: Map<K, B>): Map<K, Pair<A?, B?>> =
  padZip(other) { _, a, b -> a to b }

public fun <K, A, B, C> Map<K, A>.padZip(other: Map<K, B>, fa: (K, A?, B?) -> C): Map<K, C> =
  padZip(other, { k, a -> fa(k, a, null) }, { k, b -> fa(k, null, b) }) { k, a, b -> fa(k, a, b) }

@Suppress("UNCHECKED_CAST")
public inline fun <K, A, B, C> Map<K, A>.padZip(
  other: Map<K, B>,
  left: (K, A) -> C,
  right: (K, B) -> C,
  both: (K, A, B) -> C
): Map<K, C> = buildMap {
  (this@padZip.keys + other.keys).forEach { key ->
    when {
      this@padZip.containsKey(key) && other.containsKey(key) ->
        put(key, both(key, this@padZip[key] as A, other[key] as B))

      this@padZip.containsKey(key) -> put(key, left(key, this@padZip[key] as A))
      other.containsKey(key) -> put(key, right(key, other[key] as B))
    }
  }
}

/**
 * Splits a union into its component parts.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   mapOf(
 *     "first" to Ior.Both("A", 1),
 *     "second" to Ior.Both("B", 2),
 *     "third" to Ior.Left("C")
 *   ).unalign() shouldBe Pair(mapOf("first" to "A", "second" to "B", "third" to "C"), mapOf("first" to 1, "second" to 2))
 * }
 * ```
 * <!--- KNIT example-map-05.kt -->
 * <!--- lines.isEmpty() -->
 */
public fun <K, A, B> Map<K, Ior<A, B>>.unalign(): Pair<Map<K, A>, Map<K, B>> =
  unalign { (_, ior) -> ior }

/**
 * after applying the given function, splits the resulting union shaped structure into its components parts
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   mapOf("1" to 1, "2" to 2, "3" to 3)
 *     .unalign { (key, value) ->
 *       when(key) {
 *         "1" -> Ior.Left(value)
 *         "2" -> Ior.Right(key)
 *         else -> Ior.Both(value, key)
 *       }
 *     } shouldBe Pair(mapOf("1" to 1, "3" to 3), mapOf("2" to 2, "3" to 3))
 * }
 * ```
 * <!--- KNIT example-map-06.kt -->
 * <!--- lines.isEmpty() -->
 */
public inline fun <K, A, B, C> Map<K, C>.unalign(fa: (Map.Entry<K, C>) -> Ior<A, B>): Pair<Map<K, A>, Map<K, B>> {
  val lefts = mutableMapOf<K, A>()
  val rights = mutableMapOf<K, B>()
  forEach { entry ->
    fa(entry).fold(
      { lefts[entry.key] = it },
      { rights[entry.key] = it },
      { a, b ->
        lefts[entry.key] = a
        rights[entry.key] = b
      }
    )
  }
  return lefts to rights
}

/**
 * Unzips the structure holding the resulting elements in an `Pair`
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   mapOf(
 *     "first" to ("A" to 1),
 *     "second" to ("B" to 2)
 *   ).unzip() shouldBe Pair(mapOf("first" to "A", "second" to "B"), mapOf("first" to 1, "second" to 2))
 * }
 * ```
 * <!--- KNIT example-map-07.kt -->
 * <!--- lines.isEmpty() -->
 */
public fun <K, A, B> Map<K, Pair<A, B>>.unzip(): Pair<Map<K, A>, Map<K, B>> =
  unzip { (_, pair) -> pair }

/**
 * After applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   mapOf("first" to "A:1", "second" to "B:2", "third" to "C:3").unzip { (_, e) ->
 *     e.split(":").let {
 *       it.first() to it.last()
 *     }
 *   } shouldBe Pair(
 *     mapOf("first" to "A", "second" to "B", "third" to "C"),
 *     mapOf("first" to "1", "second" to "2", "third" to "3")
 *   )
 * }
 * ```
 * <!--- KNIT example-map-08.kt -->
 * <!--- lines.isEmpty() -->
 */
public inline fun <K, A, B, C> Map<K, C>.unzip(fc: (Map.Entry<K, C>) -> Pair<A, B>): Pair<Map<K, A>, Map<K, B>> {
  val lefts = mutableMapOf<K, A>()
  val rights = mutableMapOf<K, B>()
  forEach { entry ->
    val (a, b) = fc(entry)
    lefts[entry.key] = a
    rights[entry.key] = b
  }
  return lefts to rights
}

@Suppress("UNCHECKED_CAST")
public fun <K, V> Map<K, V>.getOrNone(key: K): Option<V> {
  val value = get(key)
  if (value == null && !containsKey(key)) {
    return None
  } else {
    @Suppress("UNCHECKED_CAST")
    return Some(value as V)
  }
}

/** Combines two maps using [combine] to combine values for the same key. */
public fun <K, A> Map<K, A>.combine(other: Map<K, A>, combine: (A, A) -> A): Map<K, A> =
  if (size < other.size) fold(other) { my, (k, b) -> my + Pair(k, my[k]?.let { combine(b, it) } ?: b) }
  else other.fold(this@combine) { my, (k, a) -> my + Pair(k, my[k]?.let { combine(it, a) } ?: a) }

public inline fun <K, A, B> Map<K, A>.fold(initial: B, operation: (acc: B, Map.Entry<K, A>) -> B): B {
  var accumulator = initial
  forEach { accumulator = operation(accumulator, it) }
  return accumulator
}
