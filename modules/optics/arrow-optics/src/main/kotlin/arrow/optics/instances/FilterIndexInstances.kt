package arrow.optics.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*
import arrow.optics.Traversal
import arrow.optics.listToListKW
import arrow.optics.stringToList
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.filterIndex

@instance(ListKW::class)
interface ListKWFilterIndexInstance<A> : FilterIndex<ListKWOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<ListKWOf<A>, A> = FilterIndex.fromTraverse<ForListKW, A>({ aas ->
        aas.reify().mapIndexed { index, a -> a toT index }.k()
    }, ListKW.traverse()).filter(p)
}

@instance(NonEmptyList::class)
interface NonEmptyListFilterIndexInstance<A> : FilterIndex<NonEmptyListOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<NonEmptyListOf<A>, A> = FilterIndex.fromTraverse<ForNonEmptyList, A>({ aas ->
        aas.reify().all.mapIndexed { index, a -> a toT index }.let {
            NonEmptyList.fromListUnsafe(it)
        }
    }, NonEmptyList.traverse()).filter(p)
}

@instance(SequenceKW::class)
interface SequenceKWFilterIndexInstance<A> : FilterIndex<SequenceKWOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<SequenceKWOf<A>, A> = FilterIndex.fromTraverse<ForSequenceKW, A>({ aas ->
        aas.reify().mapIndexed { index, a -> a toT index }.k()
    }, SequenceKW.traverse()).filter(p)
}

@instance(MapKW::class)
interface MapKWFilterIndexInstance<K, V> : FilterIndex<MapKWOf<K, V>, K, V> {

    override fun filter(p: (K) -> Boolean): Traversal<MapKWOf<K, V>, V> = object : Traversal<MapKWOf<K, V>, V> {
        override fun <F> modifyF(FA: Applicative<F>, s: Kind<Kind<ForMapKW, K>, V>, f: (V) -> Kind<F, V>): Kind<F, Kind<Kind<ForMapKW, K>, V>> =
                ListKW.traverse().traverse(s.reify().map.toList().k(), { (k, v) ->
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
            stringToList compose listToListKW() compose filterIndex<ListKW<Char>, Int, Char>().filter(p)
}

object StringFilterIndexInstanceImplicits {
    @JvmStatic fun instance(): StringFilterIndexInstance = StringFilterIndexInstance
}
