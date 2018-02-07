package arrow.optics.instances

import arrow.data.ListKW
import arrow.data.ListKWKind
import arrow.data.MapKW
import arrow.data.MapKWKind
import arrow.data.NonEmptyList
import arrow.data.NonEmptyListKind
import arrow.data.SequenceKW
import arrow.data.SequenceKWKind
import arrow.data.reify
import arrow.data.k
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
interface ListKWIndexInstance<A> : Index<ListKWKind<A>, Int, A> {
    override fun index(i: Int): Optional<ListKWKind<A>, A> = POptional(
            getOrModify = { it.reify().getOrNull(i)?.right() ?: it.reify().left() },
            set = { a -> { l -> l.reify().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
    )
}

@instance(MapKW::class)
interface MapKWIndexInstance<K, V> : Index<MapKWKind<K, V>, K, V> {
    override fun index(i: K): Optional<MapKWKind<K, V>, V> = POptional(
            getOrModify = { it.reify()[i]?.right() ?: it.left() },
            set = { v -> { m -> m.reify().mapValues { (k, vv) -> if (k == i) v else vv }.k() } }
    )
}

@instance(SequenceKW::class)
interface SequenceKWIndexInstance<A> : Index<SequenceKWKind<A>, Int, A> {
    override fun index(i: Int): Optional<SequenceKWKind<A>, A> = POptional(
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
interface NonEmptyListIndexInstance<A> : Index<NonEmptyListKind<A>, Int, A> {

    override fun index(i: Int): Optional<NonEmptyListKind<A>, A> = POptional(
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