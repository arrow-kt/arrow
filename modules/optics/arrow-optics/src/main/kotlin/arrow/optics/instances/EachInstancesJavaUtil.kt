package java_util

import arrow.Kind
import arrow.data.ListK
import arrow.data.MapK
import arrow.data.extract
import arrow.data.k
import arrow.data.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

interface ListEachInstance<A> : Each<List<A>, A> {
    override fun each() = object : Traversal<List<A>, A> {
        override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
                ListK.traverse().traverse(s.k(), f, FA).let {
                    FA.map(it) {
                        it.list
                    }
                }
    }
}

object ListEachInstanceImplicits {
    @JvmStatic
    fun <A> instance(): Each<List<A>, A> = object : ListEachInstance<A> {}
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