package arrow.core

import arrow.Kind
import arrow.KindDeprecation
import arrow.typeclasses.Applicative
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)class ForMapK private constructor() { companion object }
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)typealias MapKOf<K, A> = arrow.Kind2<ForMapK, K, A>
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)typealias MapKPartialOf<K> = arrow.Kind<ForMapK, K>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)inline fun <K, A> MapKOf<K, A>.fix(): MapK<K, A> = this as MapK<K, A>

data class MapK<K, out A>(private val map: Map<K, A>) : MapKOf<K, A>, Map<K, A> by map {

  fun <B> map(f: (A) -> B): MapK<K, B> = this.map.map { it.key to f(it.value) }.toMap().k()

  fun <B, Z> map2(fb: MapK<K, B>, f: (A, B) -> Z): MapK<K, Z> =
    if (fb.isEmpty()) emptyMap<K, Z>().k()
    else this.map.flatMap { (k, a) ->
      fb[k]?.let { mapOf(k to f(a, it)) } ?: emptyMap()
    }.k()

  fun <B, Z> map2Eval(fb: Eval<MapK<K, B>>, f: (A, B) -> Z): Eval<MapK<K, Z>> =
    if (fb.value().isEmpty()) Eval.now(emptyMap<K, Z>().k())
    else fb.map { b -> this.map2(b, f) }

  fun <B> ap(ff: MapK<K, (A) -> B>): MapK<K, B> =
    ff.flatMap { this.map(it) }

  fun <B, Z> ap2(f: MapK<K, (A, B) -> Z>, fb: MapK<K, B>): Map<K, Z> =
    f.map.flatMap { (k, f) ->
      this.flatMap { a -> fb.flatMap { b -> mapOf(Tuple2(k, f(a, b))).k() } }[k]
        ?.let { Pair(k, it) }.asIterable().toMap()
    }.k()

  fun <B> flatMap(f: (A) -> MapK<K, B>): MapK<K, B> =
    this.map.flatMap { (_, v) -> f(v) }.k()

  fun <B> foldRight(b: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = this.map.values.iterator().iterateRight(b, f)

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.map.values.fold(b, f)

  fun <B> foldLeft(b: MapK<K, B>, f: (MapK<K, B>, Tuple2<K, A>) -> MapK<K, B>): MapK<K, B> =
    this.map.foldLeft(b) { m, (k, v) -> f(m.k(), Tuple2(k, v)) }.k()

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, MapK<K, B>> = GA.run {
    map.iterator().iterateRight(Eval.always { just(emptyMap<K, B>().k()) }) { kv, lbuf ->
      f(kv.value).apEval(lbuf.map { it.map { m -> { b: B -> (mapOf(kv.key to b).k() + m).k() } } })
    }.value()
  }

  @Deprecated(ShowDeprecation)
  fun show(SK: Show<K>, SA: Show<A>): String = "Map(${toList().k().map { it.toTuple2() }.show(Show { show(SK, SA) })})"

  override fun toString(): String =
    map.toString()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is MapK<*, *> -> this.map == other.map
      is Map<*, *> -> this.map == other
      else -> false
    }

  override fun hashCode(): Int = map.hashCode()

  companion object {

    inline fun <Key, B, C, D> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      map: (Key, B, C) -> D
    ): Map<Key, D> =
      mapN(b, c, emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>()) { key, bb, cc, _, _, _, _, _, _, _, _ ->
        map(key, bb, cc)
      }

