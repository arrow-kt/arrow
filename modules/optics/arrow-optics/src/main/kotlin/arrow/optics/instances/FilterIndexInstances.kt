package arrow.optics.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*
import arrow.optics.Traversal
import arrow.optics.listToListK
import arrow.optics.stringToList
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.filterIndex

@instance(ListK::class)
interface ListKFilterIndexInstance<A> : FilterIndex<ListKOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<ListKOf<A>, A> = FilterIndex.fromTraverse<ForListK, A>({ aas ->
        aas.extract().mapIndexed { index, a -> a toT index }.k()
    }, ListK.traverse()).filter(p)
}

@instance(NonEmptyList::class)
interface NonEmptyListFilterIndexInstance<A> : FilterIndex<NonEmptyListOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<NonEmptyListOf<A>, A> = FilterIndex.fromTraverse<ForNonEmptyList, A>({ aas ->
        aas.extract().all.mapIndexed { index, a -> a toT index }.let {
            NonEmptyList.fromListUnsafe(it)
        }
    }, NonEmptyList.traverse()).filter(p)
}

@instance(SequenceK::class)
interface SequenceKFilterIndexInstance<A> : FilterIndex<SequenceKOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<SequenceKOf<A>, A> = FilterIndex.fromTraverse<ForSequenceK, A>({ aas ->
        aas.extract().mapIndexed { index, a -> a toT index }.k()
    }, SequenceK.traverse()).filter(p)
}

@instance(MapK::class)
interface MapKFilterIndexInstance<K, V> : FilterIndex<MapKOf<K, V>, K, V> {

    override fun filter(p: (K) -> Boolean): Traversal<MapKOf<K, V>, V> = object : Traversal<MapKOf<K, V>, V> {
        override fun <F> modifyF(FA: Applicative<F>, s: Kind<Kind<ForMapK, K>, V>, f: (V) -> Kind<F, V>): Kind<F, Kind<Kind<ForMapK, K>, V>> =
                ListK.traverse().traverse(s.extract().map.toList().k(), { (k, v) ->
                    FA.map(if (p(k)) f(v) else FA.pure(v)) {
                        k to it
                    }
                }, FA).let {
                    FA.map(it) {
                        it.toMap().k()
                    }
                }

    }
}

object StringFilterIndexInstance : FilterIndex<String, Int, Char> {
    override fun filter(p: (Int) -> Boolean): Traversal<String, Char> =
            stringToList compose listToListK() compose filterIndex<ListK<Char>, Int, Char>().filter(p)
}

object StringFilterIndexInstanceImplicits {
    @JvmStatic fun instance(): StringFilterIndexInstance = StringFilterIndexInstance
}
