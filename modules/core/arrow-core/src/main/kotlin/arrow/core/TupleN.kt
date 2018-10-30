package arrow.core

import arrow.*
import java.util.*

@higherkind
data class Tuple2<out A, out B>(val a: A, val b: B) : Tuple2Of<A, B> {
  fun <C> map(f: (B) -> C) =
    a toT f(b)

  fun <C, D> bimap(fl: (A) -> C, fr: (B) -> D) =
    fl(a) toT fr(b)

  fun <C> ap(f: Tuple2Of<*, (B) -> C>) =
    map(f.fix().b)

  fun <C> flatMap(f: (B) -> Tuple2Of<@UnsafeVariance A, C>) =
    f(b).fix()

  fun <C> coflatMap(f: (Tuple2Of<A, B>) -> C) =
    a toT f(this)

  fun extract() =
    b

  fun <C> foldL(b: C, f: (C, B) -> C) =
    f(b, this.b)

  fun <C> foldR(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>) =
    f(b, lb)

  fun reverse(): Tuple2<B, A> = Tuple2(b, a)

  companion object
}

@higherkind
data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C) : Tuple3Of<A, B, C> {
  fun reverse(): Tuple3<C, B, A> = Tuple3(c, b, a)

  companion object
}

@higherkind
data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) : Tuple4Of<A, B, C, D> {
  companion object
}

@higherkind
data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) : Tuple5Of<A, B, C, D, E> {
  companion object
}

@higherkind
data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) : Tuple6Of<A, B, C, D, E, F> {
  companion object
}

@higherkind
data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G) : Tuple7Of<A, B, C, D, E, F, G> {
  companion object
}

@higherkind
data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H) : Tuple8Of<A, B, C, D, E, F, G, H> {
  companion object
}

@higherkind
data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I) : Tuple9Of<A, B, C, D, E, F, G, H, I> {
  companion object
}

@higherkind
data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J) : Tuple10Of<A, B, C, D, E, F, G, H, I, J> {
  companion object
}

@higherkind
data class Tuple11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K) : Tuple11Of<A, B, C, D, E, F, G, H, I, J, K> {
  companion object
}

@higherkind
data class Tuple12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L) : Tuple12Of<A, B, C, D, E, F, G, H, I, J, K, L> {
  companion object
}

@higherkind
data class Tuple13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M) : Tuple13Of<A, B, C, D, E, F, G, H, I, J, K, L, M> {
  companion object
}

@higherkind
data class Tuple14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N) : Tuple14Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N> {
  companion object
}

@higherkind
data class Tuple15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O) : Tuple15Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> {
  companion object
}

@higherkind
data class Tuple16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P) : Tuple16Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> {
  companion object
}

@higherkind
data class Tuple17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q) : Tuple17Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> {
  companion object
}

@higherkind
data class Tuple18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R) : Tuple18Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> {
  companion object
}

@higherkind
data class Tuple19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S) : Tuple19Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> {
  companion object
}

@higherkind
data class Tuple20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T) : Tuple20Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> {
  companion object
}

@higherkind
data class Tuple21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U) : Tuple21Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> {
  companion object
}

@higherkind
data class Tuple22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V) : Tuple22Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> {
  companion object
}

private const val INT_MAX_POWER_OF_TWO: Int = Int.MAX_VALUE / 2 + 1

infix fun <A, B> A.toT(b: B): Tuple2<A, B> = Tuple2(this, b)

fun <K, V> Tuple2<K, V>.toPair() : Pair<K, V> = Pair(this.a, this.b)

fun <K, V> Pair<K, V>.toTuple2() : Tuple2<K, V> = Tuple2(this.first, this.second)

fun <K, V> mapOf(vararg tuples: Tuple2<K, V>): Map<K, V> =
  if (tuples.isNotEmpty()) tuples.toMap(LinkedHashMap(mapCapacity(tuples.size))) else emptyMap()

