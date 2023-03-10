@file:OptIn(ExperimentalTypeInference::class)

package arrow.core

import arrow.typeclasses.SemigroupDeprecation
import kotlin.experimental.ExperimentalTypeInference
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.either
import arrow.core.raise.fold
import arrow.core.raise.nullable
import arrow.core.raise.option
import arrow.typeclasses.Semigroup
import arrow.typeclasses.combine

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
public inline fun <Key, A, B, C> Map<Key, A>.zip(other: Map<Key, B>, map: (Key, A, B) -> C): Map<Key, C> =
  buildMap(size) {
    this@zip.forEach { (key, bb) ->
      nullable {
        put(key, map(key, bb, other[key].bind()))
      }
    }
  }

public inline fun <Key, B, C, D, E> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  map: (Key, B, C, D) -> E
): Map<Key, E> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    nullable {
      put(key, map(key, bb, c[key].bind(), d[key].bind()))
    }
  }
}

public inline fun <Key, B, C, D, E, F> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  map: (Key, B, C, D, E) -> F
): Map<Key, F> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    nullable {
      put(key, map(key, bb, c[key].bind(), d[key].bind(), e[key].bind()))
    }
  }
}

public inline fun <Key, B, C, D, E, F, G> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  map: (Key, B, C, D, E, F) -> G
): Map<Key, G> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    nullable {
      put(key, map(key, bb, c[key].bind(), d[key].bind(), e[key].bind(), f[key].bind()))
    }
  }
}

public inline fun <Key, B, C, D, E, F, G, H> Map<Key, B>.zip(
  c: Map<Key, C>,
  d: Map<Key, D>,
  e: Map<Key, E>,
  f: Map<Key, F>,
  g: Map<Key, G>,
  map: (Key, B, C, D, E, F, G) -> H
): Map<Key, H> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    nullable {
      put(key, map(key, bb, c[key].bind(), d[key].bind(), e[key].bind(), f[key].bind(), g[key].bind()))
    }
  }
}

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
    nullable {
      put(key, map(key, bb, c[key].bind(), d[key].bind(), e[key].bind(), f[key].bind(), g[key].bind(), h[key].bind()))
    }
  }
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
): Map<Key, J> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    nullable {
      put(
        key,
        map(
          key,
          bb,
          c[key].bind(),
          d[key].bind(),
          e[key].bind(),
          f[key].bind(),
          g[key].bind(),
          h[key].bind(),
          i[key].bind()
        )
      )
    }
  }
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
): Map<Key, K> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    nullable {
      put(
        key,
        map(
          key,
          bb,
          c[key].bind(),
          d[key].bind(),
          e[key].bind(),
          f[key].bind(),
          g[key].bind(),
          h[key].bind(),
          i[key].bind(),
          j[key].bind()
        )
      )
    }
  }
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
): Map<Key, L> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    nullable {
      put(
        key,
        map(
          key,
          bb,
          c[key].bind(),
          d[key].bind(),
          e[key].bind(),
          f[key].bind(),
          g[key].bind(),
          h[key].bind(),
          i[key].bind(),
          j[key].bind(),
          k[key].bind()
        )
      )
    }
  }
}

/**
 * Transform every [Map.Entry] of the original [Map] using [f],
 * only keeping the [Map.Entry] of the transformed map that match the input [Map.Entry].
 */
public fun <K, A, B> Map<K, A>.flatMap(f: (Map.Entry<K, A>) -> Map<K, B>): Map<K, B> =
  buildMap {
    this@flatMap.forEach { entry ->
      f(entry)[entry.key]?.let { put(entry.key, it) }
    }
  }

