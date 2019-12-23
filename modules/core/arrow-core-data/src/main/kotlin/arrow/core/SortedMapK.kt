package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative

@higherkind
data class SortedMapK<A : Comparable<A>, B>(private val map: SortedMap<A, B>) : SortedMapKOf<A, B>, SortedMapKKindedJ<A, B>, SortedMap<A, B> by map {

  fun <C> map(f: (B) -> C): SortedMapK<A, C> =
    this.map.map { it.key to f(it.value) }.toMap().toSortedMap().k()

  fun <C, Z> map2(fc: SortedMapK<A, C>, f: (B, C) -> Z): SortedMapK<A, Z> =
    if (fc.isEmpty()) sortedMapOf<A, Z>().k()
    else this.map.flatMap { (a, b) ->
      fc.getOption(a).map { Tuple2(a, f(b, it)) }.k().asIterable()
    }.k()

  fun <C, Z> map2Eval(fc: Eval<SortedMapK<A, C>>, f: (B, C) -> Z): Eval<SortedMapK<A, Z>> =
    if (fc.value().isEmpty()) Eval.now(sortedMapOf<A, Z>().k())
    else fc.map { c -> this.map2(c, f) }

  fun <C> ap(ff: SortedMapK<A, (B) -> C>): SortedMapK<A, C> =
    ff.flatMap { this.map(it) }

  fun <C, Z> ap2(f: SortedMapK<A, (B, C) -> Z>, fc: SortedMapK<A, C>): SortedMap<A, Z> =
    f.map.flatMap { (k, f) ->
      this.flatMap { a -> fc.flatMap { c -> sortedMapOf(k to f(a, c)).k() } }
        .getOption(k).map { Tuple2(k, it) }.k().asIterable()
    }.k()

  fun <C> flatMap(f: (B) -> SortedMapK<A, C>): SortedMapK<A, C> =
    this.map.flatMap { (k, v) ->
      f(v).getOption(k).map { Tuple2(k, it) }.k().asIterable()
    }.k()

  fun <C> foldRight(c: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    this.map.values.iterator().iterateRight(c, f)

  fun <C> foldLeft(c: C, f: (C, B) -> C): C = this.map.values.fold(c, f)

  fun <C> foldLeft(c: SortedMapK<A, C>, f: (SortedMapK<A, C>, Tuple2<A, B>) -> SortedMapK<A, C>): SortedMapK<A, C> =
    this.map.foldLeft(c) { m: SortedMap<A, C>, (a, b) -> f(m.k(), Tuple2(a, b)) }.k()

  fun <G, C> traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, SortedMapK<A, C>> = GA.run {
    map.iterator().iterateRight(Eval.always { just(sortedMapOf<A, C>().k()) }) { kv, lbuf ->
      Eval.later { f(kv.value).lazyAp { lbuf.value().map { xs -> { b: C -> (mapOf(kv.key to b).k() + xs).toSortedMap().k() } } } }
    }.value()
  }

  override fun equals(other: Any?): Boolean =
    when (other) {
      is SortedMapK<*, *> -> this.map == other.map
      is SortedMap<*, *> -> this.map == other
      else -> false
    }

  override fun hashCode(): Int = map.hashCode()

  companion object
}

fun <A : Comparable<A>, B> SortedMap<A, B>.k(): SortedMapK<A, B> = SortedMapK(this)

fun <A : Comparable<A>, B> Option<Tuple2<A, B>>.k(): SortedMapK<A, B> = this.fold(
  { sortedMapOf<A, B>().k() },
  { mapEntry -> sortedMapOf(mapEntry.a to mapEntry.b).k() }
)

fun <K : Comparable<K>, V, G> SortedMapKOf<K, Kind<G, V>>.sequence(GA: Applicative<G>): Kind<G, SortedMapK<K, V>> =
  fix().traverse(GA, ::identity)

fun <A : Comparable<A>, B> List<Map.Entry<A, B>>.k(): SortedMapK<A, B> =
  this.map { it.key to it.value }.toMap().toSortedMap().k()

fun <A, B> SortedMap<A, B>.getOption(k: A): Option<B> = Option.fromNullable(this[k])

fun <A : Comparable<A>, B> SortedMapK<A, B>.updated(k: A, value: B): SortedMapK<A, B> {
  val sortedMutableMap = this.toSortedMap()
  sortedMutableMap.put(k, value)

  return sortedMutableMap.k()
}

fun <A, B, C> SortedMap<A, B>.foldLeft(b: SortedMap<A, C>, f: (SortedMap<A, C>, Map.Entry<A, B>) -> SortedMap<A, C>): SortedMap<A, C> {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}

fun <A : Comparable<A>, B, C> SortedMap<A, B>.foldRight(b: SortedMap<A, C>, f: (Map.Entry<A, B>, SortedMap<A, C>) -> SortedMap<A, C>): SortedMap<A, C> =
  this.entries.reversed().k().foldLeft(b) { x: SortedMap<A, C>, y: Map.Entry<A, B> -> f(y, x) }