fun <K, V> Iterable<Tuple2<K, V>>.toMap(): Map<K, V> {
  if (this is Collection) {
    return when (size) {
      0 -> emptyMap()
      1 -> mapOf(if (this is List) this[0] else iterator().next())
      else -> toMap(LinkedHashMap(mapCapacity(size)))
    }
  }
  return toMap(LinkedHashMap()).optimizeReadOnlyMap()
}

fun <K, V> Array<out Tuple2<K, V>>.toMap(): Map<K, V> = when (size) {
  0 -> emptyMap()
  1 -> mapOf(this[0])
  else -> toMap(LinkedHashMap(mapCapacity(size)))
}

fun <K, V> Sequence<Tuple2<K, V>>.toMap(): Map<K, V> = toMap(LinkedHashMap()).optimizeReadOnlyMap()

fun <K, V> mapOf(pair: Tuple2<K, V>): Map<K, V> = Collections.singletonMap(pair.a, pair.b)

internal fun <K, V, M : MutableMap<in K, in V>> Iterable<Tuple2<K, V>>.toMap(destination: M): M =
  destination.apply { putAll(this@toMap) }

internal fun <K, V, M : MutableMap<in K, in V>> Array<out Tuple2<K, V>>.toMap(destination: M): M =
  destination.apply { putAll(this@toMap) }

internal fun <K, V, M : MutableMap<in K, in V>> Sequence<Tuple2<K, V>>.toMap(destination: M): M =
  destination.apply { putAll(this@toMap) }

internal fun <K, V> MutableMap<in K, in V>.putAll(tuples: Iterable<Tuple2<K, V>>) {
  for ((key, value) in tuples) {
    put(key, value)
  }
}

internal fun <K, V> MutableMap<in K, in V>.putAll(tuples: Array<out Tuple2<K, V>>) {
  for ((key, value) in tuples) {
    put(key, value)
  }
}

internal fun <K, V> MutableMap<in K, in V>.putAll(tuples: Sequence<Tuple2<K, V>>) {
  for ((key, value) in tuples) {
    put(key, value)
  }
}

operator fun <K, V> Map<out K, V>.plus(tuple: Tuple2<K, V>): Map<K, V> =
  if (this.isEmpty()) mapOf(tuple) else LinkedHashMap(this).apply { put(tuple.a, tuple.b) }

operator fun <K, V> Map<out K, V>.plus(tuples: Iterable<Tuple2<K, V>>): Map<K, V> =
  if (this.isEmpty()) tuples.toMap() else LinkedHashMap(this).apply { putAll(tuples) }

operator fun <K, V> Map<out K, V>.plus(tuples: Array<out Tuple2<K, V>>): Map<K, V> =
  if (this.isEmpty()) tuples.toMap() else LinkedHashMap(this).apply { putAll(tuples) }

operator fun <K, V> Map<out K, V>.plus(tuples: Sequence<Tuple2<K, V>>): Map<K, V> =
  LinkedHashMap(this).apply { putAll(tuples) }.optimizeReadOnlyMap()

inline fun <K, V> Map.Entry<K, V>.toTuple2(): Tuple2<K, V> = Tuple2(key, value)

internal fun mapCapacity(expectedSize: Int): Int =
  when {
    expectedSize < 3 -> expectedSize + 1
    expectedSize < INT_MAX_POWER_OF_TWO -> expectedSize + expectedSize / 3
    else -> Int.MAX_VALUE
  }

// do not expose for now @PublishedApi
internal fun <K, V> Map<K, V>.optimizeReadOnlyMap() =
  when (size) {
    0 -> emptyMap()
    1 -> this.toSingletonMap()
    else -> this
  }

// creates a singleton copy of map
internal fun <K, V> Map<out K, V>.toSingletonMap(): Map<K, V> =
  with(entries.iterator().next()) {
    Collections.singletonMap(key, value)
  }
