package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative

@higherkind
data class MapK<K, out A>(private val map: Map<K, A>) : MapKOf<K, A>, Map<K, A> by map {

  fun <B> map(f: (A) -> B): MapK<K, B> = this.map.map { it.key to f(it.value) }.toMap().k()

  fun <B, Z> map2(fb: MapK<K, B>, f: (A, B) -> Z): MapK<K, Z> =
    if (fb.isEmpty()) emptyMap<K, Z>().k()
    else this.map.flatMap { (k, a) ->
      fb.getOption(k).map { Tuple2(k, f(a, it)) }.k().asIterable()
    }.k()

  fun <B, Z> map2Eval(fb: Eval<MapK<K, B>>, f: (A, B) -> Z): Eval<MapK<K, Z>> =
    if (fb.value().isEmpty()) Eval.now(emptyMap<K, Z>().k())
    else fb.map { b -> this.map2(b, f) }

  fun <B> ap(ff: MapK<K, (A) -> B>): MapK<K, B> =
    ff.flatMap { this.map(it) }

  fun <B, Z> ap2(f: MapK<K, (A, B) -> Z>, fb: MapK<K, B>): Map<K, Z> =
    f.map.flatMap { (k, f) ->
      this.flatMap { a -> fb.flatMap { b -> mapOf(Tuple2(k, f(a, b))).k() } }
        .getOption(k).map { Tuple2(k, it) }.k().asIterable()
    }.k()

  fun <B> flatMap(f: (A) -> MapK<K, B>): MapK<K, B> =
    this.map.flatMap { (k, v) ->
      f(v).getOption(k).map { Tuple2(k, it) }.k().asIterable()
    }.k()

  fun <B> foldRight(b: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = this.map.values.iterator().iterateRight(b, f)

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.map.values.fold(b, f)

  fun <B> foldLeft(b: MapK<K, B>, f: (MapK<K, B>, Tuple2<K, A>) -> MapK<K, B>): MapK<K, B> =
    this.map.foldLeft(b) { m, (k, v) -> f(m.k(), Tuple2(k, v)) }.k()

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, MapK<K, B>> = GA.run {
    map.iterator().iterateRight(Eval.always { just(emptyMap<K, B>().k()) }) { kv, lbuf ->
      Eval.later { f(kv.value).lazyAp { lbuf.value().map { m -> { b: B -> (mapOf(kv.key to b).k() + m).k() } } } }
    }.value()
  }

  override fun equals(other: Any?): Boolean =
    when (other) {
      is MapK<*, *> -> this.map == other.map
      is Map<*, *> -> this.map == other
      else -> false
    }

  override fun hashCode(): Int = map.hashCode()

  companion object
}

fun <K, A> Map<K, A>.k(): MapK<K, A> = MapK(this)

fun <K, A> Option<Tuple2<K, A>>.k(): MapK<K, A> =
  when (this) {
    is Some -> mapOf(this.t).k()
    is None -> emptyMap<K, A>().k()
  }

fun <K, V, G> MapKOf<K, Kind<G, V>>.sequence(GA: Applicative<G>): Kind<G, MapK<K, V>> =
  fix().traverse(GA, ::identity)

fun <K, A> List<Map.Entry<K, A>>.k(): MapK<K, A> = this.map { it.key to it.value }.toMap().k()

fun <K, A> Map<K, A>.getOption(k: K): Option<A> = Option.fromNullable(this[k])

fun <K, A> MapK<K, A>.updated(k: K, value: A): MapK<K, A> = (this + (k to value)).k()

fun <K, A, B> Map<K, A>.foldLeft(b: Map<K, B>, f: (Map<K, B>, Map.Entry<K, A>) -> Map<K, B>): Map<K, B> {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}

fun <K, A, B> Map<K, A>.foldRight(b: Map<K, B>, f: (Map.Entry<K, A>, Map<K, B>) -> Map<K, B>): Map<K, B> =
  this.entries.reversed().k().foldLeft(b) { x, y: Map.Entry<K, A> -> f(y, x) }

fun <K, V> mapOf(vararg tuple: Tuple2<K, V>): MapK<K, V> =
  if (tuple.isNotEmpty()) tuple.map { it.a to it.b }.toMap().k() else emptyMap<K, V>().k()
