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
            getOrModify = { it.fix().getOrNull(i)?.right() ?: it.fix().left() },
            set = { a -> { l -> l.fix().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
    )
}

@instance(MapK::class)
interface MapKIndexInstance<K, V> : Index<MapKOf<K, V>, K, V> {
    override fun index(i: K): Optional<MapKOf<K, V>, V> = POptional(
            getOrModify = { it.fix()[i]?.right() ?: it.left() },
            set = { v -> { m -> m.fix().mapValues { (k, vv) -> if (k == i) v else vv }.k() } }
    )
}

@instance(SequenceK::class)
interface SequenceKIndexInstance<A> : Index<SequenceKOf<A>, Int, A> {
    override fun index(i: Int): Optional<SequenceKOf<A>, A> = POptional(
            getOrModify = { it.fix().sequence.elementAtOrNull(i)?.right() ?: it.fix().left() },
            set = { a -> { it.fix().mapIndexed { index, aa -> if (index == i) a else aa }.k() } }
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
                l.fix().all.getOrNull(i)?.right() ?: l.fix().left()
            },
            set = { a ->
                { l ->
                    NonEmptyList.fromListUnsafe(
                            l.fix().all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
                    )
                }
            }
    )
}