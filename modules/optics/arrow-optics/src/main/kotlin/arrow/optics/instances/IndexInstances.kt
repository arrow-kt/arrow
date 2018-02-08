package arrow.optics.instances

import arrow.data.*
import arrow.instance
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.listToListK
import arrow.optics.stringToList
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.index
import arrow.syntax.either.left
import arrow.syntax.either.right

@instance(ListK::class)
interface ListKIndexInstance<A> : Index<ListKOf<A>, Int, A> {
    override fun index(i: Int): Optional<ListKOf<A>, A> = POptional(
            getOrModify = { it.reify().getOrNull(i)?.right() ?: it.reify().left() },
            set = { a -> { l -> l.reify().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
    )
}

@instance(MapK::class)
interface MapKIndexInstance<K, V> : Index<MapKOf<K, V>, K, V> {
    override fun index(i: K): Optional<MapKOf<K, V>, V> = POptional(
            getOrModify = { it.reify()[i]?.right() ?: it.left() },
            set = { v -> { m -> m.reify().mapValues { (k, vv) -> if (k == i) v else vv }.k() } }
    )
}

@instance(SequenceK::class)
interface SequenceKIndexInstance<A> : Index<SequenceKOf<A>, Int, A> {
    override fun index(i: Int): Optional<SequenceKOf<A>, A> = POptional(
            getOrModify = { it.reify().sequence.elementAtOrNull(i)?.right() ?: it.reify().left() },
            set = { a -> { it.reify().mapIndexed { index, aa -> if (index == i) a else aa }.k() } }
    )
}

object StringIndexInstance : Index<String, Int, Char> {

    override fun index(i: Int): Optional<String, Char> =
            stringToList compose listToListK() compose index<ListK<Char>, Int, Char>().index(i)
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