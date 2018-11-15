package arrow.optics.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.optics.*
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

fun <K, V> MapInstances.at(): At<Map<K, V>, K, Option<V>> = MapAtInstance()

/**
 * [At] instance definition for [Map].
 */
interface MapAtInstance<K, V> : At<Map<K, V>, K, Option<V>> {
  override fun at(i: K): Lens<Map<K, V>, Option<V>> = PLens(
    get = { it.getOption(i) },
    set = { map, optV ->
      optV.fold({
        (map - i)
      }, {
        (map + (i to it))
      })
    }
  )

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : MapAtInstance<K, V> {}
  }
}

fun <K, V> MapInstances.traversal(): Traversal<Map<K, V>, V> = MapTraversal()

/**
 * [Traversal] for [Map] that focuses in each [V] of the source [Map].
 */
interface MapTraversal<K, V> : Traversal<Map<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
    s.k().traverse(FA, f)
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke(): MapTraversal<K, V> = object : MapTraversal<K, V> {}
  }
}

fun <K, V> MapInstances.each(): Each<Map<K, V>, V> = MapEachInstance()

/**
 * [Each] instance definition for [Map].
 */
interface MapEachInstance<K, V> : Each<Map<K, V>, V> {
  override fun each() = MapTraversal<K, V>()

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : MapEachInstance<K, V> {}
  }
}

fun <K, V> MapInstances.filterIndex(): FilterIndex<Map<K, V>, K, V> = MapFilterIndexInstance()

/**
 * [FilterIndex] instance definition for [Map].
 */
interface MapFilterIndexInstance<K, V> : FilterIndex<Map<K, V>, K, V> {
  override fun filter(p: Predicate<K>) = object : Traversal<Map<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
      s.toList().k().traverse(FA) { (k, v) ->
        (if (p(k)) f(v) else just(v)).map {
          k to it
        }
      }.map { it.toMap() }
    }
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : MapFilterIndexInstance<K, V> {}
  }
}

fun <K, V> MapInstances.index(): Index<Map<K, V>, K, V> = MapIndexInstance()

/**
 * [Index] instance definition for [Map].
 */
interface MapIndexInstance<K, V> : Index<Map<K, V>, K, V> {
  override fun index(i: K): Optional<Map<K, V>, V> = POptional(
    getOrModify = { it[i]?.right() ?: it.left() },
    set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv } }
  )

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : MapIndexInstance<K, V> {}
  }
}
