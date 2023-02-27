@file:OptIn(ExperimentalTypeInference::class)

package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.experimental.ExperimentalTypeInference
import kotlin.collections.flatMap as _flatMap
import arrow.core.raise.Raise
import arrow.core.raise.fold

/**
 * Combines to structures by taking the intersection of their shapes
 * and using `Pair` to hold the elements.
 *
 * ```kotlin
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
 * <!--- KNIT example-map-01.kt -->
 */
public fun <K, A, B> Map<K, A>.zip(other: Map<K, B>): Map<K, Pair<A, B>> =
  zip(other) { _, a, b -> Pair(a, b) }

/**
 * Combines to structures by taking the intersection of their shapes
 * and combining the elements with the given function.
 *
 * ```kotlin
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
 * <!--- KNIT example-map-02.kt -->
 */
public inline fun <Key, A, B, C> Map<Key, A>.zip(other: Map<Key, B>, map: (Key, A, B) -> C): Map<Key, C> {
  val destination = LinkedHashMap<Key, C>(size)
  for ((key, bb) in this) {
    Nullable.zip(other[key]) { cc -> map(key, bb, cc) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

public inline fun <Key, B, C, D, E> Map<Key, B>.zip(
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

public inline fun <Key, B, C, D, E, F> Map<Key, B>.zip(
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

public inline fun <Key, B, C, D, E, F, G> Map<Key, B>.zip(
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

public inline fun <Key, B, C, D, E, F, G, H> Map<Key, B>.zip(
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

public inline fun <Key, B, C, D, E, F, G, H, I> Map<Key, B>.zip(
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
    Nullable.zip(c[key], d[key], e[key], f[key], g[key], h[key]) { cc, dd, ee, ff, gg, hh ->
      map(
        key,
        bb,
        cc,
        dd,
        ee,
        ff,
        gg,
        hh
      )
    }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

public inline fun <Key, B, C, D, E, F, G, H, I, J> Map<Key, B>.zip(
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
    Nullable.zip(c[key], d[key], e[key], f[key], g[key], h[key], i[key]) { cc, dd, ee, ff, gg, hh, ii ->
      map(
        key,
        bb,
        cc,
        dd,
        ee,
        ff,
        gg,
        hh,
        ii
      )
    }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

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
): Map<Key, K> {
  val destination = LinkedHashMap<Key, K>(size)
  for ((key, bb) in this) {
    Nullable.zip(
      c[key],
      d[key],
      e[key],
      f[key],
      g[key],
      h[key],
      i[key],
      j[key]
    ) { cc, dd, ee, ff, gg, hh, ii, jj -> map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj) }
      ?.let { l -> destination.put(key, l) }
  }
  return destination
}

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
): Map<Key, L> {
  val destination = LinkedHashMap<Key, L>(size)
  for ((key, bb) in this) {
    Nullable.zip(
      c[key],
      d[key],
      e[key],
      f[key],
      g[key],
      h[key],
      i[key],
      j[key],
      k[key]
    ) { cc, dd, ee, ff, gg, hh, ii, jj, kk ->
      map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
    }?.let { l -> destination.put(key, l) }
  }
  return destination
}

public fun <K, A, B> Map<K, A>.flatMap(f: (Map.Entry<K, A>) -> Map<K, B>): Map<K, B> =
  _flatMap { entry ->
    f(entry)[entry.key]?.let { Pair(entry.key, it) }.asIterable()
  }.toMap()

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <K, E, A, B> Map<K, A>.traverse(f: (A) -> Either<E, B>): Either<E, Map<K, B>> {
  val acc = mutableMapOf<K, B>()
  forEach { (k, v) ->
    when (val res = f(v)) {
      is Right -> acc[k] = res.value
      is Left -> return@traverse res
    }
  }
  return acc.right()
}

@Deprecated("traverseEither is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(f)", "arrow.core.traverse"))
public inline fun <K, E, A, B> Map<K, A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Map<K, B>> =
  traverse(f)

public fun <K, E, A> Map<K, Either<E, A>>.sequence(): Either<E, Map<K, A>> =
  traverse(::identity)

@Deprecated("sequenceEither is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <K, E, A> Map<K, Either<E, A>>.sequenceEither(): Either<E, Map<K, A>> =
  sequence()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate({ a, b -> semigroup.run { a.combine(b)  } }) { f(it).bind() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public inline fun <K, E, A, B> Map<K, A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, Map<K, B>> =
  traverse(semigroup, f)

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate({ a, b -> semigroup.run { a.combine(b)  } }) { f(it.value).bind() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public inline fun <K, E, A, B> Map<K, A>.traverse(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, Map<K, B>> =
  mapOrAccumulate({ a, b -> semigroup.run { a.combine(b)  } }) { f(it.value).bind() }.toValidated()

public inline fun <K, E, A, B> Map<K, A>.mapOrAccumulate(
  combine: (E, E) -> E,
  @BuilderInference transform: Raise<E>.(Map.Entry<K, A>) -> B
): Either<E, Map<K, B>> {
  var left: Any? = EmptyValue
  val right = mutableMapOf<K, B>()
  for (element in this)
    fold({ transform(element) }, { left = EmptyValue.combine(left, it, combine) }, { right[element.key] = it })
  return if (left !== EmptyValue) EmptyValue.unbox<E>(left).left() else right.right()
}

public inline fun <K, E, A, B> Map<K, A>.mapOrAccumulate(
  @BuilderInference transform: AccumulatingRaise<E>.(Map.Entry<K, A>) -> B
): Either<NonEmptyList<E>, Map<K, B>> {
  val left = mutableListOf<E>()
  val right = mutableMapOf<K, B>()
  for (element in this)
    fold({ transform(AccumulatingRaise(this), element) }, { error -> left.addAll(error) }, { right[element.key] = it })
  return left.toNonEmptyListOrNull()?.left() ?: right.right()
}

@Deprecated("sequenceValidated is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence(semigroup)", "arrow.core.sequence"))
public fun <K, E, A> Map<K, Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Map<K, A>> =
  sequence(semigroup)

public fun <K, E, A> Map<K, Validated<E, A>>.sequence(semigroup: Semigroup<E>): Validated<E, Map<K, A>> =
  traverse(semigroup, ::identity)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <K, A, B> Map<K, A>.traverse(f: (A) -> Option<B>): Option<Map<K, B>> {
  val acc = mutableMapOf<K, B>()
  forEach { (k, v) ->
    when (val res = f(v)) {
      is Some -> acc[k] = res.value
      is None -> return@traverse res
    }
  }
  return acc.some()
}

@Deprecated("traverseOption is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(f)", "arrow.core.traverse"))
public inline fun <K, A, B> Map<K, A>.traverseOption(f: (A) -> Option<B>): Option<Map<K, B>> =
  traverse(f)

@Deprecated("sequenceOption is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <K, V> Map<K, Option<V>>.sequenceOption(): Option<Map<K, V>> =
  sequence()

public fun <K, V> Map<K, Option<V>>.sequence(): Option<Map<K, V>> =
  traverse(::identity)

public fun <K, A> Map<K, A>.void(): Map<K, Unit> =
  mapValues { Unit }

public fun <K, B, A : B> Map<K, A>.widen(): Map<K, B> =
  this

public fun <K, A, B> Map<K, A>.filterMap(f: (A) -> B?): Map<K, B> {
  val destination = LinkedHashMap<K, B>(mapCapacity(size))
  for ((key, a) in this) {
    f(a)?.let { l -> destination.put(key, l) }
  }
  return destination
}

public fun <K, A> Map<K, Option<A>>.filterOption(): Map<K, A> = filterMap { it.orNull() }

/**
 * Returns a Map containing all elements that are instances of specified type parameter R.
 */
public inline fun <K, reified R> Map<K, *>.filterIsInstance(): Map<K, R> =
  filterMap { it as? R }

/**
 * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
 *
 * ```kotlin
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
 * <!--- KNIT example-map-03.kt -->
 */
public fun <K, A, B> Map<K, A>.align(b: Map<K, B>): Map<K, Ior<A, B>> =
  (keys + b.keys).mapNotNull { key ->
    Ior.fromNullables(this[key], b[key])?.let { key to it }
  }.toMap()

/**
 * Combines two structures by taking the union of their shapes and combining the elements with the given function.
 *
 * ```kotlin
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
 * <!--- KNIT example-map-04.kt -->
 */
public fun <K, A, B, C> Map<K, A>.align(b: Map<K, B>, fa: (Map.Entry<K, Ior<A, B>>) -> C): Map<K, C> =
  this.align(b).mapValues(fa)

/**
 * aligns two structures and combine them with the given Semigroups '+'
 */
public fun <K, A> Map<K, A>.salign(SG: Semigroup<A>, other: Map<K, A>): Map<K, A> = SG.run {
  align(other) { (_, ior) ->
    ior.fold(::identity, ::identity) { a, b ->
      a.combine(b)
    }
  }
}

/**
 * Align two structures as in zip, but filling in blanks with null.
 */
public fun <K, A, B> Map<K, A>.padZip(other: Map<K, B>): Map<K, Pair<A?, B?>> =
  align(other) { (_, ior) ->
    ior.fold(
      { it to null },
      { null to it },
      { a, b -> a to b }
    )
  }

public fun <K, A, B, C> Map<K, A>.padZip(other: Map<K, B>, fa: (K, A?, B?) -> C): Map<K, C> =
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
 * ```kotlin
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
 * <!--- KNIT example-map-05.kt -->
 */
public fun <K, A, B> Map<K, Ior<A, B>>.unalign(): Pair<Map<K, A>, Map<K, B>> =
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
 * ```kotlin
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
 * <!--- KNIT example-map-06.kt -->
 */
public fun <K, A, B, C> Map<K, C>.unalign(fa: (Map.Entry<K, C>) -> Ior<A, B>): Pair<Map<K, A>, Map<K, B>> =
  mapValues(fa).unalign()

/**
 * Unzips the structure holding the resulting elements in an `Pair`
 *
 * ```kotlin
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
 * <!--- KNIT example-map-07.kt -->
 */
public fun <K, A, B> Map<K, Pair<A, B>>.unzip(): Pair<Map<K, A>, Map<K, B>> =
  entries.fold(emptyMap<K, A>() to emptyMap()) { (ls, rs), (k, v) ->
    ls.plus(k to v.first) to rs.plus(k to v.second)
  }

/**
 * After applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin
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
 * <!--- KNIT example-map-08.kt -->
 */
public fun <K, A, B, C> Map<K, C>.unzip(fc: (Map.Entry<K, C>) -> Pair<A, B>): Pair<Map<K, A>, Map<K, B>> =
  mapValues(fc).unzip()

public fun <K, V> Map<K, V>.getOrNone(key: K): Option<V> = this[key].toOption()

public fun <K, A> Map<K, A>.combine(SG: Semigroup<A>, b: Map<K, A>): Map<K, A> = with(SG) {
  if (size < b.size) foldLeft(b) { my, (k, b) -> my + Pair(k, b.maybeCombine(my[k])) }
  else b.foldLeft(this@combine) { my, (k, a) -> my + Pair(k, a.maybeCombine(my[k])) }
}

@Deprecated("use fold instead", ReplaceWith("fold(Monoid.map(SG))", "arrow.core.fold", "arrow.typeclasses.Monoid"))
public fun <K, A> Iterable<Map<K, A>>.combineAll(SG: Semigroup<A>): Map<K, A> =
  fold(Monoid.map(SG))

public inline fun <K, A, B> Map<K, A>.fold(initial: B, operation: (acc: B, Map.Entry<K, A>) -> B): B {
  var accumulator = initial
  forEach { accumulator = operation(accumulator, it) }
  return accumulator
}

@Deprecated("Use fold instead align with Kotlin Std naming", ReplaceWith("fold<K, A, B>(b, f)"))
public inline fun <K, A, B> Map<K, A>.foldLeft(b: B, f: (B, Map.Entry<K, A>) -> B): B {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}

internal fun <K, A> Pair<K, A>?.asIterable(): Iterable<Pair<K, A>> =
  when (this) {
    null -> emptyList()
    else -> listOf(this)
  }
