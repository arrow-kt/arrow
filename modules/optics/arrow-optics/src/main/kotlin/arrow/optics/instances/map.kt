package arrow.optics.instances

import arrow.Kind
import arrow.core.Left
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.Right
import arrow.data.*
import arrow.optics.*
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

interface MapAtInstance<K, V> : At<Map<K, V>, K, Option<V>> {
  override fun at(i: K): Lens<Map<K, V>, Option<V>> = PLens(
    get = { it.getOption(i) },
    set = { optV ->
      { map ->
        optV.fold({
          (map - i)
        }, {
          (map + (i to it))
        })
      }
    }
  )

  companion object {
    operator fun <K, V> invoke() = object : MapAtInstance<K, V> {}
  }
}

interface MapTraversal<K, V> : Traversal<Map<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = with(MapK.traverse<K>()) {
    FA.run { s.k().traverse(this, f).map { it.map } }
  }

  companion object {
    operator fun <K, V> invoke(): MapTraversal<K, V> = object : MapTraversal<K, V> {}
  }
}

interface MapEachInstance<K, V> : Each<Map<K, V>, V> {
  override fun each() =
    MapTraversal<K, V>()

  companion object {
    operator fun <K, V> invoke() = object : MapEachInstance<K, V> {}
  }
}

interface MapFilterIndexInstance<K, V> : FilterIndex<Map<K, V>, K, V> {
  override fun filter(p: Predicate<K>) = object : Traversal<Map<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
      ListK.traverse().run {
        s.toList().k().traverse(FA, { (k, v) ->
          (if (p(k)) f(v) else just(v))
            .map {
              k to it
            }
        }).map { it.toMap() }
      }
    }
  }

  companion object {
    operator fun <K, V> invoke() = object : MapFilterIndexInstance<K, V> {}
  }
}

interface MapIndexInstance<K, V> : Index<Map<K, V>, K, V> {
  override fun index(i: K): Optional<Map<K, V>, V> = POptional(
    getOrModify = { it[i]?.let(::Right) ?: it.let(::Left) },
    set = { v -> { m -> m.mapValues { (k, vv) -> if (k == i) v else vv } } }
  )

  companion object {
    operator fun <K, V> invoke() = object : MapIndexInstance<K, V> {}
  }
}