@Deprecated(
  "",
  ReplaceWith("either<E, Map<K, B>> { this.mapValues { (_, a) -> f(a).bind() } }", "arrow.core.raise.either")
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <K, E, A, B> Map<K, A>.traverse(f: (A) -> Either<E, B>): Either<E, Map<K, B>> =
  either { mapValues { (_, a) -> f(a).bind() } }

@Deprecated(
  "traverseEither is being renamed to traverse to simplify the Arrow API",
  ReplaceWith("traverse(f)", "arrow.core.traverse")
)
public inline fun <K, E, A, B> Map<K, A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Map<K, B>> =
  traverse(f)

@Deprecated(
  "",
  ReplaceWith("either<E, Map<K, B>> { this.mapValues { (_, a) -> a.bind() } }", "arrow.core.raise.either")
)
public fun <K, E, A> Map<K, Either<E, A>>.sequence(): Either<E, Map<K, A>> =
  either { mapValues { (_, a) -> a.bind() } }

@Deprecated(
  "sequenceEither is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <K, E, A> Map<K, Either<E, A>>.sequenceEither(): Either<E, Map<K, A>> =
  sequence()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate(semigroup::combine) { f(it).bind() }.toValidated()",
    "arrow.core.mapOrAccumulate",
    "arrow.typeclasses.combine"
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
    "mapOrAccumulate(semigroup::combine) { f(it.value).bind() }.toValidated()",
    "arrow.core.mapOrAccumulate",
    "arrow.typeclasses.combine"
  )
)
public inline fun <K, E, A, B> Map<K, A>.traverse(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, Map<K, B>> =
  mapOrAccumulate(semigroup::combine) { f(it.value).bind() }.toValidated()

public inline fun <K, E, A, B> Map<K, A>.mapOrAccumulate(
  combine: (E, E) -> E,
  @BuilderInference transform: RaiseAccumulate<E>.(Map.Entry<K, A>) -> B
): Either<E, Map<K, B>> {
  var left: Any? = EmptyValue
  val right = mutableMapOf<K, B>()
  for (element in this)
    fold(
      { transform(RaiseAccumulate(this), element) },
      { errors -> left = EmptyValue.combine(left, errors.reduce(combine), combine) },
      { right[element.key] = it }
    )
  return if (left !== EmptyValue) EmptyValue.unbox<E>(left).left() else right.right()
}

public inline fun <K, E, A, B> Map<K, A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<E>.(Map.Entry<K, A>) -> B
): Either<NonEmptyList<E>, Map<K, B>> {
  val left = mutableListOf<E>()
  val right = mutableMapOf<K, B>()
  for (element in this)
    fold({ transform(RaiseAccumulate(this), element) }, { error -> left.addAll(error) }, { right[element.key] = it })
  return left.toNonEmptyListOrNull()?.left() ?: right.right()
}

@Deprecated(
  "sequenceValidated is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence(semigroup)", "arrow.core.sequence")
)
public fun <K, E, A> Map<K, Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Map<K, A>> =
  sequence(semigroup)

public fun <K, E, A> Map<K, Validated<E, A>>.sequence(semigroup: Semigroup<E>): Validated<E, Map<K, A>> =
  traverse(semigroup, ::identity)