    inline fun <Key, B, C, D, E> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      d: Map<Key, D>,
      map: (Key, B, C, D) -> E
    ): Map<Key, E> =
      mapN(b, c, d, emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>()) { key, bb, cc, dd, _, _, _, _, _, _, _ ->
        map(key, bb, cc, dd)
      }

    inline fun <Key, B, C, D, E, F> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      d: Map<Key, D>,
      e: Map<Key, E>,
      map: (Key, B, C, D, E) -> F
    ): Map<Key, F> =
      mapN(b, c, d, e, emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>()) { key, bb, cc, dd, ee, _, _, _, _, _, _ ->
        map(key, bb, cc, dd, ee)
      }

    inline fun <Key, B, C, D, E, F, G> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      d: Map<Key, D>,
      e: Map<Key, E>,
      f: Map<Key, F>,
      map: (Key, B, C, D, E, F) -> G
    ): Map<Key, G> =
      mapN(b, c, d, e, f, emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>()) { key, bb, cc, dd, ee, ff, _, _, _, _, _ ->
        map(key, bb, cc, dd, ee, ff)
      }

    inline fun <Key, B, C, D, E, F, G, H> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      d: Map<Key, D>,
      e: Map<Key, E>,
      f: Map<Key, F>,
      g: Map<Key, G>,
      map: (Key, B, C, D, E, F, G) -> H
    ): Map<Key, H> =
      mapN(b, c, d, e, f, g, emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>()) { key, bb, cc, dd, ee, ff, gg, _, _, _, _ ->
        map(key, bb, cc, dd, ee, ff, gg)
      }

    inline fun <Key, B, C, D, E, F, G, H, I> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      d: Map<Key, D>,
      e: Map<Key, E>,
      f: Map<Key, F>,
      g: Map<Key, G>,
      h: Map<Key, H>,
      map: (Key, B, C, D, E, F, G, H) -> I
    ): Map<Key, I> =
      mapN(b, c, d, e, f, g, h, emptyMap<Key, Unit>(), emptyMap<Key, Unit>(), emptyMap<Key, Unit>()) { key, bb, cc, dd, ee, ff, gg, hh, _, _, _ ->
        map(key, bb, cc, dd, ee, ff, gg, hh)
      }

    inline fun <Key, B, C, D, E, F, G, H, I, J> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      d: Map<Key, D>,
      e: Map<Key, E>,
      f: Map<Key, F>,
      g: Map<Key, G>,
      h: Map<Key, H>,
      i: Map<Key, I>,
      map: (Key, B, C, D, E, F, G, H, I) -> J
    ): Map<Key, J> =
      mapN(b, c, d, e, f, g, h, i, emptyMap<Key, Unit>(), emptyMap<Key, Unit>()) { key, bb, cc, dd, ee, ff, gg, hh, ii, _, _ ->
        map(key, bb, cc, dd, ee, ff, gg, hh, ii)
      }

    inline fun <Key, B, C, D, E, F, G, H, I, J, K> mapN(
      b: Map<Key, B>,
      c: Map<Key, C>,
      d: Map<Key, D>,
      e: Map<Key, E>,
      f: Map<Key, F>,
      g: Map<Key, G>,
      h: Map<Key, H>,
      i: Map<Key, I>,
      j: Map<Key, J>,
      map: (Key, B, C, D, E, F, G, H, I, J) -> K
    ): Map<Key, K> =
      mapN(b, c, d, e, f, g, h, i, j, emptyMap<Key, Unit>()) { key, bb, cc, dd, ee, ff, gg, hh, ii, jj, _ ->
        map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj)
      }

    inline fun <Key, B, C, D, E, F, G, H, I, J, K, L> mapN(
      b: Map<Key, B>,
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
      val destination = LinkedHashMap<Key, L>(b.size)
      for ((key, bb) in b) {
        Nullable.mapN(c[key], d[key], e[key], f[key], g[key], h[key], i[key], j[key], k[key]) { cc, dd, ee, ff, gg, hh, ii, jj, kk ->
          map(key, bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
        }?.let { l -> destination.put(key, l) }
      }
      return destination
    }
  }
}

fun <K, A> Map<K, A>.k(): MapK<K, A> = MapK(this)

@Deprecated("Deprecated, use nullable instead", ReplaceWith("Tuple2<K, A>>?.let { ... }"))
fun <K, A> Option<Tuple2<K, A>>.k(): MapK<K, A> =
  when (this) {
    is Some -> mapOf(this.t).k()
    is None -> emptyMap<K, A>().k()
  }

fun <K, V, G> MapKOf<K, Kind<G, V>>.sequence(GA: Applicative<G>): Kind<G, MapK<K, V>> =
  fix().traverse(GA, ::identity)

fun <K, A> List<Map.Entry<K, A>>.k(): MapK<K, A> = this.map { it.key to it.value }.toMap().k()

@Deprecated("Deprecated, use nullable instead", ReplaceWith("map[k]?.let { ... }"))
fun <K, A> Map<K, A>.getOption(k: K): Option<A> = Option.fromNullable(this[k])

fun <K, A> MapK<K, A>.updated(k: K, value: A): MapK<K, A> = (this + (k to value)).k()

@Deprecated("Available for binary compat", level = DeprecationLevel.HIDDEN)
fun <K, A, B> Map<K, A>.foldLeft(b: Map<K, B>, f: (Map<K, B>, Map.Entry<K, A>) -> Map<K, B>): Map<K, B> {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}

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

@Deprecated("Available for binary compat", level = DeprecationLevel.HIDDEN)
fun <K, A, B> Map<K, A>.foldRight(b: Map<K, B>, f: (Map.Entry<K, A>, Map<K, B>) -> Map<K, B>): Map<K, B> =
  this.entries.reversed().k().foldLeft(b) { x, y: Map.Entry<K, A> -> f(y, x) }

inline fun <K, A, B> Map<K, A>.foldRight(b: B, f: (Map.Entry<K, A>, B) -> B): B =
  this.entries.reversed().k().foldLeft(b) { x, y: Map.Entry<K, A> -> f(y, x) }

fun <K, V> mapOf(vararg tuple: Tuple2<K, V>): MapK<K, V> =
  if (tuple.isNotEmpty()) tuple.map { it.a to it.b }.toMap().k() else emptyMap<K, V>().k()
