package java_util

import arrow.Kind
import arrow.core.Option
import arrow.core.Predicate
import arrow.data.ListK
import arrow.data.MapK
import arrow.data.extract
import arrow.data.getOption
import arrow.data.k
import arrow.data.traverse
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.syntax.either.left
import arrow.syntax.either.right
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
}

object MapAtInstanceImplicits {
    @JvmStatic
    fun <K, V> instance(): At<Map<K, V>, K, Option<V>> = object : MapAtInstance<K, V> {}
}

interface MapKEachInstance<K, V> : Each<Map<K, V>, V> {
    override fun each() = object : Traversal<Map<K, V>, V> {
        override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> =
                MapK.traverse<K>().traverse(s.k(), f, FA).let {
                    FA.map(it) {
                        it.extract().map
                    }
                }

    }
}

object MapEachInstanceImplicits {
    @JvmStatic
    fun <K, V> instance(): Each<Map<K, V>, V> = object : MapKEachInstance<K, V> {}
}

interface MapFilterIndexInstance<K, V> : FilterIndex<Map<K, V>, K, V> {
    override fun filter(p: Predicate<K>) = object : Traversal<Map<K, V>, V> {
        override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> =
                ListK.traverse().traverse(s.toList().k(), { (k, v) ->
                    FA.map(if (p(k)) f(v) else FA.pure(v)) {
                        k to it
                    }
                }, FA).let {
                    FA.map(it) {
                        it.toMap()
                    }
                }
    }
}

object MapFilterIndexInstanceImplicits {
    @JvmStatic
    fun <K, V> instance(): FilterIndex<Map<K, V>, K, V> = object : MapFilterIndexInstance<K, V> {}
}

interface MapIndexInstance<K, V> : Index<Map<K, V>, K, V> {
    override fun index(i: K): Optional<Map<K, V>, V> = POptional(
            getOrModify = { it[i]?.right() ?: it.left() },
            set = { v -> { m -> m.mapValues { (k, vv) -> if (k == i) v else vv } } }
    )
}

object MapIndexInstanceImplicits {
    @JvmStatic
    fun <K, V> instance(): Index<Map<K, V>, K, V> = object : MapIndexInstance<K, V> {}
}