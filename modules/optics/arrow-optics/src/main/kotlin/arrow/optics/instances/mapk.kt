package arrow.optics.instances

import arrow.Kind
import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.data.*
import arrow.instance
import arrow.optics.*
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

@instance(MapK::class)
interface MapKAtInstance<K, V> : At<MapK<K, V>, K, Option<V>> {
  override fun at(i: K): Lens<MapK<K, V>, Option<V>> = PLens(
    get = { it.fix().getOption(i) },
    set = { optV ->
      { map ->
        optV.fold({
          (map - i).k()
        }, {
          (map + (i to it)).k()
        })
      }
    }
  )
}

fun <K, V> MapK.Companion.traversal(): Traversal<MapK<K, V>, V> = object : Traversal<MapK<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: MapK<K, V>, f: (V) -> Kind<F, V>): Kind<F, MapK<K, V>> =
    MapK.traverse<K>().run { s.traverse(FA, f) }
}

@instance(MapK::class)
interface MapKEachInstance<K, V> : Each<MapK<K, V>, V> {
  override fun each(): Traversal<MapK<K, V>, V> =
    MapK.traversal()
}

@instance(MapK::class)
interface MapKFilterIndexInstance<K, V> : FilterIndex<MapK<K, V>, K, V> {
  override fun filter(p: (K) -> Boolean): Traversal<MapK<K, V>, V> = object : Traversal<MapK<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: MapK<K, V>, f: (V) -> Kind<F, V>): Kind<F, MapK<K, V>> = FA.run {
      ListK.traverse().run {
        s.map.toList().k().traverse(FA, { (k, v) ->
          (if (p(k)) f(v) else just(v)).map {
            k to it
          }
        })
      }.map { it.toMap().k() }
    }
  }
}

@instance(MapK::class)
interface MapKIndexInstance<K, V> : Index<MapK<K, V>, K, V> {
  override fun index(i: K): Optional<MapK<K, V>, V> = POptional(
    getOrModify = { it[i]?.let(::Right) ?: it.let(::Left) },
    set = { v -> { m -> m.mapValues { (k, vv) -> if (k == i) v else vv }.k() } }
  )
}