@Deprecated(
  "",
  ReplaceWith("option<Map<K, B>> { this.mapValues { (_, a) -> f(a).bind() } }", "arrow.core.raise.option")
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <K, A, B> Map<K, A>.traverse(f: (A) -> Option<B>): Option<Map<K, B>> =
  option { mapValues { (_, a) -> f(a).bind() } }

@Deprecated(
  "traverseOption is being renamed to traverse to simplify the Arrow API",
  ReplaceWith("traverse(f)", "arrow.core.traverse")
)
public inline fun <K, A, B> Map<K, A>.traverseOption(f: (A) -> Option<B>): Option<Map<K, B>> =
  traverse(f)

@Deprecated(
  "sequenceOption is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <K, V> Map<K, Option<V>>.sequenceOption(): Option<Map<K, V>> =
  sequence()

@Deprecated(
  "",
  ReplaceWith("option<Map<K, B>> { this.mapValues { (_, a) -> a.bind() } }", "arrow.core.raise.option")
)
public fun <K, V> Map<K, Option<V>>.sequence(): Option<Map<K, V>> =
  option { mapValues { (_, a) -> a.bind() } }

@Deprecated(
  "",
  ReplaceWith("this.mapValues { }")
)
public fun <K, A> Map<K, A>.void(): Map<K, Unit> =
  mapValues { }

public fun <K, B, A : B> Map<K, A>.widen(): Map<K, B> =
  this

public fun <K, A, B> Map<K, A>.mapNotNull(transform: (Map.Entry<K, A>) -> B?): Map<K, B> =
  buildMap {
    this@mapNotNull.forEach { entry ->
      transform(entry)?.let { put(entry.key, it) }
    }
  }

@Deprecated(
  "",
  ReplaceWith("mapNotNull { (_, a) -> f(a) }", "arrow.core.mapNotNull")
)
public fun <K, A, B> Map<K, A>.filterMap(f: (A) -> B?): Map<K, B> =
  mapNotNull { (_, a) -> f(a) }

public fun <K, A> Map<K, Option<A>>.filterOption(): Map<K, A> =
  buildMap {
    this@filterOption.forEach { (key, option) ->
      option.fold({ }, { put(key, it) })
    }
  }

/**
 * Returns a Map containing all elements that are instances of specified type parameter R.
 */
public inline fun <K, reified R> Map<K, *>.filterIsInstance(): Map<K, R> =
  mapNotNull { it as? R }

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
  padZip(b, { _, a -> Ior.Left(a) }, { _, bb -> Ior.Right(bb) }) { _, a, bb -> Ior.Both(a, bb) }

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

public fun <K, A> Map<K, A>.align(other: Map<K, A>, combine: (A, A) -> A): Map<K, A> =
  padZip(other, { _, a -> a }, { _, b -> b }) { _, a, b -> combine(a, b) }

@Deprecated(
  "${SemigroupDeprecation}\n use align instead",
  ReplaceWith("align(other, SG::combine)", "arrow.typeclasses.combine")
)
public fun <K, A> Map<K, A>.salign(SG: Semigroup<A>, other: Map<K, A>): Map<K, A> =
  align(other, SG::combine)

/**
 * Align two structures as in zip, but filling in blanks with null.
 */
public fun <K, A, B> Map<K, A>.padZip(other: Map<K, B>): Map<K, Pair<A?, B?>> =
  padZip(other) { _, a, b -> a to b }

public fun <K, A, B, C> Map<K, A>.padZip(other: Map<K, B>, fa: (K, A?, B?) -> C): Map<K, C> =
  padZip(other, { k, a -> fa(k, a, null) }, { k, b -> fa(k, null, b) }) { k, a, b -> fa(k, a, b) }

public fun <K, A, B, C> Map<K, A>.padZip(
  other: Map<K, B>,
  left: (K, A) -> C,
  right: (K, B) -> C,
  both: (K, A, B) -> C
): Map<K, C> =
  buildMap {
    (keys + other.keys).forEach { key ->
      @Suppress("UNCHECKED_CAST")
      when {
        key in this@padZip && key in other -> put(key, both(key, this@padZip[key] as A, other[key] as B))
        key in this@padZip -> put(key, left(key, this@padZip[key] as A))
        key in other -> put(key, right(key, other[key] as B))
      }
    }
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
  unalign { (_, ior) -> ior }

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
  unzip { (_, pair) -> pair }

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
public fun <K, V> Map<K, V>.getOrNone(key: K): Option<V> =
  if (containsKey(key)) Some(get(key) as V) else None

/** Combines two maps using [combine] to combine values for the same key. */
public fun <K, A> Map<K, A>.combine(other: Map<K, A>, combine: (A, A) -> A): Map<K, A> =
  if (size < other.size) fold(other) { my, (k, b) -> my + Pair(k, my[k]?.let { combine(b, it) } ?: b) }
  else other.fold(this@combine) { my, (k, a) -> my + Pair(k, my[k]?.let { combine(a, it) } ?: a) }

@Deprecated(SemigroupDeprecation, ReplaceWith("combine(b, SG::combine)", "arrow.typeclasses.combine"))
public fun <K, A> Map<K, A>.combine(SG: Semigroup<A>, b: Map<K, A>): Map<K, A> =
  combine(b, SG::combine)

@Deprecated(
  "Use fold & Map.combine instead.\n$NicheAPI",
  ReplaceWith(
    "fold(emptyMap()) { acc, map -> acc.combine(map, SG::combine) }",
    "arrow.core.combine",
    "arrow.typeclasses.combine"
  )
)
public fun <K, A> Iterable<Map<K, A>>.combineAll(SG: Semigroup<A>): Map<K, A> =
  fold(emptyMap()) { acc, map -> acc.combine(map, SG::combine) }

public inline fun <K, A, B> Map<K, A>.fold(initial: B, operation: (acc: B, Map.Entry<K, A>) -> B): B {
  var accumulator = initial
  forEach { accumulator = operation(accumulator, it) }
  return accumulator
}

@Deprecated("Use fold instead foldLeft", ReplaceWith("fold<K, A, B>(b, f)"))
public inline fun <K, A, B> Map<K, A>.foldLeft(b: B, f: (B, Map.Entry<K, A>) -> B): B =
  fold(b, f)

@Deprecated("Internal method will be removed from binary in 2.0.0")
internal fun <K, A> Pair<K, A>?.asIterable(): Iterable<Pair<K, A>> =
  when (this) {
    null -> emptyList()
    else -> listOf(this)
  }
