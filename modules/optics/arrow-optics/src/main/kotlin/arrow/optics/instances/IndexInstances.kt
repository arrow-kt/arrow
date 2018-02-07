package arrow.optics.instances

import arrow.data.*
import arrow.instance
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.listToListKW
import arrow.optics.stringToList
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.index
import arrow.syntax.either.left
import arrow.syntax.either.right

@instance(ListKW::class)
interface ListKWIndexInstance<A> : Index<ListKWOf<A>, Int, A> {
    override fun index(i: Int): Optional<ListKWOf<A>, A> = POptional(
            getOrModify = { it.reify().getOrNull(i)?.right() ?: it.reify().left() },
            set = { a -> { l -> l.reify().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
    )
}

@instance(MapKW::class)
interface MapKWIndexInstance<K, V> : Index<MapKWOf<K, V>, K, V> {
    override fun index(i: K): Optional<MapKWOf<K, V>, V> = POptional(
            getOrModify = { it.reify()[i]?.right() ?: it.left() },
            set = { v -> { m -> m.reify().mapValues { (k, vv) -> if (k == i) v else vv }.k() } }
    )
}

@instance(SequenceKW::class)
interface SequenceKWIndexInstance<A> : Index<SequenceKWOf<A>, Int, A> {
    override fun index(i: Int): Optional<SequenceKWOf<A>, A> = POptional(
            getOrModify = { it.reify().sequence.elementAtOrNull(i)?.right() ?: it.reify().left() },
            set = { a -> { it.reify().mapIndexed { index, aa -> if (index == i) a else aa }.k() } }
    )
}

object StringIndexInstance : Index<String, Int, Char> {

    override fun index(i: Int): Optional<String, Char> =
            stringToList compose listToListKW() compose index<ListKW<Char>, Int, Char>().index(i)
}

object StringIndexInstanceImplicits {
    @JvmStatic
    fun instance(): StringIndexInstance = StringIndexInstance
}

@instance(NonEmptyList::class)
interface NonEmptyListIndexInstance<A> : Index<NonEmptyListOf<A>, Int, A> {

    override fun index(i: Int): Optional<NonEmptyListOf<A>, A> = POptional(
            getOrModify = { l ->
                l.reify().all.getOrNull(i)?.right() ?: l.reify().left()
            },
            set = { a ->
                { l ->
                    NonEmptyList.fromListUnsafe(
                            l.reify().all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
                    )
                }
            }
    )
}